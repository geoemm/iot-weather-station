package com.mpsp.unipi.iotweatherstation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

class DBManager {

    private DatabaseHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (database.isOpen()) {
            dbHelper.close();
        }
    }

    public void insert(int temperatureUni, int sync_per) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.TEMPERATURE_UNIT, temperatureUni);
        contentValue.put(DatabaseHelper.SYNC_PERIOD, sync_per);
        database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.TEMPERATURE_UNIT, DatabaseHelper.SYNC_PERIOD };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long _id, int temperatureUni, int sync_per) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.TEMPERATURE_UNIT, temperatureUni);
        contentValues.put(DatabaseHelper.SYNC_PERIOD, sync_per);
        int i = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
        return i;
    }

    public void delete(long _id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
    }

}