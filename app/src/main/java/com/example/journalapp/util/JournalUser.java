package com.example.journalapp.util;

import android.app.Application;

//This class is created so that at one time only one user should be logged in and to make it possible we have used singleton pattern
public class JournalUser extends Application {
    private String username;
    private String userId;

    private static JournalUser instance;

    // following the Singleton Design Pattern

    public static JournalUser getInstance(){
        if (instance == null){
            instance = new JournalUser();
        }
        return instance;
    }

    public JournalUser(){
        // Empty Constructor
    }

    // Getter
    public String getUsername(){
        return username;
    }

    public String getUserId() {
        return userId;
    }

    // Setter
    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
