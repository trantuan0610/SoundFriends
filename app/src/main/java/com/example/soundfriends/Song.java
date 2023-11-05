package com.example.soundfriends;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.soundfriends.fragments.Model.Songs;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Song extends AppCompatActivity {
    boolean isPlaying = false;
    boolean isDirty = false;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private final Handler handler = new Handler();
    private int currentPosition;

    private String audioURL;

    private String songId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        Song thisActivity = this;

        // Retrieve data from the Intent
        Intent intent = getIntent();
        if (intent != null) {
            songId = intent.getStringExtra("songId");
        }

        // Create a reference to the "songs" node in your Firebase Realtime Database
        DatabaseReference songsRef = FirebaseDatabase.getInstance().getReference().child("songs");


        System.out.println("ID+++++++++: " + songId);

        // Query the specific song by its ID
        Query songQuery = songsRef.orderByChild("id").equalTo(songId);


        // Truy vấn dữ liệu
        songQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                System.out.println("HHUHUHUHUHHUHUU");

                for (DataSnapshot songSnapshot : dataSnapshot.getChildren()) {
                    // Lấy ra kết quả truy vấn được
                    String title = songSnapshot.child("title").getValue(String.class);
                    String artist = songSnapshot.child("artist").getValue(String.class);
                    String imgUrl = songSnapshot.child("urlImg").getValue(String.class);
                    String srl = songSnapshot.child("srl").getValue(String.class);

                    audioURL = srl;

                    TextView textViewMusicTitle = findViewById(R.id.txtMusicTitle);
                    textViewMusicTitle.setText(title);

                    TextView txtArtist = findViewById(R.id.txtartist);
                    txtArtist.setText(artist);

                    ImageView imgSong = findViewById(R.id.imgsong);


                    // Lấy chuỗi bitmap từ Firebase (giả sử 'model.getUrlImg()' chứa chuỗi bitmap)
                    String base64Image = imgUrl;
                    // Chuyển đổi chuỗi bitmap thành mảng byte
                    byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);

                    // Kiểm tra xem mảng byte có hợp lệ không
                    if (imageBytes == null || imageBytes.length == 0) {
                        Toast.makeText(imgSong.getContext(), "Không thể xử lý chuỗi", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Chuyển đổi mảng byte thành bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                    // Kiểm tra xem bitmap có hợp lệ không
                    if (bitmap == null) {
                        Toast.makeText(imgSong.getContext(), "Không thể xử lý chuỗi", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Bitmap đã tải xong, hiển thị nó bằng Glide
                    Glide.with(imgSong.getContext())
                            .as(Bitmap.class)
                            .load(bitmap)
                            .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                            .error(com.google.firebase.database.ktx.R.drawable.common_google_signin_btn_icon_dark_normal)
                            .circleCrop()
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    imgSong.setImageBitmap(resource);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    // Xử lý khi tải bị xóa (nếu cần)
                                }
                            });


                    try {
                        mediaPlayer.setDataSource(srl);
                        mediaPlayer.prepare();

                        seekBar = findViewById(R.id.seekbar);
                        seekBar.setMax(mediaPlayer.getDuration());

                        final Runnable updateSeekBar = new Runnable() {
                            @Override
                            public void run() {
                                TextView txtCurrentDuration = findViewById(R.id.txtCurrentDuration);
                                TextView txtDuration = findViewById(R.id.txtDuration);
                                currentPosition = mediaPlayer.getCurrentPosition();
                                txtCurrentDuration.setText(formatDuration(mediaPlayer.getCurrentPosition()));
                                txtDuration.setText(formatDuration(mediaPlayer.getDuration()));
                                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                                handler.postDelayed(this, 100); // update every 100ms
                            }
                        };
                        handler.postDelayed(updateSeekBar, 100);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors
            }
        });


        // Khơi tạo MediaPlayer
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        TextView txtPlay = findViewById(R.id.txtplay);
        ImageView imgDownload = findViewById(R.id.btnDownload);

        txtPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDirty) {
                    playAudio(thisActivity);
                    txtPlay.setText("Pause");
                    isPlaying = true;
                    isDirty = true;
                    return;
                }
                if (!isPlaying) {
                    resumeAudio();
                    txtPlay.setText("Pause");
                } else {
                    mediaPlayer.pause();
                    txtPlay.setText("Play");
                }
                isPlaying = !isPlaying;
            }
        });

        imgDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadAudio(audioURL);
            }
        });
    }

    private void resumeAudio() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(currentPosition);
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    private void playAudio(Song thisActivity) {
        // Chạy các hàm cần thực thi sau khi lấy được nhạc
        mediaPlayer.start();
        // below line is use to display a toast message.
        Toast.makeText(this, "Audio started playing..", Toast.LENGTH_SHORT).show();
    }

    private String formatDuration(long durationMs) {
        long hours = TimeUnit.MILLISECONDS.toHours(durationMs);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void downloadAudio(String AUDIO_URL) {
        Uri uri = Uri.parse(AUDIO_URL);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("My Audio");
        request.setDescription("Downloading audio...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "my_audio.mp3");

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);

        // You can track the download progress using the downloadId if needed
        // For example, you can query the DownloadManager for download status
        // by calling downloadManager.query(new DownloadManager.Query().setFilterById(downloadId))
        // and updating your UI accordingly.
    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }
}