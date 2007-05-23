package x10.runtime.cws;


import static x10.runtime.cws.ClosureStatus.*;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.Future;


public abstract class Job extends Closure implements Future {
	public volatile int result;
	public int resultInt() { return result;}
	public static class JobFrame extends Frame {
		
		volatile public int PC;
		public int x;
		public JobFrame() {
			super();
		}
		public Closure makeClosure() {
			assert false;
			return null;
		}
		public void setOutletOn(final Closure c) {
			if (false && Worker.reporting) {
				System.out.println(Thread.currentThread() + ": " + this + " sets outlet on " + c);
			}
			c.setOutlet(
					new Outlet() {
						
						public void run() {
							JobFrame f = (JobFrame) c.parentFrame();
							int v = c.resultInt();
							if (false && Worker.reporting)
								System.out.println(Thread.currentThread() + " " + this 
										+ " sets x on " + f + " to " + v);
							f.x = v;
						}
						public String toString() { return "OutletInto x from " + c;}
						});
		}
		public String toString() {
			return "JobFrame(#" + hashCode() + " " + x + ", PC=" + PC+")";
		}
	}
	final Pool pool;
	public Job(Pool pool) {
		super(new JobFrame());
		this.pool=pool;
		parent = null;
		joinCount=0;
		status = READY;
		
	}
	public static final int LABEL_1=1, LABEL_2=2, LABEL_3=3;
	@Override
	protected void compute(Worker w, Frame frame) {
		JobFrame f = (JobFrame) frame;
		
		if (f.PC!=LABEL_1 && f.PC!=LABEL_2) {
			f.PC=LABEL_1;
			// spawning
			try {
				int x = spawnTask(w);
				if (w.popFrameCheck(x)) return;
				f.x=x;
			} catch (StealAbort z) {
				
				return;
			}
		}
		if (f.PC < LABEL_2) {
			f.PC=LABEL_2;
			if (sync()) {
				return;
			}
		}
		result=f.x;
		setupReturn();
		return;
	}
	
	abstract public int spawnTask(Worker ws) throws StealAbort;
	public void completed() {
		super.completed();
		//System.out.println("Completed " + this + " result=" + result);
        synchronized(this) {
            notifyAll();
        }
        pool.jobCompleted();
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
