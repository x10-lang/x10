/**
 * Simple array test #3. Tests declaration of arrays, storing in local variables, accessing and updating for
 * general 1-d arrays.
 */
import x10.lang.*;
public class Array31 {

	public boolean run() {
		
		region e= region.factory.region(1,10); //(low,high)
		distribution d=distribution.factory.local(e);
		int[.] ia = new int[d];
		ia[1] = 42;
		return 42 == ia[1];
	
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new Array31()).run();
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
