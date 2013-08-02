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

import x10.io.SerialData;
import x10.util.Map;
import x10.util.HashMap;

/**
 * Runtime representation of an async. Only to be used in the runtime implementation.
 */
class Activity {

    // This flag is hacked to be false in the APGAS C++ library
    // TODO: refactor XRX so body is more than a ()=>void so that run
    //       can simply ask the body if it should be deallocated on completion.
    private static DEALLOC_BODY:Boolean = true;

    static class ClockPhases extends HashMap[Clock,Int] {
        // compute spawnee clock phases from spawner clock phases in async clocked(clocks)
        // and register spawnee on these on clocks
        static def make(clocks:Rail[Clock]) {
            val clockPhases = new ClockPhases();
            for(var i:Long = 0; i < clocks.size; i++) 
                clockPhases.put(clocks(i), clocks(i).register());
            return clockPhases;
        }

        // next statement
        def advanceAll() {
            for(entry:Map.Entry[Clock,Int] in entries()) entry.getKey().resumeUnsafe();
            for(entry:Map.Entry[Clock,Int] in entries()) entry.getKey().advanceUnsafe();
        }

        // resume all clocks
        def resumeAll() {
            for(entry:Map.Entry[Clock,Int] in entries()) entry.getKey().resumeUnsafe();
        }

        // drop all clocks
        def drop() {
            for(entry:Map.Entry[Clock,Int] in entries()) entry.getKey().dropInternal();
            clear();
        }

        // HashMap implements CustomSerialization, so we must as well
        public def serialize():SerialData {
	    // minor optimization instead of doing:
            //    new SerialData(null, super.serialize())
            // just return super.serialize() directly
            return super.serialize();
        }
        def this() { super(); }
        def this(a:SerialData) { 
            super(a);  // see optimization in serialize();
        }
    }

    //val parentActivity:Activity = Runtime.activity();

    /**
     * the finish state governing the execution of this activity (may be remote)
     */
    private var finishState:FinishState;

    /**
     * The user-specified code for this activity.
     */
    private val body:()=>void;

    /**
     * The mapping from registered clocks to phases for this activity.
     * Lazily created.
     */
    var clockPhases:ClockPhases;

    /** Set to true unless this activity represents the body of an 'at' statement.
     */
    val shouldNotifyTermination:Boolean;

    /**
     * Depth of enclosong atomic blocks
     */
    private var atomicDepth:int = 0n;

    /**
     * Create activity.
     */
    def this(body:()=>void, srcPlace:Place, finishState:FinishState) {
        this(body, srcPlace, finishState, true);
    }
    def this(body:()=>void, srcPlace:Place, finishState:FinishState, nac:Boolean) {
        this(body, srcPlace, finishState, nac, true);
    }
    def this(body:()=>void, srcPlace:Place, finishState:FinishState, nac:Boolean, nt:Boolean) {
        this.finishState = finishState;
        if (nac) finishState.notifyActivityCreation(srcPlace);
        this.shouldNotifyTermination = nt;
        this.body = body;
    }

    /**
     * Create clocked activity.
     */
    def this(body:()=>void, srcPlace:Place, finishState:FinishState, clockPhases:ClockPhases) {
        this(body, srcPlace, finishState);
        this.clockPhases = clockPhases;
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
    def finishState():FinishState = finishState;

    /**
     * Enter finish block
     */
    def swapFinish(f:FinishState) {
        val old = finishState;
        finishState = f;
        return old;
    }

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
    def run():void {
        try {
            body();
        } catch (t:Error) {
            finishState.pushException(new WrappedThrowable(t));
        } catch (t:Exception) {
            finishState.pushException(t);
        }
        if (null != clockPhases) clockPhases.drop();
        if (shouldNotifyTermination) finishState.notifyActivityTermination();
        if (DEALLOC_BODY) Unsafe.dealloc(body);
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
