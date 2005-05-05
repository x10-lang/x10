
/**
 * @author Christoph von Praun
 * Test for the interaction of clocks and future
 * clock.doNext should not wait for futures to 
 * terminate.
 */

public class ClockFuture  {
    
    private boolean clock_has_advanced;
    
    public int m() {
	int ret = 0;
	when ( clock_has_advanced ) {
	    ret = 42;
	}
	return ret;
    }

    public boolean run() {
	final clock c = clock.factory.clock();
	future<int@here> f = future (here) { m() };
	System.out.print("1 ... ");
	// this next should not wait on the future
	next; 
	System.out.print("2 ... ");
	atomic{ clock_has_advanced = true; }
	System.out.print("3 ...");
	int result = f.force();
	chk(result == 42);
	System.out.println("4");
	return true;
    }
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
	    finish b.val=(new ClockFuture()).run();
        } catch (Throwable e) {
	    e.printStackTrace();
	    b.val=false;
        }

        System.out.println("++++++ "+(b.val?"Test succeeded.":"Test failed."));
        x10.lang.Runtime.setExitCode(b.val?0:1);
    }

     static void chk(boolean b) { 
	 if (!b) throw new Error();
     }

    static class boxedBoolean {
        boolean val=false;
    }

	
}
