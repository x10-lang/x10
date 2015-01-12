/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */
package x10.xrx;

import x10.compiler.*;
import x10.util.HashMap;
import x10.util.concurrent.SimpleLatch;

public class ResilientStorePlace0[K,V] {V haszero} extends ResilientStore[K,V] {
    private static val verbose = ResilientStore.verbose;
    
    static type KLUDGE = Any{KLUDGE haszero};
    private static val ALL = (here.id==0) ? new HashMap[Any,KLUDGE]() : null;
    
    @NonEscaping private val root:GlobalRef[ResilientStorePlace0[K,V]];
    private def getMe() = root.getLocalOrCopy(); // should be called at place 0
    
    private def this() {
        assert here.id==0;
        root = GlobalRef[ResilientStorePlace0[K,V]](this);
    }
    
    public static def make[K,V](name:Any){V haszero}:ResilientStorePlace0[K,V] {
        if (verbose>=3) debug("ResilientStorePlace0.make called, name="+name);
        val r = Runtime.evalImmediateAt[ResilientStorePlace0[K,V]](Place(0), ()=>{
            var rs:ResilientStorePlace0[K,V];
            atomic {
                rs = ALL.getOrElse(name, null) as ResilientStorePlace0[K,V];
                if (rs == null) {
                    rs = new ResilientStorePlace0[K,V]();
                    ALL.put(name, rs);
                }
            }
            return rs;
        });
        if (verbose>=3) debug("ResilientStorePlace0.make returning result="+r);
        return r;
    }
    
    public static def delete(name:Any):void {
        if (verbose>=3) debug("delete called, name="+name);
        Runtime.runImmediateAt(Place(0), ()=>{ atomic { ALL.delete(name); } });
        if (verbose>=3) debug("delete returning");
    }
    
    private transient val hm:HashMap[K,V] = new HashMap[K,V]();
    
    public def create(key:K, value:V):void { put(key, value); }
    public def put(key:K, value:V):void {
        if (verbose>=3) debug("put called, key="+key + " value="+value);
        Runtime.runImmediateAt(root.home, ()=>{ atomic { getMe().hm.put(key, value); } });
        if (verbose>=3) debug("put returning");
    }
    
    public def getOrElse(key:K, orelse:V):V {
        if (verbose>=3) debug("getOrElse called, key="+key);
        val result = new Cell[V](orelse);
        val r = Runtime.evalImmediateAt[V](root.home, ()=>{
            val v:V; atomic { v = getMe().hm.getOrElse(key, orelse); } return v;
        });
        if (verbose>=3) debug("getOrElse returning, result="+r);
        return r;
    }
    
    public def remove(key:K):void {
        Runtime.runImmediateAt(root.home, ()=>{ atomic { getMe().hm.remove(key); } });
    }
    
    // private transient val lk:SimpleLatch = new SimpleLatch();
    // 
    // public def lock():void {
    //     if (verbose>=3) debug("lock called");
    //     Runtime.runImmediateAt(root.home, ()=>{ getMe().lk.lock(); }); // blocking op should not be used
    //     if (verbose>=3) debug("lock returning (locked)");
    // }
    // 
    // public def unlock():void {
    //     if (verbose>=3) debug("unlock called");
    //     Runtime.runImmediateAt(root.home, ()=>{ getMe().lk.unlock(); });
    //     if (verbose>=3) debug("unlock returning (unlocked)");
    // }
    
    /*
     * Lock/unlock mechanism without blocking inside Runtime.runImmediateAt
     * Should be used inside atomic
     */
    private static class MyQueue[E] {
        private static class Entry[E] { val e:E; var next:Entry[E] = null; def this(e:E) { this.e = e; } }
        private var head:Entry[E] = null, tail:Entry[E] = null;
        private var size:Long = 0;
        def size():Long = size;
        def add(e:E):Long { // returns old size
            val entry = new Entry[E](e);
            if (tail == null) head = entry; else tail.next = entry;
            tail = entry;
            val oldSize = size++;
            return oldSize;
        }
        def remove():E {
            assert size>0;
            val entry = head; // should not be null
            head = entry.next; if (head == null) tail = null;
            size--;
            return entry.e;
        }
        def peek():E {
            assert size>0;
            val entry = head; // should not be null
            return entry.e;
        }
    }
    
