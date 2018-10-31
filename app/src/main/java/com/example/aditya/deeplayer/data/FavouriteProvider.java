package com.example.aditya.deeplayer.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class FavouriteProvider extends ContentProvider {

    private FavouriteDbHelper mDbHelper;

    private static final int FAVOURITES = 100;
    private static final int FAVOURITES_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(FavouriteContract.CONTENT_AUTHORITY, FavouriteContract.PATH_FAVOURITE, FAVOURITES);
        sUriMatcher.addURI(FavouriteContract.CONTENT_AUTHORITY, FavouriteContract.PATH_FAVOURITE + "/#", FAVOURITES_ID);
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new FavouriteDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match){
            case FAVOURITES:
                cursor = database.query(FavouriteContract.FavouriteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case FAVOURITES_ID:
                selection = FavouriteContract.FavouriteEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(FavouriteContract.FavouriteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query for uri " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case FAVOURITES:
                return insertFavourite(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion not possible for " + uri);
        }
    }

    private Uri insertFavourite(Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(FavouriteContract.FavouriteEntry.TABLE_NAME, null, contentValues);
        if(id == -1){ return null; }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        int match = sUriMatcher.match(uri);
        switch (match){
            case FAVOURITES:
                rowsDeleted = database.delete(FavouriteContract.FavouriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVOURITES_ID:
                selection = FavouriteContract.FavouriteEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(FavouriteContract.FavouriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalStateException("Favourite cannot be deleted for " + uri);
        }
        if(rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
