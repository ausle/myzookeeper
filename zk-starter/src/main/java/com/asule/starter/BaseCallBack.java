package com.asule.starter;

import org.apache.zookeeper.AsyncCallback;

public class BaseCallBack implements AsyncCallback.StringCallback{

    @Override
    public void processResult(int i, String s, Object o, String s1) {

        System.out.println(s);//路径信息

        System.out.println(o);//ctx信息
    }
}
