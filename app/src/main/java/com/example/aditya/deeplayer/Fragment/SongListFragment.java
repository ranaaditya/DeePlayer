package com.example.aditya.deeplayer.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aditya.deeplayer.Adapter.SongAdapter;
import com.example.aditya.deeplayer.PlayerActivity;
import com.example.aditya.deeplayer.PlayerFiles;
import com.example.aditya.deeplayer.R;
import com.example.aditya.deeplayer.SettingsGrantPermissionsActivity;
import com.example.aditya.deeplayer.Song;

import java.util.ArrayList;
import java.util.List;

public class SongListFragment extends Fragment {

    private android.support.v7.widget.SearchView mSearchView;
    private RecyclerView mSongRecyclerView;
    private ProgressBar mProgressBar;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView mEmptyViewTextView;
    private Handler mCheckUpdatationStatusHandler = new Handler();

    private SongAdapter mAdapter;
    //private SongListFragmentListner mSongListFragmentListner;

    private List<Song> mSongList = new ArrayList<>();
    private List<Song> mSongListChangeble = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.library_list_fragment, container, false);

        mEmptyViewTextView = (TextView) view.findViewById(R.id.library_emptyView);
        mProgressBar = (ProgressBar) view.findViewById(R.id.library_progressBar);

        mSongRecyclerView = (RecyclerView) view.findViewById(R.id.list_library);
        mSongRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mSongRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SongAdapter(getContext(), mSongListChangeble, new SongAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(Song song) {
                int songPosition = mSongList.indexOf(song);
                Intent intent = new Intent(getContext(), PlayerActivity.class);
                intent.putExtra("songPosition", songPosition);
                startActivity(intent);
            }
        });
        mSongRecyclerView.setAdapter(mAdapter);
        mCheckUpdatationStatusHandler.postDelayed(mUpdationRunnable, 100);

        return view;
    }

    private Runnable mUpdationRunnable = new Runnable() {
        @Override
        public void run() {
            if(!PlayerFiles.mUpdatingStatus){
                mCheckUpdatationStatusHandler.postDelayed(this, 100);
            } else {
                refreshAdapter();
            }
        }
    };

    public void refreshAdapter(){
        mSongList.clear();
        mSongListChangeble.clear();
        mSongList.addAll(PlayerFiles.mSongList);
        mSongListChangeble.addAll(mSongList);
        mAdapter.notifyDataSetChanged();
    }

    /* public interface SongListFragmentListner{
        public void refreshAdapter();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mSongListFragmentListner = (SongListFragmentListner) context;
        } catch (ClassCastException e){
            throw new ClassCastException("Error in retrieving data. Please try again!");
        }
    } */


}
