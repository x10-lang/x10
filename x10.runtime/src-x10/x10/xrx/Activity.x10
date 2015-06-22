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

package x10.xrx;

/**
 * Runtime representation of an async. Only to be used in the runtime implementation.
 */
public class Activity {

    // This flag is hacked to be false in the APGAS C++ library
    // TODO: refactor XRX so body is more than a ()=>void so that run
    //       can simply ask the body if it should be deallocated on completion.
    private static DEALLOC_BODY:Boolean = canDealloc();
    private static def canDealloc():Boolean = true;  // sigh. Block constant propagation.

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
    var clockPhases:Clock.ClockPhases;

    /** Set to true unless this activity represents the body of an 'at' statement.
     */
    val shouldNotifyTermination:Boolean;

    /**
     * Src place from which the activity was spawned
     */
    val srcPlace:Place;

    /**
     * Depth of enclosing atomic blocks
     */
    private var atomicDepth:Int = 0n;
    
    public var epoch:Long;

    /**
     * Create activity.
     */
    def this(epoch:Long, body:()=>void, srcPlace:Place, finishState:FinishState) {
        this(epoch, body, srcPlace, finishState, true);
    }
    // TODO: This constructor is only used by ResilientFinish.runAt.
    //       Once that code is restructured to use @Immediate, it is likely we can
    //       get rid of the shouldNotifyTermination flag.
    def this(epoch:Long, body:()=>void, srcPlace:Place, finishState:FinishState, nt:Boolean) {
        this.epoch = epoch;
        this.srcPlace = srcPlace;
        this.finishState = finishState;
        this.shouldNotifyTermination = nt;
        this.body = body;
    }

    /**
     * Create clocked activity.
     */
    def this(epoch:Long, body:()=>void, srcPlace:Place, finishState:FinishState, clockPhases:Clock.ClockPhases) {
        this(epoch, body, srcPlace, finishState);
        this.clockPhases = clockPhases;
    }

    /**
     * Return the clock phases
     */
    def clockPhases():Clock.ClockPhases {
        if (null == clockPhases) {
            clockPhases = Clock.ClockPhases.make();
        }
        return clockPhases;
    }

    /**
     * Return the innermost finish state
     */
    public def finishState():FinishState = finishState;

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
        } catch (wt:WrappedThrowable) {
            finishState.pushException(wt.getCheckedCause());
        } catch (t:CheckedThrowable) {
            finishState.pushException(t);
        }
        if (null != clockPhases) clockPhases.drop();
        if (shouldNotifyTermination) {
            try {
                finishState.notifyActivityTermination();
            } catch (DeadPlaceException) {}
        }
        if (DEALLOC_BODY) Unsafe.dealloc(body);
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
