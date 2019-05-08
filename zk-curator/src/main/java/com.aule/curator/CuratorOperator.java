package com.aule.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.*;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.util.List;

public class CuratorOperator {

    public CuratorFramework client = null;
    public static final String zkServerPath = "192.168.98.130:2181";

    public CuratorOperator() {

        /*
            curator连接zookeeper的策略有下面几种：
        */

        /*
            ExponentialBackoffRetry
                baseSleepTimeMs：初始sleep的时间
		        maxRetries：最大重试次数
		        maxSleepMs：最大重试时间
        */
        new ExponentialBackoffRetry(1000,5);


        /*
            RetryNTimes
                n,重试的次数
                sleepMsBetweenRetries，每次重试的间隔
        */
        RetryPolicy retryPolicy = new RetryNTimes(3, 5000);


        /*
            RetryOneTime，只重试一次
                sleepMsBetweenRetry,每次重试的时间
        */
        new RetryOneTime(5000);

        //RetryForever，永远重试
//        new RetryForever(retryIntervalMs);


        /*
            RetryUntilElapsed
                maxElapsedTimeMs,最大重试时间
                sleepMsBetweenRetries,每次重试间隔
                超过最大重试时间后，就不再尝试连接
        */
//        new RetryUntilElapsed(5000,1000);


        client=CuratorFrameworkFactory.builder()
                .connectString(zkServerPath)
                .sessionTimeoutMs(4000)
                .retryPolicy(retryPolicy)
                .namespace("wangjing")
                .build();
        client.start();
    }


    public static void main(String[] args) throws Exception{
        CuratorOperator cto = new CuratorOperator();
        boolean isZkCuratorStarted = cto.client.isStarted();
        System.out.println("当前客户的状态：" + (isZkCuratorStarted ? "连接中" : "已关闭"));



//        创建节点
//        cto.client.create()
//                .creatingParentsIfNeeded()
//                .withMode(CreateMode.PERSISTENT)
//                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
//                .forPath("/aa/cc","aa-cc".getBytes());
//
        //修改数据
//        cto.client.setData()
//                .withVersion(0)
//                .forPath("/aa/bb/cc","aa-bb-cc".getBytes());

        //删除节点
//        cto.client.delete()
//                .guaranteed()               //如果删除失败，那么在后端还是继续会删除，直到成功
//                .deletingChildrenIfNeeded()   //如果不添加该方法，那么无法删除有子节点的节点
//                .withVersion(0)
//                .forPath("/aa");


        //读取节点数据
//        Stat stat = new Stat();
//        byte[] bytes = cto.client.getData()
//                .storingStatIn(stat)
//                .forPath("/aa/cc");
//
//        System.out.println("读取的节点数据为："+new String(bytes));
//        System.out.println("当前节点的数据版本为："+stat.getVersion());


        //查询子节点
//        List<String> list =
//                cto.client.getChildren().forPath("/aa");
//        for (String node:list){
//            System.out.println("该父节点的子节点有："+node);
//        }

        //判断子节点是否存在
//        Stat stat = cto.client.checkExists().forPath("/aa/cc");
//        System.out.println(stat==null?"该节点不存在":"该节点存在");


        //为节点添加watcher
        //使用usingWatcher添加的watcher事件，只监听一次。且不监听该节点子节点的状态
        /*cto.client.checkExists().usingWatcher(new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getType()== Event.EventType.NodeChildrenChanged){

                }else if (watchedEvent.getType()== Event.EventType.NodeDataChanged){

                }else if (watchedEvent.getType()== Event.EventType.NodeDeleted){

                }else if (watchedEvent.getType()== Event.EventType.NodeCreated){

                }
                System.out.println("watcher事件触发，节点路径为："+watchedEvent.getPath());
            }
        }).forPath("/aa");

        cto.client.checkExists().usingWatcher(new CuratorWatcher() {
            @Override
            public void process(WatchedEvent watchedEvent) throws Exception {
                System.out.println("curator-watcher事件触发，节点路径为："+watchedEvent.getPath());

            }
        }).forPath("/aa");*/


        //为节点添加watcher，添加一次watcher，每次都会触发
//        String nodePath="/aa";
//        NodeCache nodeCache = new NodeCache(cto.client, nodePath);
//        //buildInitial为true，初始化时获取node的值并缓存
//        nodeCache.start(true);
//        if (nodeCache.getCurrentData()==null){
//            System.out.println("节点数据为空");
//        }else{
//            System.out.println("节点数据为："+new String(nodeCache.getCurrentData().getData()));
//        }
//        nodeCache.getListenable().addListener(new NodeCacheListener() {
//            @Override
//            public void nodeChanged() throws Exception {
//                if (nodeCache.getCurrentData()==null){
//                    System.out.println("节点数据为空");
//                    return;
//                }
//                System.out.println("节点数据为："+new String(nodeCache.getCurrentData().getData()));
//            }
//        });


        //为子节点添加watcher事件
        String childNodePath="/aa";
        //cacheData：缓存节点的数据
        PathChildrenCache pathChildrenCache = new PathChildrenCache(cto.client, childNodePath, true);
        /**
         * StartMode: 初始化方式
         *      POST_INITIALIZED_EVENT：异步初始化，初始化之后会触发watcher事件
         *      NORMAL：异步初始化
         *      BUILD_INITIAL_CACHE：同步初始化
         */
        pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

        //输出子节点path与数据
        List<ChildData> childDatas = pathChildrenCache.getCurrentData();
        for (ChildData data:childDatas){
            System.out.println("子节点数据："+new String(data.getData())+"子节点路径为："+data.getPath());
        }

        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework,
                                   PathChildrenCacheEvent event) throws Exception {
                if(event.getType().equals(PathChildrenCacheEvent.Type.INITIALIZED)){
                    System.out.println("子节点初始化ok...");
                }

                else if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)){
                    String path = event.getData().getPath();
                    System.out.println("添加的子节点的路径为："+path);

                }else if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)){
                    System.out.println("删除子节点:" + event.getData().getPath());
                }else if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)){
                    System.out.println("修改子节点路径:" + event.getData().getPath());
                    System.out.println("修改子节点数据:" + new String(event.getData().getData()));
                }
            }
        });



        Thread.sleep(100000);
    }



    /**
     * 关闭ZK客户端
     */
    public void closeZKClient() {
        if (client != null) {
            this.client.close();
        }
    }
}
