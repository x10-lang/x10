/**
 * Minimal test for future.
 */
public class FutureTest2 {

	public boolean run() {
        future<boolean@here> ret = future (here) { this.m() };
	    boolean@here syn = ret.force();
	    return syn;
	}
	
    boolean m() {
      	return true;
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new FutureTest2()).run();
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
