package com.lihb.babyvoice.model;

import java.util.List;

/**
 * Created by lihb on 2017/3/11.
 */

public class GrowUpRecord {

    public String date;

    public String content;

    public List<String> picList;

    public GrowUpRecord(String date, String content, List<String> picList) {
        this.date = date;
        this.content = content;
        this.picList = picList;
    }
}
