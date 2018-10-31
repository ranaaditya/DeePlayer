package com.example.aditya.deeplayer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;

import com.example.aditya.deeplayer.data.FavouriteContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerFiles {

    public static boolean mUpdatingStatus = false;
    public static List<Song> mSongList = new ArrayList<>();
    private static List<String> mSongDataList = new ArrayList<>();
    private static List<Song> tempSongList = new ArrayList<>();

    public static List<Song> mFavouriteSongs = new ArrayList<>();

    public static void loadFavouritesFromDb(Context context){
        String[] projection = new String[]{FavouriteContract.FavouriteEntry._ID,
                FavouriteContract.FavouriteEntry.COLUMN_FAVOURITE_SONG_POSITION};
        Cursor cursor = context.getContentResolver().query(FavouriteContract.FavouriteEntry.CONTENT_URI, projection, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                String songPosition = cursor.getString(cursor.getColumnIndex(FavouriteContract.FavouriteEntry.COLUMN_FAVOURITE_SONG_POSITION));
                Song tempSong = mSongList.get(Integer.parseInt(songPosition));
                mFavouriteSongs.add(tempSong);
                mSongList.get(mSongList.indexOf(tempSong)).setFavourite(true);

            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    public static Song mCurrentSongPlaying = null;
    public static boolean mSongPlayingStatus = false;

    public static int mTheme = R.style.VioletAppTheme;

    public static void updateSongList(final Context context){
        mUpdatingStatus = false;
        tempSongList.clear();
        mSongDataList.clear();
        Handler songHandler = new Handler();
        Runnable songRunnable = new Runnable() {
            @Override
            public void run() {
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
                String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
                Cursor songCursor = Objects.requireNonNull(context.getContentResolver().query(uri, null, selection, null, sortOrder, null));
                if (songCursor != null && songCursor.moveToFirst()) {
                    do {
                        String songTitle = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                        String songArtist = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        String songData = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        long albumId = songCursor.getLong(songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                        String albumName = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                        String thumbnailPath = getCoverArtPath(albumId, context);
                        long songSize = songCursor.getLong(songCursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                        String trackId = songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.TRACK));
                        long songDuration = songCursor.getLong(songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

                        tempSongList.add(new Song(songTitle, songArtist, songData, albumId, albumName, thumbnailPath, songSize, trackId, songDuration, false, -1));
                        mSongDataList.add(songData);
                    } while (songCursor.moveToNext());
                }
                mSongList.clear();
                mSongList.addAll(tempSongList);
                mUpdatingStatus = true;
            }
        };
        songHandler.postDelayed(songRunnable, 0);
    }

    private static String getCoverArtPath(long androidAlbumId, Context context) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + "=?",
                new String[]{Long.toString(androidAlbumId)},
                null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(0);
            }
            cursor.close();
        }
        return path;
    }

    public static void addToFavourites(Song song, Context context){
        mFavouriteSongs.add(song);
        ContentValues values = new ContentValues();
        values.put(FavouriteContract.FavouriteEntry.COLUMN_FAVOURITE_SONG_POSITION, mSongList.indexOf(song));
        Uri idUri = context.getContentResolver().insert(FavouriteContract.FavouriteEntry.CONTENT_URI, values);
        long id = Long.valueOf(idUri.getLastPathSegment());
        mSongList.get(mSongList.indexOf(song)).setDatabaseId(id);

    }

    public static void removeFromFavourites(Song song, Context context){
        Uri currentSongUri = ContentUris.withAppendedId(FavouriteContract.FavouriteEntry.CONTENT_URI, song.getDatabaseId());
        context.getContentResolver().delete(currentSongUri, null, null);
        mFavouriteSongs.remove(song);
    }

    public static void removeAllFavourites(Context context){
        context.getContentResolver().delete(FavouriteContract.FavouriteEntry.CONTENT_URI, null, null);
        mFavouriteSongs.clear();
    }

    public static boolean isServiceRunning(String serviceName, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if(serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
