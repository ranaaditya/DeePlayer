package com.example.aditya.deeplayer.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aditya.deeplayer.Adapter.AlbumAdapter;
import com.example.aditya.deeplayer.Adapter.SongAdapter;
import com.example.aditya.deeplayer.AlbumArtistActivity;
import com.example.aditya.deeplayer.PlayerActivity;
import com.example.aditya.deeplayer.PlayerFiles;
import com.example.aditya.deeplayer.R;
import com.example.aditya.deeplayer.Song;

public class FavouritesFragment extends Fragment{

    private android.support.v7.widget.SearchView mSearchView;
    private RecyclerView mSongRecyclerView;
    private ProgressBar mProgressBar;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView mEmptyViewTextView;
    private Handler mCheckUpdatationStatusHandler = new Handler();

    private SongAdapter mAdapter;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.library_list_fragment, container, false);

        mSongRecyclerView = (RecyclerView) view.findViewById(R.id.list_library);
        mSongRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mSongRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SongAdapter(getContext(), PlayerFiles.mFavouriteSongs, new SongAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(Song song) {
                int position = PlayerFiles.mSongList.indexOf(song);
                Intent intent = new Intent(getContext(), PlayerActivity.class);
                intent.putExtra("songPosition", position);
                startActivity(intent);
            }
        });
        mSongRecyclerView.setAdapter(mAdapter);

        return view;
    }

    public void refreshAdapter(){
        mAdapter.notifyDataSetChanged();
    }


}
