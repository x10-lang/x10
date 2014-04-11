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
    
    private static def lowLevelAt(dst:Place, cl:()=>void)
    = FinishResilient.lowLevelAt(dst, cl);
    private static def lowLevelFetch[T](dst:Place, result:Cell[T], cl:()=>T):Boolean
    = FinishResilient.lowLevelFetch[T](dst, result, cl);
    
    static val ALL = (here.id==0) ? new HashMap[Any,Any]() : null;
    
    @NonEscaping private val root:GlobalRef[ResilientStorePlace0[K,V]];
    private transient val hm:HashMap[K,V] = new HashMap[K,V]();
    private transient val lk:SimpleLatch = new SimpleLatch();
    
    private def this() {
        assert here.id==0;
        root = GlobalRef[ResilientStorePlace0[K,V]](this);
    }
    
    public static def make[K,V](name:Any):ResilientStorePlace0[K,V] {
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
        return result();
    }
    
    public static def delete(name:Any):void {
        lowLevelAt(Place(0), ()=>{ atomic { ALL.remove(name); } });
    }
    
    private def getHM() {
        val r = root as GlobalRef[ResilientStorePlace0[K,V]]{home==here}; // to avoid compile error
        return r().hm;
    }
    
    public def create(key:K, value:V):void { put(key, value); }
    
    public def put(key:K, value:V):void {
        lowLevelAt(root.home, ()=>{ atomic { getHM().put(key, value); } });
    }
    
    public def getOrElse(key:K, orelse:V):V {
        val result = new Cell[V](orelse);
        lowLevelFetch(root.home, result, ()=>{
            val v:V; atomic { v = getHM().getOrElse(key, orelse); } return v;
        });
        return result();
    }
    
    public def remove(key:K):void {
        lowLevelAt(root.home, ()=>{ atomic { getHM().remove(key); } });
    }
    
    private def getLK() {
        val r = root as GlobalRef[ResilientStorePlace0[K,V]]{home==here}; // to avoid compile error
        return r().lk;
    }
    
    public def lock():void {
        if (verbose>=3) debug("lock called");
        lowLevelAt(root.home, ()=>{ getLK().lock(); });
        if (verbose>=3) debug("lock returning");
    }
    
    public def unlock():void {
        if (verbose>=3) debug("unlock called");
        lowLevelAt(root.home, ()=>{ getLK().unlock(); });
        if (verbose>=3) debug("unlock returning");
    }
}
