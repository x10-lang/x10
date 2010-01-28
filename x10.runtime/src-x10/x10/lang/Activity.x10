/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.util.Stack;

/**
 * @author tardieu
 */
public class Activity {

    /**
     * Sleep for the specified number of milliseconds.
     * [IP] NOTE: Unlike Java, x10 sleep() simply exits when interrupted.
     * @param millis the number of milliseconds to sleep
     * @return true if completed normally, false if interrupted
     */
    public static def sleep(millis:long):Boolean {
        try {
            Runtime.increaseParallelism();
            Runtime.Thread.sleep(millis);
            Runtime.decreaseParallelism(1);
            return true;
        } catch (e:InterruptedException) {
            Runtime.decreaseParallelism(1);
            return false;
        }
    }

    /**
     * the finish state governing the execution of this activity (may be remote)
     */
    val finishState:Runtime.FinishState;

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
    var clockPhases:Runtime.ClockPhases!;

    /**
     * The finish states for the finish statements currently executed by this activity.
     * Lazily created.
     */
    var finishStack:Stack[Runtime.FinishState!]!;

    /**
     * Create activity.
     */
    def this(body:()=>Void, finishState:Runtime.FinishState, safe:Boolean) {
        this.finishState = finishState;
        this.safe = safe;
        finishState.notifyActivityCreation();
        this.body = body;
    }

    /**
     * Create clocked activity.
     */
    def this(body:()=>Void, finishState:Runtime.FinishState, clocks:ValRail[Clock], phases:ValRail[Int]) {
        this(body, finishState, false);
        clockPhases = Runtime.ClockPhases.make(clocks, phases);
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
        Runtime.dealloc(body);
    }

    /**
     * Activity-local storage
     */
    public var tag:Object!;
}

// vim:shiftwidth=4:tabstop=4:expandtab
