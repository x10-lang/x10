
import java.util.concurrent.atomic.AtomicInteger;

import x10.runtime.cws.*;
import x10.runtime.cws.Job.GloballyQuiescentVoidJob;

/**
* An NQueens program that
* -- uses an counter per worker, and a global atomic counter, updated by a worker
*    when it quiesces.
* -- descends into an async instead of pushing it onto the deque.
* -- Further, the board is constructed lazily at the next ply. 
* 
* @author vj
*
*/
public class NQueensG3  {
	
	static int boardSize;
	//static Job theJob;
	public static final StealAbort abort = new StealAbort();
	
	public static final int[] expectedSolutions = new int[] {
		0, 1, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200,
		73712, 
		365596, 
	    2279184,
	    14772512};
	public  static final class MyWorker extends Worker {
		MyWorker(Pool p, int index) {
			super(p, index);
		}
		int result=0;
	}
	public static Pool.WorkerMaker workerMaker = new Pool.WorkerMaker() {
		public Worker makeWorker(Pool p, int i) {
			return new MyWorker(p, i);
		}
		
	};
	public static void main(String[] args) throws Exception {
		int procs, nReps, num;
		try {
			num = Integer.parseInt(args[2]);
			procs = Integer.parseInt(args[0]);
			nReps = Integer.parseInt(args[1]);
			System.out.println("Number of procs=" + procs +" nReps="+nReps
					+ " num=" + num);
		}
		catch (Exception e) {
			System.out.println("Usage: java NQueensG2 <threads> <numRepeatation> <num>");
			return;
		}
		Pool g = new Pool(procs,workerMaker);
		long sc = 0, sa = 0;
		int[] Ns = new int[] { num};
		for (int i = 0; i < Ns.length; i++) {
			boardSize = Ns[i];
			int result = 0;
			
			long s = System.nanoTime();
			for (int j=0; j<nReps; j++) {				
				//System.gc();
				GloballyQuiescentVoidJob job = 
					new GloballyQuiescentVoidJob(g, new NFrame(new int[0])) {
					AtomicInteger result = new AtomicInteger();
					@Override public int resultInt() { return result.get();}
					@Override
					protected void onCheckIn(Worker ww) {
						MyWorker w = (MyWorker) ww;
						int r = w.result;
						if (r!= 0) {
							result.addAndGet(r);
							w.result=0;
						}
					}
				};
				g.submit(job);
				result = job.getInt();
				
			}
			long t = System.nanoTime();
			
			System.out.println("VJCWS Queens(" + (Ns[i]) +")"+"\t"
					+(t-s)/1000000/nReps  + " ms" + "\t" + 
					(Ns[i] < expectedSolutions.length ? 
							(result==expectedSolutions[Ns[i]]?"ok" : "fail") :"")
					+"\t " + result
					+ "\t" + "steals=" +((g.getStealCount()-sc)/nReps)
	    			+ "\t" + "stealAttempts=" +((g.getStealAttempts()-sa)/nReps));
	    	  
	    	  sc=g.getStealCount();
	    	  sa=g.getStealAttempts();
		}
		g.shutdown();    
	}
	
	// Boards are represented as arrays where each cell 
	// holds the column number of the queen in that row
	
	@AllocateOnStack
	public static class NFrame extends Frame {
		final int[] sofar;
		int q;
		public NFrame(int[] a) { sofar=a;}
		public void compute(Worker ww) throws StealAbort {
			//w.popAndReturnFrame();
			final int bSize = boardSize;
			final int[] a = sofar;
			int row = a.length;
			MyWorker w = (MyWorker) ww;
			for (int myQ=q; myQ < bSize; myQ++) {
				q++;
				boolean attacked = false;
				for (int i = 0; ! attacked && i < row ; ++i) {
					final int p = myQ-a[i], delta=row-i;
					attacked = (p == 0 || p == delta || p== -delta);
				}
				if (!attacked) { 
					if (row + 1 == bSize) {
						w.result++;
					} else {
						int[] next = new int[row+1];
						System.arraycopy(a, 0, next, 0, row);
						next[row] = myQ;
						nQueens(w, next); // invoke fast code.
						w.abortOnSteal();
					}
				}
			}
			w.popFrame();
		}
		public String toString() { 
			String s="[";
			for (int i=0; i < sofar.length; i++) s += sofar[i]+","; 
			return "NF(" + s + "],q="  + q +")";
		}
	}
	public static void nQueens(MyWorker w, int[] a) throws StealAbort {
		final int bSize = boardSize;
		final int row = a.length;
		NFrame frame = new NFrame(a);
		frame.q=1;
		w.pushFrame(frame);
		for (int q=0; q < bSize; ++q) {
			boolean attacked = false;
			for (int i = 0; ! attacked &&i < row ; ++i) {
				final int p = q-a[i], delta=row-i;
				attacked = (p == 0 || p == delta || p == -delta);
			}
			if (!attacked) { 
				if (row+1 == bSize) {
					w.result++;
					
				} else {
					int[] next = new int[row+1];
					System.arraycopy(a, 0, next, 0, row);
					next[row] = q;
					nQueens(w, next);
					w.abortOnSteal();
				}
			}
			frame.q++;
		}
		w.popFrame();
	}
	
}

