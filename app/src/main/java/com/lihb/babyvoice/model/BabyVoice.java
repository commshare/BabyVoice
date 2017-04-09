package com.lihb.babyvoice.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lhb on 2017/2/8.
 */

public class BabyVoice implements Parcelable {

    public String name;
    public String date;
    public String duration;
    public String category;
    public String url;

    public BabyVoice(String name, String date, String duration, String category, String url) {
        this.name = name;
        this.date = date;
        this.duration = duration;
        this.category = category;
        this.url = url;
    }

    public BabyVoice() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "BabyVoice{" +
                "name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", duration='" + duration + '\'' +
                ", category='" + category + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.date);
        dest.writeString(this.duration);
        dest.writeString(this.category);
        dest.writeString(this.url);
    }

    protected BabyVoice(Parcel in) {
        this.name = in.readString();
        this.date = in.readString();
        this.duration = in.readString();
        this.category = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<BabyVoice> CREATOR = new Parcelable.Creator<BabyVoice>() {
        @Override
        public BabyVoice createFromParcel(Parcel source) {
            return new BabyVoice(source);
        }

        @Override
        public BabyVoice[] newArray(int size) {
            return new BabyVoice[size];
        }
    };
}
