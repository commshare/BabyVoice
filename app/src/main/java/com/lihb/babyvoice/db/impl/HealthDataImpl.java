package com.lihb.babyvoice.db.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lihb.babyvoice.BabyVoiceApp;
import com.lihb.babyvoice.db.DBHelper;
import com.lihb.babyvoice.db.IDBRxManager;
import com.lihb.babyvoice.model.HealthQuota;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2017/3/20.
 */

public class HealthDataImpl implements IDBRxManager<HealthQuota> {

    private DBHelper dbHelper;

    private HealthDataImpl(){
        dbHelper = new DBHelper(BabyVoiceApp.getInstance());
    }

    private static HealthDataImpl instance = new HealthDataImpl();

    public static HealthDataImpl getInstance(){
        return instance;
    }
    @Override
    public Observable<Boolean> insertData(final HealthQuota healthQuota) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                ContentValues values = new ContentValues();
                values.put(DBHelper.HEALTH_PROTECT_ENTRY.COLUMN_HEAD_SIZE, healthQuota.headSize);
                values.put(DBHelper.HEALTH_PROTECT_ENTRY.COLUMN_HEIGHT, healthQuota.height);
                values.put(DBHelper.HEALTH_PROTECT_ENTRY.COLUMN_WEIGHT, healthQuota.weight);
                values.put(DBHelper.HEALTH_PROTECT_ENTRY.COLUMN_TEMPERATURE, healthQuota.temperature);
                values.put(DBHelper.HEALTH_PROTECT_ENTRY.COLUMN_GENDER, healthQuota.gender);
                values.put(DBHelper.HEALTH_PROTECT_ENTRY.COLUMN_HEARTBEAT, healthQuota.heartBeat);
                values.put(DBHelper.HEALTH_PROTECT_ENTRY.COLUMN_FONTANELSIZE, healthQuota.fontanelSize);
                values.put(DBHelper.HEALTH_PROTECT_ENTRY.COLUMN_EXAMINERESULT, healthQuota.examineResult);
                values.put(DBHelper.HEALTH_PROTECT_ENTRY.COLUMN_RECORD_DATE, healthQuota.recordDate);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                if (db.insert(DBHelper.HEALTH_PROTECT_ENTRY.TABLE_NAME, null, values) != -1) {
                    subscriber.onNext(true);
                    Logger.i("insert health data success!");
                }else {
                    Logger.e("insert health data failed!");
                    subscriber.onNext(false);
                }
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Void> batchInsertData(List<HealthQuota> dataList) {
        return null;
    }

    @Override
    public Observable<List<HealthQuota>> queryAllData() {
        return Observable.create(new Observable.OnSubscribe<List<HealthQuota>>() {
            @Override
            public void call(Subscriber<? super List<HealthQuota>> subscriber) {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("select * from " + DBHelper.HEALTH_PROTECT_ENTRY.TABLE_NAME, null);
                List<HealthQuota> result = new ArrayList<HealthQuota>();

                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    for (int i = 0; i < cursor.getCount(); i++) {
                        cursor.moveToPosition(i);
                        HealthQuota healthQuota = new HealthQuota();
                        healthQuota.headSize = cursor.getInt(cursor.getColumnIndex(DBHelper.HEALTH_PROTECT_ENTRY.COLUMN_HEAD_SIZE));
                        healthQuota.weight = cursor.getInt(cursor.getColumnIndex(DBHelper.HEALTH_PROTECT_ENTRY.COLUMN_WEIGHT));
                        healthQuota.height = cursor.getInt(cursor.getColumnIndex(DBHelper.HEALTH_PROTECT_ENTRY.COLUMN_HEIGHT));
                        healthQuota.temperature = cursor.getInt(cursor.getColumnIndex(DBHelper.HEALTH_PROTECT_ENTRY.COLUMN_TEMPERATURE));
                        healthQuota.gender = cursor.getInt(cursor.getColumnIndex(DBHelper.HEALTH_PROTECT_ENTRY.COLUMN_GENDER));
                        healthQuota.heartBeat = cursor.getInt(cursor.getColumnIndex(DBHelper.HEALTH_PROTECT_ENTRY.COLUMN_HEARTBEAT));
                        healthQuota.fontanelSize = cursor.getInt(cursor.getColumnIndex(DBHelper.HEALTH_PROTECT_ENTRY.COLUMN_FONTANELSIZE));
                        healthQuota.examineResult = cursor.getString(cursor.getColumnIndex(DBHelper.HEALTH_PROTECT_ENTRY.COLUMN_EXAMINERESULT));
                        healthQuota.recordDate = cursor.getString(cursor.getColumnIndex(DBHelper.HEALTH_PROTECT_ENTRY.COLUMN_RECORD_DATE));
                        result.add(healthQuota);
                    }
                    cursor.close();
                }
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> queryData(HealthQuota healthQuota) {
        return null;
    }

    @Override
    public Observable<Boolean> delData(HealthQuota healthQuota) {
        return null;
    }

    @Override
    public Observable<Boolean> updateData(HealthQuota healthQuota) {
        return null;
    }
}
