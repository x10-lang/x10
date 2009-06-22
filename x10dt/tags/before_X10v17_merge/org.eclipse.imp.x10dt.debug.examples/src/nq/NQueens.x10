package nq;

public class NQueens {
	int nSolutions;
	const int N = 14;
	const int[] expectedSolutions =
	{ 0, 1, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200,
			73712, 365596, 2279184, 14772512 };
	void start() {
		new Board().search();
	}
    class Board { 
    	private int[] q;
    	Board() { q = new int[0];}
    	Board(final int [] old, final int newItem) {
    		final int n = old.length;
    		q = new int[n+1];
    		System.arraycopy(old, 0, q, 0,n);
    		q[n]=newItem;
    	}
    	boolean safe(int j) {
    		final int n = q.length; 
    		for (point [k] : [0:n-1]) {
    			if (j == q[k] ||
    					Math.abs(n-k) == Math.abs(j-q[k])) 
    				return false;
    		}
    		return true;
    	}
    	/** Search for all solutions in parallel, on finding
    	 * a solution update nSolutions.
    	 */
    	 void search() {
    		if (q.length == N) { 
    			 atomic nSolutions++;
    			return; 
    		}
    		for (point [k] : [0:N-1])
    			if (safe(k)) 
    				  async new Board(q, k).search(); 
    	}
    }
    public static void main(String[] args) {
    	NQueens nq = new NQueens();
    	long start = -System.nanoTime();
    	finish nq.start();
    	boolean result = nq.nSolutions==expectedSolutions[N];
    	start += System.nanoTime();
    	start /=1000000;
    	System.out.println("NQueens " + N + " has " + nq.nSolutions + " solutions" +
    			(result? " (ok)." : " (wrong).") + "time=" + start + "ms");
    }
}
