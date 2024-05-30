package com.example.geofencing.model;

public class ChildData {
    private String name;
    private String pairKey;
    private String parentId;

    public ChildData() {
    }

    public ChildData(String name, String pairKey, String parentId) {
        this.name = name;
        this.pairKey = pairKey;
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPairKey() {
        return pairKey;
    }

    public void setPairKey(String pairKey) {
        this.pairKey = pairKey;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
