/**
 * Checks if creation and force of Future in different activities work.
 *
 * @author Christoph von Praun, 5/2005
 * @author kemal, 5/2005
 *
 */

public class FutureTest5 {

    nullable future<int> fut;

    /**
     * Create a future in one activity, and then 
     * force it in a different activity.
     */
    public boolean run() {
	return 
	    testUp_(false) && 
	    testUp_(true) &&
	    testDown_() && 
	    testSibling_(false) &&
	    testSibling_(true);
    }

    /**
     * Create future in child, force it in parent.
     */
    private boolean testUp_(final boolean del) {
	atomic fut = null;
	async (here) { 
	    future<int> t1 = future (here) { 42 } ;
            atomic fut=t1;
	    if (del)
		delay(500);
	};
        nullable future<int> t2;
	when (fut != null) {t2=fut;}
        int@here fortytwo = t2.force();
	System.out.println("up done");
	return fortytwo == 42;
    }
    
    /**
     * Create future in parent, force it in child.
     */
    private boolean testDown_() {
	final future<int> fut_l = future (here) { 42 } ;
	finish async (here) { 
	    int@here fortytwo = fut_l.force();
	    System.out.println("down done");
	};
	return true;
    }
    
    /**
     * Create future in child 1, force it in child 2.
     */
    private boolean testSibling_(final boolean del) {
	atomic fut = null;
	async (here) { 
	    future<int> t1= future (here) { 42 } ;
            atomic fut=t1;
	    if (del)
		delay(500);
	}
	finish async (here) {
            nullable future<int> t2;
	    when (fut != null) {t2=fut;}
	    int@here fortytwo = t2.force();
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

    /**
     * main method
     */
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
