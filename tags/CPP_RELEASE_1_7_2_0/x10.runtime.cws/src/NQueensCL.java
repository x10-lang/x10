
/**
 * Uses a long to represent a chess board in order to see how the app performs
 * if excessive garbage is not created. The leftmost nibble represents
 * the queen in row 1. A zero in row i means the queen has not yet been placed.
 * @author vj
 */
import x10.runtime.cws.*;

public class NQueensCL extends Closure {
	
	static int boardSize;
	
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
			System.out.println("Usage: java NQueensCL <threads> ");
			return;
		}
		Pool g = new Pool(procs);
		for (int i = 1; i < 16; i++) {
			boardSize = i;
			Job job = new Job(g) {
				int result;
				public void setResultInt(int x) { result=x;}
				public int resultInt() { return result;}
				@Override
				public int spawnTask(Worker ws) throws StealAbort { 
					return nQueens(ws, 0L,1);
				}
				public String toString() { return "Job(NQ,#" + hashCode()+")";}
			};
			System.gc();
			long s = System.nanoTime();
			g.submit(job);
			int result = job.getInt();
			long t = System.nanoTime();
			System.out.println("Result:" + i + " " + (t-s)/1000000 
					+ " " + result + " " + (result==expectedSolutions[i]?"ok" : "fail") );
		}
		g.shutdown();    
	}
	
	// Boards are represented as arrays where each cell 
	// holds the column number of the queen in that row
	
	NQueensCL(NFrame f) { 
		super(f);
	}
	public static class NFrame extends Frame {
//		 The label at which computation must be continued by the associated
		// closure.
		public  int PC;
		final long sofar;
		final int row;
		 int q;
		int sum;
		@Override
		public void setOutletOn(final Closure c) {}
		@Override
		public void acceptInlet(int index, int value) {
			sum +=value;
		}
		public Closure makeClosure() {
			Closure c = new NQueensCL(this);
			return c;
		}
		public NFrame(long a, int r) { sofar=a;row=r;}
		public String toString() { 
			return "NF(" + board(sofar) + ",q="  + q + ",sum=" + sum+")";
		}
	}
	public static final int LABEL_0=0, LABEL_1=1, LABEL_2=2, LABEL_3=3;
	public static final int[] shifts = {0/*dummy*/, 60,56,52,48,44,40,36,32,28,24,20,16,12,8,4,0};
	
	public static final long[] masks = { //1 indexed.
		0x0,
		((long) 0xF)<< shifts[1],
		((long) 0xF)<< shifts[2],
		((long) 0xF)<< shifts[3],
		((long) 0xF)<< shifts[4],
		((long) 0xF)<< shifts[5],
		((long) 0xF)<< shifts[6],
		((long) 0xF)<< shifts[7],
		((long) 0xF)<< shifts[8],
		((long) 0xF)<< shifts[9],
		((long) 0xF)<< shifts[10],
		((long) 0xF)<< shifts[11],
		((long) 0xF)<< shifts[12],
		((long) 0xF)<< shifts[13],
		((long) 0xF)<< shifts[14],
		((long) 0xF)<< shifts[15],
		((long) 0xF)<< shifts[16]
	};
	int result;
	@Override public void setResultInt(int x) { result=x;}
	@Override public int resultInt() { return result;}
	
	static String board(long l) {
		StringBuffer s = new StringBuffer("[");
		for (int i=1; i <=boardSize-1; i++) s.append(Integer.toString(atRow(l,i))).append(",");
		return s.append(Long.toString(l&masks[boardSize])).append("]").toString();
	}
 
	static int atRow(long board, int c) { 
		assert 1<=c && c <= boardSize;
		int value = (int) ((board & masks[c]) >>> shifts[c]);
		assert 0 <= value && value < 16;
		return value;
	}
	public static int nQueens(Worker w, long a, int row) throws StealAbort {
		if (row > boardSize) return 1;
		NFrame frame = new NFrame(a,row);
		frame.q=2;
		frame.sum=0;
		frame.PC=LABEL_1;
		w.pushFrame(frame);
		int sum=0;
		int q=1;
		while (q <= boardSize) {
			boolean attacked = false;
			int deltaRow = row-1;
			for (int i = 1; i < row && ! attacked; i++, deltaRow--) {
				int p = atRow(a,i);
				int delta = q-p;
				attacked = (delta == 0 || delta ==  deltaRow || delta == -deltaRow);
			}
			if (!attacked) { 
				assert (a & shifts[row]) == 0;
				long next = a | (((long) q) << shifts[row]);
				final int y = nQueens(w, next,row+1);
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
		final long a = f.sofar;
		int row = f.row;
		
		switch (f.PC) {
		case LABEL_0:
			if (row > boardSize) {
				result =1;
				setupReturn();
				return;
			}
		case LABEL_1: 
			int q=f.q;
			int sum=0;
			while (q <= boardSize) {
				f.q =q+1;
				boolean attacked = false;
				for (int i = 1; i < row && ! attacked; i++) {
					int p = atRow(a,i);
					attacked = (q == p || q == p - (row - i) || q == p + (row - i));
				}
				if (!attacked) { 
					assert (a & shifts[row]) == 0;
					long next = a | (((long) q) << shifts[row]);
					int y= nQueens(w, next,row+1);
					w.abortOnSteal(y);
					sum += y;
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

