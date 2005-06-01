/**
 * Minimal test for atomic.  If the atomic code is not executed atomically
 * the other activity can check that the value difference is not N.
 */
public class AtomicTest  {
	
	long val=0;
	static final long N=1000000000;
	long startCount = 0;
	long endCount = 0;
	
	
	public boolean run() {
		boolean b; // temp
		async(this) { atomic { startCount = this.val; for (int i=0;i<N;i++) this.val++; endCount = this.val; } }
		for (long i=0;i<N*100;i++) {
			atomic{this.val = i;b=(endCount!=0);}
			if (b) break;
		}
		// need a memory fence here
		atomic{b=(startCount + N == endCount);}
		return b;
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new AtomicTest()).run();
        } catch (Throwable e) {
                e.printStackTrace();
                b.val=false;
        }
        System.out.println("++++++ "+(b.val?"Test succeeded.":"Test failed."));
        x10.lang.Runtime.setExitCode(b.val?0:1);
    }
    static class boxedBoolean {
        boolean val=false;
    }

}
