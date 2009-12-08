/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime;

import x10.util.Stack;

/**
 * @author tardieu
 */
public class Activity {
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
    var clockPhases:ClockPhases!;

    /**
     * The finish states for the finish statements currently executed by this activity.
     * Lazily created.
     */
    var finishStack:Stack[FinishState!]!;

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
    def this(body:()=>Void, finishState:FinishState, clocks:ValRail[Clock], phases:ValRail[Int]) {
        this(body, finishState, false);
        clockPhases = ClockPhases.make(clocks, phases);
    }

    /**
     * Run activity.
     */
    def run():Void {
    try {
        body();
    } catch (t:Throwable) {
        finishState.pushException(t);
    }
    if (null != clockPhases) clockPhases.drop();
        finishState.notifyActivityTermination();
    NativeRuntime.dealloc(body);
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
