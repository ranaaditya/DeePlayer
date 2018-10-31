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

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private Context mContext;
    private List<Song> mArtistList;
    private OnItemClickListner mOnItemClickListner;

    public interface OnItemClickListner{
        void onItemClick(Song song);
    }

    public ArtistAdapter(Context context, List<Song> songList, OnItemClickListner onItemClickListner){
        mArtistList = songList;
        mOnItemClickListner = onItemClickListner;
        mContext = context;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_artist_list_fragment_recycle_view, viewGroup, false);

        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder albumViewHolder, int position) {
        albumViewHolder.Bind(mArtistList.get(position), mOnItemClickListner);
    }

    @Override
    public int getItemCount() {
        return mArtistList.size();
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder{

        private ImageView mArtistThumbnail;
        private TextView mArtistTitle;

        public ArtistViewHolder(View view){
            super(view);
            mArtistTitle = (TextView) view.findViewById(R.id.artist_list_song_artist_listItem);
            mArtistThumbnail = (ImageView) view.findViewById(R.id.artist_list_song_thumbnail_listItem);
        }

        public void Bind(final Song currentSong, final OnItemClickListner onItemClickListner) {
            mArtistTitle.setText(currentSong.getSongArtist());
            Bitmap albumArtwork = getAlbumArtwork(currentSong.getThumbnailPath());
            if( albumArtwork != null){
                mArtistThumbnail.setImageBitmap(albumArtwork);
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
                imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 64, 64, true);
                return imageBitmap;
            } else {
                Bitmap defaultImageBitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.song_default_thumbnail);
                defaultImageBitmap = Bitmap.createScaledBitmap(defaultImageBitmap, 64, 64, true);
                return defaultImageBitmap;
            }
        }
    }


}
