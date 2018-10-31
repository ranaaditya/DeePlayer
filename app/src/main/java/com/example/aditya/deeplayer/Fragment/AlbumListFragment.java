package com.example.aditya.deeplayer.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aditya.deeplayer.Adapter.AlbumAdapter;
import com.example.aditya.deeplayer.AlbumArtistActivity;
import com.example.aditya.deeplayer.PlayerFiles;
import com.example.aditya.deeplayer.R;
import com.example.aditya.deeplayer.Song;

import java.util.ArrayList;
import java.util.List;

public class AlbumListFragment extends Fragment {

    private android.support.v7.widget.SearchView mSearchView;
    private RecyclerView mSongRecyclerView;
    private ProgressBar mProgressBar;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView mEmptyViewTextView;
    private Handler mCheckUpdatationStatusHandler = new Handler();

    private final static int ALBUM_TYPE = 100001;


    private AlbumAdapter mAdapter;
    //private AlbumListFragmentListner mAlbumListFragmentListner;

    private List<Song> mAlbumList = new ArrayList<>();
    private List<Song> mAlbumListChangeble = new ArrayList<>();
    private List<String> mAlbumNames = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.library_list_fragment, container, false);

        mEmptyViewTextView = (TextView) view.findViewById(R.id.library_emptyView);
        mProgressBar = (ProgressBar) view.findViewById(R.id.library_progressBar);

        mSongRecyclerView = (RecyclerView) view.findViewById(R.id.list_library);
        mSongRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getContext(), 2);
        mSongRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AlbumAdapter(getContext(), mAlbumListChangeble, new AlbumAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(Song song) {
                Intent intent = new Intent(getContext(), AlbumArtistActivity.class);
                intent.putExtra("listType", ALBUM_TYPE);
                intent.putExtra("name", song.getAlbumName());
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
        mAlbumList.clear();
        mAlbumNames.clear();
        mAlbumListChangeble.clear();

        String currentAlbumName;

        for(int i = 0; i < PlayerFiles.mSongList.size(); i++){
            currentAlbumName = PlayerFiles.mSongList.get(i).getAlbumName();
            if(mAlbumNames.indexOf(currentAlbumName) == -1){
                mAlbumNames.add(currentAlbumName);
                mAlbumList.add(PlayerFiles.mSongList.get(i));
            }
        }
        mAlbumListChangeble.addAll(mAlbumList);
        mAdapter.notifyDataSetChanged();
    }

    /* public interface AlbumListFragmentListner{
        public void refreshAdapter();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mAlbumListFragmentListner = (AlbumListFragmentListner) context;
        } catch (ClassCastException e){
            throw new ClassCastException("Error in retrieving data. Please try again!");
        }
    }*/
}
