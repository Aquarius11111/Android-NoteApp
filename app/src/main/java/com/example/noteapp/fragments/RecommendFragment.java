package com.example.noteapp.fragments;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.noteapp.adapters.NoteAdapter;
import com.example.noteapp.avtivities.NoteDetailActivity;
import com.example.noteapp.database.DBHelper;
import com.example.noteapp.models.Note;

import java.util.List;

public class RecommendFragment extends Fragment implements NoteAdapter.OnNoteClickListener {

    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private List<Note> noteList;
    private DBHelper dbHelper;
    private EditText etSearch;
    private ImageButton btnSearch;
    private int userId;

    public RecommendFragment() {}

    public static RecommendFragment newInstance(int userId) {
        RecommendFragment fragment = new RecommendFragment();
        Bundle args = new Bundle();
        args.putInt("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);

        recyclerView = view.findViewById(R.id.recycler_notes);
        etSearch = view.findViewById(R.id.et_search);
        btnSearch = view.findViewById(R.id.btn_search);

        if (getArguments() != null) {
            userId = getArguments().getInt("userId");
        }

        // 设置瀑布流布局
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
                2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        dbHelper = DBHelper.getInstance(requireContext());
        noteList = dbHelper.getAllNotes(null); // 初始加载所有笔记

        adapter = new NoteAdapter(noteList, this, userId);
        recyclerView.setAdapter(adapter);

        // 设置搜索按钮点击事件
        btnSearch.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();
            searchNotes(keyword);
        });

        return view;
    }

    public void searchNotes(String keyword) {
        noteList.clear();
        if (keyword == null) {
            noteList.addAll(dbHelper.getAllNotes(null));
        } else {
            noteList.addAll(dbHelper.getAllNotes(keyword));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNoteClick(Note note) {
        // 处理笔记点击事件
        Intent intent = new Intent(requireContext(), NoteDetailActivity.class);
        intent.putExtra("noteId", note.getId());
        intent.putExtra("userId", userId);
        intent.putExtra("mode", "view"); // 预览模式
        startActivity(intent);
    }

//    private RecyclerView recyclerView;
//    private NoteAdapter adapter;
//    private List<Note> noteList;
//    private DBHelper dbHelper;
//    private EditText etSearch;
//    private ImageButton btnSearch;
//    private int userId;

//    public RecommendFragment() {}
//
//    public static RecommendFragment newInstance(int userId) {
//        RecommendFragment fragment = new RecommendFragment();
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
//        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
//
//        recyclerView = view.findViewById(R.id.recycler_notes);
//        etSearch = view.findViewById(R.id.et_search);
//        btnSearch = view.findViewById(R.id.btn_search);
//
//        if (getArguments() != null) {
//            userId = getArguments().getInt("userId");
//        }
//
//        // 设置瀑布流布局
//        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
//                2, StaggeredGridLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(layoutManager);
//
//        dbHelper = DBHelper.getInstance(requireContext());
//        noteList = dbHelper.getAllNotes(null); // 初始加载所有笔记
//
//        adapter = new NoteAdapter(noteList, this);
//        recyclerView.setAdapter(adapter);
//
//        // 设置搜索按钮点击事件
//        btnSearch.setOnClickListener(v -> {
//            String keyword = etSearch.getText().toString().trim();
//            searchNotes(keyword);
//        });
//
//        return view;
//    }
//
//    private void searchNotes(String keyword) {
//        noteList.clear();
//        if (keyword == null) {
//            noteList.addAll(dbHelper.getAllNotes(null));
//        } else {
//            noteList.addAll(dbHelper.getAllNotes(keyword));
//        }
//        adapter.notifyDataSetChanged();
//    }
//
//    @Override
//    public void onNoteClick(Note note) {
//        // 处理笔记点击事件
//        Intent intent = new Intent(requireContext(), NoteDetailActivity.class);
//        intent.putExtra("noteId", note.getId());
//        intent.putExtra("userId",userId);
//        intent.putExtra("mode", "view"); // 预览模式
//        startActivity(intent);
//    }
}