package com.example.noteapp.models;

// models/Comment.java

import java.util.Date;

public class Comment {
    private int id;
    private int noteId;
    private int userId;
    private String comment;
    private Date timestamp;

    public Comment() {}

    public Comment(int id, int noteId, int userId, String comment, Date timestamp) {
        this.id = id;
        this.noteId = noteId;
        this.userId = userId;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}