package com.muchemi.smartfarm.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Crop implements Parcelable {

    public String id;
    public String image;
    public String name;
    public String description;

    public Crop(String id, String image, String name, String description) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.description = description;
    }

    protected Crop(Parcel in) {
        id = in.readString();
        image = in.readString();
        name = in.readString();
        description = in.readString();
    }

    @Override
    public String toString() {
        return "Crop{" +
                "id='" + id + '\'' +
                ", image='" + image + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public static final Creator<Crop> CREATOR = new Creator<Crop>() {
        @Override
        public Crop createFromParcel(Parcel in) {
            return new Crop(in);
        }

        @Override
        public Crop[] newArray(int size) {
            return new Crop[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(image);
        dest.writeString(name);
        dest.writeString(description);
    }
}
