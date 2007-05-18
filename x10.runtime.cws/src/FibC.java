
import cws.*;
import java.util.concurrent.atomic.AtomicInteger;

import x10.runtime.cws.Closure;
import x10.runtime.cws.Frame;
import x10.runtime.cws.Pool;
import x10.runtime.cws.Worker;

/**
 * A pointless recursive Fibonacci. Meant to be used
 * to test system performance. Implements recursive Fib
 * Cilk style
 * Notes:
 * -- the thread is always working on the current task which is also stored
 * at the bottom of its ready queue and is hence available to be stolen.
 * -- therefore the state of a task may simultaneouly be accessed by multiple
 * -- threads.
 * @author vj
 *
 */
public final class FibC  extends Closure {
	volatile int result;
//	 pointer to the closure. Is this needed? 
	FibC parent; 
	static final int ENTRY=0, LABEL_1=1, LABEL_2=2,LABEL_3=3;
	static class FibFrame extends Frame {
		int PC, n,x,y;
		public FibFrame(Worker w) {
			super(w);
		}
		public Closure makeClosure() {
			return new FibC();
		}
	}
	
	static int fib(Worker w, int n) { // fast mode
		
		FibFrame frame = new FibFrame(w);
		w.pushFrame(frame);
		if (n < 2) {
			w.popFrame();
			return n;
		}
		frame.n=n;
		frame.PC=LABEL_1; // continuation pointer
		// Need a barrier?
		
		// this thread will definitely execute fib(n-1), and
		// hence set the value in the frame.
		int x = fib(w, n-1);
		// Now need to figure out who is doing fib(n-2).
		// If frame has been stolen, then this thread wont do fib(n-2).
		// it should just return, and subsequent work will be done
		// by others. 
		FibC c = w.popFrameCheck();
		if (c != null) {
			// so the frame has been stolen and c is now being executed by someone else.
			// have to supply it the value of x.
			c.onJoin0(x);
			// now return a dummy value. The real value will be supplied
			// by c.compute().
			w.popFrame();
			return 0; // 
		}
		// Now we are back in the current frame, it has not been stolen. 
		// Execute the local code to the next spawn. 
		
		// Now at the next spawn, exactly as before, set up the 
		// continuation pointer. 
		frame.PC=LABEL_2;
		frame.x=x;
		int y=fib(w, n-2);
		c = w.popFrameCheck();
		if (c != null) {
			c.onJoin1(y);
			w.popFrame();
			return 0;
		}
		// Now there is nothing more to spawn -- so no need for the frame.
		// i.e. since the worker has made it so far, it is going to complete 
		// execution of this procedure.
		
		// pop the task -- it is guaranteed to be garbage.
		w.popFrame();
		
		// the sync is a no-op.
		// return the computed value.
		return x+y;
	}
	public FibC() {
		
	}
	public void onJoin0(int value) {
		x=value;
		continueJoin();
	}
	public void onJoin1(int value) {
		y=value;
		continueJoin();
		
	}
	
	/**
	 * This contains the slow code. 
	 */
	@Override
	public void compute(Frame frame) {
		// get the frame and push it.
		// f must be a FibFrame.
		FibFrame f = (FibFrame) frame;
		Worker w = (Worker) Thread.currentThread();
		int n;
		if (f.PC!=1 && f.PC!=2 && f.PC!=3) {
			n = f.n;
			if (n < 2) {
				result = n;
				return;
			}
			f.PC=1;
			f.n=n;
			f.fork();
			int x = fib(w, n-1);
			FibC c = w.popFrameCheck(f);
			if (c != null) {
				c.onJoin0(x);
				return;
			}
			f.x=x;
		}
		if (f.PC == 1) {
			n=f.n;
			f.PC=2;
			f.fork();
			int y=fib(w,n-2);
			FibC c = w.popFrameCheck(f);
			if (c != null) {
				c.onJoin1(y);
				return;
			}
			f.y=y;
		}
		if (f.PC <=2) {
			f.PC=3;
			if (w.sync()) 
				return;
		}
		result = f.x + f.y;
		return;
	}
	

	public static void main(String[] args) throws Exception {
		int procs;
		try {
			procs = Integer.parseInt(args[0]);
			System.out.println("Number of procs=" + procs);
		} catch (Exception e) {
			System.out.println("Usage: Fib <threads>");
			return;
		}
		final Pool g = new Pool(procs);
		final int[] points = new int[] { 1, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50
		};
		
		for (int i = 0; i < points.length; i++) {
			long s = System.nanoTime();
			FibC task = new FibC(points[i]);
			g.invoke(task);
			long t = System.nanoTime();
			System.out.println(points[i] + " " + (t-s)/1000000 
					+ " " + task.result );
		}
		g.shutdown();
	}
}


