import x10.lang.*;
/**
 * Minimal test for future.
 * This test cannot really ever fail, the best it can
 * do is not compile or not terminate.
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
	public static void main(String args[]) {
		boolean b= (new FutureTest2()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
     

}
