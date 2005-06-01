import x10.lang.*;
/**
 * Minimal test for the empty region.
 */

public class RegionTest2 {

	public boolean run() {
	    region reg = region.factory.region(0,-1); // [0..-1]
	    
	    int sum = 0;
	    for (point p:reg) sum ++;
	    return sum == 0;
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new RegionTest2()).run();
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
