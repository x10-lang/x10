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

/**
 * A sequential runner for task frames. 
 * Useful for debugging.
 * @author vj
 */
import x10.util.Stack;
public final class SeqRunner[T, Z]
    implements Runner[T,Z] {
    val frame:TaskFrame[T,Z]!;
    public def this(f:TaskFrame[T,Z]!) {
	this.frame=f;
    }
    val stack = new Stack[T]();
    var taskCounter:UInt = 0;
    /**
     * Run the given task to completion, and return the number of tasks that 
     * have been executed in doing so. 
     */
    public def apply (var task:T, reducer:Reducible[Z]):Z  {
    	val result = finish (reducer) {
	    stack.push(task);
	    while (stack.size() > 0) {
		task = stack.pop();
		frame.runTask(task, stack);
		++taskCounter;
	    }
	};
        return result;
    }
    public def stats(time:Long, verbose:Boolean) {
	Console.OUT.println("Performance = "+taskCounter
	    +"/"+Counter.safeSubstring("" + (time/1E9), 0,6)
	    +" = "+ Counter.safeSubstring("" + (taskCounter/(time/1E3)), 0, 6)
		      + "M tasks/s");
	}
}
