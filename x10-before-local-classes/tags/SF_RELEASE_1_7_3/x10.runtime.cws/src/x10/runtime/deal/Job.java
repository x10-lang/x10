package x10.runtime.deal;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A Job is used to submit work to a pool of workers. The programmer should subclass Job
 * and specify the task to be executed in spawnTask. 
 * 
 *
 * @author vj
 *
 */
public abstract class Job  implements Executable {
	
	public long startTime, completionTime;
	public long executionTime() {
    	
    	return completionTime - startTime;
    }
	/**
	 * A globally quiescent job is one that detects termination through
	 * global quiescence, i.e. when all workers have discovered this job
	 * is to be worked on and have no more work to do. They discover they
	 * have no more work to do when they have tried two rounds of thefts
	 * and failed. (This is a heuristic and needs to be examined further.)
	 * The last worker to stop working 
	 * 
	 * 
	 * @author vj
	 *
	 */
	

	final Pool pool;
	public Job(Pool pool) {
		super();
		this.pool=pool;
	}
	Executable next;
	public Executable next() { return next;}
	public void setNext(Executable e) { next=e;}

	public void completed() {
		done=true;
		if ( Worker.reporting)
			System.out.println(Thread.currentThread() + " completed.");
		synchronized(this) {
			notifyAll();
		}
		pool.jobCompleted();
	}
	public synchronized void waitForCompletion() throws InterruptedException {
		while (!isDone()) wait();
	}
    
    public boolean isCancelled() { return false;}
    
    public boolean cancel(boolean b) { return false;}

    /**
     * Return result or throw exception using Future conventions
     */
    Object futureResult() throws ExecutionException {
        return resultObject();
    }

    public synchronized Object get() throws InterruptedException,
                                       ExecutionException {
        while (!isDone())
            wait();
        return futureResult();
    }

    public synchronized int getInt() throws InterruptedException, ExecutionException {
    	while (! isDone()) wait();
    	return resultInt();
    }
    protected boolean done = false;

    public boolean isDone() { return done;}
    // should be overridden
    public  int resultInt() { return 0;}
    // should be overridden
    public Object resultObject() { return null;}
	
    
    public synchronized Object get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {
        if (!isDone()) {
            long last = System.nanoTime();
            long nanos = unit.toNanos(timeout);
            while (!isDone()) {
                long now = System.nanoTime();
                nanos -= now - last;
                last = now;
                if (nanos > 0)
                    TimeUnit.NANOSECONDS.timedWait(this, nanos);
                else
                    throw new TimeoutException();    
            }
        }
        return futureResult();
    }
	/**
	 * Each worker must call this method on the job each time the worker checks in (i.e.
	 * goes looking for work). This permits the programmer to specify worker-specific 
	 * finalization of application data-structures after each episode of computation.
	 * 
	 * @param w
	 */
	protected void onCheckIn(Worker w) {
		assert Thread.currentThread()==w;
		// no action.
	}
}
