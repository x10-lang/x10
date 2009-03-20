
import java.util.concurrent.atomic.AtomicInteger;


import x10.runtime.cws.*;
import x10.runtime.cws.Job.GloballyQuiescentJob;
import x10.runtime.cws.Job.GloballyQuiescentVoidJob;

/**
 * Computes N^M, by counting up in 1s. 
 * A simple test for work-stealing.
 * @author vj
 *
 */
public class Power  {
	
	public static final StealAbort abort = new StealAbort();
	
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
	static int M, N;
	public static void main(String[] args) throws Exception {
		int procs, nReps=1, num;
		try {
			N = Integer.parseInt(args[2]);
			procs = Integer.parseInt(args[0]);
			M = Integer.parseInt(args[1]);
			System.err.println("Number of procs=" + procs +" M="+M
					+ " N=" + N);
		}
		catch (Exception e) {
			System.out.println("Usage: java CounterTest1 <threads> <M> <N>");
			return;
		}
		Pool g = new Pool(procs,workerMaker);
		long sc = 0, sa = 0;
		int[] Ns = new int[] { N};
		for (int i = 0; i < Ns.length; i++) {
			int result = 0;
			
			long s = System.nanoTime();
			for (int j=0; j<nReps; j++) {				
				//System.gc();
				GloballyQuiescentVoidJob job = 
					new GloballyQuiescentVoidJob(g, new CFrame(0,0)) {
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
					@Override public String toString() { 
						return "Power<#" + hashCode() + ">";
					}
				};
				g.submit(job);
				result = job.getInt();
				
			}
			long t = System.nanoTime();
			
			System.err.println("VJXWS Counter1(" + (Ns[i]) +")"+"\t"
					+(t-s)/1000000/nReps  + " ms" 
					+"\t " + result + " " + (result==Math.pow(N,M))
					+ "\t" + "steals=" +((g.getStealCount()-sc)/nReps)
	    			+ "\t" + "stealAttempts=" +((g.getStealAttempts()-sa)/nReps));
	    	  
	    	  sc=g.getStealCount();
	    	  sa=g.getStealAttempts();
		}
		g.shutdown();    
	}
	
	public static class CFrame extends Frame {
		final int base;
		int q;
		public CFrame(int base, int q) { this.base=base;this.q=q;}
		public void compute(Worker w) throws StealAbort {
			for (int myQ=q; myQ < N; ++myQ) {
				++q;
				counter((MyWorker) w, base+1); // invoke fast code.
				w.abortOnSteal();
			} 
			w.popFrame();
		}
		public String toString() { 
			return "CF(#" + hashCode() + " " + base + ",q="  + q +")";
		}
	}
	public static void counter(MyWorker w, int base) throws StealAbort {
		if (base >= M) {
			w.result++;
			return;
		}
		CFrame frame = new CFrame(base,0);
		w.pushFrame(frame);
		for (int q=0; q < N; ++q) {
			++frame.q;
			counter(w, base+1);
			w.abortOnSteal();
		} 
		w.popFrame();
		 
	}
	
}

