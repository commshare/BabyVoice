package com.lihb.babyvoice.model;

/**
 * Created by lihb on 2017/4/23.
 * 孕婴圈文章
 */

public class Article {

    public int id;
    public String title;
    public String content;
    public String realName;
    public int type;
    public String attachment;
    public String comments;
    public long time;
    public int totalCounts;

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", realName='" + realName + '\'' +
                ", type=" + type +
                ", attachment='" + attachment + '\'' +
                ", comments='" + comments + '\'' +
                ", time=" + time +
                ", totalCounts=" + totalCounts +
                '}';
    }
}
