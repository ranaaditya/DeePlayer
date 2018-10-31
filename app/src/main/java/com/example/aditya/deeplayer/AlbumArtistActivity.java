package com.example.aditya.deeplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.example.aditya.deeplayer.Adapter.AlbumArtistSongAdapter;
import com.example.aditya.deeplayer.Adapter.ArtistAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlbumArtistActivity extends AppCompatActivity {

    private ImageView mAlbumThumbnail;
    private RecyclerView mSongRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private AlbumArtistSongAdapter mAdapter;

    private String mName;
    private int mListType;

    private final static int ALBUM_TYPE = 100001;
    private final static int ARTIST_TYPE = 100002;

    private List<Song> mAlbumArtistTrackList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(PlayerFiles.mTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_songs);

        final Intent albumArtistIntent = getIntent();
        mListType = albumArtistIntent.getExtras().getInt("listType");
        mName = albumArtistIntent.getExtras().getString("name");
        getAllSongs(mListType, mName);

        mAlbumThumbnail = (ImageView) findViewById(R.id.albums_songs_thumbnail);
        mAlbumThumbnail.setImageDrawable(getAlbumArtworkDrawable(mAlbumArtistTrackList.get(0).getThumbnailPath()));

        mSongRecyclerView = (RecyclerView) findViewById(R.id.album_songs_recycleList);
        mSongRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mSongRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AlbumArtistSongAdapter(getApplicationContext(), mAlbumArtistTrackList, new AlbumArtistSongAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(Song song) {
                int songPosition = PlayerFiles.mSongList.indexOf(song);
                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                intent.putExtra("songPosition", songPosition);
                startActivity(intent);
            }
        });
        mSongRecyclerView.setAdapter(mAdapter);
        if( mListType == ARTIST_TYPE && mAlbumArtistTrackList.get(0).getSongArtist() != null){
            String name = mAlbumArtistTrackList.get(0).getSongArtist();
        } else if ( mListType == ALBUM_TYPE && mAlbumArtistTrackList.get(0).getAlbumName() != null){
            String name = mAlbumArtistTrackList.get(0).getAlbumName();
        } else {
            Collections.sort(mAlbumArtistTrackList);
        }
        mAdapter.notifyDataSetChanged();
    }

    private Drawable getAlbumArtworkDrawable(String thumbnailPath){
        if(thumbnailPath != null){
            return Drawable.createFromPath(thumbnailPath);
        } else {
            return getResources().getDrawable(R.drawable.song_default_thumbnail);
        }
    }

    private void getAllSongs(int listType, String name){
        if(listType == ARTIST_TYPE){
            for(int i = 0; i < PlayerFiles.mSongList.size(); i++){
                if(PlayerFiles.mSongList.get(i).getSongArtist().equals(name)){
                    mAlbumArtistTrackList.add(PlayerFiles.mSongList.get(i));
                }
            }
        } else if (listType == ALBUM_TYPE){
            for(int i = 0; i < PlayerFiles.mSongList.size(); i++){
                if(PlayerFiles.mSongList.get(i).getAlbumName().equals(name)){
                    mAlbumArtistTrackList.add(PlayerFiles.mSongList.get(i));
                }
            }
        }
    }

    /* private Bitmap getAlbumArtwork(String thumbnailPath){
        if(thumbnailPath != null){
            File imageFile = new File(thumbnailPath);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);
            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 30, 30, true);
            return imageBitmap;
        } else {
                //Bitmap defaultImageBitmap = BitmapFactory.decodeResource((new AlbumListFragment()).getResources(),R.drawable.song_default_thumbnail);
                //defaultImageBitmap = Bitmap.createScaledBitmap(defaultImageBitmap, 120, 120, true);
                //return defaultImageBitmap;
            return null;
        }
    }  */
}
