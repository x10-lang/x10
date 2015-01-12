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
package x10.util.logging;

import x10.util.HashMap;
import x10.util.Map;
import x10.util.Container;
import x10.util.concurrent.Lock;

import x10.compiler.NativeRep;
import x10.compiler.Native;

@NativeRep("java", "java.lang.Object", null, "x10.rtt.Types.ANY")
class ConsoleLogFactory extends LogFactory {

    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def this() {
        attributes = new HashMap[String, Any]();
    }
    
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def release():void {
    }
    
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def getInstance(name:String):Log {
        return new ConsoleLogger(name);
    }
        
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def getAttributeNames():Rail[String] = toRail(attributes.keySet());
    
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def getAttribute(name:String):Any {
        return attributes.getOrElse(name, null);
    }

    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def setAttribute(name:String, value:Any):void {
        attributes.put(name, value);
    }
    
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public def removeAttribute(name:String):void {
        attributes.remove(name);
    }
        
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
        private static def toRail[K](c:Container[K]):Rail[K]{self!=null} {
        val arr = Unsafe.allocRailUninitialized[K](c.size());
        var i:Long = 0;
        for (k in c) {
            arr(i++) = k;
        }
        return arr;
    }
    
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    private val attributes:Map[String,Any];
    
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public static def getFactory():LogFactory {
        factoryLock.lock();
        try {
            if (factory() == null) {
                factory() = new ConsoleLogFactory();
            }
            return factory();
        } finally {
            factoryLock.unlock();
        }
    }
    
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    public static def releaseAll():void {
        factoryLock.lock();
        try {
            factory() = null;
        } finally {
            factoryLock.unlock();
        }
    }
    
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    private static val factory:Cell[LogFactory] = new Cell[LogFactory](null);
    @Native("java", "throw new java.lang.UnsupportedOperationException()")
    private static val factoryLock:Lock{self!=null} = new Lock();
}
