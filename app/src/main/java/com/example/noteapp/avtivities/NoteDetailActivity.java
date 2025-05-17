package com.example.noteapp.avtivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.noteapp.R;
import com.example.noteapp.adapters.CommentAdapter;
import com.example.noteapp.database.DBHelper;
import com.example.noteapp.fragments.RecommendFragment;
import com.example.noteapp.models.Comment;
import com.example.noteapp.models.Note;
import com.example.noteapp.models.User;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NoteDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_NOTE_ID = "noteId";
    public static final String EXTRA_MODE = "mode";
    public static final String MODE_VIEW = "view";
    public static final String MODE_EDIT = "edit";

    private DBHelper dbHelper;
    private Note note;
    private String mode;
    private int currentUserId;

    private ImageButton btnBack, btnEdit, btnDelete;
    private Button btnSendComment;
    private LinearLayout layoutEditDelete;
    private ImageView ivLike, ivCollect;
    private TextView tvNoteTitle, tvUsername, tvPostTime, tvNoteContent, tvLikeCount, tvCollectCount;
    private EditText etComment;
    private LinearLayout layoutLike, layoutCollect;
    private RecyclerView rvComments;
    private List<Comment> commentList;
    private CommentAdapter commentAdapter;

    private boolean isLiked = false;
    private boolean isCollected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        int noteId = getIntent().getIntExtra("noteId", -1);
        currentUserId = getIntent().getIntExtra("userId", -1);
        mode = getIntent().getStringExtra("mode");

        if (noteId == -1) {
            Toast.makeText(this, "无效的笔记 ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        dbHelper = DBHelper.getInstance(this);
        note = dbHelper.getNoteById(noteId);
        if (note == null) {
            Toast.makeText(this, "笔记不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupUI();
        setupListeners();
        setupComments();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);
        btnSendComment = findViewById(R.id.btn_send_comment);
        btnSendComment.setOnClickListener(this);
        layoutEditDelete = findViewById(R.id.layout_edit_delete);

        ivLike = findViewById(R.id.iv_like);
        ivCollect = findViewById(R.id.iv_collect);

        tvNoteTitle = findViewById(R.id.tv_note_title);
        tvUsername = findViewById(R.id.tv_username);
        tvPostTime = findViewById(R.id.tv_post_time);
        tvNoteContent = findViewById(R.id.tv_note_content);
        tvLikeCount = findViewById(R.id.tv_like_count);
        tvCollectCount = findViewById(R.id.tv_collect_count);

        etComment = findViewById(R.id.et_comment);

        layoutLike = findViewById(R.id.layout_like);
        layoutCollect = findViewById(R.id.layout_collect);

        rvComments = findViewById(R.id.rv_comments);
    }

    private void setupUI() {
        tvNoteTitle.setText(note.getTitle());
        tvNoteContent.setText(note.getContent());
        tvLikeCount.setText(String.valueOf(dbHelper.getLikesCount(note.getId())));
        tvCollectCount.setText(String.valueOf(dbHelper.getFavoritesCount(note.getId())));

        User author = dbHelper.getUserById(note.getUserId());
        if (author != null) {
            tvUsername.setText(author.getUsername());
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        tvPostTime.setText(dateFormat.format(note.getTimestamp()));

        if (MODE_EDIT.equals(mode)) {
            layoutEditDelete.setVisibility(View.VISIBLE);
        } else {
            layoutEditDelete.setVisibility(View.GONE);
        }

        isLiked = checkIfLiked();
        isCollected = checkIfCollected();

        updateLikeCollectUI();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        layoutLike.setOnClickListener(this);
        layoutCollect.setOnClickListener(this);
        etComment.setOnClickListener(this);
    }

    private boolean checkIfFollowing(int authorId) {
        return false;
    }

    private boolean checkIfLiked() {
        return dbHelper.isNoteLikedByUser(note.getId(), currentUserId);
    }

    private boolean checkIfCollected() {
        return dbHelper.isNoteFavoritedByUser(note.getId(), currentUserId);
    }

    private void updateLikeCollectUI() {
        ivLike.setImageResource(isLiked ? R.drawable.ic_liked : R.drawable.ic_like);
        ivCollect.setImageResource(isCollected ? R.drawable.ic_favorited : R.drawable.ic_favorite);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("userId", currentUserId);
            intent.putExtra("defaultPage", MODE_EDIT.equals(mode)?"my":"recommend"); // 指定默认显示“我的”页面
            startActivity(intent);
            finish();
        } else if (id == R.id.btn_edit) {
            Intent intent = new Intent(this, EditNoteActivity.class);
            intent.putExtra("noteId", note.getId());
            intent.putExtra("userId", currentUserId);
            startActivity(intent);
        } else if (id == R.id.btn_delete) {
            if (dbHelper.deleteNote(note.getId())) {
                Toast.makeText(this, "笔记已删除", Toast.LENGTH_SHORT).show();
                // 跳转到“我的”界面
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("userId", currentUserId);
                intent.putExtra("defaultPage", MODE_EDIT.equals(mode)?"my":"recommend"); // 指定默认显示“我的”页面
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "删除笔记失败", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.layout_like) {
            isLiked = !isLiked;
            if (isLiked) {
                dbHelper.addLike(note.getId(), currentUserId);
            } else {
                dbHelper.removeLike(note.getId(), currentUserId);
            }
            note.setLikes(dbHelper.getLikesCount(note.getId()));
            tvLikeCount.setText(String.valueOf(note.getLikes()));
            updateLikeCollectUI();
        } else if (id == R.id.layout_collect) {
            isCollected = !isCollected;
            if (isCollected) {
                dbHelper.addFavorite(note.getId(), currentUserId);
            } else {
                dbHelper.removeFavorite(note.getId(), currentUserId);
            }
            note.setFavorites(dbHelper.getFavoritesCount(note.getId()));
            tvCollectCount.setText(String.valueOf(note.getFavorites()));
            updateLikeCollectUI();
        } else if (id == R.id.btn_send_comment) {
            String comment = etComment.getText().toString().trim();
            if (!comment.isEmpty()) {
                addComment(comment);
                etComment.setText("");
            }
        }
    }

    private void addComment(String comment) {
        if (dbHelper.addComment(note.getId(), currentUserId, comment)) {
            Toast.makeText(this, "评论已发布", Toast.LENGTH_SHORT).show();
            commentList = dbHelper.getCommentsForNote(note.getId());
            commentAdapter = new CommentAdapter(commentList);
            rvComments.setAdapter(commentAdapter);
        } else {
            Toast.makeText(this, "评论发布失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupComments() {
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        commentList = dbHelper.getCommentsForNote(note.getId());
        commentAdapter = new CommentAdapter(commentList);
        rvComments.setAdapter(commentAdapter);
    }
//    public static final String EXTRA_NOTE_ID = "noteId";
//    public static final String EXTRA_MODE = "mode";
//    public static final String MODE_VIEW = "view";
//    public static final String MODE_EDIT = "edit";
//
//    private DBHelper dbHelper;
//    private Note note;
//    private String mode;
//    private int currentUserId;
//
//    private ImageButton btnBack, btnEdit, btnDelete;
//    private Button btnSendComment;
//    private LinearLayout layoutEditDelete;
//    private ImageView ivLike, ivCollect;
//    private TextView tvNoteTitle, tvUsername, tvPostTime, tvNoteContent, tvLikeCount, tvCollectCount;
//    private EditText etComment;
//    private LinearLayout layoutLike, layoutCollect;
//    private RecyclerView rvComments;
//    private List<Comment> commentList;
//    private CommentAdapter commentAdapter;
//
//    private boolean isLiked = false;
//    private boolean isCollected = false;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_note_detail);
//
//        int noteId = getIntent().getIntExtra("noteId", -1);
//        currentUserId = getIntent().getIntExtra("userId", -1);
//        mode = getIntent().getStringExtra("mode");
//
//        if (noteId == -1) {
//            Toast.makeText(this, "无效的笔记 ID", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//        dbHelper = DBHelper.getInstance(this);
//        note = dbHelper.getNoteById(noteId);
//        if (note == null) {
//            Toast.makeText(this, "笔记不存在", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        initViews();
//        setupUI();
//        setupListeners();
//        setupComments();
//    }
//
//    private void initViews() {
//        btnBack = findViewById(R.id.btn_back);
//        btnEdit = findViewById(R.id.btn_edit);
//        btnDelete = findViewById(R.id.btn_delete);
//        btnSendComment = findViewById(R.id.btn_send_comment);
//        btnSendComment.setOnClickListener(this);
//        layoutEditDelete = findViewById(R.id.layout_edit_delete);
//
//        ivLike = findViewById(R.id.iv_like);
//        ivCollect = findViewById(R.id.iv_collect);
//
//        tvNoteTitle = findViewById(R.id.tv_note_title);
//        tvUsername = findViewById(R.id.tv_username);
//        tvPostTime = findViewById(R.id.tv_post_time);
//        tvNoteContent = findViewById(R.id.tv_note_content);
//        tvLikeCount = findViewById(R.id.tv_like_count);
//        tvCollectCount = findViewById(R.id.tv_collect_count);
//
//        etComment = findViewById(R.id.et_comment);
//
//        layoutLike = findViewById(R.id.layout_like);
//        layoutCollect = findViewById(R.id.layout_collect);
//
//        rvComments = findViewById(R.id.rv_comments);
//    }
//
//    private void setupUI() {
//        tvNoteTitle.setText(note.getTitle());
//        tvNoteContent.setText(note.getContent());
//        tvLikeCount.setText(String.valueOf(note.getLikes()));
//        tvCollectCount.setText(String.valueOf(note.getFavorites()));
//
//        User author = dbHelper.getUserById(note.getUserId());
//        if (author != null) {
//            tvUsername.setText(author.getUsername());
//        }
//
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
//        tvPostTime.setText(dateFormat.format(note.getTimestamp()));
//
//        if (MODE_EDIT.equals(mode)) {
//            layoutEditDelete.setVisibility(View.VISIBLE);
//        } else {
//            layoutEditDelete.setVisibility(View.GONE);
//        }
//
//        isLiked = checkIfLiked();
//        isCollected = checkIfCollected();
//
//        updateLikeCollectUI();
//    }
//
//    private void setupListeners() {
//        btnBack.setOnClickListener(this);
//        btnEdit.setOnClickListener(this);
//        btnDelete.setOnClickListener(this);
//        layoutLike.setOnClickListener(this);
//        layoutCollect.setOnClickListener(this);
//        etComment.setOnClickListener(this);
//    }
//
//    private boolean checkIfFollowing(int authorId) {
//        return false;
//    }
//
//    private boolean checkIfLiked() {
//        return dbHelper.isNoteLikedByUser(note.getId(), currentUserId);
//    }
//
//    private boolean checkIfCollected() {
//        return dbHelper.isNoteFavoritedByUser(note.getId(), currentUserId);
//    }
//
//    private void updateLikeCollectUI() {
//        ivLike.setImageResource(isLiked ? R.drawable.ic_liked : R.drawable.ic_like);
//        ivCollect.setImageResource(isCollected ? R.drawable.ic_favorited : R.drawable.ic_favorite);
//    }
//
//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//        if (id == R.id.btn_back) {
//            finish();
//        } else if (id == R.id.btn_edit) {
//            Intent intent = new Intent(this, EditNoteActivity.class);
//            intent.putExtra("noteId", note.getId());
//            intent.putExtra("userId", currentUserId);
//            startActivity(intent);
//        } else if (id == R.id.btn_delete) {
//            if (dbHelper.deleteNote(note.getId())) {
//                Toast.makeText(this, "笔记已删除", Toast.LENGTH_SHORT).show();
//                finish();
//            } else {
//                Toast.makeText(this, "删除笔记失败", Toast.LENGTH_SHORT).show();
//            }
//        } else if (id == R.id.layout_like) {
//            isLiked = !isLiked;
//            if (isLiked) {
//                dbHelper.addLike(note.getId(), currentUserId);
//            } else {
//                dbHelper.removeLike(note.getId(), currentUserId);
//            }
//            note.setLikes(dbHelper.getLikesCount(note.getId()));
//            tvLikeCount.setText(String.valueOf(note.getLikes()));
//            updateLikeCollectUI();
//        } else if (id == R.id.layout_collect) {
//            isCollected = !isCollected;
//            if (isCollected) {
//                dbHelper.addFavorite(note.getId(), currentUserId);
//            } else {
//                dbHelper.removeFavorite(note.getId(), currentUserId);
//            }
//            note.setFavorites(dbHelper.getFavoritesCount(note.getId()));
//            tvCollectCount.setText(String.valueOf(note.getFavorites()));
//            updateLikeCollectUI();
//        } else if (id == R.id.btn_send_comment) {
//            String comment = etComment.getText().toString().trim();
//            if (!comment.isEmpty()) {
//                addComment(comment);
//                etComment.setText("");
//            }
//        }
//    }
//
//    private void addComment(String comment) {
//        if (dbHelper.addComment(note.getId(), currentUserId, comment)) {
//            Toast.makeText(this, "评论已发布", Toast.LENGTH_SHORT).show();
//            commentList = dbHelper.getCommentsForNote(note.getId());
//            commentAdapter = new CommentAdapter(commentList);
//            rvComments.setAdapter(commentAdapter);
//        } else {
//            Toast.makeText(this, "评论发布失败", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void setupComments() {
//        rvComments.setLayoutManager(new LinearLayoutManager(this));
//        commentList = dbHelper.getCommentsForNote(note.getId());
//        commentAdapter = new CommentAdapter(commentList);
//        rvComments.setAdapter(commentAdapter);
//    }
}