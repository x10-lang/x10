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
class Activity(finishState:FinishState, safe:Boolean) {
	// the finish state governing the execution of this activity

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
	
	/**
	 * Create activity.
	 */
	def this(body:()=>Void, finishState:FinishState, safe:Boolean) {
		property(finishState, safe);
	    this.body = body;
	}

	/**
	 * Create clocked activity.
	 */
	def this(body:()=>Void, finishState:FinishState, clocks:ValRail[Clock], phases:ValRail[Int]) {
		this(body, finishState, false);
	    clockPhases = new ClockPhases();
		clockPhases.register(clocks, phases);
	}

	/**
	 * Run asynchronous activity.
	 */
	def run():Void {
		if (location.id == finishState.location.id) {
			// local async
			now();
			finishState.notifySubActivityTermination();
		} else {
			// remote async
			val state = finishState;
			try {
				// enclose computation in local finish to minimize traffic
				Runtime.startFinish();
				now();
				Runtime.stopFinish();
                val c = ()=>state.notifySubActivityTermination();
				NativeRuntime.runAt(state.location.id, c);
			} catch (t:Throwable) {
				// report exception & termination atomically
                val c = ()=>state.notifySubActivityTermination(t);
				NativeRuntime.runAt(state.location.id, c);
	    	}
		}
	}
	
	/**
	 * Run synchronous activity.
	 */
	def now():Void {
		try {
			body();
		} catch (t:Throwable) {
			Runtime.pushException(t);
		}
		drop();
	}

	/**
	 * Drop all clocks.
	 */
	private def drop():Void {
		if (null != clockPhases) clockPhases.drop();
	}

    // [DC] The correct thing to do here is do toString() on the closure
	//public def toString():String = name; 
}

// vim:shiftwidth=4:tabstop=4:expandtab
