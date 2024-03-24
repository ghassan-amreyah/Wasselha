package com.cs.wasselha.model;

import java.time.LocalDateTime;

public class Notification {


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private int user_id;
    private String user_type;
    private String title;

    private String description;
    private String date;

    public Notification() {
    }

    public Notification(int user_id, String user_type, String title, String description, String date) {
        this.user_id = user_id;
        this.user_type = user_type;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
