package com.example.zzacn.vnt_mobile.Model.Object;

import android.graphics.Bitmap;


public class Notification {
    private int serviceId, eventId, eventUser;
    private String notificationName;
    private Bitmap notificationImage;
    private boolean seen;

    public Notification(){

    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getEventUser() {
        return eventUser;
    }

    public void setEventUser(int eventUser) {
        this.eventUser = eventUser;
    }

    public String getNotificationName() {
        return notificationName;
    }

    public void setNotificationName(String notificationName) {
        this.notificationName = notificationName;
    }

    public Bitmap getNotificationImage() {
        return notificationImage;
    }

    public void setNotificationImage(Bitmap notificationImage) {
        this.notificationImage = notificationImage;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
