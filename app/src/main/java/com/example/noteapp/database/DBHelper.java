package com.example.noteapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.noteapp.models.Comment;
import com.example.noteapp.models.Note;
import com.example.noteapp.models.User;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "noteshare.db";
    private static final int DB_VERSION = 1;

    private static DBHelper instance;

    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 用户表
        db.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE," +
                "password TEXT)");

        // 笔记表
        db.execSQL("CREATE TABLE IF NOT EXISTS notes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "title TEXT," +
                "content TEXT," +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");

        // 点赞表
        db.execSQL("CREATE TABLE IF NOT EXISTS likes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "note_id INTEGER," +
                "user_id INTEGER)");

        // 收藏表
        db.execSQL("CREATE TABLE IF NOT EXISTS favorites (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "note_id INTEGER," +
                "user_id INTEGER)");

        // 评论表
        db.execSQL("CREATE TABLE IF NOT EXISTS comments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "note_id INTEGER," +
                "user_id INTEGER," +
                "comment TEXT," +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 简单升级策略，实际项目请谨慎操作
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS notes");
        db.execSQL("DROP TABLE IF EXISTS likes");
        db.execSQL("DROP TABLE IF EXISTS favorites");
        db.execSQL("DROP TABLE IF EXISTS comments");
        onCreate(db);
    }

    // ------------------- 用户操作 --------------------

    public boolean registerUser(String username,String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", password);
        long row = db.insert("users", null, cv);
        return row != -1;
    }

    public User loginUser(String username,String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, username, password FROM users WHERE username=? and password=?", new String[]{username,password});
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(0));
            user.setUsername(cursor.getString(1));
            user.setPassword(cursor.getString(2));
        }
        cursor.close();
        return user;
    }

    public User getUserById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, username, password FROM users WHERE id = ?", new String[]{String.valueOf(id)});
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(0));
            user.setUsername(cursor.getString(1));
            user.setPassword(cursor.getString(2));
        }
        cursor.close();
        return user;
    }

    // ------------------- 笔记操作 --------------------

    public boolean insertNote(Note note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user_id", note.getUserId());
        cv.put("title", note.getTitle());
        cv.put("content", note.getContent());
        long row = db.insert("notes", null, cv);
        return row != -1;
    }

    public boolean updateNote(Note note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", note.getTitle());
        cv.put("content", note.getContent());
        int rows = db.update("notes", cv, "id=?", new String[]{String.valueOf(note.getId())});
        return rows > 0;
    }

    public boolean deleteNote(int noteId) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete("notes", "id=?", new String[]{String.valueOf(noteId)});
        // 同时删除关联点赞、收藏、评论记录（可选）
        db.delete("likes", "note_id=?", new String[]{String.valueOf(noteId)});
        db.delete("favorites", "note_id=?", new String[]{String.valueOf(noteId)});
        db.delete("comments", "note_id=?", new String[]{String.valueOf(noteId)});
        return rows > 0;
    }

    public Note getNoteById(int noteId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, user_id, title, content, timestamp FROM notes WHERE id=?", new String[]{String.valueOf(noteId)});
        Note note = null;
        if (cursor.moveToFirst()) {
            note = new Note();
            note.setId(cursor.getInt(0));
            note.setUserId(cursor.getInt(1));
            note.setTitle(cursor.getString(2));
            note.setContent(cursor.getString(3));
            note.setTimestamp(cursor.getString(4));
        }
        cursor.close();
        return note;
    }

    // 查询所有笔记（可搜索标题）
    public List<Note> getAllNotes(String keyword) {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;
        if (keyword == null || keyword.isEmpty()) {
            cursor = db.rawQuery("SELECT id, user_id, title, content, timestamp FROM notes ORDER BY timestamp DESC", null);
        } else {
            cursor = db.rawQuery("SELECT id, user_id, title, content, timestamp FROM notes WHERE title LIKE ? ORDER BY timestamp DESC",
                    new String[]{"%" + keyword + "%"});
        }
        while (cursor.moveToNext()) {
            Note note = new Note();
            note.setId(cursor.getInt(0));
            note.setUserId(cursor.getInt(1));
            note.setTitle(cursor.getString(2));
            note.setContent(cursor.getString(3));
            note.setTimestamp(cursor.getString(4));
            notes.add(note);
        }
        cursor.close();
        return notes;
    }

    // 查询指定用户笔记（可搜索标题）
    public List<Note> getNotesByUser(int userId, String keyword) {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;
        if (keyword == null || keyword.isEmpty()) {
            cursor = db.rawQuery("SELECT id, user_id, title, content, timestamp FROM notes WHERE user_id=? ORDER BY timestamp DESC",
                    new String[]{String.valueOf(userId)});
        } else {
            cursor = db.rawQuery("SELECT id, user_id, title, content, timestamp FROM notes WHERE user_id=? AND title LIKE ? ORDER BY timestamp DESC",
                    new String[]{String.valueOf(userId), "%" + keyword + "%"});
        }
        while (cursor.moveToNext()) {
            Note note = new Note();
            note.setId(cursor.getInt(0));
            note.setUserId(cursor.getInt(1));
            note.setTitle(cursor.getString(2));
            note.setContent(cursor.getString(3));
//            note.setTimestamp(cursor.getString(4));
            notes.add(note);
        }
        cursor.close();
        return notes;
    }

    // ------------------- 点赞操作 --------------------

    public boolean addLike(int noteId, int userId) {
        if (isNoteLikedByUser(noteId, userId)) return false;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("note_id", noteId);
        cv.put("user_id", userId);
        long row = db.insert("likes", null, cv);
        return row != -1;
    }


    public boolean removeLike(int noteId, int userId) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete("likes", "note_id=? AND user_id=?", new String[]{String.valueOf(noteId), String.valueOf(userId)});
        return rows > 0;
    }


    public boolean isNoteLikedByUser(int noteId, int userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM likes WHERE note_id=? AND user_id=?", new String[]{String.valueOf(noteId), String.valueOf(userId)});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public int getLikesCount(int noteId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM likes WHERE note_id=?", new String[]{String.valueOf(noteId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // ------------------- 收藏操作 --------------------

    public boolean addFavorite(int noteId, int userId) {
        if (isNoteFavoritedByUser(noteId, userId)) return false;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("note_id", noteId);
        cv.put("user_id", userId);
        long row = db.insert("favorites", null, cv);
        return row != -1;
    }

    public boolean removeFavorite(int noteId, int userId) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete("favorites", "note_id=? AND user_id=?", new String[]{String.valueOf(noteId), String.valueOf(userId)});
        return rows > 0;
    }


    public boolean isNoteFavoritedByUser(int noteId, int userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM favorites WHERE note_id=? AND user_id=?", new String[]{String.valueOf(noteId), String.valueOf(userId)});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public int getFavoritesCount(int noteId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM favorites WHERE note_id=?", new String[]{String.valueOf(noteId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // ------------------- 评论操作 --------------------

    public boolean addComment(int noteId, int userId, String comment) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("note_id", noteId);
        cv.put("user_id", userId);
        cv.put("comment", comment);
        long row = db.insert("comments", null, cv);
        return row != -1;
    }

    public List<Comment> getCommentsForNote(int noteId) {
        List<Comment> comments = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, note_id, user_id, comment, timestamp FROM comments WHERE note_id=? ORDER BY timestamp DESC", new String[]{String.valueOf(noteId)});
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            int noteIdFromDb = cursor.getInt(1);
            int userId = cursor.getInt(2);
            String comment = cursor.getString(3);
            // 假设可以获取时间戳
            // Date timestamp = new Date(cursor.getLong(4));
            Comment commentObj = new Comment(id, noteIdFromDb, userId, comment, null);
            comments.add(commentObj);
        }
        cursor.close();
        return comments;
    }


}
