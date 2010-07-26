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
public final class SeqRunner[T] {
    val stack = new Stack[T]();
    var nodesCounter:UInt = 0;
    val frame:TaskFrame[T]!;
    def this(f:TaskFrame[T]!) {
    	this.frame=f;
    }
    def processStack () {
      while (stack.size() > 0) {
    	  val task = stack.pop();
          frame.runTask(task, stack);
          ++nodesCounter;
      }
    }
    /**
     * Run the given task to completion, and return the number of tasks that 
     * have been executed in doing so. 
     */
    public def run (task:T):UInt {
    	++nodesCounter; 
    	frame.runTask(task, stack);
        this.processStack();
        return nodesCounter;
	}
}
