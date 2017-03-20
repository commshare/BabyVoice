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
                    PREGNANT_EXAM_ENTRY.COLUMN_EVENT_ID + INTEGER_TYPE + COMMA_SEP +
                    PREGNANT_EXAM_ENTRY.COLUMN_WEEK + TEXT_TYPE + COMMA_SEP +
                    PREGNANT_EXAM_ENTRY.COLUMN_EVENT_NAME + TEXT_TYPE + COMMA_SEP +
                    PREGNANT_EXAM_ENTRY.COLUMN_EVENT_IS_DONE + INTEGER_TYPE + " )";

    private static final String SQL_DELETE_PREGNANT_EXAM_ENTRY =
            "DROP TABLE IF EXISTS " + PREGNANT_EXAM_ENTRY.TABLE_NAME;

    private static final String SQL_CREATE_VACCINE_ENTRY =
            "CREATE TABLE " + VACCINE_ENTRY.TABLE_NAME + " (" +
                    VACCINE_ENTRY._ID + " INTEGER PRIMARY KEY," +
                    VACCINE_ENTRY.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    VACCINE_ENTRY.COLUMN_FREE + INTEGER_TYPE + COMMA_SEP +
                    VACCINE_ENTRY.COLUMN_INJECTED + INTEGER_TYPE + COMMA_SEP +
                    VACCINE_ENTRY.COLUMN_AGETOINJECT + INTEGER_TYPE + COMMA_SEP +
                    VACCINE_ENTRY.COLUMN_INJECTDATE + TEXT_TYPE + " )";

    private static final String SQL_DELETE_VACCINE_ENTRY =
            "DROP TABLE IF EXISTS " + VACCINE_ENTRY.TABLE_NAME;

    private static final String SQL_CREATE_HEALTH_PROTECT_ENTRY =
            "CREATE TABLE " + HEALTH_PROTECT_ENTRY.TABLE_NAME + " (" +
                    HEALTH_PROTECT_ENTRY._ID + " INTEGER PRIMARY KEY," +
                    HEALTH_PROTECT_ENTRY.COLUMN_HEAD_SIZE + INTEGER_TYPE + COMMA_SEP +
                    HEALTH_PROTECT_ENTRY.COLUMN_HEIGHT + INTEGER_TYPE + COMMA_SEP +
                    HEALTH_PROTECT_ENTRY.COLUMN_WEIGHT + INTEGER_TYPE + COMMA_SEP +
                    HEALTH_PROTECT_ENTRY.COLUMN_TEMPERATURE + INTEGER_TYPE + COMMA_SEP +
                    HEALTH_PROTECT_ENTRY.COLUMN_GENDER + INTEGER_TYPE + COMMA_SEP +
                    HEALTH_PROTECT_ENTRY.COLUMN_HEARTBEAT + INTEGER_TYPE + COMMA_SEP +
                    HEALTH_PROTECT_ENTRY.COLUMN_FONTANELSIZE + INTEGER_TYPE + COMMA_SEP +
                    HEALTH_PROTECT_ENTRY.COLUMN_RECORD_DATE + TEXT_TYPE + COMMA_SEP +
                    HEALTH_PROTECT_ENTRY.COLUMN_EXAMINERESULT + TEXT_TYPE + " )";

    private static final String SQL_DELETE_HEALTH_PROTECT_ENTRY=
            "DROP TABLE IF EXISTS " + HEALTH_PROTECT_ENTRY.TABLE_NAME;

    private static final String SQL_CREATE_GROW_UP_ENTRY=
            "CREATE TABLE " + GROW_UP_ENTRY.TABLE_NAME + " (" +
                    GROW_UP_ENTRY._ID + " INTEGER PRIMARY KEY," +
                    GROW_UP_ENTRY.COLUMN_DATE + TEXT_TYPE + COMMA_SEP +
                    GROW_UP_ENTRY.COLUMN_CONTENT + TEXT_TYPE + COMMA_SEP +
                    GROW_UP_ENTRY.COLUMN_PICLIST + TEXT_TYPE  + " )";

    private static final String SQL_DELETE_GROW_UP_ENTRY =
            "DROP TABLE IF EXISTS " + GROW_UP_ENTRY.TABLE_NAME;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PREGNANT_EXAM_ENTRY);
        db.execSQL(SQL_CREATE_GROW_UP_ENTRY);
        db.execSQL(SQL_CREATE_HEALTH_PROTECT_ENTRY);
        db.execSQL(SQL_CREATE_VACCINE_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_PREGNANT_EXAM_ENTRY);
        db.execSQL(SQL_DELETE_VACCINE_ENTRY);
        db.execSQL(SQL_DELETE_HEALTH_PROTECT_ENTRY);
        db.execSQL(SQL_DELETE_GROW_UP_ENTRY);
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

    /* Inner class that defines the table contents */
    public static class VACCINE_ENTRY implements BaseColumns {
        public static final String TABLE_NAME = "vaccine_info_table";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_FREE = "isfree";
        public static final String COLUMN_INJECTED = "is_injected";
        public static final String COLUMN_AGETOINJECT = "age_to_inject";
        public static final String COLUMN_INJECTDATE = "inject_date";
    }

    /* Inner class that defines the table contents */
    public static class HEALTH_PROTECT_ENTRY implements BaseColumns {
        public static final String TABLE_NAME = "health_protect_table";
        public static final String COLUMN_HEAD_SIZE = "headSize";
        public static final String COLUMN_HEIGHT = "height";
        public static final String COLUMN_WEIGHT = "weight";
        public static final String COLUMN_TEMPERATURE = "temperature";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_HEARTBEAT = "heartBeat";
        public static final String COLUMN_FONTANELSIZE = "fontanelSize";
        public static final String COLUMN_EXAMINERESULT = "examineResult";
        public static final String COLUMN_RECORD_DATE = "recordDate";
    }

    /* Inner class that defines the table contents */
    public static class GROW_UP_ENTRY implements BaseColumns {
        public static final String TABLE_NAME = "grow_up_table";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_PICLIST = "picList";

    }


}
