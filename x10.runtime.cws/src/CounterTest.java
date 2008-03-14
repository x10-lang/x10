

import java.util.concurrent.atomic.AtomicInteger;

import x10.runtime.cws.*;
import static x10.runtime.cws.Job.*;

public class CounterTest {
	
	static int M=20,N=30;
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
	
	public static void main(String[] args) throws Exception {
		int procs, nReps, num;
		try {
			
			procs = Integer.parseInt(args[0]);
			M = Integer.parseInt(args[1]);
			N = Integer.parseInt(args[2]);
			System.out.println("Number of procs=" + procs +" M="+M
					+ " N=" + N);
		}
		catch (Exception e) {
			System.out.println("Usage: java CounterTest <threads> <M> <N>");
			return;
		}
		Pool g = new Pool(procs, workerMaker);
		long sc=0, sa=0;
		int[] Ns = new int[] { N};
		for (int i = 0; i < Ns.length; i++) {
			N = Ns[i];
			int result=0;
			long s = System.nanoTime();
			for (int j=0; j< 1; j++) {				
				//System.gc();
				GloballyQuiescentJob job = 
					new GloballyQuiescentVoidJob(g, new CFrame(0)) {
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
			System.out.println("VJXWS TestCounter(" + (Ns[i]) +")"+"\t"
					+(t-s)/1000000/1 
					+ " ms\t" +  result + " " + (result == 1510640)
					+ " " + "steals=" +((g.getStealCount()-sc)/1)
	    			+ " " + "stealAttempts=" +((g.getStealAttempts()-sa)/1));
	    	  
	    	  sc=g.getStealCount();
	    	  sa=g.getStealAttempts();
			
		}
		g.shutdown();    
	}

	public static class CFrame extends GFrame {
		int count, q;
		public CFrame(int count) { this.count=count; this.q=0;}
		public void compute(Worker w) throws StealAbort {
			w.popFrame();
			for (int i = q; i < N; ++i) {
				q++;
				((MyWorker) w).result++;
				if (i%2==0) { 
					if (count >=M) {
						((MyWorker) w).result++;
					} else {
						CFrame nextPly = new CFrame(count+1);
						w.pushFrame(nextPly);
					}
				}
			}
		
		}
		public String toString() { 
			return "NF(q=" + q + "count="+ count +")";
		}
	}
}

