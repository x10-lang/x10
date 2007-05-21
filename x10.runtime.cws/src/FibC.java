
import x10.runtime.cws.Closure;
import x10.runtime.cws.Frame;
import x10.runtime.cws.Pool;
import x10.runtime.cws.Worker;
import x10.runtime.cws.Main;


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
public class FibC  extends Closure {
	volatile int result;
	static final int ENTRY=0, LABEL_1=1, LABEL_2=2,LABEL_3=3;
	static class FibFrame extends Frame {
		int PC, n,x,y;
		public FibFrame() {
			super();
		}
		public Closure makeClosure() {
			return PC==LABEL_2 
			? new FibC() {
				@Override
				public void executeAsInlet() {
					((FibFrame) parent.frame).y  = result;
				}
				public String toString() {
					return "Fib1(n=" + n + ")";
				}
			}: new FibC() {
				@Override
				public void executeAsInlet() {
					((FibFrame) parent.frame).x  = result;
				}
				public String toString() {
					return "Fib2(n=" + n + ")";
				}
			};
				
		}
	}
	@Override
	public void executeAsInlet() {}
	
	static int fib(Worker w, int n) { // fast mode
		
		FibFrame frame = new FibFrame();
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
		Closure c = w.popFrameCheck();
		if (c != null) {
			// so the frame has been stolen and c is now being executed by someone else.
			// have to supply it the value of x.
			if (w.lastFrame())
				((FibC) c).result = x;
			// now return a dummy value. The real value will be supplied
			// by c.compute().
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
			if (w.lastFrame())
				((FibC) c).result = y;
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
	public FibC() {}
	/**
	 * This contains the slow code. 
	 */
	@Override
	public void compute(Worker w, Frame frame) {
		// get the frame and push it.
		// f must be a FibFrame.
		FibFrame f = (FibFrame) frame;
		int n;
		if (f.PC!=LABEL_1 && f.PC!=LABEL_2 && f.PC!=LABEL_3) {
			n = f.n;
			if (n < 2) {
				result = n;
				setupReturn();
				return;
			}
			f.PC=LABEL_1;
			f.n=n;
			// need a mem barrier here to ensure that everyone sees these writes.
			int x = fib(w, n-1);
			Closure c = w.popFrameCheck();
			if (c != null) {
				if (w.lastFrame())
					((FibC) c).result = x;
				return;
			}
			f.x=x;
			
		}
		if (f.PC == LABEL_1) {
			n=f.n;
			f.PC=2;
			// need a mem barrier here to ensure that everyone sees these writes.
			int y=fib(w,n-2);
			Closure c = w.popFrameCheck();
			if (c != null) {
				if (w.lastFrame())
					((FibC) c).result = y;
				return;
			}
			f.y=y;
		}
		if (f.PC <= LABEL_2) {
			f.PC=LABEL_3;
			if (sync()) {
				
				return;
			}
		}
		result=f.x+f.y;
		setupReturn();
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
		final int[] points = new int[] { 10// 1,5, 10, 15, 20, 25, 30, 35, 40
		};
		final Object lock = new Object();
		for (int i = 0; i < points.length; i++) {
			final int n = points[i];
			class FibMain extends Main {
				public FibMain(Object lock) {
					super(lock);
				}
				@Override
				public int spawnTask(Worker ws) {
					return fib(ws, n);
				}
				public String toString() {
					return "FibMain(n=" + n + ")";
				}
			}
			Main task = new FibMain(lock);
			long s = System.nanoTime();
			g.submit(task);
			try {
				synchronized (lock) {
					lock.wait();
				}
			} catch (InterruptedException z) {
				System.out.println("Caught interrupted exception " + z);
			}
			long t = System.nanoTime();
			System.out.println(points[i] + " " + (t-s)/1000000 
					+ " " + task.result );
		}
		g.shutdown();
	}
}


