package com.lihb.babyvoice.model;

/**
 * Created by lhb on 2017/2/20.
 */

public class BaseResponse<T> {
    public int code;
    public String msg;
    public T data;
}
