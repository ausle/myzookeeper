package com.asule.curator;

import org.apache.curator.framework.CuratorFramework;

public class ZKCurator{

    public CuratorFramework client = null;

    public ZKCurator(CuratorFramework client) {
        this.client = client;
    }


    public  void  init(){
        client.usingNamespace("");
    }


    public boolean isZKALive(){
        return client.isStarted();
    }
}
