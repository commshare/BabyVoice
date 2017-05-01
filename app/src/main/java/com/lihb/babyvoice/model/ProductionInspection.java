package com.lihb.babyvoice.model;

/**
 * Created by lhb on 2017/3/7.
 */

public class ProductionInspection {


    /**
     * 第几次
     */
    public int no;
    /**
     * 事项编号
     */
    public int event_id;
    /**
     * 产检名称
     */
    public String event_name;

    /**
     * 产检名称
     */
    public String event_name_en;
    /**
     * 第几周
     */
    public String week;
    /**
     * 是否已做
     */
    public int isDone;

    @Override
    public String toString() {
        return "ProductionInspection{" +
                "no=" + no +
                ", event_id=" + event_id +
                ", event_name='" + event_name + '\'' +
                ", week="+ week +
                ", isDone=" + isDone +
                '}';
    }
}
