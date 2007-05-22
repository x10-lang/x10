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
	static class JobFrame extends Frame {
		
		int PC,x;
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
							x = c.resultInt();
						}});
		}
		public String toString() {
			return "MainFrame(" + PC + ", " + x+")";
		}
	}
	final Pool pool;
	public Job(Pool pool) {
		super();
		this.pool=pool;
		parent = null;
		joinCount=0;
		status = READY;
		frame = new JobFrame();
	}
	public static final int LABEL_1=1, LABEL_2=2, LABEL_3=3;
	@Override
	protected void compute(Worker w, Frame frame) {
		JobFrame f = (JobFrame) frame;
	
		if (f.PC!=LABEL_1 && f.PC!=LABEL_2) {
			f.PC=LABEL_1;
			// spawning
			int x = spawnTask(w);
			Closure c = w.popFrameCheck();
			if (c !=null) {
				if (w.lastFrame())
				((Job) c).result = x;
				return;
			}
			f.x=x;
		}
		
		if (f.PC <= LABEL_2) {
			f.PC=LABEL_3;
			if (sync()) {
				return;
			}
		}
		result=f.x;
		synchronized (lock) {
			lock.notifyAll();
		}
		setupReturn();
		return;
	}
	
	abstract public int spawnTask(Worker ws);
	public void completed() {
        synchronized(this) {
        	done= true;
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
