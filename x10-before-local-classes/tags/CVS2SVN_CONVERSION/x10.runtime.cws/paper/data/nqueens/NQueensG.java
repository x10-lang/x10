

import java.util.concurrent.atomic.AtomicInteger;

import x10.runtime.cws.*;
import x10.runtime.cws.Closure.Outlet;
import static x10.runtime.cws.Job.*;

public class NQueensG extends Closure {
	
	static int boardSize;
	public boolean requiresGlobalQuiescence() { return true; }
	public static final int[] expectedSolutions = new int[] {
		0, 1, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200,
		73712, 365596, 2279184, 14772512};
	
	public static void main(String[] args) throws Exception {
		int procs;
		try {
			procs = Integer.parseInt(args[0]);
			System.out.println("Number of procs=" + procs);
		}
		catch (Exception e) {
			System.out.println("Usage: java NQueensG <threads> ");
			return;
		}
		Pool g = new Pool(procs);
		for (int i = 11; i < 16; i++) {
			boardSize = i;
			GloballyQuiescentJob job = new GloballyQuiescentJob(g) {
//				Result is accumulated here.
				AtomicInteger result = new AtomicInteger();
				
				public void setResultInt(int x) { result.set(x);}
				public int resultInt() { return result.get();}
				public void accumulateResultInt(int x) { result.addAndGet(x);}
				@Override
				public int spawnTask(Worker ws) throws StealAbort { 
					return nQueens(ws, new int[]{});
				}
				public String toString() { 
					return "GJob(NQG,#" + hashCode()+",i="+ boardSize 
					+ ",status=" + status + ",frame=" + frame + ")";}
			};
			System.gc();
			long s = System.nanoTime();
			g.submit(job);
			int result = job.getInt();
			long t = System.nanoTime();
			System.out.println("Result:" + i + 
					" " + result + (job.resultInt()==expectedSolutions[i]?" ok" : " fail") 
					+ " Time=" +  (t-s)/1000000  + " ms"
					+  " Steals=" + g.getStealCount() 
			);
		}
		g.shutdown();    
	}
	
	// Boards are represented as arrays where each cell 
	// holds the column number of the queen in that row
	
	NQueensG(NFrame f) { 
		super(f);
	}
	public static class NFrame extends GFrame {
//		 The label at which computation must be continued by the associated
		// closure.
		public volatile int PC;
		final int[] sofar;
		volatile int q;
		int sum;
		public NFrame(int[] a) { sofar=a;}
		@Override public void setOutletOn(final Closure c) {
			c.setOutlet(
					new Outlet() {
						public void run() {
							int value = c.resultInt();
							
							sum += value;
							
							if ( Worker.reporting) {
								Closure p = c.parent();
								System.out.println(Thread.currentThread() + " " + c + " --> " + value
										+ " into " + p);
							}
						}
						public String toString() { return "OutletInto x from " + c;}
					});
		}
		public Closure makeClosure() {
			Closure c = new NQueensG(this);
			return c;
		}
		public String toString() { 
			String s="[";
			for (int i=0; i < sofar.length; i++) s += sofar[i]+","; 
			return "GNF(" + s + "],q="  + q + ",sum=" + sum+")";
		}
	}
	public static final int LABEL_0=0, LABEL_1=1, LABEL_2=2, LABEL_3=3;

	@Override public void accumulateResultInt(int x) { 
		Worker w  = (Worker) Thread.currentThread();
		w.currentJob().accumulateResultInt(x);
	}
	int result;
	@Override public void setResultInt(int x) { result=x;}
	@Override public int resultInt() { return result;}
	
	public static int nQueens(Worker w, int[] a) throws StealAbort {
		final int row = a.length;
		if (row >= boardSize) {
			return 1;
		}
		NFrame frame = new NFrame(a);
		frame.q=1;
		frame.sum=0;
		frame.PC=LABEL_1;
		w.pushFrame(frame);
		//int sum=0;
		int q=0;
		while (q < boardSize) {
			boolean attacked = false;
			for (int i = 0; i < row && ! attacked; i++) {
				int p = a[i];
				attacked = (q == p || q == p - (row - i) || q == p + (row - i));
			}
			if (!attacked) { 
				int[] next = new int[row+1];
				for (int k = 0; k < row; ++k) 
					next[k] = a[k];
				next[row] = q;
				
				final int y = nQueens(w, next);
				w.abortOnSteal(y);
				//sum +=y;
				frame.sum +=y;
			}
			q++;
			frame.q=q+1;
		}
		w.popFrame();
		return frame.sum;
		
	}
	public void compute(Worker w, Frame frame) throws StealAbort {
		NFrame f = (NFrame) frame;
		final int[] a = f.sofar;
		int row = a.length;
		
		switch (f.PC) {
		case LABEL_0:
			if (row >= boardSize) {
				result=1;
				setupReturn();
				return;
			}
		case LABEL_1: 
			int q=f.q;
			//int sum=0;
			while (q < boardSize) {
				f.q =q+1;
				boolean attacked = false;
				for (int i = 0; i < row && ! attacked; i++) {
					int p = a[i];
					attacked = (q == p || q == p - (row - i) || q == p + (row - i));
				}
				if (!attacked) { 
					int[] next = new int[row+1];
					for (int k = 0; k < row; ++k) 
						next[k] = a[k];
					next[row] = q;
					
					int y= nQueens(w, next);
					w.abortOnSteal(y);
					//sum += y;
					// this cannot be f.sum=y. f.sum may have been updated by other
					// joiners in the meantime.
					f.sum +=y;
					
				}
				q++;
			}
			/*f.PC=LABEL_2;
			if (w.sync())
				return;
		case LABEL_2:*/
			result=f.sum;
			setupReturn();
		}
		return;
	}
	public String toString() { return "NQG(#" + hashCode()+" " + frame + ",result=" 
		+ result+",jc=" + joinCount + ")";}
	
}

