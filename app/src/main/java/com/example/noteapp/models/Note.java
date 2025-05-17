package com.example.noteapp.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Note {
    private int id;
    private int userId;  // 发布者id
    private String title;
    private String content;
    private int likes;
    private int favorites;

    private Date timestamp;

    public Note() {}

    public Note(int id, int userId, String title, String content, int likes, int favorites) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.likes = likes;
        this.favorites = favorites;
        this.timestamp = new Date();
    }

    public Date getTimestamp(){
        return this.timestamp;
    }

    public void setTimestamp(String date){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()); // 指定日期格式
        try {
            this.timestamp = formatter.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getFavorites() {
        return favorites;
    }

    public void setFavorites(int favorites) {
        this.favorites = favorites;
    }


}
