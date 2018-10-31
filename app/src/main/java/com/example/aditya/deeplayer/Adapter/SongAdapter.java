package com.example.aditya.deeplayer.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aditya.deeplayer.R;
import com.example.aditya.deeplayer.Song;

import java.io.File;
import java.util.List;

public class SongAdapter  extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private Context mContext;
    private List<Song> mSongList;
    private OnItemClickListner mOnItemClickListner;

    public interface OnItemClickListner{
        void onItemClick(Song song);
    }

    public class SongViewHolder extends RecyclerView.ViewHolder{

        private TextView mSongTitle;
        private TextView mSongArtist;
        private ImageView mSongThumbnail;

        public SongViewHolder(View view){
            super(view);
            mSongTitle = (TextView) view.findViewById(R.id.song_list_song_name_listItem);
            mSongArtist = (TextView) view.findViewById(R.id.song_list_song_artist_listItem);
            mSongThumbnail = (ImageView) view.findViewById(R.id.song_list_song_thumbnail_listItem);
        }

        public void Bind(final Song currentSong, final OnItemClickListner onItemClickListner) {
            mSongTitle.setText(currentSong.getSongTitle());
            mSongArtist.setText(currentSong.getSongArtist());
            Bitmap albumArtwork = getAlbumArtwork(currentSong.getThumbnailPath());
            if (albumArtwork != null){
                mSongThumbnail.setImageBitmap(albumArtwork);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    onItemClickListner.onItemClick(currentSong);
                }
            });
        }

        private Bitmap getAlbumArtwork(String thumbnailPath){
            if(thumbnailPath != null){
                File imageFile = new File(thumbnailPath);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);
                imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 56, 56, true);
                return imageBitmap;
            } else {
                Bitmap defaultImageBitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.song_default_thumbnail);
                defaultImageBitmap = Bitmap.createScaledBitmap(defaultImageBitmap, 56, 56, true);
                return defaultImageBitmap;
            }
        }
    }

    public SongAdapter(Context context, List<Song> songList, OnItemClickListner onItemClickListner){
        mSongList = songList;
        mOnItemClickListner = onItemClickListner;
        mContext = context;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_song_list_fragment_recycle_view, viewGroup, false);

        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder songViewHolder, int position) {
        songViewHolder.Bind(mSongList.get(position), mOnItemClickListner);
    }

    @Override
    public int getItemCount() {
        return mSongList.size();
    }
}
