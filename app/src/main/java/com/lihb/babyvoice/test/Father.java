package com.lihb.babyvoice.test;

/**
 * Created by lihb on 2017/6/18.
 */

public class Father {

    static {
        System.out.println("父类静态代码块");
    }

    {
        System.out.println("父类非静态代码块");
    }

    public Father(){
        System.out.println("父类构造函数");
    }

}
