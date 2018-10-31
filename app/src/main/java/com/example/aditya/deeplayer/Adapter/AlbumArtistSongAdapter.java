package com.example.aditya.deeplayer.Adapter;


import android.content.ContentUris;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.aditya.deeplayer.R;
import com.example.aditya.deeplayer.Song;
import java.util.List;

public class AlbumArtistSongAdapter extends  RecyclerView.Adapter<AlbumArtistSongAdapter.AlbumArtistSongViewHolder>{

    private Context mContext;
    private List<Song> mAlbumArtistList;
    private OnItemClickListner mOnItemClickListner;

    public interface OnItemClickListner{
        void onItemClick(Song song);
    }

    public AlbumArtistSongAdapter(Context context, List<Song> songList, OnItemClickListner onItemClickListner){
        mAlbumArtistList = songList;
        mOnItemClickListner = onItemClickListner;
        mContext = context;
    }

    @NonNull
    @Override
    public AlbumArtistSongViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_album_artist_song_list_recycle_view, viewGroup, false);

        return new AlbumArtistSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumArtistSongViewHolder albumArtistSongViewHolder, int position) {
        albumArtistSongViewHolder.Bind(mAlbumArtistList.get(position), mOnItemClickListner);
    }

    @Override
    public int getItemCount() {
        return mAlbumArtistList.size();
    }

    public class AlbumArtistSongViewHolder extends RecyclerView.ViewHolder{

        private TextView mAlbumArtistTrackNumber;
        private TextView mAlbumArtistTrackName;
        private TextView mAlbumArtistTrackDuration;

        public AlbumArtistSongViewHolder(View view){
            super(view);
            mAlbumArtistTrackNumber = (TextView) view.findViewById(R.id.album_artist_song_list_track_number_listItem);
            mAlbumArtistTrackName = (TextView) view.findViewById(R.id.album_artist_list_song_name_listItem);
            mAlbumArtistTrackDuration = (TextView) view.findViewById(R.id.album_artist_song_list_track_duration_listItem);
        }

        public void Bind(final Song currentSong, final OnItemClickListner onItemClickListner) {
            mAlbumArtistTrackName.setText(currentSong.getSongTitle());
            mAlbumArtistTrackNumber.setText(currentSong.getTrackId());
            mAlbumArtistTrackDuration.setText(milliSecondsToTimer(currentSong.getSongDuration()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    onItemClickListner.onItemClick(currentSong);
                }
            });
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

    }

}
