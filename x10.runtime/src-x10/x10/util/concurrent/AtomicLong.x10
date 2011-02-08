/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.util.concurrent;

import x10.compiler.Native;
import x10.compiler.NativeRep;
import x10.compiler.Volatile;

@NativeRep("java", "x10.core.concurrent.AtomicLong", null, "x10.core.concurrent.AtomicLong._RTT")
public final class AtomicLong {
    private @Volatile var value:long;
    
    public def this() { value = 0; }
    public def this(v:long) { value = v; }
    
    @Native("java", "#0.get()")
    public def get():long = value;
    
    @Native("java", "#0.set(#1)")
    public def set(newV:long):void {
        value = newV;
    }
    
    @Native("java", "#0.compareAndSet(#1,#2)")
    @Native("c++", "x10aux::atomic_long_funs::compareAndSet(#this,#expect,#update)")
    public native def compareAndSet(expect:long, update:long):boolean;

    @Native("java", "#0.weakCompareAndSet(#1,#2)")
    @Native("c++", "x10aux::atomic_long_funs::weakCompareAndSet(#this,#expect,#update)")
    public native def weakCompareAndSet(expect:long, update:long):boolean;
    
    @Native("java", "#0.getAndIncrement()")
    public def getAndIncrement():long = getAndAdd(1);

    @Native("java", "#0.getAndDecrement()")
    public def getAndDecrement():long = getAndAdd(-1);
    
    @Native("java", "#0.getAndAdd(#1)")
    @Native("c++", "x10aux::atomic_long_funs::getAndAdd(#this,#delta)")
    public native def getAndAdd(delta:long):long;
    
    @Native("java", "#0.incrementAndGet()")
    public def incrementAndGet():long = addAndGet(1);

    @Native("java", "#0.decrementAndGet()")
    public def decrementAndGet():long = addAndGet(-1);
    
    @Native("java", "#0.addAndGet(#1)")
    @Native("c++", "x10aux::atomic_long_funs::addAndGet(#this, #delta)")
    public native def addAndGet(delta:long):long;
    
    @Native("java", "#0.toString()")
    public def toString():String = get().toString();

    @Native("java", "#0.intValue()")
    public def intValue():int = get() as Int;

    @Native("java", "#0.longValue()")
    public def longValue():long = get();
    
    @Native("java", "#0.floatValue()")
    public def floatValue():float = get() as Float;
    
    @Native("java", "#0.doubleValue()")
    public def doubleValue():double = get() as Double;
}
