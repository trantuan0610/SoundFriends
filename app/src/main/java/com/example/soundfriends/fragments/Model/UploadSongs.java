package com.example.soundfriends.fragments.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import android.util.Base64;
import android.widget.Toast;


public class UploadSongs extends FirebaseRecyclerAdapter<Songs, UploadSongs.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public UploadSongs(@NonNull FirebaseRecyclerOptions<Songs> options) {
        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull Songs model) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userIDLogin = currentUser.getUid();

        if (model.getUserID().equals(userIDLogin)) {
            holder.title.setText(model.getTitle());
            holder.artist.setText(model.getArtist());
            holder.category.setText(model.getCategory());

            // Lấy chuỗi bitmap từ Firebase (giả sử 'model.getUrlImg()' chứa chuỗi bitmap)
            String base64Image = model.getUrlImg();
            // Chuyển đổi chuỗi bitmap thành mảng byte
            byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);

            // Kiểm tra xem mảng byte có hợp lệ không
            if (imageBytes == null || imageBytes.length == 0) {
                Toast.makeText(holder.imageView.getContext(), "Không thể xử lý chuỗi", Toast.LENGTH_SHORT).show();
                return;
            }

            // Chuyển đổi mảng byte thành bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            // Kiểm tra xem bitmap có hợp lệ không
            if (bitmap == null) {
                Toast.makeText(holder.imageView.getContext(), "Không thể xử lý chuỗi", Toast.LENGTH_SHORT).show();
                return;
            }

            // Bitmap đã tải xong, hiển thị nó bằng Glide
            Glide.with(holder.imageView.getContext())
                    .as(Bitmap.class)
                    .load(bitmap)
                    .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                    .error(com.google.firebase.database.ktx.R.drawable.common_google_signin_btn_icon_dark_normal)
                    .circleCrop()
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            holder.imageView.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // Xử lý khi tải bị xóa (nếu cần)
                        }
                    });
        } else {
            holder.itemView.setVisibility(View.GONE);
        }
    }




    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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


        }


    }
}

