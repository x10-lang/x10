
import x10.lang.*;
/**
 * Checks if creation and force of Future in different activities work.
 */

public class FutureTest5 {

    nullable future<int> fut;
    public boolean run() {
	return 
	    testUp_(false) && 
	    testUp_(true) &&
	    testDown_() && 
	    testSibling_(false) &&
	    testSibling_(true);
    }

    private boolean testUp_(final boolean del) {
	fut = null;
	async (here) { 
	    fut = future (here) { 42 } ;
	    if (del)
		delay(500);
	};
	while (fut == null) ;
	int@here fortytwo = fut.force();
	System.out.println("up done");
	return fortytwo == 42;
    }
    
    private boolean testDown_() {
	final future<int> fut_l = future (here) { 42 } ;
	finish async (here) { 
	    int@here fortytwo = fut_l.force();
	    System.out.println("down done");
	};
	return true;
    }
    
    private boolean testSibling_(final boolean del) {
	fut = null;
	async (here) { 
	    fut = future (here) { 42 } ;
	    if (del)
		delay(500);
	}
	finish async (here) {
	    while (fut == null) ;
	    int@here fortytwo = fut.force();
	    System.out.println("sibling done");
	};
	return true;
    }

    /**
     * sleep for millis milliseconds.
     */
    static void delay(int millis) {
	try {
	    java.lang.Thread.sleep(millis);
	} catch(InterruptedException e) {}
    }

    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
	    finish b.val=(new FutureTest5()).run();
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
