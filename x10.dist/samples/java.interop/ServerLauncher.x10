/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */


import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import x10.interop.Java;


/*
 * An example to illustrate how to implement Java interface in X10.
 *
 * Just execute and kill this program in different console several times
 * and see ZooKeeper notifies the addition and the deletion of your process to the all live processes. 
 * 
 * This program assumes ZooKeeper server is up and running at localhost:2181
 * and zookeeper-3.3.6.jar is in your classpath.

 * Compile as "x10c -cp zookeeper-3.3.6.jar ServerLauncher.x10"
 * Run as "x10 -cp .:zookeeper-3.3.6.jar:log4j-1.2.15.jar ServerLauncher [zooServer]"
 */
public class ServerLauncher {

    static class ChildrenChangedWatcher implements Watcher {
        val zk:ZooKeeper;
        val cb:DumpChildrenCallback;
        def this(zk:ZooKeeper) {
            this.zk = zk;
            this.cb = new DumpChildrenCallback(zk);
        }
        public def process(event:WatchedEvent):void {
            try {
                val path = event.getPath();
                // N.B. X10 doesn't support switch on enums
                /*
                switch (event.getType()) {
                case Watcher.Event.EventType.NodeChildrenChanged:
                    // Note: NodeChildrenChanged event is triggered only for the change of *direct* children                    
                    Console.OUT.println("Membership has been changed.");

                    // TODO implement recovery process.
                    // ideally, in case of node failure or addition of a new node, we will change request routing for load balancing.
                    // for the first demo, we just terminate all x10 places and restart the cluster.
                    // for now, we just dump all children recursively.

                    // recursively dump children
                    zk.getChildren(path, this, cb, null);

                    break;
                case Watcher.Event.EventType.NodeDeleted:
                    // this is the event of deletion of parent node.
                    // since all children node no longer exist, we don't need to register the watcher again.
                    break;
                default:
                    Console.OUT.println("Unknown event occurred: " + event);
                    assert false : "should not happen";
                    zk.getChildren(path, this); // register the watcher again
                    break;
                }
                */
                val eventType = event.getType();
                if (Watcher.Event.EventType.NodeChildrenChanged.equals(eventType)) {
                    // Note: NodeChildrenChanged event is triggered only for the change of *direct* children                    
                    Console.OUT.println("Membership has been changed.");

                    // TODO implement recovery process.
                    // ideally, in case of node failure or addition of a new node, we will change request routing for load balancing.
                    // for the first demo, we just terminate all x10 places and restart the cluster.
                    // for now, we just dump all children recursively.

                    // recursively dump children
                    zk.getChildren(path, this, cb, null);
                }
                else if (Watcher.Event.EventType.NodeDeleted.equals(eventType)) {
                    // this is the event of deletion of parent node.
                    // since all children node no longer exist, we don't need to register the watcher again.
                }
                else {
                    Console.OUT.println("Unknown event occurred: " + event);
                    assert false : "should not happen";
                    zk.getChildren(path, this); // register the watcher again
                }
            } catch (e:CheckedException) {
                e.printStackTrace();
            }
        }
    }

    static class DumpChildrenCallback implements AsyncCallback.ChildrenCallback {
        val zk:ZooKeeper;
        def this(zk:ZooKeeper) {
            this.zk = zk;
        }
        public def processResult(rc:Int, path:String, ctx:Any, children:java.util.List):void {
            var parent:String = path;
            // remove trailing slash
            if (parent.endsWith("/")) parent = parent.substring(0n, parent.length()-1n);
            val childStat = new Stat();
            try {
                for (_childAny in children) {
                    val _child = _childAny as String;
                    val child = parent + "/" + _child;
                    val childData = Java.convert(zk.getData(child, false, childStat));
                    Console.OUT.println(child + " : " + new String(childData));
                    // recursively dump children
                    zk.getChildren(child, false, this, null);
                }
            } catch (e:CheckedException) {
                e.printStackTrace();
            }
        }
    }


    static val PROJECT_ROOT = "/" + java.lang.System.getProperty("user.name", "nobody");
    static val PROJECT_SERVER_ROOT = PROJECT_ROOT + "/servers";
    static val PROJECT_SERVER_NAME_PREFIX = "/server";

    static val PROJECT_ZOO_SERVER_DEFAULT = "localhost:2181";
    static val PROJECT_ZOO_TIMEOUT = 1000n;

    public static def main(args:Rail[String]) throws CheckedException :void {
        val PROJECT_ZOO_SERVER = args.size > 0 ? args(0) : PROJECT_ZOO_SERVER_DEFAULT;

        ensureRootExists(PROJECT_ZOO_SERVER);

        val zk = new ZooKeeper(PROJECT_ZOO_SERVER + PROJECT_SERVER_ROOT, PROJECT_ZOO_TIMEOUT, null);
        Console.OUT.println("Connected to ZooKeeper server at " + PROJECT_ZOO_SERVER);

        // monitor the change of children
        zk.getChildren("/", new ChildrenChangedWatcher(zk));

        val name = x10.xrx.Runtime.getName(); // pid@hostname
        val split = name.split("@");      
        val data = "host=" + split(1) + ", pid=" + split(0);
        // create an ephemeral node representing this Java VM
        val newNode = zk.create(PROJECT_SERVER_NAME_PREFIX, Java.convert(data.bytes()), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        Console.OUT.println(newNode + " has been created");


        // TODO call normal x10 main here.
        //Server.$Main.main(args);
        // for now, just create another thread and sleep forever.
        finish {
            async {
                while (true) {
                    // do something wonderful
                    // N.B. use System.threadSleep instead of System.sleep to avoid busy loop in x10 runtime.
                    //System.sleep(1024L);
                    System.threadSleep(1024L);
                }
            }
        }


        zk.close();
    }

    /*
     * do "mkdir -p PROJECT_SERVER_ROOT"
     */
    static def ensureRootExists(zooServer:String) throws CheckedException :void {
        val zk = new ZooKeeper(zooServer, PROJECT_ZOO_TIMEOUT, null);
        if (zk.exists(PROJECT_ROOT, false) == null) {
            zk.create(PROJECT_ROOT, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        if (zk.exists(PROJECT_SERVER_ROOT, false) == null) {
            zk.create(PROJECT_SERVER_ROOT, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        zk.close();
    }

}
