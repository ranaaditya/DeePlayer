package com.example.aditya.deeplayer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavouriteDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "favouriteDatabase.db";
    public static final int DATABASE_VERSIION = 1;

    public FavouriteDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSIION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_FIXTURES_TABLE = "CREATE TABLE " + FavouriteContract.FavouriteEntry.TABLE_NAME + "("
                + FavouriteContract.FavouriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + FavouriteContract.FavouriteEntry.COLUMN_FAVOURITE_SONG_POSITION + " TEXT);";
        sqLiteDatabase.execSQL(SQL_CREATE_FIXTURES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
