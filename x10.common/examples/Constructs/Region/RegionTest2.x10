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
	public static void main(String args[]) {
		boolean b= (new RegionTest2()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}
