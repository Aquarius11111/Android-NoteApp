// adapters/NoteAdapter.java
package com.example.noteapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.R;
import com.example.noteapp.database.DBHelper;
import com.example.noteapp.models.Note;
import com.example.noteapp.models.User;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> noteList;
    private OnNoteClickListener listener;
    private DBHelper dbHelper;
    private int currentUserId;

    public NoteAdapter(List<Note> noteList, OnNoteClickListener listener, int currentUserId) {
        this.noteList = noteList;
        this.listener = listener;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 创建 ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note_1, parent, false);
        return new NoteViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.tvTitle.setText(note.getTitle());

        DBHelper dbHelper = DBHelper.getInstance(holder.itemView.getContext());
        boolean isLiked = dbHelper.isNoteLikedByUser(note.getId(), currentUserId);
        boolean isCollected = dbHelper.isNoteFavoritedByUser(note.getId(), currentUserId);

        holder.ivLike.setImageResource(isLiked ? R.drawable.ic_liked : R.drawable.ic_like);
        holder.ivCollect.setImageResource(isCollected ? R.drawable.ic_favorited : R.drawable.ic_favorite);

        holder.tvLikeCount.setText(String.valueOf(dbHelper.getLikesCount(note.getId())));
        holder.tvCollectCount.setText(String.valueOf(dbHelper.getFavoritesCount(note.getId())));
//        holder.layoutLike.setOnClickListener(v -> {
//            if (isLiked) {
//                dbHelper.removeLike(note.getId(), currentUserId);
//                note.setLikes(dbHelper.getLikesCount(note.getId()));
//            } else {
//                dbHelper.addLike(note.getId(), currentUserId);
//                note.setLikes(dbHelper.getLikesCount(note.getId()));
//            }
//            notifyItemChanged(position);
//        });
//
//        holder.layoutCollect.setOnClickListener(v -> {
//            if (isCollected) {
//                dbHelper.removeFavorite(note.getId(), currentUserId);
//                note.setFavorites(dbHelper.getFavoritesCount(note.getId()));
//            } else {
//                dbHelper.addFavorite(note.getId(), currentUserId);
//                note.setFavorites(dbHelper.getFavoritesCount(note.getId()));
//            }
//            notifyItemChanged(position);
//        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNoteClick(note);
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvLikeCount, tvCollectCount;
        ImageView ivLike, ivCollect;
        LinearLayout layoutLike, layoutCollect;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_note_title);
            tvLikeCount = itemView.findViewById(R.id.tv_like_count);
            tvCollectCount = itemView.findViewById(R.id.tv_collect_count);
            ivLike = itemView.findViewById(R.id.iv_like);
            ivCollect = itemView.findViewById(R.id.iv_collect);
            layoutLike = itemView.findViewById(R.id.layout_like);
            layoutCollect = itemView.findViewById(R.id.layout_collect);
        }
    }

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
    }

//    @NonNull
//    @Override
//    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_note_1, parent, false);
//        dbHelper = DBHelper.getInstance(parent.getContext());
//        return new NoteViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
//        Note note = noteList.get(position);
//
//        holder.tvTitle.setText(note.getTitle());
//
//        // 获取用户信息
//        User user = dbHelper.getUserById(note.getUserId());
//        if (user != null) {
//            holder.tvUsername.setText(user.getUsername());
//        }
//
//        // 设置互动数据
//        holder.tvLikeCount.setText(String.valueOf(note.getLikes()));
//        holder.tvCommentCount.setText(String.valueOf(note.getFavorites()));
//
//
//
//        holder.itemView.setOnClickListener(v -> {
//            if (listener != null) {
//                listener.onNoteClick(note);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return noteList.size();
//    }
//
//    public interface OnNoteClickListener {
//        void onNoteClick(Note note);
//    }
//
//    public static class NoteViewHolder extends RecyclerView.ViewHolder {
//        ImageView ivNoteImage;
//        TextView tvTitle;
//        ImageView ivUserAvatar;
//        TextView tvUsername;
//        TextView tvLikeCount;
//        TextView tvCommentCount;
//
//        public NoteViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tvTitle = itemView.findViewById(R.id.tv_note_title);
//            tvUsername = itemView.findViewById(R.id.tv_username);
//            tvLikeCount = itemView.findViewById(R.id.tv_like_count);
//            tvCommentCount = itemView.findViewById(R.id.tv_comment_count);
//        }
//    }
}