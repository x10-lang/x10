import x10.lang.*;

/**
 * Minimal test for distributions.
 */

public class DistributionTest1 {
	
	public boolean run() {
		region r = region.factory.region(0,100); //(low,high)
		final region R = region.factory.region(new region[] {r,r});
		final distribution D = distribution.factory.constant(R,here);
		return ((D[0,0]== here) &&
				(D.rank==2) && 
				(R.rank==2) &&
				(R.rank(1).high()-R.rank(1).low()+1 == 101));
	}
	public static void main(String args[]) {
		boolean b= (new DistributionTest1()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
	
}
