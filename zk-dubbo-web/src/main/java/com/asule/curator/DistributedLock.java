package com.asule.curator;


import com.asule.controller.CulsterServiceImpl;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * 实现分布式锁的工具类
 */
public class DistributedLock {

    //挂起当前请求，等待锁的释放，再放行
    private static CountDownLatch countDownLatch=new CountDownLatch(1);

    private static final String ZK_LOCK_PROJECT="asule-locks";

    private static final String DISTRIBUTE_LOCK="distribute-lock";


    final static Logger log = LoggerFactory.getLogger(DistributedLock.class);



    public CuratorFramework client = null;

    public DistributedLock(CuratorFramework client) {
        this.client = client;
    }


    public void  init(){
        this.client.usingNamespace("zkLocks-Namespace");
        try {
            if (client.checkExists().forPath("/"+ZK_LOCK_PROJECT)==null){
                client.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath("/"+ZK_LOCK_PROJECT);
            }
            addWatcherToLock("/"+ZK_LOCK_PROJECT);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("curator客户端连接zookeeper失败");
        }
    }


    public void getLock(){
        //当获取锁时，会创建一个临时节点。当下一个请求来时，若该临时节点存在(未删除)，则会处于阻塞状态，
        //直到等待上一个请求删除该节点后，才会跳出该循环
        while (true){
            try {
                client.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath("/"+ZK_LOCK_PROJECT+"/"+DISTRIBUTE_LOCK);
                return;
            } catch (Exception e) {
                try {
                    //CountDownLatch的计数归0后，需要重新创建CountDownLatch
                    if (countDownLatch.getCount()<=0){
                        countDownLatch=new CountDownLatch(1);
                    }
                    countDownLatch.await();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }


    public boolean releaseLock(){
        try {
            if (client.checkExists().forPath("/"+ZK_LOCK_PROJECT+"/"+DISTRIBUTE_LOCK)!=null){
                client.delete()
                        .guaranteed()
                        .forPath("/"+ZK_LOCK_PROJECT+"/"+DISTRIBUTE_LOCK);
            }
        } catch (Exception e) {
            log.info("删除节点失败....");
            return false;
        }
        log.info("删除节点成功....");
        return true;
    }



    private void addWatcherToLock(String path) throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(this.client, path, true);
        pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework,
                                   PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                if (pathChildrenCacheEvent.getType()==PathChildrenCacheEvent.Type.CHILD_REMOVED){
                    log.info("已经释放锁，删除节点");
                    String childpath = pathChildrenCacheEvent.getData().getPath();

                    log.info("该临时节点为:"+childpath);

                    if (childpath.contains(DISTRIBUTE_LOCK)){
                        countDownLatch.countDown();
                    }
                }
            }
        });

    }


}
