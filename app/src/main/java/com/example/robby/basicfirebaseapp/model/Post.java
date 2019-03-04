package com.example.robby.basicfirebaseapp.model;

import android.util.Base64;

public class Post {
    private String title;
    private long time;
    private String image;
    private String uid;

    // empty constructor for Firebase
    public Post() {
    }

    public Post(String title, long time, String image, String uid) {
        this.title = title;
        this.time = time;
        this.image = image;
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
