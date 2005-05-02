/**
* Test the shorthand syntax for an array initializer.
*/
import x10.lang.*;
public class ArrayOverRegion {

	public boolean run() {
		region r =  [1:10, 1:10];
		double[.] ia = new double[r] (point [i,j]) { return i+j; };
		
		for(point p[i,j]: [1:10,1:10]) 
			if(ia[p]!=i+j) return false;
		return true;
	}
	
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new ArrayOverRegion()).run();
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