    private static class MyLatch extends SimpleLatch implements Runtime.Mortal { }
    
    private transient val lockQueue:MyQueue[GlobalRef[MyLatch]] = new MyQueue[GlobalRef[MyLatch]]();
    
    public def lock():void { //TODO: should support recursive lock?
        if (verbose>=3) debug("lock called");
        val latch = new MyLatch(), gLatch = GlobalRef[MyLatch](latch);
        val needWait = Runtime.evalImmediateAt[Boolean](root.home, ()=>{
            val me = getMe();
            var oldSize:Long;
            atomic { oldSize = me.lockQueue.add(gLatch); }
            return (oldSize > 0);
        });
        if (needWait) {
            if (verbose>=3) debug("lock waiting gLatch="+gLatch);
            latch.await();
            if (verbose>=2) debug("lock waited gLatch="+gLatch);
        } else {
            if (verbose>=3) debug("lock need not wait");
        }
        if (verbose>=3) debug("lock returning (locked)");
    }
    
    public def unlock():void {
        if (verbose>=3) debug("unlock called");
        Runtime.runImmediateAt(root.home, ()=>{
            val me = getMe();
            var gLatch:GlobalRef[MyLatch] = GlobalRef(null as MyLatch);
            atomic {
                me.lockQueue.remove(); // remove the current gLatch  TODO: racing with notifyPlaceDeath?
                while (me.lockQueue.size() > 0) {
                    val g = me.lockQueue.peek();
                    if (!g.home.isDead()) { gLatch = g; break; }
                    if (verbose>=3) debug("unlock skipping deadPlace gLatch="+gLatch);
                    me.lockQueue.remove(); // remove the gLatch at dead place
                }
            }
            if (gLatch.isNull()) { // no living waiter
                if (verbose>=3) debug("unlock no living waiter");
                return;
            }
            if (verbose>=3) debug("unlock need to release gLatch="+gLatch);
            val g = gLatch;
            at (g.home) @Immediate("unlock_gLatch_release") async {
                if (verbose>=3) debug("unlock releasing gLatch="+g);
                g.getLocalOrCopy().release();
                if (verbose>=3) debug("unlock released gLatch="+g);
            }
        });
        if (verbose>=3) debug("unlock returning (unlocked)");
    }
    
    public def notifyPlaceDeath():void { // called from the lock user (TOOD: change this)
        if (verbose>=3) debug("notifyPlaceDeath called");
        if (root.home != here) {
            if (verbose>=3) debug("not my place");
        } else { // Place0
            val me = getMe();
            atomic {
                if (me.lockQueue.size() > 0) {
                    val gLatch = me.lockQueue.peek();
                    val lockerPlace = gLatch.home;
                    if (verbose>=3) debug("lockerPlace=" + lockerPlace);
                    if (lockerPlace.isDead()) {
                        if (verbose>=3) debug("forcing unlock because lockerPlace is dead");
                        unlock();
                        if (verbose>=3) debug("forced unlock");
                    }
                }
            }
        }
        if (verbose>=3) debug("notifyPlaceDeath returning");
    }
    
    // public def unlock():void {
    //     if (verbose>=3) debug("unlock called");
    //     val toRelease = Runtime.evalImmediateAt[GlobalRef[MyLatch]](root.home, ()=>{
    //         val me = getMe();
    //         var gLatch:GlobalRef[MyLatch] = GlobalRef(null as MyLatch);
    //         atomic {
    //             if (--me.waitCount >= 0) gLatch = me.waitQueue.remove();
    //         }
    //         return gLatch;
    //     });
    //     if (!gLatch.isNull()) {
    //         if (verbose>=3) debug("unlock need to release gLatch="+gLatch);
    //         val g = gLatch;
    //         at (g.home) @Immediate("unlock_gLatch_release") async {
    //             if (verbose>=3) debug("unlock releasing gLatch="+g);
    //             g.getLocalOrCopy().release();
    //             if (verbose>=3) debug("unlock released gLatch="+g);
    //         }
    //     }
    //     if (verbose>=3) debug("unlock returning (unlocked)");
    // }
}
