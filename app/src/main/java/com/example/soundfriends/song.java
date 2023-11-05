package com.example.soundfriends;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class song extends AppCompatActivity {
    private RecyclerView rcvSongs;
    private songadapter songadapter;
    boolean isPlaying = false;
    boolean isDirty = false;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private final Handler handler = new Handler();
    private int currentPosition;

    private String audioURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        song thisActivity = this;

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

    private void playAudio(song thisActivity) {


        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("songs");


        FirebaseDatabase dtb = myRef.getDatabase();


//        System.out.print("DTB=========: "+ dtb.toString());

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Handle data retrieval here
                // dataSnapshot contains the data from the specified location
//                String value = dataSnapshot.getValue(String.class);

//                TextView textViewMusicTitle = findViewById(R.id.txtMusicTitle);
//                textViewMusicTitle.setText(value);

                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + map);

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String title = ds.child("title").getValue(String.class);
                    String artist = ds.child("artist").getValue(String.class);
                    String imgUrl = ds.child("imgUrl").getValue(String.class);
                    String srl = ds.child("srl").getValue(String.class);

                    TextView textViewMusicTitle = findViewById(R.id.txtMusicTitle);
                    textViewMusicTitle.setText(title);

                    TextView txtArtist = findViewById(R.id.txtartist);
                    txtArtist.setText(artist);

                    ImageView imgSong = findViewById(R.id.imgsong);


                    Glide.with(thisActivity).load(imgUrl).into(imgSong);

                    try {
                        mediaPlayer.setDataSource(srl);
                        mediaPlayer.prepare();
                        // Chạy các hàm cần thực thi sau khi lấy được nhạc
                        mediaPlayer.start();
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
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });


        // Lấy ra file audio muốn download
        // ------------------------------------------------------------------------------ //
//        String DEFAULT_AUDIO_FILE_NAME = "talacuanhau.mp3";
//        StorageReference audioRef = storageRef.child(DEFAULT_AUDIO_FILE_NAME);

//        File localFile = null;
//        try {
//            localFile = File.createTempFile("audio", "mp3");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        audioRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
//            audioRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                String downloadUrl = uri.toString();
//                audioURL=downloadUrl;
////                TextView textViewMusicTitle = findViewById(R.id.txtMusicTitle);
////                textViewMusicTitle.setText(downloadUrl);
//                try {
//                    mediaPlayer.setDataSource(downloadUrl);
//                    mediaPlayer.prepare();
//                    // Chạy các hàm cần thực thi sau khi lấy được nhạc
//                    mediaPlayer.start();
//                    seekBar = findViewById(R.id.seekbar);
//                    seekBar.setMax(mediaPlayer.getDuration());
//
//                    final Runnable updateSeekBar = new Runnable() {
//                        @Override
//                        public void run() {
//                            TextView txtCurrentDuration = findViewById(R.id.txtCurrentDuration);
//                            TextView txtDuration = findViewById(R.id.txtDuration);
//                            currentPosition = mediaPlayer.getCurrentPosition();
//                            txtCurrentDuration.setText(formatDuration(mediaPlayer.getCurrentPosition()));
//                            txtDuration.setText(formatDuration(mediaPlayer.getDuration()));
//                            seekBar.setProgress(mediaPlayer.getCurrentPosition());
//                            handler.postDelayed(this, 100); // update every 100ms
//                        }
//                    };
//                    handler.postDelayed(updateSeekBar, 100);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//        }).addOnFailureListener(e -> {
//            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//
//        });
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