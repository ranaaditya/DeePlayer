package com.example.aditya.deeplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.print.PrinterId;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.File;

public class NotificationService extends Service {

    private Notification mNotificationControls;

    private final static String START_FOREGROUND = "start_foreground";
    private final static String MAIN_ACTION = "main";
    private final static String PREVIOUS_SONG = "previous";
    private final static String PLAY_PAUSE_SONG = "play_pause";
    private final static String NEXT_SONG = "next";
    private final static String STOP_FOREGROUND = "stop_foreground";

    public static final String NOTIFY_PREVIOUS = "previous";
    public static final String NOTIFY_DELETE = "delete";
    public static final String NOTIFY_PAUSE = "pause";
    public static final String NOTIFY_PLAY = "play";
    public static final String NOTIFY_NEXT = "next";

    private final static int FOREGROUND_ID = 10002;

    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

    }

    private void showNotification(){
        String songName = PlayerFiles.mCurrentSongPlaying.getSongTitle();
        String songArtist = PlayerFiles.mCurrentSongPlaying.getSongArtist();
        RemoteViews expandedView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notifications_controls);
        Notification notification = new NotificationCompat.Builder(getApplicationContext()).setSmallIcon(R.drawable.ic_music).setContentTitle(songName).build();

        setListeners(expandedView);

        notification.contentView = expandedView;

        String thumbnailPath = PlayerFiles.mCurrentSongPlaying.getThumbnailPath();
        Bitmap songThumbnaiBitmap = getAlbumArtwork(thumbnailPath);
        notification.contentView.setImageViewBitmap(R.id.notification_controls_album_thumbnail, songThumbnaiBitmap);
        if(PlayerFiles.mSongPlayingStatus){
            notification.contentView.setImageViewResource(R.id.notification_controls_play_pause_imageButton, R.drawable.round_play_arrow_black_36dp);
        } else {
            notification.contentView.setImageViewResource(R.id.notification_controls_play_pause_imageButton, R.drawable.round_pause_black_36dp);
        }
        notification.contentView.setTextViewText(R.id.notification_controls_song_title_textView, songName);
        notification.contentView.setTextViewText(R.id.notification_controls_song_artist_textView, songArtist);
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        startForeground(FOREGROUND_ID, notification);
    }

    public void setListeners(RemoteViews view) {
        Intent previous = new Intent(NOTIFY_PREVIOUS);
        Intent delete = new Intent(NOTIFY_DELETE);
        Intent pause = new Intent(NOTIFY_PAUSE);
        Intent next = new Intent(NOTIFY_NEXT);
        Intent play = new Intent(NOTIFY_PLAY);

        PendingIntent pPrevious = PendingIntent.getBroadcast(getApplicationContext(), 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.notification_controls_previous_imageButton, pPrevious);

        /* PendingIntent pDelete = PendingIntent.getBroadcast(getApplicationContext(), 0, delete, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnDelete, pDelete); */

        PendingIntent pPause = PendingIntent.getBroadcast(getApplicationContext(), 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.notification_controls_play_pause_imageButton, pPause);

        PendingIntent pNext = PendingIntent.getBroadcast(getApplicationContext(), 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.notification_controls_next_imageButton, pNext);

        /* PendingIntent pPlay = PendingIntent.getBroadcast(getApplicationContext(), 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPlay, pPlay); */
    }

    private Bitmap getAlbumArtwork(String thumbnailPath){
        if(thumbnailPath != null){
            File imageFile = new File(thumbnailPath);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);
            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 150, 150, true);
            return imageBitmap;
        } else {
                /* Bitmap defaultImageBitmap = BitmapFactory.decodeResource((new AlbumListFragment()).getResources(),R.drawable.song_default_thumbnail);
                defaultImageBitmap = Bitmap.createScaledBitmap(defaultImageBitmap, 120, 120, true);
                return defaultImageBitmap; */
            return null;
        }
    }
}
