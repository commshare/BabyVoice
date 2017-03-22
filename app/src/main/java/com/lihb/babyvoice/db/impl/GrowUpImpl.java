package com.lihb.babyvoice.db.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lihb.babyvoice.BabyVoiceApp;
import com.lihb.babyvoice.db.DBHelper;
import com.lihb.babyvoice.db.IDBRxManager;
import com.lihb.babyvoice.model.GrowUpRecord;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by Administrator on 2017/3/20.
 */

public class GrowUpImpl implements IDBRxManager<GrowUpRecord> {

    private DBHelper dbHelper;

    private GrowUpImpl() {
        dbHelper = new DBHelper(BabyVoiceApp.getInstance());
    }

    private static GrowUpImpl instance = new GrowUpImpl();

    public static GrowUpImpl getInstance() {
        return instance;
    }

    @Override
    public Observable<Boolean> insertData(final GrowUpRecord growUpRecord) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                queryData(growUpRecord)
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                if (!aBoolean) {
                                    ContentValues values = new ContentValues();
                                    values.put(DBHelper.GROW_UP_ENTRY.COLUMN_CONTENT, growUpRecord.content);
                                    values.put(DBHelper.GROW_UP_ENTRY.COLUMN_DATE, growUpRecord.date);
                                    values.put(DBHelper.GROW_UP_ENTRY.COLUMN_PIC_FIRST, growUpRecord.picList.get(0));
                                    values.put(DBHelper.GROW_UP_ENTRY.COLUMN_PIC_SECOND, growUpRecord.picList.get(1));
                                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                                    if (db.insert(DBHelper.GROW_UP_ENTRY.TABLE_NAME, null, values) != -1) {
                                        subscriber.onNext(true);
                                        Logger.i("insert growup record success!");
                                    } else {
                                        Logger.e("insert growup record failed!");
                                        subscriber.onNext(false);
                                    }
                                    subscriber.onCompleted();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public Observable<Void> batchInsertData(List<GrowUpRecord> dataList) {
        return null;
    }

    @Override
    public Observable<List<GrowUpRecord>> queryAllData() {
        return Observable.create(new Observable.OnSubscribe<List<GrowUpRecord>>() {
            @Override
            public void call(Subscriber<? super List<GrowUpRecord>> subscriber) {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("select * from " + DBHelper.GROW_UP_ENTRY.TABLE_NAME, null);
                List<GrowUpRecord> result = new ArrayList<GrowUpRecord>();

                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    for (int i = 0; i < cursor.getCount(); i++) {
                        cursor.moveToPosition(i);
                        GrowUpRecord growUpRecord = new GrowUpRecord();
                        growUpRecord.date = cursor.getString(cursor.getColumnIndex(DBHelper.GROW_UP_ENTRY.COLUMN_DATE));
                        growUpRecord.content = cursor.getString(cursor.getColumnIndex(DBHelper.GROW_UP_ENTRY.COLUMN_CONTENT));
                        String pic1 = cursor.getString(cursor.getColumnIndex(DBHelper.GROW_UP_ENTRY.COLUMN_PIC_FIRST));
                        String pic2 = cursor.getString(cursor.getColumnIndex(DBHelper.GROW_UP_ENTRY.COLUMN_PIC_SECOND));
                        growUpRecord.picList = new ArrayList<String>();
                        if (null != pic1) {
                            growUpRecord.picList.add(pic1);
                        }
                        if (null != pic2) {
                            growUpRecord.picList.add(pic2);
                        }
                        result.add(growUpRecord);
                    }
                    cursor.close();
                }
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> queryData(final GrowUpRecord growUpRecord) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("select * from " + DBHelper.GROW_UP_ENTRY.TABLE_NAME + " where "
                                + DBHelper.GROW_UP_ENTRY.COLUMN_DATE + " = ? and "
                                + DBHelper.GROW_UP_ENTRY.COLUMN_CONTENT + " = ? ",
                        new String[]{growUpRecord.date + "", growUpRecord.content});
                if (cursor != null && cursor.getCount() > 0) {
                    subscriber.onNext(true);
                } else {
                    subscriber.onNext(false);
                }
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> delData(final GrowUpRecord growUpRecord) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                int result = db.delete(DBHelper.GROW_UP_ENTRY.TABLE_NAME,
                        DBHelper.GROW_UP_ENTRY.COLUMN_DATE + " = ? and "
                                + DBHelper.GROW_UP_ENTRY.COLUMN_CONTENT + " = ? ",
                        new String[]{growUpRecord.date, growUpRecord.content});
                if (result != 0) {
                    subscriber.onNext(true);
                } else {
                    subscriber.onNext(false);
                }
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> updateData(GrowUpRecord growUpRecord) {
        return null;
    }
}
