/**
* Test the shorthand syntax for an array initializer.
*/
import x10.lang.*;
public class ArrayOverRegion {

	public boolean run() {
		region r =  [1:10, 1:10];
		double[.] ia = new double[r] (point [i,j]) { return i+j; };
		
		for(point p[i,j]: [1:10,1:10]) 
			if(ia[p]!=i+j) return false;
		return true;
	}
	
	public static void main(String args[]) {
		boolean b= (new ArrayOverRegion()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
	
}
