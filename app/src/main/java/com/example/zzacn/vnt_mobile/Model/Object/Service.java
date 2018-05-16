package com.example.zzacn.vnt_mobile.Model.Object;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;


public class Service implements Parcelable {
    private int id;
    private Bitmap image;
    private String name;

    public Service() {

    }

    protected Service(Parcel in) {
        id = in.readInt();
        image = in.readParcelable(Bitmap.class.getClassLoader());
        name = in.readString();
    }

    public static final Creator<Service> CREATOR = new Creator<Service>() {
        @Override
        public Service createFromParcel(Parcel in) {
            return new Service(in);
        }

        @Override
        public Service[] newArray(int size) {
            return new Service[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeParcelable(image, i);
        parcel.writeString(name);
    }
}
