package com.example.geofencing.model;

public class LocationHistory {
    private double latitude;
    private double longitude;

    public LocationHistory(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
