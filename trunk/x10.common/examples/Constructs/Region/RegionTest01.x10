import x10.lang.*;
/** Minimal test for regions.
 */
public class RegionTest01 {

	public boolean run() {
		region r = region.factory.region(0,100); // (low,high)
		region reg = region.factory.region(r,r);
		
		int sum = 0;
		for(point p:reg) sum += p.get(0) - p.get(1);
		
		return sum == 0;
	}
	public static void main(String args[]) {
		boolean b= (new RegionTest01()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}
