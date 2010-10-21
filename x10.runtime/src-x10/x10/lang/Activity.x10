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

import x10.util.HashMap;
import x10.util.Stack;

/**
 * Runtime representation of an async. Only to be used in the runtime implementation.
 * 
 * @author tardieu
 */
class Activity {
    static class ClockPhases extends HashMap[Clock,Int] {
        static def make(clocks:Array[Clock]{rail}, phases:Array[Int]{rail}):ClockPhases {
            val clockPhases = new ClockPhases();
            for(var i:Int = 0; i < clocks.size; i++) 
                clockPhases.put(clocks(i), phases(i));
            return clockPhases;
        }

        def register(clocks:Array[Clock]{rail}) {
            return new Array[Int](clocks.size, (i:Int)=>clocks(i).register());
        }

        def next() {
            for(clock:Clock in keySet()) clock.resumeUnsafe();
            for(clock:Clock in keySet()) clock.nextUnsafe();
        }
        def resume() {
            for(clock:Clock in keySet()) clock.resume();
        }

        def drop() {
            for(clock:Clock in keySet()) clock.dropInternal();
            clear();
        }

        // HashMap implments CustomSerialization, so we must as well
        // Only constructor is actually required, but stub out serialize as well
        // as a reminder that if instance fields are added to ClockPhases then
        // work will have to be done here to serialize them.
        public def serialize() = super.serialize();
        def this() { super(); }
        def this(a:Any) { super(a); }
    }

    /**
     * Return the clock phases for the current activity
     */
    def clockPhases():ClockPhases {
        if (null == clockPhases)
            clockPhases = new ClockPhases();
        return clockPhases;
    }

    def safe():Boolean {
        return safe && (null == clockPhases);
    }

    /**
     * Return the innermost finish state for the current activity
     */
    def currentState():FinishState {
        if (null == finishStack || finishStack.isEmpty())
            return finishState;
        return finishStack.peek();
    }

	// Useful for the Java runtime? 
	private val root = GlobalRef[Activity](this);
	def home():Place=root.home();

    /**
     * the finish state governing the execution of this activity (may be remote)
     */
    val finishState:FinishState;

    /**
     * safe to run pending jobs while waiting for a finish (temporary)
     */
    val safe:Boolean;

    /**
     * The user-specified code for this activity.
     */
     private val body:()=>Void;

    /**
     * The mapping from registered clocks to phases for this activity.
     * Lazily created.
     */
    var clockPhases:ClockPhases;

    /**
     * The finish states for the finish statements currently executed by this activity.
     * Lazily created.
     */
    var finishStack:Stack[FinishState];
     
    var atomicDepth:int = 0;

    /**
     * Create activity.
     */
    def this(body:()=>Void, finishState:FinishState, safe:Boolean) {
        this.finishState = finishState;
        this.safe = safe;
        finishState.notifyActivityCreation();
        this.body = body;
    }

    /**
     * Create clocked activity.
     */
    def this(body:()=>Void, finishState:FinishState, clocks:Array[Clock]{rail}, phases:Array[Int]{rail}) {
        this(body, finishState, false);
        clockPhases = ClockPhases.make(clocks, phases);
    }

    def pushAtomic() {
    	atomicDepth++;
    }
    def popAtomic() {
    	atomicDepth--;
    }
    def inAtomic():boolean=atomicDepth > 0;
    def ensureNotInAtomic() {
    	if (atomicDepth > 0)
    		throw new IllegalOperationException();
    }
    /**
     * Create uncounted activity.
     */
    def this(body:()=>Void, safe:Boolean) {
        this.finishState = null;
        this.safe = safe;
        this.body = body;
    }

    /**
     * Run activity.
     */
    def run():Void {
        try {
            body();
        } catch (t:Throwable) {
            if (null != finishState) {
                finishState.pushException(t);
            } else {
                Runtime.println("Uncaught exception in uncounted activity");
                t.printStackTrace();
            }
        }
        if (null != clockPhases) clockPhases.drop();
        if (null != finishState) finishState.notifyActivityTermination();
        Runtime.dealloc(body);
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
