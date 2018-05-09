package com.example.zzacn.vnt_mobile.Model.Object;

import android.graphics.Bitmap;


public class NearLocation {
    private int nearLocationId;
    private String nearLocationName;
    private String nearLocationDistance;
    private Bitmap nearLocationImage;

    public NearLocation() {
    }

    public int getNearLocationId() {
        return nearLocationId;
    }

    public void setNearLocationId(int nearLocationId) {
        this.nearLocationId = nearLocationId;
    }

    public String getNearLocationName() {
        return nearLocationName;
    }

    public void setNearLocationName(String nearLocationName) {
        this.nearLocationName = nearLocationName;
    }

    public String getNearLocationDistance() {
        return nearLocationDistance;
    }

    public void setNearLocationDistance(String nearLocationDistance) {
        this.nearLocationDistance = nearLocationDistance;
    }

    public Bitmap getNearLocationImage() {
        return nearLocationImage;
    }

    public void setNearLocationImage(Bitmap nearLocationImage) {
        this.nearLocationImage = nearLocationImage;
    }
}
