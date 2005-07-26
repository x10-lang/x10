/**
 * Test for repeating a polyglot type check error.
 * @author Christoph von Praun 5/2005
 */

public class PolyglotTypeCheckError {

    future<int> fut;
    public boolean run() {
	async (here) { 
	    fut = future (here) { 42 } ;
	};
	while (fut = null) ;
	int@here fortytwo = fut.force();
	return fortytwo == 42;
    }
    
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
	    finish b.val=(new PolyglotTypeCheckError()).run();
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
