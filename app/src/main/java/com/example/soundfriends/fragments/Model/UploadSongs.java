package com.example.soundfriends.fragments.Model;

import static androidx.core.content.ContentProviderCompat.requireContext;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.soundfriends.R;
import com.example.soundfriends.Song;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadSongs extends FirebaseRecyclerAdapter<Songs, UploadSongs.myViewHolder> {
//    ImageView imageView;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */


    private Context context;
    public UploadSongs(@NonNull FirebaseRecyclerOptions<Songs> options) {
        super(options);
        Log.d("huhu", "UploadSongs: " + getItemCount());

    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull Songs model) {
        holder.title.setText(model.getTitle());
        holder.artist.setText(model.getArtist());
        holder.category.setText(model.getCategory());
//        holder.id.setText(model.getId());
//        Glide.with(holder.imageView.getContext())
//                .asBitmap()
//                .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
//                .circleCrop()
//                .load(model.getSrl())
//                .into(new CustomTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                        holder.imageView.setImageBitmap(resource);
//                        Log.d("kaka", "onResourceReady: "+ resource);
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//                        // Đây là phương thức được gọi khi imageView được xóa do cuộc gọi vòng đời hoặc vì một số lý do khác.
//                        // Nếu bạn đang tham chiếu bitmap ở nơi khác ngoài imageView này
//                        // hãy xóa nó ở đây vì bạn không thể sử dụng bitmap nữa
//                    }
//                });
        String imageUrl = model.getUrlImg(); // Đường dẫn URL của hình ảnh

        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);

            // Thiết lập hình ảnh vào ImageView
            holder.imageView.setImageBitmap(bitmap);

            // Đóng kết nối và input stream
            input.close();
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            // Xử lý lỗi tải hình ảnh
        }




        onClickHolder(holder, model);

    }




    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_songs, parent, false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        ImageButton imgbtn;
        ImageView imageView;

        TextView title, artist, category, tvsrl;
        MediaPlayer mediaPlayer;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.img2);
            title = (TextView) itemView.findViewById(R.id.tv_song);
            artist = (TextView) itemView.findViewById(R.id.tv_artist);
            category = (TextView) itemView.findViewById(R.id.tv_category);
            imgbtn = (ImageButton) itemView.findViewById(R.id.imgbtn);
            tvsrl = (TextView) itemView.findViewById(R.id.tvsrl);
            mediaPlayer = new MediaPlayer();


        }


    }

    private void onClickHolder(myViewHolder holder, Songs model) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create an Intent to open the target Activity
                Intent intent = new Intent(context, Song.class);

                // Pass any necessary data to the SongActivity (e.g., selected item data)
                intent.putExtra("songId", model.getId());

//                System.out.println("========" + model.getId());

                // Start the target Activity
                context.startActivity(intent);
            }
        });
    }
}

