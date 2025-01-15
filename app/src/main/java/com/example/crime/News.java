package com.example.crime;

public class News {
    private String title;
    private String content;
    private String timestamp;
    private String author;

    // Default constructor required for Firebase
    public News() {
    }

    public News(String title, String content, String timestamp, String author) {
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.author = author;
    }

    // Getters and Setters
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
