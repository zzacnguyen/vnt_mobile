package com.example.zzacn.vnt_mobile.Model.Object;

import android.graphics.Bitmap;


public class Service {
    private int id;
    private Bitmap image;
    private String name;

    public Service() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
