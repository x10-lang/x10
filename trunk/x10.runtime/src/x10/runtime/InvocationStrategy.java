package x10.runtime;

public abstract class InvocationStrategy {

	public static final InvocationStrategy ASYNC = new DefaultStrategy();
	public static final InvocationStrategy ASYNC_IN_FINISH = new AsyncInFinishStragegy();

	public abstract void invokeX10Task(Activity activityToInvoke);
	
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

	public static class AsyncInFinishStragegy extends InvocationStrategy {
		private AsyncInFinishStragegy() { 
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
