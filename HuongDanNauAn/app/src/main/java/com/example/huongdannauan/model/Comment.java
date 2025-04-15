package com.example.huongdannauan.model;

import java.util.List;

public class Comment {
    private String commentId;
    private String content;
    private String date;
    private int likes;
    private String userEmail;
    private List<Reply> replies;
    private String sentiment;

    public Comment(String content, String userEmail, String sentiment) {
        this.content = content;
        this.userEmail = userEmail;
        this.sentiment = sentiment;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public Comment() {
        // Required empty constructor for Firebase
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
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

    public List<Reply> getReplies() {
        return replies;
    }

    public void setReplies(List<Reply> replies) {
        this.replies = replies;
    }
}
