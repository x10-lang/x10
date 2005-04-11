import x10.lang.*;

/**
 * Test for arrays, regions and distributions
 * Based on original arraycopy2
 * @author kemal 1/2005
 *
 * Temporarily disabled boolean arrays
 */

public class IntArrayMaxAbs {
	
	public boolean run() {
		region e= region.factory.region(1,10); //(low,high)
		region r = region.factory.region(new region[]{e, e}); 
		final distribution D=distribution.factory.constant(r,here);
		final int[.] ia = new int[D];
			
		finish ateach(point p:D) { ia[p]= -p.get(0);}
	
		return ia.maxAbs()==10;
	}
	
	public static void main(String args[]) {
		boolean b= (new IntArrayMaxAbs()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}
