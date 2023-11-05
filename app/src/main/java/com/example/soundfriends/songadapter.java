package com.example.soundfriends;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class songadapter extends RecyclerView.Adapter<songadapter.songViewHolder>{
   private List<song>  ListSongs;

    public songadapter(List<song> listSongs) {
        ListSongs = listSongs;
    }

    @NonNull
    @Override
    public songViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new songViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull songViewHolder holder, int position) {
        song song = ListSongs.get(position);
        if(song == null){
            return;
        }
        holder.tvtitle.setText("Title: " +song.getTitle());

    }

    @Override
    public int getItemCount() {
        if(ListSongs != null){
            return ListSongs.size();
        }
        return 0;
    }

    public class songViewHolder extends RecyclerView.ViewHolder{

        private TextView tvtitle;
        private TextView tvartist;

        public songViewHolder(@NonNull View itemView) {
            super(itemView);
            tvtitle = itemView.findViewById(R.id.tv_title);
            tvartist = itemView.findViewById(R.id.tv_artist);
        }

    }
}
