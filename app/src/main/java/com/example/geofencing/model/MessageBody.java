package com.example.geofencing.model;

public class MessageBody {
    private String token;
    private NotificationBody notification;

    public MessageBody(String token, NotificationBody notification) {
        this.token = token;
        this.notification = notification;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public NotificationBody getNotification() {
        return notification;
    }

    public void setNotification(NotificationBody notification) {
        this.notification = notification;
    }
}
