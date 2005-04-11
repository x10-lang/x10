import x10.lang.*;
/**
 * Test that loops forever.  
 * Useful for testing the time limit feature
 * This test is supposed to fail after the time limit elapses.
 */
public class InfiniteLoopTest_MustFailTimeout {
   boolean flag=X.t();
   public boolean run() {
       while(flag) ;
       return true;
   }
	public static void main(String args[]) {
		boolean b= (new InfiniteLoopTest_MustFailTimeout()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}

class X {
    public static boolean t() { return true;}
}
