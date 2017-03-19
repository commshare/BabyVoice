package com.lihb.babyvoice.model;

/**
 * Created by lihb on 2017/3/11.
 */

public class HealthQuota {

    /**
     * 头围
     */
    public int headSize;

    /**
     * 身高
     */
    public int height;

    /**
     * 体重
     */
    public int weight;

    /**
     * 体温
     */
    public int temperature;

    /**
     * 性别
     */
    public String gender;

    /**
     * 心跳
     */
    public int heartBeat;

    /**
     * 囟门尺寸
     */
    public int fontanelSize;

    /**
     * 检测结果
     */
    public String examineResult;

    public HealthQuota() {
    }

    public HealthQuota(int headSize, int height, int weight, int temperature, String gender, int heartBeat, int fontanelSize) {
        this.headSize = headSize;
        this.height = height;
        this.weight = weight;
        this.temperature = temperature;
        this.gender = gender;
        this.heartBeat = heartBeat;
        this.fontanelSize = fontanelSize;
        this.examineResult = examineResult;
    }
}
