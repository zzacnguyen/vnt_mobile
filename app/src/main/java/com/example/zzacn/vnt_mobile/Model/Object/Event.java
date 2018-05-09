package com.example.zzacn.vnt_mobile.Model.Object;

import android.graphics.Bitmap;


public class Event {
    private int eventId;
    private String eventName;
    private String eventDate;
    private Bitmap eventImage;
    private boolean seen;

    public Event(){

    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public Bitmap getEventImage() {
        return eventImage;
    }

    public void setEventImage(Bitmap eventImage) {
        this.eventImage = eventImage;
    }
}
