import x10.lang.*;

/**
 * Minimal test for clock.  Does not do anything
 * interesting.  Only possible failure is to not
 * compile or hang.		
 */
public class ClockTest {

	public boolean run() {
		clock c = clock.factory.clock();
        next;
		c.resume();
  		c.drop();		
	    return true;
	}
	public static void main(String args[]) {
		boolean b= (new ClockTest()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}
