/**
 * Test for foreach
 *
 * @author: kemal, 12/2004
 */

public class Foreach2 {
	static final int N=100;
	int nActivities=0;
	
	public boolean run() {
		
		final place P0=here; // save current place
		final region r= region.factory.region(0,N-1);
		final dist d=dist.factory.constant(r,P0);
		
		finish
		foreach(point p:d) {
			// Ensure each activity spawned by foreach
			// runs at P0
			// and that the hasbug array was
			// all false initially
			if (P0 != d.get(p) || P0 != here)
				throw new Error("Test failed.");
			atomic{nActivities++;}
		}
		return nActivities==N;
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new Foreach2()).run();
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
