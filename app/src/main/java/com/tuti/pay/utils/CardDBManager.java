package com.tutipay.app.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CardDBManager {

    private CardDBHelper dbHelper;

    private Context context;

    private SQLiteDatabase database;

    public CardDBManager(Context c) {
        context = c;
    }

    public CardDBManager open() throws SQLException {
        dbHelper = new CardDBHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(String pan, String expdate, String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CardDBHelper.PAN, pan);
        contentValues.put(CardDBHelper.EXPDATE, expdate);
        contentValues.put(CardDBHelper.NAME, name);
        database.insert(CardDBHelper.TABLE_NAME, null, contentValues);
    }

    public void replace(String key, String pan, String expdate, String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CardDBHelper.PAN, pan);
        contentValues.put(CardDBHelper.EXPDATE, expdate);
        contentValues.put(CardDBHelper.NAME, name);
        Log.i("sql_replace", "the value of pan is: " + pan);
        database.update(CardDBHelper.TABLE_NAME, contentValues, "pan=?", new String[]{key});
    }

    public Cursor fetch() {
        String[] columns = new String[]{CardDBHelper._ID, CardDBHelper.PAN, CardDBHelper.EXPDATE, CardDBHelper.NAME};
        Cursor cursor = database.query(CardDBHelper.TABLE_NAME, columns, null, null, null, null, "count desc");
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getAll() {
        String[] columns = new String[]{CardDBHelper._ID, CardDBHelper.PAN, CardDBHelper.EXPDATE, CardDBHelper.NAME};
        Cursor cursor = database.query(CardDBHelper.TABLE_NAME, columns, null, null, null, null, "count desc");
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long _id, String pan, String expdate, String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CardDBHelper.PAN, pan);
        contentValues.put(CardDBHelper.EXPDATE, expdate);
        contentValues.put(CardDBHelper.NAME, name);
        return database.update(CardDBHelper.TABLE_NAME, contentValues, CardDBHelper._ID + " = " + _id, null);

    }

    public void updateCount(String pan) {
//        String q = "insert into cards(count) values(" + count + ")" + "where pan == " + pan;

        database.execSQL("update cards set count = count + 1 where pan == ?", new String[]{pan});
    }


    public void delete(String pan) {
        database.execSQL("delete from cards where pan = ?", new String[]{pan});
    }

    public void deleteAll() {
//        database.delete(CardDBHelper.TABLE_NAME);
        database.execSQL("DELETE FROM CARDS");
    }

}


