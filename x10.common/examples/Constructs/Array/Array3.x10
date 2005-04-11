/**
 * Simple array test #3. Tests declaration of arrays, storing in local variables, accessing and updating.
 */
import x10.lang.*;
public class Array3 {

	public boolean run() {
		
		region e= region.factory.region(1,10); //(low,high)
		region r = region.factory.region(e, e); 
		distribution d=distribution.factory.local(r);
		int[.] ia = new int[d];
		ia[1,1] = 42;
		return 42 == ia[1,1];
	
	}
	public static void main(String args[]) {
		boolean b= (new Array3()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
