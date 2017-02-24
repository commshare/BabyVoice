package com.lihb.babyvoice.model;

import java.util.List;

/**
 * Created by lhb on 2017/2/22.
 */

public class HttpResList<T> {

    public int start;
    public int count;
    public int total;
    public List<T> dataList;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    @Override
    public String toString() {
        return "HttpResList{" +
                "start=" + start +
                ", count=" + count +
                ", total=" + total +
                ", dataList=" + dataList +
                '}';
    }
}
