import x10.lang.*;

/**
 * Test that does nothing.  Useful to test the testing
 * harness.
 */
public class NopTest {

   public boolean run() {
      return true;
   }
	public static void main(String args[]) {
		boolean b= (new NopTest()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}
