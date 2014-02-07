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

@NativeCPPInclude("x10/util/concurrent/AtomicBooleanNatives.h")
@NativeRep("java", "x10.core.concurrent.AtomicBoolean", null, "x10.core.concurrent.AtomicBoolean.$RTT")
public final class AtomicBoolean {
   /*
    * An int that will only contain 0 or 1 and is interpreted as an boolean.
    * We do this instead of using a boolean so that we know that compareAndSet_32 
    * can work on the whole memory word.
    */
    private @Volatile var value:Int;
    
    public def this():AtomicBoolean {
        value = 0n;
        // Memory model: acts like store of volatile field
        Fences.storeLoadBarrier();
    }
    public def this(v:Boolean):AtomicBoolean {
        value = v ? 1n : 0n;
        // Memory model: acts like store of volatile field
        Fences.storeLoadBarrier();
    }
    
    @Native("java", "#this.get()")
    public def get():Boolean {
      // Memory model: acts like read of volatile field;
      Fences.loadStoreBarrier();
      Fences.storeLoadBarrier();
      return value == 1n;
   }

    @Native("java", "#this.set(#v)")
    public def set(v:Boolean):void {
        value = v ? 1n : 0n;
        // Memory model: acts like store of volatile field
        Fences.storeLoadBarrier();
    }

    @Native("java", "#this.compareAndSet(#expect,#update)")
    @Native("c++", "::x10::util::concurrent::AtomicBooleanNatives::compareAndSet(#this, #expect, #update)")
    public native def compareAndSet(expect:Boolean, update:Boolean):Boolean;

    @Native("java", "#this.weakCompareAndSet(#expect,#update)")
    @Native("c++", "::x10::util::concurrent::AtomicBooleanNatives::weakCompareAndSet(#this, #expect, #update)")
    public native def weakCompareAndSet(expect:Boolean, update:Boolean):Boolean;
    
    @Native("java", "#this.getAndSet(#v)")
    public def getAndSet(v:Boolean):Boolean {
	val oldVal = get();
	set(v);
	return oldVal;
    }

    @Native("java", "#this.toString()")
    public def toString():String = get().toString();
}
 
