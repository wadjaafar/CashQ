package net.soluspay.cashq.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

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

    public Cursor fetch() {
        String[] columns = new String[]{CardDBHelper._ID, CardDBHelper.PAN, CardDBHelper.EXPDATE, CardDBHelper.NAME};
        Cursor cursor = database.query(CardDBHelper.TABLE_NAME, columns, null, null, null, null, null);
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
        int i = database.update(CardDBHelper.TABLE_NAME, contentValues, CardDBHelper._ID + " = " + _id, null);
        return i;
    }

    public void delete(long _id) {
        database.delete(CardDBHelper.TABLE_NAME, CardDBHelper._ID + "=" + _id, null);
    }


}
