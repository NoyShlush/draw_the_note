package com.example.finalproject;

import java.nio.file.Path;
import java.util.Calendar;

public class Note {
    private int id;
    private Calendar createdAt;
    private String path;
    private Calendar notificationAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Calendar getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Calendar createdAt) {
        this.createdAt = createdAt;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Calendar getNotificationAt() {
        return notificationAt;
    }

    public void setNotificationAt(Calendar notificationAt) {
        this.notificationAt = notificationAt;
    }

    public Note(){}

    public Note(int id, Calendar createdAt, String path, Calendar notificationAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.path = path;
        this.notificationAt = notificationAt;
    }

    @Override
    public String toString() {
        return  createdAt +  " - " + notificationAt + " - " + path;
    }
}
