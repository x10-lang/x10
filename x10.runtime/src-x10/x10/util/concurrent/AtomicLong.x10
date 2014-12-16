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

package x10.util.concurrent;

import x10.compiler.Native;
import x10.compiler.NativeRep;
import x10.compiler.Volatile;
import x10.compiler.NativeCPPInclude;

@NativeCPPInclude("x10/util/concurrent/AtomicLongNatives.h")
@NativeRep("java", "x10.core.concurrent.AtomicLong", null, "x10.core.concurrent.AtomicLong.$RTT")
public final class AtomicLong {
    private @Volatile var value:Long;
    
    public def this() { value = 0; }
    public def this(v:Long) { 
        value = v; 
        // Memory model: acts like store of volatile field
        Fences.storeLoadBarrier();
    }
    
    @Native("java", "#this.get()")
    public def get():Long {
      // Memory model: acts like read of volatile field;
      Fences.loadStoreBarrier();
      Fences.storeLoadBarrier();
      return value;
    }
    
    @Native("java", "#this.set(#newV)")
    public def set(newV:Long):void {
        value = newV;
        // Memory model: acts like store of volatile field
        Fences.storeLoadBarrier();
    }
    
    @Native("java", "#this.compareAndSet(#expect,#update)")
    @Native("c++", "::x10::util::concurrent::AtomicLongNatives::compareAndSet(#this,#expect,#update)")
    public native def compareAndSet(expect:Long, update:Long):Boolean;

    @Native("java", "#this.weakCompareAndSet(#expect,#update)")
    @Native("c++", "::x10::util::concurrent::AtomicLongNatives::weakCompareAndSet(#this,#expect,#update)")
    public native def weakCompareAndSet(expect:Long, update:Long):Boolean;
    
    @Native("java", "#this.getAndIncrement()")
    public def getAndIncrement():Long = getAndAdd(1);

    @Native("java", "#this.getAndDecrement()")
    public def getAndDecrement():Long = getAndAdd(-1);
    
    @Native("java", "#this.getAndAdd(#delta)")
    @Native("c++", "::x10::util::concurrent::AtomicLongNatives::getAndAdd(#this,#delta)")
    public native def getAndAdd(delta:Long):Long;
    
    @Native("java", "#this.incrementAndGet()")
    public def incrementAndGet():Long = addAndGet(1);

    @Native("java", "#this.decrementAndGet()")
    public def decrementAndGet():Long = addAndGet(-1);
    
    @Native("java", "#this.addAndGet(#delta)")
    @Native("c++", "::x10::util::concurrent::AtomicLongNatives::addAndGet(#this, #delta)")
    public native def addAndGet(delta:Long):Long;
    
    @Native("java", "#this.toString()")
    public def toString():String = get().toString();

    @Native("java", "#this.intValue()")
    public def intValue():Int = get() as Int;

    @Native("java", "#this.longValue()")
    public def longValue():Long = get();
    
    @Native("java", "#this.floatValue()")
    public def floatValue():Float = get() as Float;
    
    @Native("java", "#this.doubleValue()")
    public def doubleValue():Double = get() as Double;
}
