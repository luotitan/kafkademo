package com.kafka.leader;

import java.io.IOException;

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
public class Create {
	public static void main(String[] args) throws KeeperException, InterruptedException, IOException {
		ZooKeeper zk = new ZooKeeper("localhost:2181", 6000, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				System.out.println("Stat :" + event.getState());
			}
		});
		String zkpath = zk.create("/chroot/leader",null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println(zkpath);
//		Thread.sleep(5000);
//		zk.create("/chroot/leader",null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
//		Thread.sleep(5000);
//		zk.create("/chroot/leader",null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

		Stat stat1 = zk.exists("/chroot", new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				System.out.println(event.getPath() + " | " + event.getType().name());
				try {
					zk.exists("/chroot",this);
				}catch (KeeperException | InterruptedException e){

				}
			}
		});

//		Watcher watcher = event -> {
//			System.out.println(event.getPath() + " | " + event.getType().name());
////			System.out.println(event.getPath() + " | " + event.getType().name());
//		};
//		zk.exists("/chroot",watcher);
		Thread.sleep(1000000);
		zk.close();

	}
}
