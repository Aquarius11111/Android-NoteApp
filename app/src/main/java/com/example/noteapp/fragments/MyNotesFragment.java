package com.example.noteapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.example.noteapp.R;
import com.example.noteapp.avtivities.NoteDetailActivity;
import com.example.noteapp.adapters.NoteAdapter;
import com.example.noteapp.database.DBHelper;
import com.example.noteapp.models.Note;
import java.util.ArrayList;
import java.util.List;

public class MyNotesFragment extends Fragment implements NoteAdapter.OnNoteClickListener {

    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private List<Note> noteList;
    private DBHelper dbHelper;
    private int userId;
    private EditText etSearch;
    private ImageButton btnSearch;

    public MyNotesFragment() {}

    public static MyNotesFragment newInstance(int userId) {
        MyNotesFragment fragment = new MyNotesFragment();
        Bundle args = new Bundle();
        args.putInt("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_notes, container, false);

        recyclerView = view.findViewById(R.id.recycler_my_notes);
        etSearch = view.findViewById(R.id.et_my_search);
        btnSearch = view.findViewById(R.id.btn_my_search);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
                2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        dbHelper = DBHelper.getInstance(requireContext());

        if (getArguments() != null) {
            userId = getArguments().getInt("userId");
            noteList = dbHelper.getNotesByUser(userId, null);
        }

        adapter = new NoteAdapter(noteList, this, userId);
        recyclerView.setAdapter(adapter);

        btnSearch.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();
            searchMyNotes(keyword);
        });

        return view;
    }

    public void searchMyNotes(String keyword) {
        noteList.clear();
        if (TextUtils.isEmpty(keyword)) {
            noteList.addAll(dbHelper.getNotesByUser(userId, null));
        } else {
            noteList.addAll(dbHelper.getNotesByUser(userId, keyword));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNoteClick(Note note) {
        Intent intent = new Intent(requireContext(), NoteDetailActivity.class);
        intent.putExtra("noteId", note.getId());
        intent.putExtra("mode", NoteDetailActivity.MODE_EDIT);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }




//    private RecyclerView recyclerView;
//    private NoteAdapter adapter;
//    private List<Note> noteList;
//    private DBHelper dbHelper;
//    private int userId;
//    private EditText etSearch;
//    private ImageButton btnSearch;
//
//    public MyNotesFragment() {}
//
//    public static MyNotesFragment newInstance(int userId) {
//        MyNotesFragment fragment = new MyNotesFragment();
//        Bundle args = new Bundle();
//        args.putInt("userId", userId);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_my_notes, container, false);
//
//        recyclerView = view.findViewById(R.id.recycler_my_notes);
//        etSearch = view.findViewById(R.id.et_my_search);
//        btnSearch = view.findViewById(R.id.btn_my_search);
//
//        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
//                2, StaggeredGridLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(layoutManager);
//
//        dbHelper = DBHelper.getInstance(requireContext());
//
//        if (getArguments() != null) {
//            userId = getArguments().getInt("userId");
//            noteList = dbHelper.getNotesByUser(userId, null);
//        }
//
//        adapter = new NoteAdapter(noteList, this);
//        recyclerView.setAdapter(adapter);
//
//        btnSearch.setOnClickListener(v -> {
//            String keyword = etSearch.getText().toString().trim();
//            searchMyNotes(keyword);
//        });
//
//        return view;
//    }
//
//    private void searchMyNotes(String keyword) {
//        noteList.clear();
//        if (TextUtils.isEmpty(keyword)) {
//            noteList.addAll(dbHelper.getNotesByUser(userId, null));
//        } else {
//            noteList.addAll(dbHelper.getNotesByUser(userId, keyword));
//        }
//        adapter.notifyDataSetChanged();
//    }
//
//    @Override
//    public void onNoteClick(Note note) {
//        Intent intent = new Intent(requireContext(), NoteDetailActivity.class);
//        intent.putExtra("noteId", note.getId());
//        intent.putExtra("mode", NoteDetailActivity.MODE_EDIT);
//        intent.putExtra("userId", userId);
//        startActivity(intent);
//    }
}