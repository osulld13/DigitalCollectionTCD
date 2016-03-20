package com.example.osulld13.digitalcollections;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Donal on 20/03/2016.
 */
public class DigitalCollectionsDbHelper extends SQLiteOpenHelper {
    private final String TAG = DigitalCollectionsDbHelper.class.getSimpleName();

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DigitalCollections.db";

    public DigitalCollectionsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DigitalCollectionsContract.SQL_CREATE_QUERIES);
        Log.d(TAG, DigitalCollectionsContract.SQL_CREATE_QUERIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DigitalCollectionsContract.SQL_DELETE_QUERIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}