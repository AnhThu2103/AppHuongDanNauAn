package com.example.huongdannauan.model;

public class Reply {
    private String content;
    private String date;
    private int likes;
    private String userEmail;

    public Reply() {
        // Required empty constructor for Firebase
    }

    // Getters and setters
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
