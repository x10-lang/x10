/**
 * Simple array test #3
 */
import x10.lang.*;
public class Array3Boolean {

	public boolean run() {
		
		region e= region.factory.region(1,10); //(low,high)
		region r = region.factory.region(e, e); 
		distribution d=distribution.factory.local(r);
		boolean[.] ia = new boolean[d];
		ia[1,1] = true;
		return (true == ia[1,1]);
	
	}
	public static void main(String args[]) {
		boolean b= (new Array3Boolean()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
