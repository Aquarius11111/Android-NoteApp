package com.example.noteapp.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.noteapp.R;
import com.example.noteapp.database.DBHelper;
import com.example.noteapp.models.Note;

public class PublishFragment extends Fragment {

    private EditText etTitle, etContent;
    private Button btnPublish;
    private int userId;

    private DBHelper dbHelper;

    public PublishFragment() {}

    public static PublishFragment newInstance(int userId) {
        PublishFragment fragment = new PublishFragment();
        Bundle args = new Bundle();
        args.putInt("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_publish, container, false);

        etTitle = view.findViewById(R.id.et_title);
        etContent = view.findViewById(R.id.et_content);
        btnPublish = view.findViewById(R.id.btn_publish);

        if (getArguments() != null) {
            userId = getArguments().getInt("userId");
        }

        dbHelper = DBHelper.getInstance(requireContext());

        btnPublish.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                Toast.makeText(requireContext(), "标题不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(requireContext(), "内容不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            Note note = new Note();
            note.setUserId(userId);
            note.setTitle(title);
            note.setContent(content);

            boolean success = dbHelper.insertNote(note);
            if (success) {
                Toast.makeText(requireContext(), "发布成功", Toast.LENGTH_SHORT).show();
                etTitle.setText("");
                etContent.setText("");
            } else {
                Toast.makeText(requireContext(), "发布失败", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
