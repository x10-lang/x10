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
		try {
		    finishStack.push(new FinishState());
			try {
				body();
			} catch (t:Throwable) {
				finishStack.peek().pushException(t);
			}
	    	clockPhases.drop();
			finishStack().pop().waitForFinish();
		} catch (t:Throwable) {
			val state = finishStack.peek();
			NativeRuntime.runAt(state.location.id, ()=>{
				state.pushException(t);
    			state.notifySubActivityTermination();
    		});
    		return;
    	}
    	val state = finishStack.peek();
		NativeRuntime.runAt(state.location.id, ()=>state.notifySubActivityTermination());
	}
	
	public def toString():String = name; 
}
