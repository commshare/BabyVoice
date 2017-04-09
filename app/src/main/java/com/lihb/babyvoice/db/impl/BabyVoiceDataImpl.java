package com.lihb.babyvoice.db.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lihb.babyvoice.BabyVoiceApp;
import com.lihb.babyvoice.db.DBHelper;
import com.lihb.babyvoice.db.IDBRxManager;
import com.lihb.babyvoice.model.BabyVoice;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by Administrator on 2017/4/9.
 */

public class BabyVoiceDataImpl implements IDBRxManager<BabyVoice> {

    private DBHelper dbHelper;

    private BabyVoiceDataImpl() {
        dbHelper = new DBHelper(BabyVoiceApp.getInstance());
    }

    private static BabyVoiceDataImpl instance = new BabyVoiceDataImpl();

    public static BabyVoiceDataImpl getInstance() {
        return instance;
    }
    @Override
    public Observable<Boolean> insertData(final BabyVoice babyVoice) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                queryData(babyVoice)
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                if (!aBoolean) {
                                    ContentValues values = new ContentValues();
                                    values.put(DBHelper.BABY_VOICE_ENTRY.COLUMN_NAME, babyVoice.name);
                                    values.put(DBHelper.BABY_VOICE_ENTRY.COLUMN_DATE, babyVoice.date);
                                    values.put(DBHelper.BABY_VOICE_ENTRY.COLUMN_DURATION, babyVoice.duration);
                                    values.put(DBHelper.BABY_VOICE_ENTRY.COLUMN_CATEGORY, babyVoice.category);
                                    values.put(DBHelper.BABY_VOICE_ENTRY.COLUMN_URL, babyVoice.url);
                                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                                    if (db.insert(DBHelper.BABY_VOICE_ENTRY.TABLE_NAME, null, values) != -1) {
                                        subscriber.onNext(true);
                                        Logger.i("insert  data success!");
                                    } else {
                                        Logger.e("insert  data failed!");
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
    public Observable<Void> batchInsertData(List<BabyVoice> dataList) {
        return null;
    }

    @Override
    public Observable<List<BabyVoice>> queryAllData() {
        return null;
    }


    public Observable<List<BabyVoice>> queryDataByCondition(final int start, final int limit) {
        return Observable.create(new Observable.OnSubscribe<List<BabyVoice>>() {
            @Override
            public void call(Subscriber<? super List<BabyVoice>> subscriber) {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("select * from " + DBHelper.BABY_VOICE_ENTRY.TABLE_NAME + " limit " + limit + " offset " + start, null);
                List<BabyVoice> result = new ArrayList<BabyVoice>();
                int count = cursor.getCount();
                if (cursor != null && count > 0) {
                    cursor.moveToFirst();
                    for (int i = 0; i < count; i++) {
                        cursor.moveToPosition(i);
                        BabyVoice babyVoice = new BabyVoice();
                        babyVoice.name = cursor.getString(cursor.getColumnIndex(DBHelper.BABY_VOICE_ENTRY.COLUMN_NAME));
                        babyVoice.date = cursor.getString(cursor.getColumnIndex(DBHelper.BABY_VOICE_ENTRY.COLUMN_DATE));
                        babyVoice.duration = cursor.getString(cursor.getColumnIndex(DBHelper.BABY_VOICE_ENTRY.COLUMN_DURATION));
                        babyVoice.category = cursor.getString(cursor.getColumnIndex(DBHelper.BABY_VOICE_ENTRY.COLUMN_CATEGORY));
                        babyVoice.url = cursor.getString(cursor.getColumnIndex(DBHelper.BABY_VOICE_ENTRY.COLUMN_URL));
                        result.add(babyVoice);
                    }
                    cursor.close();
                }
                subscriber.onNext(result);
                subscriber.onCompleted();

            }
        });
    }

    @Override
    public Observable<Boolean> queryData(final BabyVoice babyVoice) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("select * from " + DBHelper.BABY_VOICE_ENTRY.TABLE_NAME + " where "
                        + DBHelper.BABY_VOICE_ENTRY.COLUMN_NAME + " = ? and "
                        + DBHelper.BABY_VOICE_ENTRY.COLUMN_URL + " = ?", new String[]{babyVoice.name, babyVoice.url});
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
    public Observable<Boolean> delData(BabyVoice babyVoice) {
        return null;
    }

    @Override
    public Observable<Boolean> updateData(BabyVoice babyVoice) {
        return null;
    }
}
