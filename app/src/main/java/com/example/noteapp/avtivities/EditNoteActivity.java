package com.example.noteapp.avtivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.noteapp.R;
import com.example.noteapp.database.DBHelper;
import com.example.noteapp.models.Note;

public class EditNoteActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private Note note;
    private EditText etTitle, etContent;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        dbHelper = DBHelper.getInstance(this);

        int noteId = getIntent().getIntExtra("noteId", -1);
        int userId = getIntent().getIntExtra("userId", -1);

        if (noteId == -1) {
            Toast.makeText(this, "笔记不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        note = dbHelper.getNoteById(noteId);
        if (note == null) {
            Toast.makeText(this, "笔记不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupUI();
        setupListeners();
    }

    private void initViews() {
        etTitle = findViewById(R.id.et_edit_note_title);
        etContent = findViewById(R.id.et_edit_note_content);
        btnSave = findViewById(R.id.btn_save_edit_note);
    }

    private void setupUI() {
        etTitle.setText(note.getTitle());
        etContent.setText(note.getContent());
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveEditedNote());
    }

    private void saveEditedNote() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "标题和内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        note.setTitle(title);
        note.setContent(content);

        if (dbHelper.updateNote(note)) {
            Toast.makeText(this, "笔记已保存", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditNoteActivity.this, NoteDetailActivity.class);
            intent.putExtra(NoteDetailActivity.EXTRA_NOTE_ID, note.getId());
            intent.putExtra(NoteDetailActivity.EXTRA_MODE, NoteDetailActivity.MODE_EDIT);
            intent.putExtra("userId", getIntent().getIntExtra("userId", -1));
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "保存笔记失败", Toast.LENGTH_SHORT).show();
        }
    }
}