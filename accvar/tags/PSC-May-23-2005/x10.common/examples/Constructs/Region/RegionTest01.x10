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
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new RegionTest01()).run();
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
