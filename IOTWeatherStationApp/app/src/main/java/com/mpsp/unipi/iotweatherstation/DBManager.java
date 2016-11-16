package com.mpsp.unipi.iotweatherstation;

/**
 * Created by g90 on 11/14/16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(float temperature, float humidity, float luminosity) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.TEMPERATURE, temperature);
        contentValue.put(DatabaseHelper.HUMIDITY, humidity);
        contentValue.put(DatabaseHelper.LUMINOSITY, luminosity);
        database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.TIME, DatabaseHelper.TEMPERATURE, DatabaseHelper.HUMIDITY, DatabaseHelper.LUMINOSITY };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long _id, float temperature, float humidity, float luminosity) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.TEMPERATURE, temperature);
        contentValues.put(DatabaseHelper.HUMIDITY, humidity);
        contentValues.put(DatabaseHelper.LUMINOSITY, luminosity);
        int i = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
        return i;
    }

    public void delete(long _id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
    }

}