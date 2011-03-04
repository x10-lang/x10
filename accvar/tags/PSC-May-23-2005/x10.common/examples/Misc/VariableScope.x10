
/**
 * Testing a variable scope problem 
 * The second q's scope does not overlap with the first q
 */
import x10.lang.*;
public class VariableScope {

	public boolean run() {
		
		final int N=10;
		region e= region.factory.region(1,N); //(low,high)
		region r= region.factory.region(new region[]{e, e}); 
		dist d=dist.factory.constant(r,here);
		int n=0;
		
		for(point p: e)
			for (point q:e) {
				n++;
			}
		
		for(point p: d) {
			nullable point q = null;
			n++;
		}
		
		return n==2*N*N;
	}

	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new VariableScope()).run();
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
