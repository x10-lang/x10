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

    // FIXME: remove implements clause when codegen has been fixed (XTENLANG-1961)
    static class ClockPhases extends HashMap[Clock,Int] implements x10.io.CustomSerialization {
        // compute spawnee clock phases from spawner clock phases in async clocked(clocks)
        // and register spawnee on these on clocks
        static def make(clocks:Array[Clock]{rail}) {
            val clockPhases = new ClockPhases();
            for(var i:Int = 0; i < clocks.size; i++) 
                clockPhases.put(clocks(i), clocks(i).register());
            return clockPhases;
        }

        // next statement
        def next() {
            for(clock:Clock in keySet()) clock.resumeUnsafe();
            for(clock:Clock in keySet()) clock.nextUnsafe();
        }

        // resume all clocks
        def resume() {
            for(clock:Clock in keySet()) clock.resume();
        }

        // drop all clocks
        def drop() {
            for(clock:Clock in keySet()) clock.dropInternal();
            clear();
        }

        // HashMap implements CustomSerialization, so we must as well
        // Only constructor is actually required, but stub out serialize as well
        // as a reminder that if instance fields are added to ClockPhases then
        // work will have to be done here to serialize them.
        public def serialize() = super.serialize();
        def this() { super(); }
        def this(a:Any) { super(a); }
    }

    /**
     * the finish state governing the execution of this activity (may be remote)
     */
    private val finishState:FinishState;

    /**
     * safe to run pending jobs while waiting for a finish (temporary)
     */
    private val safe:Boolean;

    /**
     * The user-specified code for this activity.
     */
    private val body:()=>Void;

    /**
     * The mapping from registered clocks to phases for this activity.
     * Lazily created.
     */
    private var clockPhases:ClockPhases;

    /**
     * The finish states for the finish statements currently executed by this activity.
     * Lazily created.
     */
    private var finishStack:Stack[FinishState];

    /**
     * Depth of enclosong atomic blocks
     */
    private var atomicDepth:int = 0;

    /**
     * The place of the activity (for the java backend).
     */
    val home = Runtime.hereInt();

    /**
     * Create activity.
     */
    def this(body:()=>Void, finishState:FinishState, safe:Boolean) {
        this.finishState = finishState;
        this.safe = safe;
        finishState.notifyActivityCreation();
        this.body = body;
    }

    def this(body:()=>Void, finishState:FinishState) {
        this.finishState = finishState;
        this.safe = true;
        finishState.notifyActivityCreation();
        this.body = body;
    }

    /**
     * Create clocked activity.
     */
    def this(body:()=>Void, finishState:FinishState, clockPhases:ClockPhases) {
        this(body, finishState, false);
        this.clockPhases = clockPhases;
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
     * Return the clock phases
     */
    def clockPhases():ClockPhases {
        if (null == clockPhases)
            clockPhases = new ClockPhases();
        return clockPhases;
    }

    /**
     * Return the innermost finish state
     */
    def finishState():FinishState {
        if (null == finishStack || finishStack.isEmpty())
            return finishState;
        return finishStack.peek();
    }

    /**
     * Enter finish block
     */
    def pushFinish(f:FinishState) {
        if (null == finishStack)
            finishStack = new Stack[FinishState]();
        finishStack.push(f);
    }

    /**
     * Exit finish block
     */
    def popFinish():FinishState = finishStack.pop();

    def safe():Boolean = safe && (null == clockPhases);

    // about atomic blocks

    def pushAtomic() {
        atomicDepth++;
    }

    def popAtomic() {
        atomicDepth--;
    }

    def ensureNotInAtomic() {
        if (atomicDepth > 0)
            throw new IllegalOperationException();
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
