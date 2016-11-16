package com.mpsp.unipi.iotweatherstation;

/**
 * Created by g90 on 11/14/16.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "WEATHER_DATA";

    // Table columns
    public static final String _ID = "_id";
    public static final String TIME = "tim";
    public static final String TEMPERATURE = "temperature";
    public static final String HUMIDITY = "humidity";
    public static final String LUMINOSITY = "luminosity";

    // Database Information
    static final String DB_NAME = "IOT_WEATHER_STATION.DB";

    // database version
    static final int DB_VERSION = 1;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " + TEMPERATURE + " REAL NOT NULL, " + HUMIDITY + " REAL NOT NULL, " + LUMINOSITY + " REAL NOT NULL);";

    public DatabaseHelper(Context context) {
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