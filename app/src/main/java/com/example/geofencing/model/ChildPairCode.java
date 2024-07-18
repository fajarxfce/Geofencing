package com.example.geofencing.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ChildPairCode {
    public String username;
    public String email;
    public String childId;

    public ChildPairCode(String username, String email, String childId) {
        this.username = username;
        this.email = email;
        this.childId = childId;
    }
}