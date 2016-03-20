package com.example.osulld13.digitalcollections;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by Donal on 20/03/2016.
 */
public final class DigitalCollectionsContract {

    private final String TAG = DigitalCollectionsContract.class.getSimpleName();

    // Blank constructor to prevent the class being instantiated
    public DigitalCollectionsContract(){}

    // Useful reusable values
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String DATETIME_TYPE = " DATETIME";
    private static final String PRIMARY_KEY_CONSTRAINT = " PRIMARY KEY";
    private static final String COMMA_SEP = ",";
    private static final String OPENING_PAREN = " (";
    private static final String CLOSING_PAREN = " )";
    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    private static final String DEFAULT = " DEFAULT";
    private static final String CURRENT_TIMESTAMP = " CURRENT_TIMESTAMP";

    // Inner class that defines the query table inner contents
    public static abstract class CollectionQuery implements BaseColumns {
        // Table and Column names
        public static final String TABLE_NAME = "query";
        public static final String COLUMN_NAME_TEXT = "text";
        public static final String COLUMN_NAME_TIME = "time";
    }

    // SQL commands for query table
    public static final String SQL_CREATE_QUERIES =
                CREATE_TABLE + CollectionQuery.TABLE_NAME + OPENING_PAREN +
                CollectionQuery._ID + INTEGER_TYPE + PRIMARY_KEY_CONSTRAINT + COMMA_SEP +
                CollectionQuery.COLUMN_NAME_TEXT + TEXT_TYPE + COMMA_SEP +
                CollectionQuery.COLUMN_NAME_TIME + DATETIME_TYPE + DEFAULT + CURRENT_TIMESTAMP + CLOSING_PAREN;

    public static final String SQL_DELETE_QUERIES =
            DROP_TABLE + CollectionQuery.TABLE_NAME;

    // Inner class that defines the query table inner contents
    public static abstract class CollectionBookmark implements BaseColumns {
        // Table and Column names
        public static final String TABLE_NAME = "bookmark";
        public static final String COLUMN_NAME_PID = "pid";
        public static final String COLUMN_NAME_FOLDER_NUMBER = "folderNumber";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_GENRE = "genre";
        public static final String COLUMN_NAME_TIME = "time";
    }

    // SQL commands for bookmark table
    public static final String SQL_CREATE_BOOKMARKS =
                CREATE_TABLE + CollectionBookmark.TABLE_NAME + OPENING_PAREN +
                CollectionBookmark._ID + INTEGER_TYPE + PRIMARY_KEY_CONSTRAINT + COMMA_SEP +
                CollectionBookmark.COLUMN_NAME_PID + TEXT_TYPE + COMMA_SEP +
                CollectionBookmark.COLUMN_NAME_FOLDER_NUMBER + TEXT_TYPE + COMMA_SEP +
                CollectionBookmark.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                CollectionBookmark.COLUMN_NAME_GENRE + TEXT_TYPE + COMMA_SEP +
                CollectionBookmark.COLUMN_NAME_TIME + DATETIME_TYPE + DEFAULT + CURRENT_TIMESTAMP + CLOSING_PAREN;

    public static final String SQL_DELETE_BOOKMARKS =
            DROP_TABLE + CollectionBookmark.TABLE_NAME;

}
