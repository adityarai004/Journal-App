package com.example.journalapp;

import android.app.Application;

//This class is created so that at one time only one user should be logged in and to make it possible we have used singleton pattern
public class JournalUser extends Application {
    private String username,userId;

    private static JournalUser instance;

    public static JournalUser getInstance(){
        if(instance == null){
            instance = new JournalUser();
        }
        return instance;
    }

    public JournalUser() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
