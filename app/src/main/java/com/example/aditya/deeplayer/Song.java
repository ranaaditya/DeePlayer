package com.example.aditya.deeplayer;

import android.support.annotation.NonNull;

public class Song implements Comparable<Song>{

    private String mSongTitle;
    private String mSongArtist;
    private String mSongData;
    private long mSongAlbumId;
    private String mAlbumName;
    private String mThumbnailPath;
    private long mSongSize;
    private String mTrackId;
    private long mSongDuration;
    private boolean mFavourite;
    private long mDatabaseId;

    public Song(String songTitle, String songArtist, String songData, long albumId, String albumName, String thumbnailPath, long songSize, String trackId, long songDuration, boolean favourite, long dataBaseId){
        mSongTitle = songTitle;
        mSongArtist = songArtist;
        mSongData = songData;
        mSongAlbumId = albumId;
        mAlbumName = albumName;
        mThumbnailPath = thumbnailPath;
        mSongSize = songSize;
        mTrackId = trackId;
        mSongDuration = songDuration;
        mFavourite = favourite;
        mDatabaseId = dataBaseId;
    }

    public String getSongTitle() {
        return mSongTitle;
    }

    public String getSongArtist() {
        return mSongArtist;
    }

    public String getSongData() {
        return mSongData;
    }

    public long getSongAlbumId() {
        return mSongAlbumId;
    }

    public long getSongSize() {
        return mSongSize;
    }

    public String getAlbumName() {
        return mAlbumName;
    }

    public String getThumbnailPath() {
        return mThumbnailPath;
    }

    public String getTrackId() {
        return mTrackId;
    }

    public long getSongDuration() {
        return mSongDuration;
    }

    public boolean getFavourite(){
        return mFavourite;
    }

    public void setFavourite(boolean favourite){
        mFavourite = favourite;
    }

    public long getDatabaseId(){
        return mDatabaseId;
    }

    public void setDatabaseId(long databaseId){
        mDatabaseId = databaseId;
    }


    @Override
    public int compareTo(@NonNull Song song) {
        return (Integer.compare(Integer.parseInt(this.getTrackId()), Integer.parseInt(song.getTrackId())));
    }
}
