package com.lihb.babyvoice.test;

/**
 * Created by lihb on 2017/6/18.
 */

public class Son extends Father{

    static {
        System.out.println("子类静态代码块");
    }

    {
        System.out.println("子类非静态代码块");
    }

    public Son(){
        System.out.println("子类构造函数");
    }

}
