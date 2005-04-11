import x10.lang.*;
/**
 * Test for for loop on an array
 *
 * @author: vj
 */
public class ForLoopOnArray {
        static final int N=3;

	public boolean run() {
		double[.] a = new double[0:10] (point [i]) { return i;};
		
		for(point [i]: a) {
			if (a[i] != i) return false;
		}
		return true;

	}

	public static void main(String args[]) {
		boolean b= (new ForLoopOnArray()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}