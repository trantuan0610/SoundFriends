package com.example.soundfriends.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.soundfriends.R;
import com.example.soundfriends.fragments.Model.Comment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> comments;
    private Context context;

    public CommentAdapter(Context context,List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent,false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);

        if(comment == null) return;

        holder.tvAccount.setText(comment.getUsername());
        holder.tvBody.setText(comment.getBody());
        holder.tvTime.setText(comment.getTimestamp());
        Glide.with(context).load(Uri.parse(comment.getAvatarUrl())).placeholder(R.drawable.empty_avatar).into(holder.avatarComment);
        holder.tvTextLike.setText(String.valueOf(comment.getLikeCount()));
    }

    @Override
    public int getItemCount() {
        if(comments != null){
            return comments.size();
        }
        return 0;
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        public ImageView avatarComment, btnLikeComment;
        public TextView tvAccount, tvTime, tvBody, tvTextLike;
        public Button btnReplyComment;

        public CommentViewHolder(View itemView) {
            super(itemView);

            avatarComment = itemView.findViewById(R.id.avatarComment);
            tvAccount = itemView.findViewById(R.id.accountComment);
            tvTime = itemView.findViewById(R.id.timeComment);
            tvBody = itemView.findViewById(R.id.bodyComment);
            tvTextLike = itemView.findViewById(R.id.textLikeComment);
            btnLikeComment = itemView.findViewById(R.id.likeComment);
        }
    }
}
