

import x10.runtime.cws.*;

public class NQueensC extends Closure {
	
	static int boardSize;
	public static final StealAbort abort = new StealAbort();
	
	public static final int[] expectedSolutions = new int[] {
		0, 1, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200,
		73712, 365596, 2279184, 14772512};
	
	public static void main(String[] args) throws Exception {
		int procs, nReps;
		try {
			procs = Integer.parseInt(args[0]);
			nReps = Integer.parseInt(args[1]);
			System.out.println("Number of procs=" + procs +" nReps="+nReps);
		}
		catch (Exception e) {
			System.out.println("Usage: java NQueensC <threads> <numRepeatation>");
			return;
		}
		Pool g = new Pool(procs);
		long sc = 0, sa = 0;
		for (int i = 1; i < 16; i++) {
			boardSize = i;
			/*Job job = new Job(g) {
				int result;
				public void setResultInt(int x) { result=x;}
				public int resultInt() { return result;}
				@Override
				public int spawnTask(Worker ws) throws StealAbort { 
					return nQueens(ws, new int[]{});
				}
				public String toString() { return "Job(NQ,#" + hashCode()+")";}
			};
						
			g.submit(job);
			int result = job.getInt();*/
			int result = 0;
			
			long s = System.nanoTime();
			for (int j=0; j<nReps; j++) {				
				//System.gc();
				Job job = new Job(g) {
					int result;
					public void setResultInt(int x) { result=x;}
					public int resultInt() { return result;}
					@Override
					public int spawnTask(Worker ws) throws StealAbort { 
						return nQueens(ws, new int[]{});
					}
					public String toString() { return "Job(NQ,#" + hashCode()+")";}
				};
				
				g.submit(job);
				result = job.getInt();
				
			}
			long t = System.nanoTime();
			
			System.out.println("VJCWS Queens(" + i +")"+"\t"+(t-s)/1000000/nReps  + " ms" + "\t" + 
					(result==expectedSolutions[i]?"ok" : "fail")
					+ "\t" + "steals=" +((g.getStealCount()-sc)/nReps)
	    			+ "\t" + "stealAttempts=" +((g.getStealAttempts()-sa)/nReps));
	    	  
	    	  sc=g.getStealCount();
	    	  sa=g.getStealAttempts();
			/*System.out.println("Result:" + i + 
					" " + result + (result==expectedSolutions[i]?" ok" : " fail") 
					+ " Time=" +  (t-s)/1000000/nReps  + "ms "
					+  " Steals=" + g.getStealCount() 
					);*/
			
		}
		g.shutdown();    
	}
	
	// Boards are represented as arrays where each cell 
	// holds the column number of the queen in that row
	
	NQueensC(NFrame f) { 
		super(f);
	}
	@AllocateOnStack
	public static class NFrame extends Frame {
//		 The label at which computation must be continued by the associated
		// closure.
		public volatile int PC;
		final int[] sofar;
		volatile int q;
		int sum;
		@Override
		public void setOutletOn(final Closure c) {
			c.setOutlet(
					new Outlet() {
						public void run() {
							NFrame f = (NFrame) c.parentFrame();
							int value = c.resultInt();
							f.sum += value;
							if (Worker.reporting) {
								System.out.println(Thread.currentThread() + " --> " + value
										+ " into " + c.parent() + " from " + c);
							}
						}
						public String toString() { return "OutletInto x from " + c;}
					});
		}
		public Closure makeClosure() {
			Closure c = new NQueensC(this);
			return c;
		}
		public NFrame(int[] a) { sofar=a;}
		public String toString() { 
			String s="[";
			for (int i=0; i < sofar.length; i++) s += sofar[i]+","; 
			return "NF(" + s + "],q="  + q + ",sum=" + sum+")";
		}
	}
	public static final int LABEL_0=0, LABEL_1=1, LABEL_2=2, LABEL_3=3;

	int result;
	@Override
	public void setResultInt(int x) { result=x;}
	@Override
	public int resultInt() { return result;}
	
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
		int sum=0;
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
				sum +=y;
				frame.sum +=y;
			}
			q++;
			frame.q=q+1;
		}
		w.popFrame();
		return sum;
		
	}
	public void compute(Worker w, Frame frame) throws StealAbort {
		NFrame f = (NFrame) frame;
		final int[] a = f.sofar;
		int row = a.length;
		
		switch (f.PC) {
		case LABEL_0:
			if (row >= boardSize) {
				result =1;
				setupReturn();
				return;
			}
		case LABEL_1: 
			int q=f.q;
			int sum=0;
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
					sum += y;
					// this cannot be f.sum=y. f.sum may have been updated by other
					// joiners in the meantime.
					f.sum +=y;
					
				}
				q++;
			}
			f.PC=LABEL_2;
			if (w.sync())
				return;
		case LABEL_2:
			result=f.sum;
			setupReturn();
		}
		return;
	}
	public String toString() { return "NQ(#" + hashCode()+" " + frame + ",result=" 
		+ result+",jc=" + joinCount + ")";}
	
}

