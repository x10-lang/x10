import x10.lang.*;

/**
 * Minimal test for atomic method qualifier.  
 */
public class AtomicMethodTest  {
	
	long val=0;
	const int N=1000000000;
	long startCount = 0;
	long endCount = 0;
	atomic void body() {
		startCount = this.val; 
		for (int i=0;i<N;i++) this.val++; 
		endCount = this.val;
	}
	
	public boolean run() {
		boolean b; // temp
		async(here) body();
		for (long i=0;i<N*100;i++) {
			atomic{this.val = i;b=(endCount!=0);}
			if (b) break;
		}
		// need a memory fence here
		atomic{b=(startCount + N == endCount);}
		return b;
	}
	public static void main(String args[]) {
		boolean b= (new AtomicTest()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
