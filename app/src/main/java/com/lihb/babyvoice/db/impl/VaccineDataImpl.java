package com.lihb.babyvoice.db.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lihb.babyvoice.BabyVoiceApp;
import com.lihb.babyvoice.db.DBHelper;
import com.lihb.babyvoice.db.IDBRxManager;
import com.lihb.babyvoice.model.VaccineInfo;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by Administrator on 2017/3/20.
 */

public class VaccineDataImpl implements IDBRxManager<VaccineInfo> {

    private DBHelper dbHelper;

    private VaccineDataImpl() {
        dbHelper = new DBHelper(BabyVoiceApp.getInstance());
    }

    private static VaccineDataImpl instance = new VaccineDataImpl();

    public static VaccineDataImpl getInstance() {
        return instance;
    }

    @Override
    public Observable<Boolean> insertData(final VaccineInfo vaccineInfo) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(final Subscriber<? super Boolean> subscriber) {
                queryData(vaccineInfo)
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                if (!aBoolean) {
                                    ContentValues values = new ContentValues();
                                    values.put(DBHelper.VACCINE_ENTRY.COLUMN_NAME, vaccineInfo.vaccineName);
                                    values.put(DBHelper.VACCINE_ENTRY.COLUMN_FREE, vaccineInfo.isFree);
                                    values.put(DBHelper.VACCINE_ENTRY.COLUMN_INJECTDATE, vaccineInfo.injectDate);
                                    values.put(DBHelper.VACCINE_ENTRY.COLUMN_INJECTED, vaccineInfo.isInjected);
                                    values.put(DBHelper.VACCINE_ENTRY.COLUMN_AGETOINJECT, vaccineInfo.ageToInject);
                                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                                    if (db.insert(DBHelper.VACCINE_ENTRY.TABLE_NAME, null, values) != -1) {
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
    public Observable<Void> batchInsertData(final List<VaccineInfo> dataList) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                final SQLiteDatabase db = dbHelper.getWritableDatabase();
                for (final VaccineInfo vaccineInfo : dataList) {
                    queryData(vaccineInfo)
                            .subscribe(new Action1<Boolean>() {
                                @Override
                                public void call(Boolean aBoolean) {
                                    if(!aBoolean){
                                        ContentValues values = new ContentValues();
                                        values.put(DBHelper.VACCINE_ENTRY.COLUMN_NAME, vaccineInfo.vaccineName);
                                        values.put(DBHelper.VACCINE_ENTRY.COLUMN_FREE, vaccineInfo.isFree);
                                        values.put(DBHelper.VACCINE_ENTRY.COLUMN_INJECTDATE, vaccineInfo.injectDate);
                                        values.put(DBHelper.VACCINE_ENTRY.COLUMN_INJECTED, vaccineInfo.isInjected);
                                        values.put(DBHelper.VACCINE_ENTRY.COLUMN_AGETOINJECT, vaccineInfo.ageToInject);
                                        if (db.insert(DBHelper.VACCINE_ENTRY.TABLE_NAME, null, values) == -1) {
                                            Log.e("VaccineDataImpl", vaccineInfo.toString() + "插入失败");
                                        }
                                    }

                                }
                            });

                }
                subscriber.onNext(null);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<VaccineInfo>> queryAllData() {
        return Observable.create(new Observable.OnSubscribe<List<VaccineInfo>>() {
            @Override
            public void call(Subscriber<? super List<VaccineInfo>> subscriber) {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("select * from " + DBHelper.VACCINE_ENTRY.TABLE_NAME, null);
                List<VaccineInfo> result = new ArrayList<VaccineInfo>();

                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    for (int i = 0; i < cursor.getCount(); i++) {
                        cursor.moveToPosition(i);
                        VaccineInfo vaccineInfo = new VaccineInfo();
                        vaccineInfo.vaccineName = cursor.getString(cursor.getColumnIndex(DBHelper.VACCINE_ENTRY.COLUMN_NAME));
                        vaccineInfo.ageToInject = cursor.getInt(cursor.getColumnIndex(DBHelper.VACCINE_ENTRY.COLUMN_AGETOINJECT));
                        vaccineInfo.isFree = cursor.getInt(cursor.getColumnIndex(DBHelper.VACCINE_ENTRY.COLUMN_FREE));
                        vaccineInfo.injectDate = cursor.getString(cursor.getColumnIndex(DBHelper.VACCINE_ENTRY.COLUMN_INJECTDATE));
                        vaccineInfo.isInjected = cursor.getInt(cursor.getColumnIndex(DBHelper.VACCINE_ENTRY.COLUMN_INJECTED));
                        result.add(vaccineInfo);
                    }
                    cursor.close();
                }
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> queryData(final VaccineInfo vaccineInfo) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("select * from " + DBHelper.VACCINE_ENTRY.TABLE_NAME + " where "
                        + DBHelper.VACCINE_ENTRY.COLUMN_NAME + " = ? and "
                        + DBHelper.VACCINE_ENTRY.COLUMN_AGETOINJECT + " = ?", new String[]{vaccineInfo.vaccineName + "", vaccineInfo.ageToInject + ""});
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
    public Observable<Boolean> delData(final VaccineInfo vaccineInfo) {
        return null;
    }

    @Override
    public Observable<Boolean> updateData(final VaccineInfo vaccineInfo) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DBHelper.VACCINE_ENTRY.COLUMN_NAME, vaccineInfo.vaccineName);
                values.put(DBHelper.VACCINE_ENTRY.COLUMN_FREE, vaccineInfo.isFree);
                values.put(DBHelper.VACCINE_ENTRY.COLUMN_INJECTDATE, vaccineInfo.injectDate);
                values.put(DBHelper.VACCINE_ENTRY.COLUMN_INJECTED, vaccineInfo.isInjected);
                values.put(DBHelper.VACCINE_ENTRY.COLUMN_AGETOINJECT, vaccineInfo.ageToInject);
                int res = db.update(DBHelper.VACCINE_ENTRY.TABLE_NAME, values,
                        DBHelper.VACCINE_ENTRY.COLUMN_NAME + "=? and "
                                + DBHelper.VACCINE_ENTRY.COLUMN_AGETOINJECT + "=?", new String[]{vaccineInfo.vaccineName, vaccineInfo.ageToInject + ""});
                if (res != 0) {
                    subscriber.onNext(true);
                } else {
                    subscriber.onNext(false);
                }
                subscriber.onCompleted();
            }
        });
    }
}
