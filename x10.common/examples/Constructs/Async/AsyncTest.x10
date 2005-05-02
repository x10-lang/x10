import x10.lang.*;

/**
 * Minimal busy-waiting style test for async.
 * Written in this particular way to 
 * a) always terminate
 * b) not use any other x10 construct
 */
 
public class AsyncTest {

	boolean flag=false;
        static final long N= 1000000000;

	public boolean run() {
        	boolean b=false;
		async ( here ) { atomic{this.flag = true;} }
		for (long i=0;i<N*100;i++) {
			atomic {b=flag;}
                         
			if (b) break;
                }
	    return b;
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new AsyncTest()).run();
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
