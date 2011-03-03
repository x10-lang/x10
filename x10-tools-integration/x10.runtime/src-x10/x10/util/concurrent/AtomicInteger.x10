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

@NativeRep("java", "x10.core.concurrent.AtomicInteger", null, "x10.core.concurrent.AtomicInteger.$RTT")
public final class AtomicInteger {

    private @Volatile var value:int;
    
    public def this() { value = 0; }

    public def this(v:int) { value = v; }

    @Native("java", "#0.get()")
    public def get():int  = value;
    
    @Native("java", "#0.set(#1)")
    public def set(newV:int):void {
        value = newV;
    }
    
    @Native("java", "#0.compareAndSet(#1,#2)")
    @Native("c++", "x10aux::atomic_int_funs::compareAndSet(#this,#expect,#update)")
    public native def compareAndSet(expect:int, update:int):boolean;

    @Native("java", "#0.weakCompareAndSet(#1,#2)")
    @Native("c++", "x10aux::atomic_int_funs::weakCompareAndSet(#this,#expect,#update)")
    public native def weakCompareAndSet(expect:int, update:int):boolean;
    
    @Native("java", "#0.getAndIncrement()")
    public def getAndIncrement() = getAndAdd(1);

    @Native("java", "#0.getAndDecrement()")
    public def getAndDecrement() = getAndAdd(-1);
    
    @Native("java", "#0.getAndAdd(#1)")
    @Native("c++", "x10aux::atomic_int_funs::getAndAdd(#this, #delta)")
    public native def getAndAdd(delta:int):int;
    
    @Native("java", "#0.incrementAndGet()")
    public def incrementAndGet():int = addAndGet(1);

    @Native("java", "#0.decrementAndGet()")
    public def decrementAndGet():int = addAndGet(-1);
    
    @Native("java", "#0.addAndGet(#1)")
    @Native("c++", "x10aux::atomic_int_funs::addAndGet(#this, #delta)")
    public native def addAndGet(delta:int):int;
    
    @Native("java", "#0.toString()")
    public def toString():String = get().toString();

    @Native("java", "#0.intValue()")
    public def intValue():int = get();

    @Native("java", "#0.longValue()")
    public def longValue():long = get() as Long;
    
    @Native("java", "#0.floatValue()")
    public def floatValue():float = get() as Float;
    
    @Native("java", "#0.doubleValue()")
    public def doubleValue():double = get() as Double;
}
