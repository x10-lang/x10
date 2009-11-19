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
     * the finish state governing the execution of this activity
     */
    val finishState:FinishState!;
    /**
     * safe to run pending jobs while waiting for a finish (temporary)
     */
    val safe:Boolean;
    /**
     * whether to dealloc the body after executing it
     */
    private val free:Boolean;

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
    def this(body:()=>Void, finishState:FinishState!, safe:Boolean) {
        this(body, finishState, safe, false);
    }

    /**
     * Create activity.
     */
    def this(body:()=>Void, finishState:FinishState!, safe:Boolean, free:Boolean) {
        this.finishState = finishState;
        this.safe = safe;
        finishState.incr();
        this.body = body;
        this.free = free;
    }

	/**
	 * Create clocked activity.
	 */
	def this(body:()=>Void, finishState:FinishState{self.at(here)}, clocks:ValRail[Clock], phases:ValRail[Int]) {
		this(body, finishState, false);
		val cp = new ClockPhases();
		clockPhases = cp;
	    cp.register(clocks, phases);
	}

	/**
	 * Run activity.
	 */
	def run():Void {
        try {
            body();
        } catch (t:Throwable) {
            Runtime.pushException(t);
        }
        if (null != clockPhases) clockPhases.drop();
		finishState.notifySubActivityTermination();
        if (free) NativeRuntime.dealloc(body);
	}
	
    // [DC] The correct thing to do here is do toString() on the closure
	// public def toString():String = name; 
}

// vim:shiftwidth=4:tabstop=4:expandtab
