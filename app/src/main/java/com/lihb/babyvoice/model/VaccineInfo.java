package com.lihb.babyvoice.model;

/**
 * Created by lhb on 2017/3/6.
 */

public class VaccineInfo {

    /**
     * 疫苗名称
     */
    public String vaccineName;

    /**
     * 是否免费
     */
    public boolean isFree;

    /**
     * 是否已经注射
     */
    public boolean isInjected;

    /**
     * 注射日期
     */
    public String injectDate;

    /**
     * 注射该疫苗时，小孩所需年龄
     */
    public int ageToInject;

}
