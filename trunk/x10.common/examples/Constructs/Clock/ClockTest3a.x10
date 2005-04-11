/*
 * Test for 'now'.  Very likely to fail if now is not translated
 * properly (but depends theoretically on the scheduler).
 */
/**
 * Clock test for  barrier functions
 * @author kemal 3/2005
 */
public class ClockTest3a {
	
	int val=0;
	static final int N=32;
	
	public boolean run() {
		final clock c = clock.factory.clock();
		
		foreach (point [i]: 0:(N-1)) clocked(c) {
			now(c) {atomic val++;}
			next;
			if (val != N) {
			    throw new Error();
			}
			next;
			now(c) {atomic val++;}
			next;
		}
		next; next; next;
		if (val !=2*N) {
			throw new Error();
		}
		return true;
	}
	
	public static void main(String args[]) {
		boolean b= (new ClockTest3a()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
	
}