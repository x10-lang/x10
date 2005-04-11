import x10.lang.*;
/**
 * Array Initializer test.
 */

public class ArrayInitializer {
	
	public boolean run() {
		
		region e= region.factory.region(0,9); //(low,high)
		region r = region.factory.region(new region[]{e, e, e}); 
		distribution d=distribution.factory.constant(r,here);
		
		final int value [d] ia = 
			new int value [d] 
				new intArray.pointwiseOp() {
					public int apply(point p) {
						return p.get(0);
					}
		};
		for(point p:d) {
			int i = p.get(0);
			int j = p.get(1);
			int k = p.get(2);
			if (ia[i,j,k]!=i) return false;
		}
		
		return true;
	}
	public static void main(String args[]) {
		boolean b= (new ArrayInitializer()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
