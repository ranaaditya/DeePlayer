package com.example.aditya.deeplayer;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.aditya.deeplayer.Adapter.SongAdapter;
import com.example.aditya.deeplayer.Fragment.AlbumListFragment;
import com.example.aditya.deeplayer.Fragment.ArtistListFragment;
import com.example.aditya.deeplayer.Fragment.FavouritesFragment;
import com.example.aditya.deeplayer.Fragment.SongListFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SongLibraryActivity extends AppCompatActivity {

    private android.support.v7.widget.SearchView mSearchView;

    private RecyclerView mSongRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private SongAdapter mAdapter;
    private ProgressBar mProgressBar;
    private TextView mEmptySongsTextView;

    private LinearLayout mViewPagerContainerLinearLayout;
    private RelativeLayout mRecycleViewContainerLinearLayout;

    private ViewPager mViewPager;
    private SongLibraryPageAdapter mPageAdapter;
    private TabLayout mTabLayout;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;
    private ActionBar mActionBar;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private final static int MY_PERMISSION_TO_READ_EXTERNAL_STORAGE = 200;

    private List<Song> mSongListChangeble = new ArrayList<>();

    private Handler mCheckUpdatationStatusHandler = new Handler();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(SongLibraryActivity.this);
        mEditor = mPreferences.edit();
        PlayerFiles.mTheme = mPreferences.getInt("theme", R.style.VioletAppTheme);
        setTheme(PlayerFiles.mTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_library);

        mEmptySongsTextView = (TextView) findViewById(R.id.library_emptyView);
        mProgressBar = (ProgressBar) findViewById(R.id.library_progressBar);
        mRecycleViewContainerLinearLayout = (RelativeLayout) findViewById(R.id.library_recycler_container_linearLayout);
        mViewPagerContainerLinearLayout = (LinearLayout) findViewById(R.id.library_viewPAger_container_linearLayout);

        mSongRecyclerView = (RecyclerView) findViewById(R.id.list_library);
        mSongRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(SongLibraryActivity.this);
        mSongRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SongAdapter(getApplicationContext(), mSongListChangeble, new SongAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(Song song) {
                int songPosition = PlayerFiles.mSongList.indexOf(song);
                Intent intent = new Intent(SongLibraryActivity.this, PlayerActivity.class);
                intent.putExtra("songPosition", songPosition);
                startActivity(intent);
            }
        });
        mSongRecyclerView.setAdapter(mAdapter);

        mViewPager = (ViewPager) findViewById(R.id.library_viewPager);
        mPageAdapter = new SongLibraryPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPageAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tabs_song_library);
        mTabLayout.setupWithViewPager(mViewPager);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.library_drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.library_navigationView);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                switch (menuItem.getItemId()){
                    case R.id.navigation_drawer_refresh_song_list:
                        PlayerFiles.updateSongList(SongLibraryActivity.this);
                        mCheckUpdatationStatusHandler.postDelayed(mUpdationRunnable, 100);
                        menuItem.setChecked(false);
                        break;
                    case R.id.navigation_drawer_grant_permissions:
                        startActivity(new Intent(SongLibraryActivity.this, SettingsGrantPermissionsActivity.class));
                        menuItem.setChecked(false);
                        break;
                    case R.id.navigation_drawer_change_theme:
                        Intent intent = new Intent(SongLibraryActivity.this, SettingsChangeTheme.class);
                        intent.putExtra("context", (Serializable) getApplicationContext());
                        startActivity(intent);
                        menuItem.setChecked(false);
                    case R.id.navigation_drawer_clear_favourites:
                        PlayerFiles.removeAllFavourites(getApplicationContext());
                        menuItem.setChecked(false);
                }

                return true;
            }
        });

        mToolbar = (Toolbar) findViewById(R.id.library_toolbar);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);


        getSongListWithPermission();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_song_library, menu);
        final MenuItem searchItem = menu.findItem(R.id.menu_library_searchView);
        mSearchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchString) {
                mViewPagerContainerLinearLayout.setVisibility(View.GONE);
                mRecycleViewContainerLinearLayout.setVisibility(View.VISIBLE);
                mEmptySongsTextView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                int vivi = mRecycleViewContainerLinearLayout.getVisibility();
                if (!searchString.equals("")) {
                    List<Song> tempSongList = new ArrayList<>();
                    String searchStringRegex = "(.*)" + searchString.toLowerCase() + "(.*)";
                    for (int i = 0; i < PlayerFiles.mSongList.size(); i++) {
                        String currentSongName = PlayerFiles.mSongList.get(i).getSongTitle().toLowerCase();
                        if (currentSongName.matches(searchStringRegex)) {
                            tempSongList.add(PlayerFiles.mSongList.get(i));
                        }
                    }
                    mSongListChangeble.clear();
                    mSongListChangeble.addAll(tempSongList);
                    mAdapter.notifyDataSetChanged();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String searchString) {
                if (searchString.equals("")) {
                    mSongListChangeble.clear();
                    mAdapter.notifyDataSetChanged();
                    mRecycleViewContainerLinearLayout.setVisibility(View.GONE);
                    notifyAdapters();
                    mViewPagerContainerLinearLayout.setVisibility(View.VISIBLE);
                    return true;
                } else {
                    return false;
                }
            }
        });
        return true;
    }

    @Override
    protected void onResume() {
        notifyAdapters();
        super.onResume();
    }

    @Override
    protected void onStart() {
        notifyAdapters();
        super.onStart();
    }

    private void getSongListFromPhone() {
        PlayerFiles.updateSongList(getApplicationContext());
        mCheckUpdatationStatusHandler.postDelayed(mUpdationRunnable, 100);
    }

    private Runnable mUpdationRunnable = new Runnable() {
        @Override
        public void run() {
            if(!PlayerFiles.mUpdatingStatus){
                mEmptySongsTextView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                mViewPagerContainerLinearLayout.setVisibility(View.GONE);
                mRecycleViewContainerLinearLayout.setVisibility(View.VISIBLE);
                mCheckUpdatationStatusHandler.postDelayed(this, 100);
            } else {
                notifyAdapters();
                PlayerFiles.loadFavouritesFromDb(getApplicationContext());
                if (PlayerFiles.mSongList.size() == 0){
                    mProgressBar.setVisibility(View.GONE);
                    mViewPagerContainerLinearLayout.setVisibility(View.GONE);
                    mRecycleViewContainerLinearLayout.setVisibility(View.VISIBLE);
                    mEmptySongsTextView.setVisibility(View.VISIBLE);
                    mEmptySongsTextView.setText(R.string.library_empty_view_no_song_text);
                } else {
                    mProgressBar.setVisibility(View.GONE);
                    notifyAdapters();
                    mViewPagerContainerLinearLayout.setVisibility(View.VISIBLE);
                    mRecycleViewContainerLinearLayout.setVisibility(View.GONE);
                }
            }
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }



    private List<Song> getSongListWithPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            getSongListFromPhone();
        } else {
            //returns true if the app has requested this permission previously and the user denied the request
            if (ActivityCompat.shouldShowRequestPermissionRationale(SongLibraryActivity.this , Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(SongLibraryActivity.this, "Grant permission to write storage", Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);
                mViewPagerContainerLinearLayout.setVisibility(View.GONE);
                mRecycleViewContainerLinearLayout.setVisibility(View.VISIBLE);
                mEmptySongsTextView.setVisibility(View.VISIBLE);
                mEmptySongsTextView.setText(R.string.library_empty_view_no_permission_read_storage);
            } else {
                ActivityCompat.requestPermissions(SongLibraryActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_TO_READ_EXTERNAL_STORAGE);
            }
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_TO_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSongListFromPhone();
                } else {
                    mProgressBar.setVisibility(View.GONE);
                    mViewPagerContainerLinearLayout.setVisibility(View.GONE);
                    mRecycleViewContainerLinearLayout.setVisibility(View.VISIBLE);
                    mEmptySongsTextView.setVisibility(View.VISIBLE);
                    mEmptySongsTextView.setText(R.string.library_empty_view_no_permission_read_storage);
                }
            }
        }
        return;
    }

    private void notifyAdapters(){
        SongListFragment songListFragment = (SongListFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.library_viewPager + ":" + 0);
        if (songListFragment != null){
            songListFragment.refreshAdapter();
        }
        AlbumListFragment albumListFragment = (AlbumListFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.library_viewPager + ":" + 1);
        if (albumListFragment != null){
            albumListFragment.refreshAdapter();
        }

        ArtistListFragment artistListFragment = (ArtistListFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.library_viewPager + ":" + 2);
        if(artistListFragment != null){
            artistListFragment.refreshAdapter();
        }

        FavouritesFragment favouritesFragment = (FavouritesFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.library_viewPager + ":" + 3);
        if (favouritesFragment != null){
            favouritesFragment.refreshAdapter();
        }
    }

    private class SongLibraryPageAdapter extends FragmentPagerAdapter{

        public SongLibraryPageAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new SongListFragment();
                case 1:
                    return new AlbumListFragment();
                case 2:
                    return new ArtistListFragment();
                default:
                    return new FavouritesFragment();
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "Songs";
                case 1:
                    return "Albums";
                case 2:
                    return "Artists";
                default:
                    return "Favourites";
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }


}
