// adapters/CommentAdapter.java
package com.example.noteapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.R;
import com.example.noteapp.database.DBHelper;
import com.example.noteapp.models.Comment;
import com.example.noteapp.models.User;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentList;
    private DBHelper dbHelper;

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        dbHelper = DBHelper.getInstance(parent.getContext());
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.tvComment.setText(comment.getComment());

        // 获取用户信息
        // 这里假设你有一个方法可以根据用户 ID 获取用户信息
         User user = dbHelper.getUserById(comment.getUserId());
         if (user != null) {
             holder.tvUsernameComment.setText(user.getUsername());
         }

        // 显示评论时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        if (comment.getTimestamp() != null) {
            holder.tvCommentTime.setText(dateFormat.format(comment.getTimestamp()));
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsernameComment;
        TextView tvComment;
        TextView tvCommentTime;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsernameComment = itemView.findViewById(R.id.tv_username_comment);
            tvComment = itemView.findViewById(R.id.tv_comment);
            tvCommentTime = itemView.findViewById(R.id.tv_comment_time);
        }
    }
}
