package com.example.geofencing.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ChildFirebase {
    public String parentId;
    public String name;
    public String pairKey;

    public ChildFirebase(String parentId, String name, String pairKey) {
        this.parentId = parentId;
        this.name = name;
        this.pairKey = pairKey;
    }
}