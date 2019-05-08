package com.asule.starter;

import com.asule.utils.AclUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZKConnect implements Watcher{

    final static Logger log = LoggerFactory.getLogger(ZKConnect.class);

    public static final Integer timeout = 5000;


    public static final String zkServerPath = "192.168.98.130:2181";

    //多个ip代表集群
//	public static final String zkServerPath = "192.168.98.130:2181,192.168.98.129:2181,192.168.98.128:2181";


    public static void main(String[] args) throws Exception {
//        method0();
//        method1();
//        method2();
//        method3();
//        method4();
//        method5();
        method6();

    }




    //建立客户端与ZK服务端的连接
    private static void method0() throws IOException, InterruptedException {
    /*
        建立客户端与ZK服务端进行连接。该连接是一个异步的过程，需要主线程休眠，才能获取服务端的watcher事件。
    */
        ZooKeeper zk = new ZooKeeper(zkServerPath, timeout, new ZKConnect());

        log.warn("客户端开始连接zookeeper服务器...");
        log.warn("连接状态：{}", zk.getState());

        new Thread().sleep(2000);

        log.warn("连接状态：{}", zk.getState());
    }


    //zk会话重连
    private static void method1() throws IOException, InterruptedException {

        ZooKeeper zk = new ZooKeeper(zkServerPath, timeout, new ZKConnect());

        log.warn("客户端开始连接zookeeper服务器...");
        log.warn("连接状态：{}", zk.getState());
        new Thread().sleep(2000);
        log.warn("连接状态：{}", zk.getState());


        long sessionId = zk.getSessionId();

        //把sessionId转换为16进制，与zk中的sessionId进行比较
        String toHexSessionId= Long.toHexString(sessionId);
        System.out.println(toHexSessionId+"   "+zk.getSessionId());


        byte[] sessionPasswd = zk.getSessionPasswd();


        //利用zk进行会话重连
        ZooKeeper zooKeeper = new ZooKeeper(zkServerPath, timeout, new ZKConnect(), sessionId, sessionPasswd);

        log.warn("重新连接状态zkSession：{}", zooKeeper.getState());
        new Thread().sleep(1000);
        log.warn("重新连接状态zkSession：{}", zooKeeper.getState());

    }

    //同步异步创建节点，修改/删除节点数据
    private static void method2() throws Exception {

        ZooKeeper zooKeeper = new ZooKeeper(zkServerPath, timeout, new ZKConnect());

        //同步方式创建节点
        //指定节点的路径，数据，ACL权限(schme:id:permission)
//        zooKeeper.create("/testNode","testNode".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//        new Thread().sleep(1000);

        //同步修改节点数据，其cversion要和节点一致。否则修改失败。对应的是dataversion
        Stat stat = zooKeeper.setData("/testNode", "testNodeSync---ojbk---wang".getBytes(), 3);
        //删除该节点，若版本不一致会删除失败。删除完后是没有通知的。若要有通知，可以异步的删除
        zooKeeper.delete("/testNode",stat.getVersion());
        System.out.println(stat.getVersion());


        //异步方式创建节点
//        String ctx = "{'delete':'success'}";
//        zooKeeper.create("/testNodeAync", "testNode".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT
//                , new BaseCallBack(),ctx);

        new Thread().sleep(1000);
    }




    //获取节点数据
    private static void method3() throws Exception {
        ZooKeeper zooKeeper = new ZooKeeper(zkServerPath, timeout, new ZKConnect());

        byte[] data = zooKeeper.getData("/testNodeAync", true, new Stat());
        String result = new String(data);
        System.out.println("当前值:" + result);
        countDown.await();
    }


    //获取子节点数据
    private static void method4() throws Exception {
        ZooKeeper zooKeeper = new ZooKeeper(zkServerPath, timeout, new ZKConnect());
        List<String> childrens = zooKeeper.getChildren("/asule", true);

        for (String children:childrens){
            log.warn("子节点为：{}",children);
        }
        countDown.await();
    }


    //判断zk节点是否存在
    private static void method5() throws Exception {
        ZooKeeper zooKeeper = new ZooKeeper(zkServerPath, timeout, new ZKConnect());
        Stat exists = zooKeeper.exists("/asule/dd", true);
        if (exists==null){
            System.out.println("节点不存在");
        }else{
            System.out.println("数据版本号为："+exists.getVersion());
        }
        countDown.await();
    }

    private static CountDownLatch countDown = new CountDownLatch(1);

    private static void method6() throws Exception{
        ZooKeeper zooKeeper = new ZooKeeper(zkServerPath, timeout, new ZKConnect());


        //该节点任何人都可以访问
//        zooKeeper.create("/testacl","testacl".getBytes(),
//                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);


        //自定义用户认证访问
        List<ACL> acls=new ArrayList<>();
//        Id digest0 = new Id("digest", AclUtils.getDigestUserPwd("ausle0:123456"));
//        Id digest1 = new Id("digest", AclUtils.getDigestUserPwd("ausle1:123456"));
//        ACL acl0 = new ACL(ZooDefs.Perms.ALL, digest0);
//        ACL acl1 = new ACL(ZooDefs.Perms.READ, digest1);
//        acls.add(acl0);
//        acls.add(acl1);
//        zooKeeper.create("/testacl/aa","aa".getBytes(),acls,CreateMode.PERSISTENT);

        //添加ip地址为ACL权限
        Id ip = new Id("ip", "192.168.199.242");
        acls.add(new ACL(ZooDefs.Perms.ALL,ip));
        //创建节点成功则说明满足ip要求
//        zooKeeper.create("/testacl/ip","ip".getBytes(),acls,CreateMode.PERSISTENT);
        Stat stat = zooKeeper.setData("/testacl/ip", "ip-192.168.199.242".getBytes(), 0);



        //验证用户的ACL权限
//        zooKeeper.addAuthInfo("digest","ausle0:123456".getBytes());
////        byte[] data = zooKeeper.getData("/testacl/aa", true, new Stat());
//        Stat stat = zooKeeper.setData("/testacl/aa", "bb".getBytes(), 0);
//        System.out.println("  "+stat.getVersion());


    }


    @Override
    public void process(WatchedEvent event) {
        try {
            if(event.getType() == Event.EventType.NodeChildrenChanged){
                System.out.println("子节点状态发生改变");
                countDown.countDown();
            }
            else if(event.getType() == Event.EventType.NodeDataChanged){
                System.out.println("节点数据已经改变");
                countDown.countDown();
            } else if(event.getType() == Event.EventType.NodeCreated) {

            } else if(event.getType() == Event.EventType.NodeChildrenChanged) {

            } else if(event.getType() == Event.EventType.NodeDeleted) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
