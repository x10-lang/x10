/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
 
package x10.runtime;

import x10.util.Stack;

/**
 * The state associated with a finish operation. Records the count of 
 * active activities associated with this finish, and the stack
 * of exceptions thrown by activities associated with this finish that 
 * have already terminated abruptly. An activity maintains a stack of FinishStates.
 * @author vj May 17, 2005
 * @author Raj Barik, Vivek Sarkar
 * @author tardieu 
 */
value FinishState {
	/**
	 * The Exception Stack is used to collect exceptions 
	 * issued when activities associated with this finish state terminate abruptly. 
	 */
	private val exceptions = new Stack[Throwable]();

	/**
	 * The monitor is used to serialize insertions into the Exception Stack. 
	 */
	private val monitor = new Monitor();

	/**
	 * Keep track of current number of activities associated with this finish state
	 */
	private val latch = new ModCountDownLatch(0);

	/**
	 * This method returns only when all spawned activity registered with this 
	 * FinishState have terminated either normally or abruptly.
	 */
	def waitForFinish():Void {
		latch.await();
		if (!exceptions.isEmpty()) {
			if (exceptions.size() == 1) {
				val t = exceptions.pop();
				if (t instanceof Error) {
					throw t as Error;
				}
				if (t instanceof RuntimeException) {		
					throw t as RuntimeException;
				}
				assert false as boolean;
			}
			throw new MultipleExceptions(exceptions);
		}
	}

	/** 
	 * An activity created under this finish has been created. Increment the count
	 * associated with the finish.
	 */
	def notifySubActivitySpawn():Void {
		at (latch) latch.updateCount();
	}

	/** 
	 * An activity created under this finish has terminated. Decrement the count
	 * associated with the finish and notify the parent activity if it is waiting.
	 */
	def notifySubActivityTermination():Void {
		at (latch) latch.countDown();
	}

	/** 
	 * Push an exception onto the stack.
	 */
	def pushException(t:Throwable):Void {
		at (exceptions) {
			monitor.lock();
			exceptions.push(t);
			monitor.unlock();
		}
	}
}
