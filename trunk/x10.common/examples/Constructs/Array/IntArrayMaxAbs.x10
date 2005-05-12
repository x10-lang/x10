import x10.lang.*;

/**
 * Test for arrays, regions and dists
 * Based on original arraycopy2
 * @author kemal 1/2005
 *
 * Temporarily disabled boolean arrays
 */

public class IntArrayMaxAbs {
	
	public boolean run() {
		region e= region.factory.region(1,10); //(low,high)
		region r = region.factory.region(new region[]{e, e}); 
		final dist D=dist.factory.constant(r,here);
		final int[.] ia = new int[D];
			
		finish ateach(point p:D) { ia[p]= -p.get(0);}
	
		return ia.maxAbs()==10;
	}
	
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new IntArrayMaxAbs()).run();
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
