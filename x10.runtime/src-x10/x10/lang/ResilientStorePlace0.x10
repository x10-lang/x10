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
package x10.lang;
import x10.compiler.*;
import x10.util.HashMap;
import x10.util.concurrent.SimpleLatch;

class ResilientStorePlace0[K,V] extends ResilientStore[K,V] {
    private static val verbose = ResilientStore.verbose;
    
    private static def lowLevelSend(dst:Place, cl:()=>void) = FinishResilient.lowLevelSend(dst, cl);
    private static def lowLevelAt(dst:Place, cl:()=>void) = FinishResilient.lowLevelAt(dst, cl);
    private static def lowLevelFetch[T](dst:Place, result:Cell[T], cl:()=>T):Boolean = FinishResilient.lowLevelFetch[T](dst, result, cl);
    
    static val ALL = (here.id==0) ? new HashMap[Any,Any]() : null;
    
    @NonEscaping private val root:GlobalRef[ResilientStorePlace0[K,V]];
    private def getMe() = root.getLocalOrCopy(); // should be called at place 0
    
    private def this() {
        assert here.id==0;
        root = GlobalRef[ResilientStorePlace0[K,V]](this);
    }
    
    public static def make[K,V](name:Any):ResilientStorePlace0[K,V] {
        if (verbose>=3) debug("make called, name="+name);
        val result = new Cell[ResilientStorePlace0[K,V]](null);
        lowLevelFetch(Place(0), result, ()=>{
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
        val r = result();
        if (verbose>=3) debug("make returning result="+r);
        return r;
    }
    
    public static def delete(name:Any):void {
        if (verbose>=3) debug("delete called, name="+name);
        lowLevelAt(Place(0), ()=>{ atomic { ALL.remove(name); } });
        if (verbose>=3) debug("delete returning");
    }
    
    private transient val hm:HashMap[K,V] = new HashMap[K,V]();
    
    public def create(key:K, value:V):void { put(key, value); }
    public def put(key:K, value:V):void {
        if (verbose>=3) debug("put called, key="+key + " value="+value);
        lowLevelAt(root.home, ()=>{ atomic { getMe().hm.put(key, value); } });
        if (verbose>=3) debug("put returning");
    }
    
    public def getOrElse(key:K, orelse:V):V {
        if (verbose>=3) debug("getOrElse called, key="+key);
        val result = new Cell[V](orelse);
        lowLevelFetch(root.home, result, ()=>{
            val v:V; atomic { v = getMe().hm.getOrElse(key, orelse); } return v;
        });
        val r = result();
        if (verbose>=3) debug("getOrElse returning, result="+r);
        return r;
    }
    
    public def remove(key:K):void {
        lowLevelAt(root.home, ()=>{ atomic { getMe().hm.remove(key); } });
    }
    
    // private transient val lk:SimpleLatch = new SimpleLatch();
    // 
    // public def lock():void {
    //     if (verbose>=3) debug("lock called");
    //     lowLevelAt(root.home, ()=>{ getMe().lk.lock(); }); // blocking op should not be used
    //     if (verbose>=3) debug("lock returning (locked)");
    // }
    // 
    // public def unlock():void {
    //     if (verbose>=3) debug("unlock called");
    //     lowLevelAt(root.home, ()=>{ getMe().lk.unlock(); });
    //     if (verbose>=3) debug("unlock returning (unlocked)");
    // }
    
    /*
     * Lock/unlock mechanism without blocking inside lowLevelAt
     */
    private static class MyQueue[E] {
        private static class Entry[E] { val e:E; var next:Entry[E] = null; def this(e:E) { this.e = e; } }
        private var head:Entry[E] = null, tail:Entry[E] = null;
        def add(e:E):void { // should be called inside atomic
            val entry = new Entry[E](e);
            if (tail == null) head = entry; else tail.next = entry;
            tail = entry;
        }
        def remove():E { // should be called inside atomic
            val entry = head; // should not be null
            head = entry.next; if (head == null) tail = null;
            return entry.e;
        }
    }
    private static class MyLatch extends SimpleLatch implements Runtime.Mortal { }
    
    private transient val waitQueue:MyQueue[GlobalRef[MyLatch]] = new MyQueue[GlobalRef[MyLatch]]();
    private transient var waitCount:Long = -1; // number of waiters, -1 means not locked
    
    public def lock():void { //TODO: should support recursive lock?
        if (verbose>=3) debug("lock called");
        val latch = new MyLatch(), gLatch = GlobalRef[MyLatch](latch);
        val needWait = new Cell[Boolean](false);
        lowLevelFetch(root.home, needWait, ()=>{
            val me = getMe();
            atomic {
                if (++me.waitCount == 0) return false;
                me.waitQueue.add(gLatch); return true;
            }
        });
        if (needWait()) {
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
        val toRelease = new Cell[GlobalRef[MyLatch]](GlobalRef(null as MyLatch));
        lowLevelFetch(root.home, toRelease, ()=>{
            val me = getMe();
            var gLatch:GlobalRef[MyLatch] = GlobalRef(null as MyLatch);
            atomic {
                if (--me.waitCount >= 0) gLatch = me.waitQueue.remove();
            }
            return gLatch;
        });
        val gLatch = toRelease();
        if (!gLatch.isNull()) {
            if (verbose>=3) debug("unlock need to release gLatch="+gLatch);
            val g = gLatch;
            lowLevelSend(g.home, ()=>{
                if (verbose>=3) debug("unlock releasing gLatch="+g);
                g.getLocalOrCopy().release();
                if (verbose>=3) debug("unlock released gLatch="+g);
            });
        }
        if (verbose>=3) debug("unlock returning (unlocked)");
    }
    // public def unlock():void {
    //     if (verbose>=3) debug("unlock called");
    //     lowLevelAt(root.home, ()=>{
    //         val me = getMe();
    //         var gLatch:GlobalRef[MyLatch] = GlobalRef(null as MyLatch);
    //         atomic {
    //             if (--me.waitCount >= 0) gLatch = me.waitQueue.remove();
    //         }
    //         if (gLatch.isNull()) return;
    //         if (verbose>=3) debug("unlock need to release gLatch="+gLatch);
    //         val g = gLatch;
    //         lowLevelSend(g.home, ()=>{
    //             if (verbose>=3) debug("unlock releasing gLatch="+g);
    //             g.getLocalOrCopy().release();
    //             if (verbose>=3) debug("unlock released gLatch="+g);
    //         });
    //     });
    //     if (verbose>=3) debug("unlock returning (unlocked)");
    // }
}
