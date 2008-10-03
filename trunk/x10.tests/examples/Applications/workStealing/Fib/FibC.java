
import x10.runtime.xws.impl.Closure;
import x10.runtime.xws.impl.Frame;
import x10.runtime.xws.impl.Job;
import x10.runtime.xws.impl.Closure.Outlet;
import x10.runtime.xws.impl.Pool;
import x10.runtime.xws.impl.Worker;
import x10.runtime.xws.impl.StealAbort;


/**
 * A pointless recursive Fibonacci written against X10 implementation of XWS.  
 * Meant to be used to test system performance.
 * Can be used to micro-benchmark the overheads of specific pieces of the XWS implementation.
 */
public class FibC {  
	/* Flags for microbenchmarking; enable/disable various pieces of XWS implementation */
	private static final boolean ELISION = false;
	private static final boolean ELIDE_DEQUE = false;
	private static final boolean ONE_FRAME = false;

	/**
	 * Closure that implements the Cilk-style Fast and Slow paths for 
	 * the following silly recursive fib method written in X10.
	 * <pre>  
	 * static def fib(n:int):int {
	 *   if (n < 2) return n;
	 *   return (async fib(n-1)) + fib(n-2);
	 * }
	 * </pre>
	 */
	private static final class FibClosure extends Closure {
		/* PC labels */
		private static final int ENTRY = 0;
		private static final int LABEL_1 = 1;
		private static final int LABEL_2 = 2;
		private static final int LABEL_3 = 4;

		/* Only used when ONE_FRAME is true */
		private static final FibFrame dummyFrame = new FibFrame(10);
		/* Used when ELIDE DEQUE to prevent JIT from optimizing away allocations of the FibFrame objects */
		private FibFrame foolTheJIT;

		private int result;
		public int resultInt() { return result;}
		public void setResultInt(int x) { result=x;}

		FibClosure(FibFrame frame) { super(frame); }

		FibClosure(int n) { this(new FibFrame(n)); }

		int fib(Worker w, int n) throws StealAbort { // fast mode
			if (n < 2) return n;
			FibFrame frame;
			if (ELISION) {
				frame = null;
			} else {
				if (ONE_FRAME) {
					frame = dummyFrame;
				} else {
					frame = new FibFrame(n);
				}
				frame.PC=LABEL_1; // continuation pointer
				if (ELIDE_DEQUE && !ONE_FRAME) {
					foolTheJIT = frame;
				} else {
					w.pushFrame(frame);
				}
			}

			// this thread will definitely execute fib(n-1), and
			// hence set the value in the frame.
			final int x = fib(w, n-1);

			if (!ELISION && !ELIDE_DEQUE) {
				// Now need to figure out who is doing fib(n-2).
				// If frame has been stolen, then this thread wont do fib(n-2).
				// it should just return, and subsequent work will be done
				// by others. 
				w.abortOnSteal(x);
			}

			// Now we are back in the current frame, it has not been stolen. 
			// Execute the local code to the next spawn. 

			if (!ELISION) {
				// Now at the next spawn, exactly as before, set up the 
				// continuation pointer. 
				frame.x=x;
				frame.PC=LABEL_2;
			}
			final int y=fib(w, n-2);
			if (!ELISION && !ELIDE_DEQUE) {
				w.abortOnSteal(y);
			}

			// Now there is nothing more to spawn -- so no need for the frame.
			// i.e. since the worker has made it so far, it is going to complete 
			// execution of this procedure.
			if (!ELISION && !ELIDE_DEQUE) {
				// pop the task -- it is guaranteed to be garbage.
				w.popFrame();
			}

			// the sync is a no-op.
			// return the computed value.
			final int result = x+y;
			return result;
		}

