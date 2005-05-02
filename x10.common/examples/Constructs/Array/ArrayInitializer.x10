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
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new ArrayInitializer()).run();
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
