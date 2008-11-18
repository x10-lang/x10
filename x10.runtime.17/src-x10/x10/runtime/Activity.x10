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
	def this(body:()=>Void, state:FinishState, name:String) {
	    property(new ClockPhases(), new Stack[FinishState](), name);
	    this.body = body;
	    finishStack.push(state);
	}

	/**
	 * Create a clocked activity.
	 */
	def this(body:()=>Void, state:FinishState, clocks:ValRail[Clock_c], phases:ValRail[Int], name:String) {
		this(body, state, name);
		clockPhases.register(clocks, phases);
	}

	/**
	 * Run the activity.
	 */
	def run():Void {
		try {
			body();
		} catch (t:Throwable) {
			finishStack.peek().pushException(t);
		}
		clockPhases.drop();
    	finishStack.peek().notifySubActivityTermination();
	}
	
	public def toString():String = name; 
}
