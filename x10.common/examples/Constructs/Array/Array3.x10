/**
 * Simple array test #3. Tests declaration of arrays, storing in local variables, accessing and updating.
 */
import x10.lang.*;
public class Array3 {

	public boolean run() {
		
		region e= region.factory.region(1,10); //(low,high)
		region r = region.factory.region(e, e); 
		dist d=dist.factory.local(r);
		int[.] ia = new int[d];
		ia[1,1] = 42;
		return 42 == ia[1,1];
	
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new Array3()).run();
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
