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
class Activity(clockPhases:ClockPhases, finishStack:Stack[FinishState], name:String) {
	private val body:()=>Void;
	
	/**
	 * Create an activity.
	 */
	def this(body:()=>Void, name:String) {
	    property(new ClockPhases(), new Stack[FinishState](), name);
	    this.body = body;
	}

	/**
	 * Create a clocked activity.
	 */
	def this(body:()=>Void, state:FinishState, clocks:ValRail[Clock], phases:ValRail[Int], name:String) {
		this(body, name);
	    finishStack.push(state);
		clockPhases.register(clocks, phases);
	}

	/**
	 * Run the activity.
	 */
	def run():Void {
		val state = finishStack.peek();
		if (location == state.location) {
			// local async
			try {
				body();
				clockPhases.drop();
				state.notifySubActivityTermination();
			} catch (t:Throwable) {
				clockPhases.drop();
				state.notifySubActivityTermination(t);
			}
		} else {
			// remote async
			try {
				body();
				clockPhases.drop();
                val c = ()=>state.notifySubActivityTermination();
				NativeRuntime.runAt(state.location.id, c);
			} catch (t:Throwable) {
				clockPhases.drop();
                val c = ()=>state.notifySubActivityTermination(t);
				NativeRuntime.runAt(state.location.id, c);
	    	}
		}
	}
	
	public def toString():String = name; 
}
