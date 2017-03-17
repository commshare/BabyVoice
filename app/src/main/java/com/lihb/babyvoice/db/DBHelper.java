package com.lihb.babyvoice.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by lhb on 2017/3/17.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "babyvoice.db";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_PREGNANT_EXAM_ENTRY =
            "CREATE TABLE " + PREGNANT_EXAM_ENTRY.TABLE_NAME + " (" +
                    PREGNANT_EXAM_ENTRY._ID + " INTEGER PRIMARY KEY," +
                    PREGNANT_EXAM_ENTRY.COLUMN_NO + INTEGER_TYPE + COMMA_SEP +
                    PREGNANT_EXAM_ENTRY.COLUMN_WEEK + TEXT_TYPE + COMMA_SEP +
                    PREGNANT_EXAM_ENTRY.COLUMN_EVENT_ID + INTEGER_TYPE + COMMA_SEP +
                    PREGNANT_EXAM_ENTRY.COLUMN_EVENT_NAME + TEXT_TYPE + COMMA_SEP +
                    PREGNANT_EXAM_ENTRY.COLUMN_EVENT_IS_DONE + INTEGER_TYPE + " )";

    private static final String SQL_DELETE_PREGNANT_EXAM_ENTRY =
            "DROP TABLE IF EXISTS " + PREGNANT_EXAM_ENTRY.TABLE_NAME;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PREGNANT_EXAM_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_PREGNANT_EXAM_ENTRY);
        onCreate(db);
    }


    /* Inner class that defines the table contents */
    public static class PREGNANT_EXAM_ENTRY implements BaseColumns {
        public static final String TABLE_NAME = "pregnant_exam_table";
        public static final String COLUMN_NO = "no";
        public static final String COLUMN_WEEK = "week";
        public static final String COLUMN_EVENT_ID = "event_id";
        public static final String COLUMN_EVENT_NAME = "event_name";
        public static final String COLUMN_EVENT_IS_DONE = "is_done";
    }


}