		public void compute(Worker w, Frame frame) throws StealAbort { // slow mode
			FibFrame f = (FibFrame)frame;
			int n = f.n;
			switch (f.PC) { // NOTE: all cases in switch are falling through!
			case ENTRY: 
				if (n < 2) {
					result = n;
					setupReturn();
					return;
				}
				f.PC=LABEL_1;
				int x = fib(w, n-1);
				w.abortOnSteal(x);
				f.x=x;

			case LABEL_1: 
				f.PC=LABEL_2;
				int y=fib(w,n-2);
				w.abortOnSteal(y);
				f.y=y;

			case LABEL_2: 
				f.PC=LABEL_3;
				if (sync(w)) {
					return;
				}
			case LABEL_3:
				result=f.x+f.y;
				setupReturn();
			}
			return;
		}

		private static final class FibFrame extends Frame {
			final int n;
			int PC;
			int x;
			int y;

			FibFrame(int n) { 
				super(); 
				this.n = n; 
			}

			public void acceptInlet(int index, int value) {
				if (index==LABEL_1) { 
					x=value;
				} else {
					y=value;
				}
			}

			public void setOutletOn(Closure c) {
				c.setOutlet(PC);
			}

			public Closure makeClosure() {
				return new FibClosure(this);
			}

			public String toString() { 
				return "FibFrame(n="+n+",x="+x+",y="+y+",PC=" + PC + ")";
			}  
		};
	};

	public static int realFib(int n) {
		if (n < 2) return n;
		int y=0,x=1;
		for (int i=0; i <= n-2; i++) {
			int temp = x; x +=y; y=temp;
		}
		return x;
	}

	public static void main(String[] args) throws Exception {
		final int procs, nReps, num;
		try {
			num = Integer.parseInt(args[2]);
			procs = Integer.parseInt(args[0]);
			nReps = Integer.parseInt(args[1]);
			System.out.println("Number of procs=" + procs + " nReps=" + nReps + " N=" + num);

		} catch (Exception e) {
			System.out.println("Usage: FibC2 <threads> <numRepeatations> <N>");
			return;
		}

		final Pool g = new Pool(procs);

		long startSC = 0;
		long startSA = 0;
		boolean valid = true;
		int result=0;
		final int realFibResult = realFib(num);
		final long[] times = new long[nReps];
		final long[] sc = new long[nReps];
		final long[] sa = new long[nReps];
		for (int j = 0; j < nReps; j++) {
			Job job = new Job(g) { 
				int result;
				public void setResultInt(int x) { result = x;}
				public int resultInt() { return result;}
				public int spawnTask(Worker ws) throws StealAbort { return new FibClosure(num).fib(ws, num); }
				public String toString() {
					return "Job(#" + hashCode() + ", fib(n=" + num +"," + status+ ",frame="+ frame+")";
				}
			};
			long startTime = System.nanoTime();
			g.invoke(job);
			result = job.getInt();
			long endTime = System.nanoTime();
			long endSC = g.getStealCount();
			long endSA = g.getStealAttempts();
			times[j] = endTime - startTime;
			sc[j] = endSC - startSC;
			sa[j] = endSA - startSA;
			startSC = endSC;
			startSA = endSA;
			if (result != realFibResult) {
				System.out.println("FAILURE: "+job);
				valid = false;
			} else {
				System.out.println("SUCCESS: "+times[j]+" "+sc[j]+" "+sa[j]+" "+job);
			}
		}

		int warmupReps = warmupReps(nReps);
		int realReps = nReps - warmupReps;
		long totalTime = 0;
		long totalSC = 0;
		long totalSA = 0;
		for (int k = warmupReps; k<nReps; k++) {
			totalTime += times[k];
			totalSC += sc[k];
			totalSA += sa[k];
		}

		System.out.println("Stats for first "+warmupReps+" iterations discarded as warmup");
		System.out.println("VJCWS Fib(" + num +")"+"\t"+(totalTime)/1000000/realReps  + " ms" +"\t" 
				+ "\t" +"steals=" +((totalSC)/realReps)
				+ "\t"+"stealAttempts=" +((totalSA)/realReps)
				+ "\t" + valid);
		g.shutdown();
	}

	private static int warmupReps(int reps) {
		if (reps == 1 || reps == 2) return 0;
		return reps / 3;
	}
}


