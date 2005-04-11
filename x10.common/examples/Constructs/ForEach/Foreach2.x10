import x10.lang.*;
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
		final distribution d=distribution.factory.constant(r,P0);
		
		finish
		foreach(point p:d) {
			// Ensure each activity spawned by foreach
			// runs at P0
			// and that the hasbug array was
			// all false initially
			if (P0 != d.get(p) || P0 != here)
				throw new Error("Test failed.");
			atomic{this.nActivities++;}
		}
		return nActivities==N;
	}
	public static void main(String args[]) {
		boolean b= (new Foreach2()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
