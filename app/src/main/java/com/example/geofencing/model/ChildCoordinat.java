package com.example.geofencing.model;

public class ChildCoordinat {
    double latitude;
    double longitude;

    public ChildCoordinat() {
    }

    public ChildCoordinat(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
