package com.mpsp.unipi.iotweatherstation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseHelper extends SQLiteOpenHelper {
    private DBManager dbManager;
    // Table Name
    static final String TABLE_NAME = "SETTINGS";

    // Table columns
    static final String _ID = "_id";
    static final String TEMPERATURE_UNIT = "temperature_unit";
    static final String SYNC_PERIOD = "sync_period";

    // Database Information
    private static final String DB_NAME = "SETTINGS.DB";

    // database version
    private static final int DB_VERSION = 2;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TEMPERATURE_UNIT + " INTEGER NOT NULL, " + SYNC_PERIOD + " INTEGER NOT NULL);";

    DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}