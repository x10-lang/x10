

import java.util.concurrent.atomic.AtomicInteger;

import x10.runtime.cws.*;
import static x10.runtime.cws.Job.*;

/**
 * An NQueens program that
 * -- uses an counter per worker, and a global atomic counter, updated by a worker
 *    when it quiesces.
 * -- pushes asyncs directly onto the worker's deque instead of descending into them
 * -- Further, the board is constructed lazily at the next ply. 
 * 
 * @author vj
 *
 */
public class NQueensG {
	
	static int boardSize;
	public  static final class MyWorker extends Worker {
		MyWorker(Pool p, int index) {
			super(p, index);
		}
		int result;
	}
	public static Pool.WorkerMaker workerMaker = new Pool.WorkerMaker() {
		public Worker makeWorker(Pool p, int i) {
			return new MyWorker(p, i);
		}
	};
	public boolean requiresGlobalQuiescence() { return true; }
	public static final int[] expectedSolutions = new int[] {
		0, 1, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200,
		73712, 365596, 2279184, 14772512};
	
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
			System.out.println("Usage: java NQueensG <threads> <numRepeatation> <N>");
			return;
		}
		Pool g = new Pool(procs, workerMaker);
		long sc=0, sa=0;
		int[] Ns = new int[] { num};
		for (int i = 0; i < Ns.length; i++) {
			boardSize = Ns[i];
			int result=0;
			long s = System.nanoTime();
			for (int j=0; j<nReps; j++) {				
				//System.gc();
				GloballyQuiescentJob job = 
					new GloballyQuiescentVoidJob(g, new NFrame(new int[0])) {
					AtomicInteger result = new AtomicInteger();
					@Override public int resultInt() { return result.get();}
					@Override
					protected void onCheckIn(Worker ww) {
						MyWorker w = (MyWorker) ww;
						if (w.result != 0) {
							result.addAndGet(w.result);
							w.result=0;
						}
					}
				};
				g.submit(job);
				result = job.getInt();
			}
			long t = System.nanoTime();
			System.out.println("VJXWS Queens(" + (Ns[i]) +")"+"\t"
					+(t-s)/1000000/nReps  + " ms" + "\t" + 
					(Ns[i] < expectedSolutions.length ? 
							(result==expectedSolutions[Ns[i]]?"ok" : "fail") :"")
					+"\t " + result
					+ " " + "steals=" +((g.getStealCount()-sc)/nReps)
	    			+ " " + "stealAttempts=" +((g.getStealAttempts()-sa)/nReps));
	    	  
	    	  sc=g.getStealCount();
	    	  sa=g.getStealAttempts();
			
		}
		g.shutdown();    
	}
	
	public static class NFrame extends GFrame {
		final int[] sofar;
		int newQ;
		public NFrame(int[] a) { sofar=a; newQ=-1; }
		public NFrame(int[] a, int q) { sofar=a; newQ=q; }
		
		public void compute(Worker ww) throws StealAbort {
			ww.popAndReturnFrame();
			int[] a = sofar;
			int row = a.length;
			if (newQ >=0) {
				int[] next = new int[row+1];
				System.arraycopy(a,0,next,0,row);
				next[row++]=newQ;
				a=next;
			}
			MyWorker w = (MyWorker) ww;
			for (int q=0; q < boardSize; ++q) {
				boolean attacked = false;
				for (int i = 0;  ! attacked && i < row; ++i) {
					final int p = q-a[i], delta=row-i;
					attacked = (p==0 || p == delta || p == -delta);
				}
				if (!attacked) { 
					if (row+1==boardSize) {
						w.result++;
					} else {
						NFrame nextPly = new NFrame(a, q);
						w.pushFrame(nextPly);
					}
				}
			}
			
		}
		public String toString() { 
			String s="[";
			for (int i=0; i < sofar.length; i++) s += sofar[i]+","; 
			return "GNF(" + s + "]"  +")";
		}
	}
}

