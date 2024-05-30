package com.example.geofencing.model;

public class Child {

    private String id;
    private String name;
    private String pairkey;

    public Child(String id, String name, String pairkey) {
        this.id = id;
        this.name = name;
        this.pairkey = pairkey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPairkey() {
        return pairkey;
    }

    public void setPairkey(String pairkey) {
        this.pairkey = pairkey;
    }
}