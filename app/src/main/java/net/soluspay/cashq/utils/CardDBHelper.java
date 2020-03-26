package net.soluspay.cashq.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CardDBHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "CARDS";

    // Table columns
    public static final String _ID = "_id";
    public static final String PAN = "pan";
    public static final String EXPDATE = "expdate";
    public static final String NAME = "name";
    public static final String COUNT = "count";

    // Database Information
    static final String DB_NAME = "CASHQ_CARDS.DB";

    // database version
    static final int DB_VERSION = 4;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + PAN + " TEXT NOT NULL, " + EXPDATE + " TEXT NOT NULL, " + NAME + " TEXT NOT NULL, " +
             COUNT + " Integer DEFAULT 0)";

    public CardDBHelper(Context context) {
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
