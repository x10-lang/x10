import x10.lang.*;

/**
 * Minimal test for distributions.
 */

public class DistributionTest {
	
	public boolean run() {
		region r = region.factory.region(0,100); //(low,high)
		final region R = region.factory.region(new region[] {r,r});
		distribution d = distribution.factory.constant(R,here);
		return ((d.rank==2) && 
				(R.rank==2) &&
				(R.rank(1).high()-R.rank(1).low()+1 == 101));
	}
	public static void main(String args[]) {
		boolean b= (new DistributionTest()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
	
}
