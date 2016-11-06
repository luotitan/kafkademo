package com.kafka.leader;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * Created by luolang on 16/11/6.
 */
public class LeaderElect {
	private String watchedNodePath = "/chroot/leader";


	public static void main(String[] args) throws KeeperException, InterruptedException, IOException {
		ZooKeeper zk = new ZooKeeper("localhost:2181", 6000, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				System.out.println("Stat :" + event.getState());
			}
		});


		//创建以后返回自己的zk路径
		String OwnPath = zk.create("/chroot/leader", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);


		List<String> childNodes = null;

		try {
			childNodes = zk.getChildren("/chroot", false);
		} catch (KeeperException | InterruptedException e) {
			throw new IllegalStateException(e);
		}


		//子节点排序
		Collections.sort(childNodes);
		String NowLeaderPath = childNodes.get(0);

		System.out.println("nowpath = " + NowLeaderPath);


		//方法1

		Stat stat1 = zk.exists(NowLeaderPath, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				System.out.println(event.getPath() + " | " + event.getType().name());

				if (NowLeaderPath.equalsIgnoreCase(OwnPath)) {
					System.out.println("当前节点选举成功!");
				} else {
					try {

						System.out.println("当前节点选举失败!");
						//回调
						zk.exists(NowLeaderPath, this);
					} catch (KeeperException | InterruptedException e) {
						// TODO: 16/11/6
						//等待失败,重新启动监听
						try {
							zk.exists(NowLeaderPath, this);
						} catch (KeeperException e1) {
							e1.printStackTrace();
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}

			}
		});


		//方法2 阻塞式

		Object locker = new Object();
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					// 等待主线程获取锁
					Thread.sleep(10000);

					// 请求locker对象的内部锁
					synchronized (locker) {
						try {
							zk.exists(NowLeaderPath, new Watcher(){

								@Override
								public void process(WatchedEvent event) {
									try {
										List<String> childNodesth = zk.getChildren("/chroot", false);
										Collections.sort(childNodesth);
										String NowLeaderPathth = childNodesth.get(0);

										if (NowLeaderPathth.equalsIgnoreCase(OwnPath)) System.out.println("当前节点选举成功!");
									} catch (KeeperException e) {
										e.printStackTrace();
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							});
						} catch (KeeperException e) {
							e.printStackTrace();
						}

					}
				} catch (InterruptedException e) {
					// TODO: handle exception
				}
			}
		}, "Blocked Thread");
		thread.start();


		Thread.sleep(1000000);
		zk.close();

	}


}
