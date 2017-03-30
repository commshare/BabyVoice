package com.lihb.babyvoice.db.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lihb.babyvoice.BabyVoiceApp;
import com.lihb.babyvoice.db.DBHelper;
import com.lihb.babyvoice.db.IDBRxManager;
import com.lihb.babyvoice.model.ProductionInspection;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by lihb on 2017/3/17.
 */

public class PregnantDataImpl implements IDBRxManager<ProductionInspection> {

    private DBHelper dbHelper;

    private PregnantDataImpl(){
        dbHelper = new DBHelper(BabyVoiceApp.getInstance());
    }

    private static PregnantDataImpl instance = new PregnantDataImpl();

    public static PregnantDataImpl getInstance(){
        return instance;
    }

    @Override
    public synchronized Observable<Boolean> insertData(final ProductionInspection productionInspection) {

        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                ContentValues values = new ContentValues();
                values.put(DBHelper.PREGNANT_EXAM_ENTRY.COLUMN_NO, productionInspection.no);
                values.put(DBHelper.PREGNANT_EXAM_ENTRY.COLUMN_EVENT_ID, productionInspection.event_id);
                values.put(DBHelper.PREGNANT_EXAM_ENTRY.COLUMN_EVENT_NAME, productionInspection.event_name);
                values.put(DBHelper.PREGNANT_EXAM_ENTRY.COLUMN_EVENT_IS_DONE, productionInspection.isDone);
                values.put(DBHelper.PREGNANT_EXAM_ENTRY.COLUMN_WEEK, productionInspection.week);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                if (db.insert(DBHelper.PREGNANT_EXAM_ENTRY.TABLE_NAME, null, values) != -1) {
                    subscriber.onNext(true);
                    Logger.i("insert pregnant data success!");
                }else {
                    Logger.e("insert pregnant data failed!");
                    subscriber.onNext(false);
                }
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Void> batchInsertData(final List<ProductionInspection> dataList) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                for (ProductionInspection productionInspection : dataList) {
                    ContentValues values = new ContentValues();
                    values.put(DBHelper.PREGNANT_EXAM_ENTRY.COLUMN_NO, productionInspection.no);
                    values.put(DBHelper.PREGNANT_EXAM_ENTRY.COLUMN_EVENT_ID, productionInspection.event_id);
                    values.put(DBHelper.PREGNANT_EXAM_ENTRY.COLUMN_EVENT_NAME, productionInspection.event_name);
                    values.put(DBHelper.PREGNANT_EXAM_ENTRY.COLUMN_EVENT_IS_DONE, productionInspection.isDone);
                    values.put(DBHelper.PREGNANT_EXAM_ENTRY.COLUMN_WEEK, productionInspection.week);
                    if (db.insert(DBHelper.PREGNANT_EXAM_ENTRY.TABLE_NAME, null, values) == -1) {
                        Logger.e("PregnantDataImpl", productionInspection.toString() + " insert failed");
                    }
                }
                subscriber.onNext(null);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProductionInspection>> queryAllData() {
        return Observable.create(new Observable.OnSubscribe<List<ProductionInspection>>() {
            @Override
            public void call(Subscriber<? super List<ProductionInspection>> subscriber) {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("select * from " + DBHelper.PREGNANT_EXAM_ENTRY.TABLE_NAME, null);
                List<ProductionInspection> result = new ArrayList<ProductionInspection>();

                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    for (int i = 0; i < cursor.getCount(); i++) {
                        cursor.moveToPosition(i);
                        ProductionInspection inspection = new ProductionInspection();
                        inspection.no = cursor.getInt(cursor.getColumnIndex(DBHelper.PREGNANT_EXAM_ENTRY.COLUMN_NO));
                        inspection.event_id = cursor.getInt(cursor.getColumnIndex(DBHelper.PREGNANT_EXAM_ENTRY.COLUMN_EVENT_ID));
                        inspection.event_name = cursor.getString(cursor.getColumnIndex(DBHelper.PREGNANT_EXAM_ENTRY.COLUMN_EVENT_NAME));
                        inspection.isDone = cursor.getInt(cursor.getColumnIndex(DBHelper.PREGNANT_EXAM_ENTRY.COLUMN_EVENT_IS_DONE));
                        inspection.week = cursor.getString(cursor.getColumnIndex(DBHelper.PREGNANT_EXAM_ENTRY.COLUMN_WEEK));
                        result.add(inspection);
                    }
                    cursor.close();
                }
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> queryData(final ProductionInspection productionInspection) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("select * from " + DBHelper.PREGNANT_EXAM_ENTRY.TABLE_NAME+" where "
                        +DBHelper.PREGNANT_EXAM_ENTRY.COLUMN_NO+" = ? and "
                        +DBHelper.PREGNANT_EXAM_ENTRY.COLUMN_EVENT_NAME +" = ?", new String[]{productionInspection.no+"", productionInspection.event_name});
                if (cursor != null && cursor.getCount() > 0) {
                    subscriber.onNext(true);
                }else {
                    subscriber.onNext(false);
                }
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> delData(final ProductionInspection productionInspection) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                int result= db.delete(DBHelper.PREGNANT_EXAM_ENTRY.TABLE_NAME,
                        DBHelper.PREGNANT_EXAM_ENTRY.COLUMN_NO +"=? and" +DBHelper.PREGNANT_EXAM_ENTRY.COLUMN_NO+" = ?",
                        new String[]{productionInspection.no+"", productionInspection.event_name});

                if (result != 0) {
                    subscriber.onNext(true);
                }else {
                    subscriber.onNext(false);
                }
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> updateData(final ProductionInspection productionInspection) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DBHelper.PREGNANT_EXAM_ENTRY.COLUMN_NO, productionInspection.no);
                values.put(DBHelper.PREGNANT_EXAM_ENTRY.COLUMN_EVENT_ID, productionInspection.event_id);
                values.put(DBHelper.PREGNANT_EXAM_ENTRY.COLUMN_EVENT_NAME, productionInspection.event_name);
                values.put(DBHelper.PREGNANT_EXAM_ENTRY.COLUMN_EVENT_IS_DONE, productionInspection.isDone);
                values.put(DBHelper.PREGNANT_EXAM_ENTRY.COLUMN_WEEK, productionInspection.week);
                int res= db.update(DBHelper.PREGNANT_EXAM_ENTRY.TABLE_NAME, values,
                        DBHelper.PREGNANT_EXAM_ENTRY.COLUMN_NO+"=? and "
                        +DBHelper.PREGNANT_EXAM_ENTRY.COLUMN_EVENT_NAME +"=?", new String[]{productionInspection.no+"", productionInspection.event_name});
                if (res != 0) {
                    subscriber.onNext(true);
                }else {
                    subscriber.onNext(false);
                }
                subscriber.onCompleted();
            }
        });
    }

}
