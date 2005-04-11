
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
		distribution d=distribution.factory.constant(r,here);
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

	public static void main(String args[]) {
		boolean b= (new VariableScope()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}
