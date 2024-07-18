package com.example.geofencing.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserChild {
    public String username;
    public String email;
    public String pairKey;

    public UserChild(String username, String email, String pairKey) {
        this.username = username;
        this.email = email;
        this.pairKey = pairKey;
    }
}