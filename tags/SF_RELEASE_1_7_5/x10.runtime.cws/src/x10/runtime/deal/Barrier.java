package x10.runtime.deal;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
public class Barrier {
	final Runnable barrierAction;
	int phaseNum=0;
	final int poolSize;
	 /**
     * Main lock protecting access to threads and run state. You might
     * think that having a single lock and condition here wouldn't
     * work so well. But serializing the starting and stopping of
     * worker threads (its main purpose) helps enough in controlling
     * startup effects, contention vs dynamic compilation, etc to be
     * better than alternatives.
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Condition triggered when new work is available so workers
     * should awaken if blocked. 
     */
    private final Condition barrier = lock.newCondition();
//  These fields must be accessed only while holding lock.
	protected int  nextSize=0, lastSize=0, numCheckedOut=0;
	public Barrier(Runnable ba, int size) {
		super();
		this.poolSize = size;
		this.barrierAction = ba;
		this.numCheckedOut = poolSize;
	}

	public String toString() {
		return "Barrier(phase=" + phaseNum + ",numCheckedOut="+numCheckedOut+")";
	}
	
	private void awakenOthersWithLock() {
		numCheckedOut=poolSize;
		lastSize=nextSize;
		nextSize=0;
		barrier.signalAll();
		if (  Worker.reporting)
			System.out.println(Thread.currentThread() + " awakens others at " + this);

	}
	
	/**
	 * Awaken the poolsize-1 workers waiting for a new job.
	 * package protected
	 */
	void awakenOthers() {
		lock.lock();
		try {
			awakenOthersWithLock();
		} finally {
			lock.unlock();
		}
	}
	/**
	 * Return the number of tasks created for the current phase.
	 * @return
	 */
	public int numTasksInPhase() {
		return lastSize;
	}
	/**
	 * Wait until all other workers have entered the barrier. The last worker to
	 * join the barrier will return false if there is more work to be done. In this
	 * case it will also increment the phaseNum, and signal all the other workers 
	 * (who will be waiting in the barrier) to proceed. 
	 * 
	 * The last worker will return true in case there is no more work to do.
	 * In this case phaseNum will be set to 0, and the remaining workers will be left 
	 * suspended at the barrier. (It is this worker's responsibility to retrieve a job 
	 * from the JobQueue and signal the suspended workers to continue.) Befoer returning
	 * the last worker will also invoke the barrier's action.
	 * 
	 * 
	 * @param w
	 * @param inPhase
	 * @param workSize
	 * @return
	 * @throws Worker.AdvancePhaseException
	 */
	public boolean arrive(Worker w, int inPhase,  int workSize) 
	throws Worker.AdvancePhaseException {
		if (inPhase !=phaseNum) {
			// TODO: Why should this ever happen?
			System.err.println("Invariant III violation at " + this);
			System.err.println(w + "calls arrive with phase " + inPhase + " on " + this);
			//	System.err.println(w + "checkInHistory:");
			//	for (String s : w.checkinHistory) System.err.println("  :" + s);
			Worker.AdvancePhaseException z = new Worker.AdvancePhaseException();
			z.printStackTrace();
			throw z;
		}
		assert inPhase == phaseNum;

		if (  Worker.reporting)
			System.err.println(Thread.currentThread() 
					+ " with workSize=" + workSize
					+ " checks in to " + this);
		lock.lock();
		try {
			nextSize += workSize;
			final int count = --numCheckedOut;
			if (count > 0) {
				try {
					barrier.await();
				} catch (InterruptedException z) {
					System.out.println(w + " interrupted in barrier.await.");
				}
				return false;
			} else {
				lastSize=nextSize;
				nextSize=0;
				if (lastSize > 0) { // Advance the phase.
					phaseNum++;
					if (  Worker.reporting)
						System.err.println(Thread.currentThread() + " moves barrier up to " + this);
					awakenOthersWithLock();
					
					return false;
				} else { 
					// Complete the job, and let the other workers sleep to be awakened
					// later using awakenOthers().
					phaseNum=0;
					lastSize=0;
					if ( Worker.reporting)
						System.err.println(Thread.currentThread() + " completes job." );
					if (barrierAction != null) 	
						barrierAction.run();
					return true;
				}
			} 
		} finally {
			lock.unlock();
		}
	}
}


