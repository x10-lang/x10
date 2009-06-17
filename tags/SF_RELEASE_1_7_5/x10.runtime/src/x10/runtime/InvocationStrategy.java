/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime;

/**
 * This class is intended to declare different invocation strategies for X10 activities.
 * The thread pool executor invokes this code which in turns invoke the actual X10 activity code. 
 * It can be use to wrap different kind of actions/operations 
 * before and after X10 activities.
 * 
 * Implementation note: We use a singleton pattern to avoid creation of new strategies
 * each time an activity is submitted to the pool.
 * 
 * @author vcave
 *
 */
public abstract class InvocationStrategy {

	/**
	 * Singleton representing the default strategy to invoke an X10 activity.
	 * Which means just call the runX10Task method and do not wait for any
	 * spawned activity to finish.
	 */
	public static final InvocationStrategy ASYNC = new DefaultStrategy();

	/**
	 * Singleton representing a strategy to invoke an X10 activity in a finish.
	 * Using this strategy means X10 activity invocation is surronded by 
	 * both start and stop finish. 
	 * Hence invokeX10Task returns only when all child activities have terminated.
	 */
	public static final InvocationStrategy ASYNC_IN_FINISH = new AsyncInFinishStrategy();

	/**
	 * This method implements the strategy used to invoke X10 activities.
	 * @param activityToInvoke X10 activity to invoke.
	 */
	public abstract void invokeX10Task(Activity activityToInvoke);
	
	/**
	 * Default X10 activity invocation strategy implementation.
	 * @author vcave
	 *
	 */
	public static class DefaultStrategy extends InvocationStrategy {
		private DefaultStrategy() { 
		}
		
		public void invokeX10Task(Activity activityToInvoke) {
			try {
				// invoking the actual X10 task code
				activityToInvoke.runX10Task();
			} catch (Throwable e) {
				activityToInvoke.finalizeTermination(e);
				return;
			}
			// post	
			activityToInvoke.finalizeTermination(); //should not throw an exception.
		}		
	}

	/**
	 * Run X10 activity in finish invocation strategy implementation.
	 * Consists in surronding X10 activity invocation by startFinish and stopFinish.
	 * @author vcave
	 *
	 */
	public static class AsyncInFinishStrategy extends InvocationStrategy {
		private AsyncInFinishStrategy() { 
		}
		
		public void invokeX10Task(Activity activityToInvoke) {
			activityToInvoke.startFinish();
			try {
				activityToInvoke.runX10Task();
			} catch(Throwable t) {
				activityToInvoke.pushException(t);
			}
			finally {
				activityToInvoke.stopFinish();
			}
		}		
	}
}
