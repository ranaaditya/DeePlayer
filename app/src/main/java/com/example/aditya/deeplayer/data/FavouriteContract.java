package com.example.aditya.deeplayer.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class FavouriteContract {

    public static final String CONTENT_AUTHORITY = "com.example.aditya.deeplayer" ;
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FAVOURITE = "favouriteSongs";

    public static final class FavouriteEntry implements BaseColumns {

        public static final String TABLE_NAME = "favouriteSongs";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_FAVOURITE_SONG_POSITION = "songPosition";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FAVOURITE);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVOURITE;

    }
}
