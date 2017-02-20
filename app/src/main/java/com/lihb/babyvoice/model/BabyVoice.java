package com.lihb.babyvoice.model;

/**
 * Created by lhb on 2017/2/8.
 */

public class BabyVoice extends BaseResponse {

    public String name;
    public String date;
    public String duration;

    public BabyVoice(String name, String date, String duration) {
        this.name = name;
        this.date = date;
        this.duration = duration;
    }
}
