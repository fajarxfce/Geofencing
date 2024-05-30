package com.example.geofencing.model;

public class Child {

    private int id;
    private String name;
    private String pairkey;

    public Child(int id, String name, String pairkey) {
        this.id = id;
        this.name = name;
        this.pairkey = pairkey;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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