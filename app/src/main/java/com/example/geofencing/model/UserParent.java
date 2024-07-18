package com.example.geofencing.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserParent {
    public String username;
    public String email;

    public UserParent(String username, String email) {
        this.username = username;
        this.email = email;
    }
}