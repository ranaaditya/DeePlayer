package com.example.aditya.deeplayer;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class PlayerActivity extends AppCompatActivity {

    private final static String LOG_TAG = "PlayerActivity";

    private ImageButton mPlayPauseImageButton;
    private ImageButton mSkipNextImageButton;
    private ImageButton mSkipPreviousImageButton;
    private ImageButton mFastForwardImageButton;
    private ImageButton mFastRewindImageButton;
    private ImageButton mShuffleRepeatImageButton;
    private ImageButton mShowLyricsImageButton;
    private ImageButton mSetFavouriteImageButton;
    private ImageView mSongThumbnailImageView;
    private TextView mCurrentDurationTextView;
    private TextView mTotalDurationTextView;
    private TextView mSongTitleTextView;
    private TextView mSongArtistNameTextView;
    private TextView mSongLyricsTextView;
    private SeekBar mSongDurationSeekBar;
    private ScrollView mLyricsContainerScrollView;

    private Toolbar mToolbar;

    private final static int FAST_FORWARD_TIME = 10000;
    private final static int FAST_REWIND_TIME = 10000;
    private final static int SHUFFLE_STATE = 101;
    private final static int REPEAT_STATE = 102;
    private final static int SHOW_LYRICS = 103;
    private final static int SHOW_ARTWORK = 104;
    private final static int MY_PERMISSION_TO_WRITE_EXTERNAL_STORAGE = 201;
    private final static int MY_PERMISSION_TO_READ_CONTACTS = 202;
    private final static int MY_PERMISSION_TO_WRITE_CONTACTS = 203;
    private final static int SELECT_PHONE_NUMBER = 301;
    private final static int DEFAULT_RINGTONE = 501;
    private final static int PARTICULAR_RINGTONE = 502;

    private int mCurrentStateRepeatShuffle = SHUFFLE_STATE;
    private int mCurrentStateArtworkLyrics = SHOW_ARTWORK;

    private MediaPlayer mMediaPlayer;
    private Handler mSongDurationHandler = new Handler();

    private Song mCurrentSong;
    private int mCurrentSongIndex;

    private String mNumber = null;
    private String mLyricsSong;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(PlayerFiles.mTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        setTitle("Music Player");

        mPlayPauseImageButton = (ImageButton) findViewById(R.id.player_play_pause_imageButton);
        mSkipNextImageButton = (ImageButton) findViewById(R.id.player_skip_next_imageButton);
        mSkipPreviousImageButton = (ImageButton) findViewById(R.id.player_skip_previous_imageButton);
        mFastForwardImageButton = (ImageButton) findViewById(R.id.player_fast_forward_imageButton);
        mFastRewindImageButton = (ImageButton) findViewById(R.id.player_fast_rewind_imageButton);
        mShuffleRepeatImageButton = (ImageButton) findViewById(R.id.player_repeat_shuffle_imageButton);
        mShowLyricsImageButton = (ImageButton) findViewById(R.id.player_show_lyrics_imageButton);
        mSetFavouriteImageButton = (ImageButton) findViewById(R.id.player_favourites_imageButton);
        mSongThumbnailImageView = (ImageView) findViewById(R.id.player_song_thumbnail);
        mSongLyricsTextView = (TextView) findViewById(R.id.player_song_lyrics_textView);
        mSongTitleTextView = (TextView) findViewById(R.id.player_song_title);
        mSongArtistNameTextView = (TextView) findViewById(R.id.player_song_artist_name);
        mCurrentDurationTextView = (TextView) findViewById(R.id.player_current_duration);
        mTotalDurationTextView = (TextView) findViewById(R.id.player_total_duration);
        mSongDurationSeekBar = (SeekBar) findViewById(R.id.player_duration_seekBar);
        mLyricsContainerScrollView = (ScrollView) findViewById(R.id.player_lyrics_container_scrollView);

        mToolbar = (Toolbar) findViewById(R.id.player_toolbar);
        setSupportActionBar(mToolbar);

        final Intent songIntent = getIntent();
        mCurrentSongIndex = songIntent.getExtras().getInt("songPosition");
        mCurrentSong = PlayerFiles.mSongList.get(mCurrentSongIndex);
        PlayerFiles.mCurrentSongPlaying = mCurrentSong;
        mMediaPlayer = new MediaPlayer();
        playCurrentSong();


        mPlayPauseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMediaPlayer.isPlaying()){
                    mMediaPlayer.pause();
                    PlayerFiles.mSongPlayingStatus = false;
                    mPlayPauseImageButton.setImageResource(R.drawable.round_play_circle_outline_white_48dp);
                } else {
                    mMediaPlayer.start();
                    PlayerFiles.mSongPlayingStatus = true;
                    mPlayPauseImageButton.setImageResource(R.drawable.round_pause_circle_outline_white_48dp);
                }
                /* boolean isServiceRunning = PlayerFiles.isServiceRunning(NotificationService.class.getName(), getApplicationContext());
                if (!isServiceRunning) {
                    Intent intent = new Intent(getApplicationContext(),NotificationService.class);
                    startService(intent);
                } */
            }
        });

        mSkipNextImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCurrentStateRepeatShuffle == SHUFFLE_STATE){
                    if(mCurrentSongIndex < (PlayerFiles.mSongList.size() -1)){
                        mCurrentSongIndex = mCurrentSongIndex + 1;
                        mCurrentSong = PlayerFiles.mSongList.get(mCurrentSongIndex);
                        PlayerFiles.mCurrentSongPlaying = mCurrentSong;
                        playCurrentSong();
                    } else {
                        mCurrentSongIndex = 0;
                        mCurrentSong = PlayerFiles.mSongList.get(mCurrentSongIndex);
                        PlayerFiles.mCurrentSongPlaying = mCurrentSong;
                        playCurrentSong();
                    }
                } else if (mCurrentStateRepeatShuffle == REPEAT_STATE){
                    playCurrentSong();
                }
            }
        });

        mSkipPreviousImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCurrentStateRepeatShuffle == SHUFFLE_STATE){
                    if(mCurrentSongIndex > 0){
                        mCurrentSongIndex = mCurrentSongIndex - 1;
                        mCurrentSong = PlayerFiles.mSongList.get(mCurrentSongIndex);
                        PlayerFiles.mCurrentSongPlaying = mCurrentSong;
                        playCurrentSong();
                    } else {
                        mCurrentSongIndex = PlayerFiles.mSongList.size() - 1;
                        mCurrentSong = PlayerFiles.mSongList.get(mCurrentSongIndex);
                        PlayerFiles.mCurrentSongPlaying = mCurrentSong;
                        playCurrentSong();
                    }
                } else if (mCurrentStateRepeatShuffle == REPEAT_STATE){
                    playCurrentSong();
                }
            }
        });

        mFastForwardImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = mMediaPlayer.getCurrentPosition();
                if((currentPosition + FAST_FORWARD_TIME) <= mMediaPlayer.getDuration()){
                    mMediaPlayer.seekTo(currentPosition + FAST_FORWARD_TIME);
                } else {
                    mMediaPlayer.seekTo(mMediaPlayer.getDuration());
                }
            }
        });

        mFastRewindImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = mMediaPlayer.getCurrentPosition();
                if((currentPosition - FAST_REWIND_TIME) >= 0){
                    mMediaPlayer.seekTo(currentPosition - FAST_REWIND_TIME);
                } else {
                    mMediaPlayer.seekTo(0);
                }
            }
        });

        mShuffleRepeatImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mCurrentStateRepeatShuffle){
                    case SHUFFLE_STATE:
                        mShuffleRepeatImageButton.setImageResource(R.drawable.round_repeat_one_white_18dp);
                        mCurrentStateRepeatShuffle = REPEAT_STATE;
                        break;
                    case REPEAT_STATE:
                        mShuffleRepeatImageButton.setImageResource(R.drawable.round_shuffle_white_18dp);
                        mCurrentStateRepeatShuffle = SHUFFLE_STATE;
                        break;
                }
            }
        });

        mShowLyricsImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mCurrentStateArtworkLyrics){
                    case SHOW_ARTWORK:
                        mSongThumbnailImageView.setVisibility(View.INVISIBLE);
                        mLyricsContainerScrollView.setVisibility(View.VISIBLE);
                        mCurrentStateArtworkLyrics = SHOW_LYRICS;
                        break;
                    case SHOW_LYRICS:
                        mSongThumbnailImageView.setVisibility(View.VISIBLE);
                        mLyricsContainerScrollView.setVisibility(View.INVISIBLE);
                        mCurrentStateArtworkLyrics = SHOW_ARTWORK;
                }
            }
        });

        mSetFavouriteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCurrentSong.getFavourite()){
                    PlayerFiles.removeFromFavourites(mCurrentSong, getApplicationContext());
                    mCurrentSong.setFavourite(false);
                    Toast.makeText(PlayerActivity.this, mCurrentSong.getSongTitle() + " removed from Favourites", Toast.LENGTH_SHORT).show();
                    mSetFavouriteImageButton.setImageResource(R.drawable.baseline_favorite_border_white_18dp);
                } else {
                    PlayerFiles.addToFavourites(mCurrentSong, getApplicationContext());
                    mCurrentSong.setFavourite(true);
                    Toast.makeText(PlayerActivity.this, mCurrentSong.getSongTitle() + " added to Favourites", Toast.LENGTH_SHORT).show();
                    mSetFavouriteImageButton.setImageResource(R.drawable.baseline_favorite_white_18dp);
                }
            }
        });

        mSongDurationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mSongDurationHandler.removeCallbacks(mUpdateDuration);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSongDurationHandler.removeCallbacks(mUpdateDuration);
                int totalDuration = mMediaPlayer.getDuration();
                int currentDuration = progressToTimer(seekBar.getProgress(), totalDuration);

                mMediaPlayer.seekTo(currentDuration);

                updateSongDurationSeekBar();
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(mCurrentStateRepeatShuffle == SHUFFLE_STATE){
                    if(mCurrentSongIndex < (PlayerFiles.mSongList.size() -1)){
                        mCurrentSongIndex = mCurrentSongIndex + 1;
                        mCurrentSong = PlayerFiles.mSongList.get(mCurrentSongIndex);
                        PlayerFiles.mCurrentSongPlaying = mCurrentSong;
                        playCurrentSong();
                    } else {
                        mCurrentSongIndex = 0;
                        mCurrentSong = PlayerFiles.mSongList.get(mCurrentSongIndex);
                        PlayerFiles.mCurrentSongPlaying = mCurrentSong;
                        playCurrentSong();
                    }
                } else if (mCurrentStateRepeatShuffle == REPEAT_STATE){
                    playCurrentSong();
                }
            }
        });

    }

    private void playCurrentSong() {
        try{
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mCurrentSong.getSongData());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            PlayerFiles.mSongPlayingStatus = true;

            mSongTitleTextView.setText(mCurrentSong.getSongTitle());
            mSongArtistNameTextView.setText(mCurrentSong.getSongArtist());
            mSongThumbnailImageView.setImageDrawable(getAlbumArtwork(mCurrentSong.getThumbnailPath()));

            if(mCurrentSong.getFavourite()){
                mSetFavouriteImageButton.setImageResource(R.drawable.baseline_favorite_white_18dp);
            } else {
                mSetFavouriteImageButton.setImageResource(R.drawable.baseline_favorite_border_white_18dp);
            }

            mPlayPauseImageButton.setImageResource(R.drawable.round_pause_circle_outline_white_48dp);

            mSongDurationSeekBar.setProgress(0);
            mSongDurationSeekBar.setMax(100);

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if(networkInfo != null && networkInfo.isConnected()){
                getLyrics();
            } else {
                mSongLyricsTextView.setText(R.string.player_no_internet_connection);
            }
            
            updateSongDurationSeekBar();
        } catch (IOException  e) {
            Log.e(LOG_TAG, "IO Exception", e);
        }
    }

    private void getLyrics(){
        GetLyricsAsyncTask getLyricsAsyncTask = new GetLyricsAsyncTask();
        getLyricsAsyncTask.execute();
    }

    private class GetLyricsAsyncTask extends AsyncTask<String, Void, Integer>{
        @Override
        protected Integer doInBackground(String... strings) {
            mLyricsSong = fetchLyricsFromJson();
            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mSongLyricsTextView.setText(mLyricsSong);
        }

        private String convertToPermittableQuery(String query){
            char[] queryCharArray = query.toCharArray();
            String convertedQuery = "";
            for (int i = 0; i < queryCharArray.length; i++){
                char tempChar = queryCharArray[i];
                if(tempChar == ' '){
                    convertedQuery += "%20";
                } else {
                    convertedQuery += tempChar;
                }
            }
            return convertedQuery;
        }

        public String fetchLyricsFromJson(){
            String requestUrl = "http://api.musixmatch.com/ws/1.1/matcher.lyrics.get?apikey=3bf6d587acb836fbf2bdb77b15196c2b&q_track=" + convertToPermittableQuery(mCurrentSong.getSongTitle()) + "&q_artist=" + convertToPermittableQuery(mCurrentSong.getSongArtist());
            URL url = createUrl(requestUrl);
            String jsonResponse = null;
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem making http request", e);
            }
            return extracLyricsFromJson(jsonResponse);
        }

        private String extracLyricsFromJson(String jsonResponse) {
            if(TextUtils.isEmpty(jsonResponse)){
                return null;
            }
            String lyrics = "";
            try{
                JSONObject baseJsonObject = new JSONObject(jsonResponse);
                JSONObject lyricsObject = baseJsonObject.getJSONObject("message").getJSONObject("body").getJSONObject("lyrics");
                lyrics = lyricsObject.optString("lyrics_body");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return lyrics;
        }

        private URL createUrl(String stringUrl){
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error getting the url: " + stringUrl, e);
            }
            return url;
        }

        private String makeHttpRequest(URL url) throws IOException{
            String jsonResponse = "";
            if(url == null){
                return null;
            }
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.connect();

                if( urlConnection.getResponseCode() == 200){
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(LOG_TAG, "Error Reponse Code:" + url);
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retreiving the json response from URL", e);
            } finally {
                if(inputStream != null){
                    inputStream.close();
                }
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException{
            StringBuilder builder = new StringBuilder();
            if(inputStream != null){
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader br = new BufferedReader(inputStreamReader);
                String line = br.readLine();
                while (line != null){
                    builder.append(line);
                    line = br.readLine();
                }
            }
            return builder.toString();
        }
    }

    private String convertToUrlString(String text){
        char[] textCharArray = text.toCharArray();
        String convertedString = "";
        for (int i = 0; i < textCharArray.length; i++){
            if(textCharArray[i] == ' '){
                convertedString += "%20";
            } else {
                convertedString += textCharArray[i];
            }
        }
        return convertedString;
    }

    private void updateSongDurationSeekBar() {
        mSongDurationHandler.postDelayed(mUpdateDuration, 100);
    }

    private Runnable mUpdateDuration = new Runnable() {
        @Override
        public void run() {
            long currentDuration = mMediaPlayer.getCurrentPosition();
            long totalDuration = mMediaPlayer.getDuration();

            mCurrentDurationTextView.setText(milliSecondsToTimer(currentDuration));
            mTotalDurationTextView.setText(milliSecondsToTimer(totalDuration));

            int progress = (int) getProgressPercentage(currentDuration, totalDuration);
            mSongDurationSeekBar.setProgress(progress);

            mSongDurationHandler.postDelayed(this, 100);
        }
    };

    private Drawable getAlbumArtwork(String thumbnailPath){
        if(thumbnailPath != null){
            return Drawable.createFromPath(thumbnailPath);
        } else {
            return getResources().getDrawable(R.drawable.song_default_thumbnail);
        }
    }

    private String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";

        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);

        if(hours > 0){
            finalTimerString = hours + ":";
        }
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}
        finalTimerString = finalTimerString + minutes + ":" + secondsString;
        return finalTimerString;
    }

    private int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        percentage =(((double)currentSeconds)/totalSeconds)*100;
        return percentage.intValue();
    }

    private int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);

        return currentDuration * 1000;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mSongDurationHandler.removeCallbacks(mUpdateDuration);
                mMediaPlayer.release();
                PlayerFiles.mSongList.clear();
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.player_menu_add_ringtone:
                if(checkForPermissionToWriteSettings()){
                    setRingtoneWithPermission(DEFAULT_RINGTONE);
                } else {
                    Toast.makeText(PlayerActivity.this, "Cannot set ringtone. \n Write Settings Permission not granted", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.player_menu_add_particular_ringtone:
                if(checkForPermissionToWriteSettings()){
                    setRingtoneWithPermission(PARTICULAR_RINGTONE);
                } else {
                    Toast.makeText(PlayerActivity.this, "Cannot set ringtone. \n Write Settings Permission not granted", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.player_menu_share_song:
                shareSong();
                return true;
            case R.id.player_menu_delete_song:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteCurrentSong() {
        File songFile = new File(mCurrentSong.getSongData());

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(songFile.getAbsolutePath());
        int rowDeleted = getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + songFile.getAbsolutePath() + "\"", null);
        if(rowDeleted != 0){
            Toast.makeText(PlayerActivity.this, "Deleted Song :" + mCurrentSong.getSongTitle(), Toast.LENGTH_SHORT).show();
        }
        PlayerFiles.mSongList.remove(mCurrentSongIndex);
        if(mCurrentStateRepeatShuffle == SHUFFLE_STATE){
            if(mCurrentSongIndex < (PlayerFiles.mSongList.size() -1)){
                mCurrentSongIndex = mCurrentSongIndex + 1;
                mCurrentSong = PlayerFiles.mSongList.get(mCurrentSongIndex);
                PlayerFiles.mCurrentSongPlaying = mCurrentSong;
                playCurrentSong();
            } else {
                mCurrentSongIndex = 0;
                mCurrentSong = PlayerFiles.mSongList.get(mCurrentSongIndex);
                PlayerFiles.mCurrentSongPlaying = mCurrentSong;
                playCurrentSong();
            }
        } else if (mCurrentStateRepeatShuffle == REPEAT_STATE){
            playCurrentSong();
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.string_player_delete_confirmatio_dialog_message);
        builder.setPositiveButton(R.string.string_player_delete_confirmatio_dialog_confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteCurrentSong();
            }
        });
        builder.setNegativeButton(R.string.string_player_delete_confirmatio_dialog_discard, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean checkForPermissionToWriteSettings(){
        boolean settingsCanWrite = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            settingsCanWrite = Settings.System.canWrite(getApplicationContext());
            if(!settingsCanWrite) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }else {
                return true;
            }
        } else {
            return true; //User allowed permission when downloaded the app
        }
        return false;
    }

    private void setRingtoneWithPermission(int setRingtoneType) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if(setRingtoneType == DEFAULT_RINGTONE){
                setCurrentSongAsDefaultRingtone();
            } else if (setRingtoneType == PARTICULAR_RINGTONE){
                setParticularRingtoneWithPermission();
            }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(PlayerActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(PlayerActivity.this, "Grant permission to write the storage to add the ringtone file.", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(PlayerActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_TO_WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    private void setParticularRingtoneWithPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                startContactIntent();
            } else {
                //returns true if previously asked for permission and user denied
                if (ActivityCompat.shouldShowRequestPermissionRationale(PlayerActivity.this, Manifest.permission.WRITE_CONTACTS)) {
                    Toast.makeText(PlayerActivity.this, "Grant permission to write the storage tot add the ringtone file.", Toast.LENGTH_SHORT).show();
                } else {
                    ActivityCompat.requestPermissions(PlayerActivity.this,
                            new String[]{Manifest.permission.WRITE_CONTACTS}, MY_PERMISSION_TO_WRITE_CONTACTS);
                }
            }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(PlayerActivity.this, Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(PlayerActivity.this, "Grant permission to write the storage tot add the ringtone file.", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(PlayerActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSION_TO_READ_CONTACTS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_TO_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(PlayerActivity.this, "Permission granted to write external storage", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PlayerActivity.this, "Couldn't fetch songs due to permission denial", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case MY_PERMISSION_TO_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(PlayerActivity.this, "Permission granted to read contacts", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PlayerActivity.this, "Couldn't fetch contacts due to permission denial", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case MY_PERMISSION_TO_WRITE_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(PlayerActivity.this, "Permission granted to write contacts", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PlayerActivity.this, "Couldn't fetch contacts due to permission denial", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void setCurrentSongAsDefaultRingtone(){
        File songFile = new File(mCurrentSong.getSongData());

        ContentValues content = new ContentValues();
        content.put(MediaStore.MediaColumns.DATA,mCurrentSong.getSongData());
        content.put(MediaStore.MediaColumns.TITLE, mCurrentSong.getSongTitle());
        content.put(MediaStore.MediaColumns.SIZE, mCurrentSong.getSongSize());
        content.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
        content.put(MediaStore.Audio.Media.ARTIST, mCurrentSong.getSongArtist());
        content.put(MediaStore.Audio.Media.DURATION, mMediaPlayer.getDuration());
        content.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        content.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        content.put(MediaStore.Audio.Media.IS_ALARM, false);
        content.put(MediaStore.Audio.Media.IS_MUSIC, true);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(songFile.getAbsolutePath());
        getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + songFile.getAbsolutePath() + "\"", null);
        Uri ringtoneUri = getContentResolver().insert(uri, content);
        if(ringtoneUri != null){
            RingtoneManager.setActualDefaultRingtoneUri(PlayerActivity.this, RingtoneManager.TYPE_RINGTONE, ringtoneUri);
            Toast.makeText(PlayerActivity.this, "Set ringtone as : " + mCurrentSong.getSongTitle(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(PlayerActivity.this, "Cannot set ringtone as : " + ringtoneUri, Toast.LENGTH_SHORT).show();
        }
    }

    private void startContactIntent(){
        Intent contactIntent = new Intent(Intent.ACTION_PICK);
        contactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(contactIntent, SELECT_PHONE_NUMBER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHONE_NUMBER && resultCode == RESULT_OK) {

            Uri contactUri = data.getData();
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor cursor = getContentResolver().query(contactUri, projection,
                    null, null, null);

            // If the cursor returned is valid, get the phone number
            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                mNumber = cursor.getString(numberIndex);
                setCurrentSongAsParticularContactRingtone();
            }
        }
    }

    private void setCurrentSongAsParticularContactRingtone(){
        final Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, mNumber);
        final String[] projection = new String[] {ContactsContract.Contacts._ID, ContactsContract.Contacts.LOOKUP_KEY};
        final Cursor contactCursor = getContentResolver().query(lookupUri, projection, null, null, null);
        contactCursor.moveToFirst();

        try {
            final long contactId = contactCursor.getLong(contactCursor.getColumnIndex(ContactsContract.Contacts._ID));
            final String lookupKey = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
            final Uri contactUri = ContactsContract.Contacts.getLookupUri(contactId, lookupKey);
            if (contactUri == null) {
                Toast.makeText(PlayerActivity.this, "Couldn't set ringtone for : " + mNumber, Toast.LENGTH_SHORT).show();
                return;
            }

            String filePath = mCurrentSong.getSongData();

            final ContentValues values = new ContentValues();
            values.put(ContactsContract.Contacts.CUSTOM_RINGTONE, filePath);
            int rowUpdated = getContentResolver().update(contactUri, values, null, null);
            if(rowUpdated != 0){
                Toast.makeText(PlayerActivity.this, "Set ringtone for : " + mNumber + " " + rowUpdated, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PlayerActivity.this, "Cannot set ringtone", Toast.LENGTH_SHORT).show();
            }
        } finally {
            contactCursor.close();
            mNumber = null;
        }

    }

    private void shareSong(){
        Uri pathUri = Uri.parse(mCurrentSong.getSongData());
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("audio/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, pathUri);
        startActivity(Intent.createChooser(shareIntent, "Share Sound File"));
    }

    @Override
    public void onBackPressed() {
        mSongDurationHandler.removeCallbacks(mUpdateDuration);
        mMediaPlayer.release();
        PlayerFiles.mCurrentSongPlaying = null;
        PlayerFiles.mSongPlayingStatus = false;
        super.onBackPressed();
    }
}