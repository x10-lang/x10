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
package x10.lang;

public abstract class ResilientStore[K,V] {
    protected static val verbose = FinishResilient.verbose; //TODO: to be separated
    protected static def debug(msg:String) { FinishResilient.debug(msg); }
    
    public static def make[K,V](name:Any){V haszero}:ResilientStore[K,V]{V haszero} {
        if (verbose>=1) debug("ResilientStore.make called, name="+name);
        var rs:ResilientStore[K,V]{V haszero};
        //TODO: support other implementations
        rs = ResilientStorePlace0.make[K,V](name);
        //rs = ResilientStoreHC.make[K,V](name);
        if (verbose>=1) debug("ResilientStore.make returning rs="+rs);
        return rs;
    }
    
    public static def delete(name:Any):void { //TODO: should pass ResilientStore instance to delete?
        if (verbose>=1) debug("ResilientStore.delete called, name="+name);
        //TODO: support other implementations
        ResilientStorePlace0.delete(name);
        if (verbose>=1) debug("ResilientStore.delete returning");
    }
    
    /*
     * Methods to be implemented in subclasses
     */
    // public static def make[K,V](name:Any):ResilientStore[K,V];
    // public static def delete(name:Any):void;
    abstract public def create(key:K, value:V):void;
    abstract public def put(key:K, value:V):void;
    abstract public def getOrElse(key:K, orelse:V):V;
    abstract public def remove(key:K):void;
    abstract public def lock():void;
    abstract public def unlock():void;
}
