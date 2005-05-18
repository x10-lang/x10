/**
 * Array Initializer test.
 */

public class ArrayInitializer {
	
	public boolean run() {
		
		region e= [0:9];
		region r = [e, e, e];
		dist d=r->here;
		
		final int value [d] ia = 
			new int value [d] 
				new intArray.pointwiseOp() {
					public int apply(point [i,j,k]) {
						return i;
					}
		};

		for(point [i,j,k]:d) chk(ia[i,j,k]==i);
		
		return true;
	}
	
    static void chk(boolean b) {if (!b) throw new Error();}

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
