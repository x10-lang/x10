package x10.runtime.abstractmetrics;


public class AbstractMetricsImpl implements AbstractMetrics {

	/**
	 * Start of code to support abstract execution model
	 */
	
	/*
	 * totalOps and critPathOps keep track of operations defined by user by calls to x10.lang.perf.addLocalOps()
	 */
	private long totalOps = 0; // Total unblocked work done by this activity (in units of user-defined ops)
	private long critPathOps = 0; // Critical path length for this activity, including dependences due to child activities (in units of user-defined ops)
	
	/*
	 * totalTime, critPathTime, and resumeTime keep tracks of actual unblocked execution in each activity.  The time that an activity is spent blocked
	 * in the X10 runtime is not counted.  However, this is still an approximate estimate because it does account for time that an activity is not
	 * executing because its Java thread in the thread pool does not have an available processor.
	 */
	private long totalUnblockedTime = 0; // Total unblocked time done by this activity (in milliseconds)
	private long critPathTime = 0; // Critical path length for this activity, including dependences due to child activities (in milliseconds)
	private long resumeTime = 0; // Time at which activity was started or unblocked (whichever is most recent)
	
	
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#getTotalOps()
	 */
	synchronized public long getTotalOps() { return totalOps; }
	
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#getCritPathOps()
	 */
	synchronized public long getCritPathOps() { return critPathOps; }
	
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#addLocalOps(long)
	 */
	synchronized public void addLocalOps(long n) { totalOps += n; critPathOps += n; }
		
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#maxCritPathOps(long)
	 */
	synchronized public void maxCritPathOps(long n) { critPathOps = Math.max(critPathOps, n); }
	
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#getTotalUnblockedTime()
	 */
	synchronized public long getTotalUnblockedTime() { return totalUnblockedTime; }
	
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#getCritPathTime()
	 */
	synchronized public long getCritPathTime() { return critPathTime; }
	
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#maxCritPathTime(long)
	 */
	synchronized public void maxCritPathTime(long t) { critPathTime = Math.max(critPathTime, t); }
	
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#getResumeTime()
	 */
	synchronized public long getResumeTime() { return resumeTime; }
	
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#setResumeTime()
	 */
	synchronized public void setResumeTime() { resumeTime = getCurrentTime(); }

	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#updateIdealTime()
	 */
	synchronized public void updateIdealTime() {
		long delta = getCurrentTime() - getResumeTime();
		totalUnblockedTime += delta;
		critPathTime += delta;
	}
	
	// Use System.currentTimeMillis() for now to measure ideal execution time.
	// This can be changed in the future
	/* (non-Javadoc)
	 * @see x10.runtime.JITTimeAnalyzer#getCurrentTime()
	 */
	public long getCurrentTime() { return System.currentTimeMillis(); }
	
	
	synchronized public void addUnblockedTime(long t) { totalUnblockedTime += t; }
}
