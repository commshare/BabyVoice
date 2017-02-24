package com.lihb.babyvoice.model;

/**
 * Created by lhb on 2017/2/8.
 */

public class BabyVoice {

    public String name;
    public String date;
    public String duration;
    public String category;

    public BabyVoice(String name, String date, String duration, String category) {
        this.name = name;
        this.date = date;
        this.duration = duration;
        this.category = category;
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

    @Override
    public String toString() {
        return "BabyVoice{" +
                "name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", duration='" + duration + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
