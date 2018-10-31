package com.example.aditya.deeplayer.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aditya.deeplayer.Adapter.ArtistAdapter;
import com.example.aditya.deeplayer.AlbumArtistActivity;
import com.example.aditya.deeplayer.PlayerFiles;
import com.example.aditya.deeplayer.R;
import com.example.aditya.deeplayer.Song;

import java.util.ArrayList;
import java.util.List;

public class ArtistListFragment extends Fragment {

    private android.support.v7.widget.SearchView mSearchView;
    private RecyclerView mSongRecyclerView;
    private ProgressBar mProgressBar;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView mEmptyViewTextView;

    private Handler mCheckUpdatationStatusHandler = new Handler();

    private final static int ARTIST_TYPE = 100002;

    private ArtistAdapter mAdapter;
    //private ArtistListFragmentListner mArtistListFragmentListner;

    private List<Song> mArtistList = new ArrayList<>();
    private List<Song> mArtistListChangeble = new ArrayList<>();
    private List<String> mArtistNames = new ArrayList<>();



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
        mAdapter = new ArtistAdapter(getContext(), mArtistListChangeble, new ArtistAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(Song song) {
                Intent intent = new Intent(getContext(), AlbumArtistActivity.class);
                intent.putExtra("listType", ARTIST_TYPE);
                intent.putExtra("name", song.getSongArtist());
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
        mArtistList.clear();
        mArtistNames.clear();
        mArtistListChangeble.clear();

        String currentArtistName;

        for(int i = 0; i < PlayerFiles.mSongList.size(); i++){
            currentArtistName = PlayerFiles.mSongList.get(i).getAlbumName();
            if(mArtistNames.indexOf(currentArtistName) == -1){
                mArtistNames.add(currentArtistName);
                mArtistList.add(PlayerFiles.mSongList.get(i));
            }
        }
        mArtistListChangeble.addAll(mArtistList);
        mAdapter.notifyDataSetChanged();
    }

    /* public interface ArtistListFragmentListner{
        public void refreshAdapter();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mArtistListFragmentListner = (ArtistListFragmentListner) context;
        } catch (ClassCastException e){
            throw new ClassCastException("Error in retrieving data. Please try again!");
        }
    } */
}
