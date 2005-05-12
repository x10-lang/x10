import x10.lang.*;

/**
 * Minimal test for dists.
 */

public class DistributionTest {
	
	public boolean run() {
		region r = region.factory.region(0,100); //(low,high)
		final region R = region.factory.region(new region[] {r,r});
		dist d = dist.factory.constant(R,here);
		return ((d.rank==2) && 
				(R.rank==2) &&
				(R.rank(1).high()-R.rank(1).low()+1 == 101));
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new DistributionTest()).run();
        } catch (Throwable e) {
                e.printStackTrace();
                b.val=false;
        }
        System.out.println("++++++ "+(b.val?"Test succeeded.":"Test failed."));
        x10.lang.Runtime.setExitCode(b.val?0:1);
    }
    static class boxedBoolean {
        boolean val=false;
    }

	
}
