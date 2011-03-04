/**
 * @author Christoph von Praun
 * Checks force for grand-children. 
 */
public class FutureForce  {

    boolean flag;
    int foo;
    
    public int bar() {
	System.out.print("waiting ...");
	delay(2000);
	System.out.println("done.");
	atomic flag = true;
	return 42;
    }

    public int foo() {
	future<int> r2 = future(here) { bar() };
	return 42;
    }
    
    public boolean run() {
	atomic flag = false;
	future<int> r1 = future(here) { foo() };
	r1.force();
        boolean b;
        atomic b=flag;
	System.out.println("The flag is b=" + b + " (should be true).");
	return (b == true);
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
	    finish async b.val=(new FutureForce()).run();
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
