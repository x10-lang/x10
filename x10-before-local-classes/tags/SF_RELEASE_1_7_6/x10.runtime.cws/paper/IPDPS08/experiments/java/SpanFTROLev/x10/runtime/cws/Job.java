package x10.runtime.cws;
import static x10.runtime.cws.Closure.Status.READY;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A Job is used to submit work to a pool of workers. The programmer should subclass Job
 * and specify the task to be executed in spawnTask. 
 * 
 * Large portions of code adapted from Doug Lea's jsr166y library,which code
 * carries the header: 
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 * 
 * The design of this library is based on the Cilk runtime, developed by the Cilk
 * group at MIT.
 * @author vj
 *
 */
public abstract class Job extends Closure implements Future {
	
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
	public abstract static class GloballyQuiescentJob extends Job {
		public boolean requiresGlobalQuiescence() { return true;}
		
		public GloballyQuiescentJob(Pool pool) {
			this(pool, new GFrame());
		}
		public GloballyQuiescentJob(Pool pool, Frame f) {
			super(f, pool);
			parent = null;
			joinCount=0;
			status = READY;
		}
		@Override
		protected void compute(Worker w, Frame frame) throws StealAbort {
			GFrame f = (GFrame) frame;
			int PC = f.PC;
			f.PC=LABEL_1;
			if (PC==0) {
				// spawning
				int x = spawnTask(w);
				w.abortOnSteal(x);
				f.x=x;
				// Accumulate into result.
				int old = resultInt();
				accumulateResultInt(f.x);
				//if (Worker.reporting)
				//System.out.println( w + " " + this + " adds " + f.x + " to move " + old + " --> " + resultInt());
			}
			setupGQReturn();
		}
		
	}
	public static class GloballyQuiescentVoidJob extends GloballyQuiescentJob {
		public GloballyQuiescentVoidJob(Pool pool, Frame f) {
			super(pool, f);
		}
		@Override
		protected void compute(Worker w, Frame frame) throws StealAbort {
			frame.compute(w);
			// The completion of the job might leave behind work (frames).
			// Do not pop the framestack.
			setupGQReturnNoArgNoPop();
		}
		@Override
		public int spawnTask(Worker ws) throws StealAbort {
			assert false;
			return 0;
		}
	}
	/**
	 * GloallyQuiescentJob and the closures invoked by their computations use GFrames.
	 * @author vj
	 *
	 */
	public static class GFrame extends JobFrame {
//		 The label at which computation must be continued by the associated
		// closure.
		public volatile int PC;
		public void setOutletOn(final Closure c) {
//			 nothing needs to be done since the abort mechanism
			// will directly feed the answer into the global closure,
			// bypassing the closure return chain.
		}
		public Closure makeClosure() {
			return null;
		}
		public String toString() {
			return "GFrame(#" + hashCode() +  " PC=" + PC+")";
		}
		public GFrame() { super();}
	}
	public static class JobFrame extends Frame {
//		 The label at which computation must be continued by the associated
		// closure.
		public volatile int PC;
		public int x;
		public JobFrame() {
			super();
		}
		public Closure makeClosure() {
			assert false;
			return null;
		}
		public void setOutletOn(final Closure c) {
			c.setOutlet(
					new Outlet() {
						public void run() {
							int v = c.resultInt();
							x = v;
							if (Worker.reporting)
								System.out.println(Thread.currentThread() + " transfers "
										+ v + " to " + JobFrame.this +".x");
						}
						public String toString() { return "OutletInto x from " + c;}
						});
		}
		public String toString() {
			return "JobFrame(#" + hashCode() + ",x=" + x + ", PC=" + PC+")";
		}
	}
	final Pool pool;
	public Job(Pool pool) {
		this(new JobFrame(), pool);
	}
	
	Job(Frame f, Pool pool) {
		super(f);
		this.pool=pool;
		parent = null;
		joinCount=0;
		status = READY;
		
	}
	public static final int LABEL_0=0,LABEL_1=1, LABEL_2=2, LABEL_3=3;
	@Override
	protected void compute(Worker w, Frame frame) throws StealAbort {
		JobFrame f = (JobFrame) frame;
		switch (f.PC) {
		case LABEL_0: 
			f.PC=LABEL_1;
			// spawning
			int x = spawnTask(w);
			w.abortOnSteal(x);
			f.x=x;
		case LABEL_1: 
			f.PC=LABEL_2;
			if (sync(w)) return;
		case LABEL_2: 
			setResultInt(f.x);
			setupReturn();
		}
		return;
	}
	abstract public int spawnTask(Worker ws) throws StealAbort;
	public void completed() {
		super.completed();
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
}
