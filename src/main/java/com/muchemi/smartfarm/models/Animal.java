package com.muchemi.smartfarm.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Animal implements Parcelable {

    public String id;
    public String profile_image;
    public String name;
    public String type;
    public String description;

    public Animal(String id, String profile_image, String name, String type, String description) {
        this.id = id;
        this.profile_image = profile_image;
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public Animal(Parcel in) {
    }

    @Override
    public String toString() {
        return "Animal{" +
                "id='" + id + '\'' +
                ", profile_image='" + profile_image + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", desctiption='" + description + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDesctiption() {
        return description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Creator<Animal> getCREATOR() {
        return CREATOR;
    }



    public static final Creator<Animal> CREATOR = new Creator<Animal>() {
        @Override
        public Animal createFromParcel(Parcel in) {
            return new Animal(in);
        }

        @Override
        public Animal[] newArray(int size) {
            return new Animal[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(profile_image);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(description);
    }
}
