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

package x10.lang;

import x10.compiler.Global;
import x10.compiler.Pinned;

/**
 * @author tardieu
 *
 * Ported from 2.0 to 2.1 via naive simulation of 
 *       2.0 style global object by injecting a root field
 *       that is a GlobalRef(this) and always accessing fields 
 *       as this.root().f instead of this.f.
 * TODO: Port to Dual Class implementation of global objects.
 */
public class Clock(name:String) {
	
	private val root = GlobalRef[Clock](this);
	public def equals(a:Any) {
		if (a == null || ! (a instanceof Clock))
			return false;
		return (a as Clock).root == this.root;
	}
	public def hashCode() = root.hashCode();
	
    public static def make(): Clock = make("");
    public static def make(name:String):Clock {
        val clock = new Clock(name);
        Runtime.clockPhases().put(clock, FIRST_PHASE);
        return clock;
    }

    public static FIRST_PHASE = 1;
    // NOTE: all transient fields must always be accessed as this.root().f (and at place this.root.home), 
    // not this.f
    private transient var count:Int = 1;
    private transient var alive:Int = 1;
    private transient var phase:Int = FIRST_PHASE;

    private def this(name:String) {
        property(name);
    }

    // should be accessed through root()
    @Pinned private def resumeLocal()  {
        atomic 
            if (--alive == 0) {
                alive = count;
                ++phase;
            }
    }
    // should be accessed through root()
    @Pinned private def dropLocal(ph:Int) {
        --count;
        if (-ph != phase)
            resumeLocal();
    }

    @Global private def get() = Runtime.clockPhases().get(this).value;
    @Global private def put(ph:Int) = Runtime.clockPhases().put(this, ph);
    @Global private def remove() = Runtime.clockPhases().remove(this).value;
    @Global def register() {
        if (dropped()) throw new ClockUseException();
        val ph = get();
        at (root) {
        	val me = root();
        	atomic {
        		 ++ me.count;
                 if (-ph != me.phase) 
                	 ++ me.alive;
        	}
        }   
        return ph;
     }
     @Global def resumeUnsafe() {
        Runtime.ensureNotInAtomic();
        val ph = get();
        if (ph < 0) return;
        at (root) {
        	val me = root();
        	me.resumeLocal();
        }
        put(-ph);
    }
    @Global def nextUnsafe() {
    	Runtime.ensureNotInAtomic();
        val ph = get();
        val abs = Math.abs(ph);
        at (root) {
        	val me = root();
            if (ph > 0) me.resumeLocal();
            when (abs < me.phase);
        }
        put(abs + 1);
    }
    @Global def dropUnsafe() {
        val ph = remove();
        async at(root) {
        	val me = root();
        	me.dropLocal(ph);
        }
    }
    @Global def dropInternal() {
        val ph = get();
        async at(root.home) {
	    val rcl:Clock = root();
            rcl.dropLocal(ph);
        }
    }
    public @Global def registered():Boolean = Runtime.clockPhases().containsKey(this);
    public @Global def dropped():Boolean = !registered();
    public @Global def phase():int {
        if (dropped()) throw new ClockUseException();
        return Math.abs(get());
    }
    public @Global def resume():void {
        if (dropped()) throw new ClockUseException();
        resumeUnsafe();
    }
    public @Global def next():void {
        if (dropped()) throw new ClockUseException();
        nextUnsafe();
    }
    public @Global def drop():void {
        if (dropped()) throw new ClockUseException();
        dropUnsafe();
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
