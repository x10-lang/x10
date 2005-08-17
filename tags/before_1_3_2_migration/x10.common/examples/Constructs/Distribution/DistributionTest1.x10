
/**
 * Minimal test for dists.
 */

public class DistributionTest1 {
	
	public boolean run() {
		region r = region.factory.region(0,100); //(low,high)
		final region R = region.factory.region(new region[] {r,r});
		final dist D = dist.factory.constant(R,here);
		return ((D[0,0]== here) &&
				(D.rank==2) && 
				(R.rank==2) &&
				(R.rank(1).high()-R.rank(1).low()+1 == 101));
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new DistributionTest1()).run();
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
