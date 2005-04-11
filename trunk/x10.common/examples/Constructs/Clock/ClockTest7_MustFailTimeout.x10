/**
 * Combination of finish and clocks, should cause deadlock
 *
 * @author kemal 3/2005
 */
public class ClockTest7_MustFailTimeout {
	
	int val=0;
	static final int N=32;
	
	public boolean run() {
		final clock c = clock.factory.clock();
		
		finish foreach (point [i]: 0:(N-1)) clocked(c) {
			now(c) {atomic val++;}
			System.out.println("Activity "+i+" phase 0");
			next;
			if (val != N) {
			    throw new Error();
			}
			System.out.println("Activity "+i+" phase 1");
			next;
			now(c) {atomic val++;}
			System.out.println("Activity "+i+" phase 2");
			next;
		}
		next; next; next;
		if (val !=2*N) {
			throw new Error();
		}
		return true;
	}
	
	public static void main(String args[]) {
		boolean b= (new ClockTest7_MustFailTimeout()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
	
}
