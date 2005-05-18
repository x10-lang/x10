/**
 * Test the shorthand syntax for an array initializer.
 */
public class ArrayInitializerShorthand {

	public boolean run() {
		dist d =  [1:10, 1:10] -> here;
		double[.] ia = new double[d] (point [i,j]) { return i+j; };
		
		for(point p[i,j]: [1:10,1:10])  chk(ia[p]==i+j);

		return true;
	}

    static void chk(boolean b) {if (!b) throw new Error();}
	
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new ArrayInitializerShorthand()).run();
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
