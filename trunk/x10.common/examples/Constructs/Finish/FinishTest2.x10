import x10.lang.*;
/**
 * @author Christoph von Praun
 * Checks if finish also consideres grand-children. 
 */
public class FinishTest2  {

    boolean flag;
    int foo;
    
    public boolean run() {
	flag = false;
	finish {
	    async (here) { 
		foo = 123;
		async (here) {
		    foo = 42;
		    System.out.print("waiting ...");
		    delay(2000);
		    System.out.println("done.");
		    flag = true;
		}
	    }
	}
	System.out.println("The flag is b=" + flag + " (should be true).");
	return (flag == true);
    }
	
    /**
     * sleep for millis milliseconds.
     */
    static void delay(int millis) {
	try {
	    java.lang.Thread.sleep(millis);
	} catch(InterruptedException e) {
	}
    }

    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
	    finish async b.val=(new FinishTest2()).run();
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
