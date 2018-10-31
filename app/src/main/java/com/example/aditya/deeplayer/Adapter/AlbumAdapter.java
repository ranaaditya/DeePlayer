package com.example.aditya.deeplayer.Adapter;

import android.content.ContentUris;
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

import com.example.aditya.deeplayer.Fragment.AlbumListFragment;
import com.example.aditya.deeplayer.R;
import com.example.aditya.deeplayer.Song;

import java.io.File;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private Context mContext;
    private List<Song> mAlbumList;
    private OnItemClickListner mOnItemClickListner;

    public interface OnItemClickListner{
        void onItemClick(Song song);
    }

    public AlbumAdapter(Context context, List<Song> songList, OnItemClickListner onItemClickListner){
        mAlbumList = songList;
        mOnItemClickListner = onItemClickListner;
        mContext = context;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_album_list_fragment_recycle_view, viewGroup, false);

        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder albumViewHolder, int position) {
        albumViewHolder.Bind(mAlbumList.get(position), mOnItemClickListner);
    }

    @Override
    public int getItemCount() {
        return mAlbumList.size();
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder{

        private ImageView mAlbumThumbnail;
        private TextView mAlbumTitle;

        public AlbumViewHolder(View view){
            super(view);
            mAlbumTitle = (TextView) view.findViewById(R.id.album_list_album_name);
            mAlbumThumbnail = (ImageView) view.findViewById(R.id.album_list_album_thumbnail);
        }

        public void Bind(final Song currentSong, final OnItemClickListner onItemClickListner) {
            mAlbumTitle.setText(currentSong.getAlbumName());
            Bitmap albumArtwork = getAlbumArtwork(currentSong.getThumbnailPath());
            if (albumArtwork != null){
                mAlbumThumbnail.setImageBitmap(albumArtwork);
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
                imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 250, 250, true);
                return imageBitmap;
            } else {
                Bitmap defaultImageBitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.song_default_thumbnail);
                defaultImageBitmap = Bitmap.createScaledBitmap(defaultImageBitmap, 250, 250, true);
                return defaultImageBitmap;
            }
        }
    }


}
