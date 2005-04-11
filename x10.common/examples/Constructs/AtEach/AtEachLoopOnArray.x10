import x10.lang.*;
/**
 * Test for an ateach loop on an array
 *
 * @author: vj
 */
public class AtEachLoopOnArray {
    boolean success = true;

	public boolean run() {
		double[.] A = new double[(0:10) -> here] (point [i]) { return i;};
		
		finish ateach(point [i]: A) 
			if (A[i] != i) 
				async (this) atomic { success = false; }
		
		return success;

	}
	public static void main(String args[]) {
		boolean b= (new AtEachLoopOnArray()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
