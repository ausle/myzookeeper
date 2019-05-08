package com.aule.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;

import java.util.ArrayList;
import java.util.List;

public class CuratorAcl {


    public CuratorFramework client = null;
    public static final String zkServerPath = "192.168.98.130:2181";

    public CuratorAcl() {
        RetryPolicy retryPolicy = new RetryNTimes(3, 5000);
        client = CuratorFrameworkFactory.builder()
                //创建客户端时，就填写上要身份验证的信息
                .authorization("digest", "asule1:123456".getBytes())
                .connectString(zkServerPath)
                .sessionTimeoutMs(10000).retryPolicy(retryPolicy)
                .namespace("wangjing").build();
        client.start();
    }

    public static void main(String[] args) throws Exception{

        CuratorAcl curatorAcl = new CuratorAcl();
        boolean isZkCuratorStarted = curatorAcl.client.isStarted();
        System.out.println("当前客户的状态：" + (isZkCuratorStarted ? "连接中" : "已关闭"));



        String nodePath="/acl/father/aa";
        List<ACL> acls = new ArrayList<ACL>();
        Id imooc1 = new Id("digest", AclUtils.getDigestUserPwd("asule0:123456"));
        Id imooc2 = new Id("digest", AclUtils.getDigestUserPwd("asule1:123456"));
        acls.add(new ACL(ZooDefs.Perms.ALL, imooc1));
        acls.add(new ACL(ZooDefs.Perms.READ, imooc2));
        acls.add(new ACL(ZooDefs.Perms.DELETE | ZooDefs.Perms.CREATE, imooc2));


        curatorAcl.client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .withACL(acls,true)             //递归的设置ACL，一般不会这么做!
                .forPath(nodePath,"蝙蝠侠".getBytes());
    }


}
