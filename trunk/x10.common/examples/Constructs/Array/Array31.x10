/**
 * Simple array test #3. Tests declaration of arrays, storing in local variables, accessing and updating for
 * general 1-d arrays.
 */
import x10.lang.*;
public class Array31 {

	public boolean run() {
		
		region e= region.factory.region(1,10); //(low,high)
		distribution d=distribution.factory.local(e);
		int[.] ia = new int[d];
		ia[1] = 42;
		return 42 == ia[1];
	
	}
	public static void main(String args[]) {
		boolean b= (new Array31()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
