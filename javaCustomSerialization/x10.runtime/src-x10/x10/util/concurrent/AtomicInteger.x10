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

    private @Volatile var value:Int;
    
    public def this() { value = 0; }

    public def this(v:Int) { value = v; }

    @Native("java", "#this.get()")
    public def get():Int  = value;
    
    @Native("java", "#this.set(#newV)")
    public def set(newV:Int):void {
        value = newV;
    }
    
    @Native("java", "#this.compareAndSet(#expect,#update)")
    @Native("c++", "x10aux::atomic_int_funs::compareAndSet(#this,#expect,#update)")
    public native def compareAndSet(expect:Int, update:Int):Boolean;

    @Native("java", "#this.weakCompareAndSet(#expect,#update)")
    @Native("c++", "x10aux::atomic_int_funs::weakCompareAndSet(#this,#expect,#update)")
    public native def weakCompareAndSet(expect:Int, update:Int):Boolean;
    
    @Native("java", "#this.getAndIncrement()")
    public def getAndIncrement():Int = getAndAdd(1);

    @Native("java", "#this.getAndDecrement()")
    public def getAndDecrement():Int = getAndAdd(-1);
    
    @Native("java", "#this.getAndAdd(#delta)")
    @Native("c++", "x10aux::atomic_int_funs::getAndAdd(#this, #delta)")
    public native def getAndAdd(delta:Int):Int;
    
    @Native("java", "#this.incrementAndGet()")
    public def incrementAndGet():Int = addAndGet(1);

    @Native("java", "#this.decrementAndGet()")
    public def decrementAndGet():Int = addAndGet(-1);
    
    @Native("java", "#this.addAndGet(#delta)")
    @Native("c++", "x10aux::atomic_int_funs::addAndGet(#this, #delta)")
    public native def addAndGet(delta:Int):Int;
    
    @Native("java", "#this.toString()")
    public def toString():String = get().toString();

    @Native("java", "#this.intValue()")
    public def intValue():Int = get();

    @Native("java", "#this.longValue()")
    public def longValue():long = get() as Long;
    
    @Native("java", "#this.floatValue()")
    public def floatValue():float = get() as Float;
    
    @Native("java", "#this.doubleValue()")
    public def doubleValue():double = get() as Double;
}
