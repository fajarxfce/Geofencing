package com.example.geofencing.dialog;

public class ChildInfo {
    private String email;
    private String pairKey;
    private String username;

    public ChildInfo() {
    }

    public ChildInfo(String email, String pairKey, String username) {
        this.email = email;
        this.pairKey = pairKey;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public String getPairKey() {
        return pairKey;
    }

    public String getUsername() {
        return username;
    }
}
