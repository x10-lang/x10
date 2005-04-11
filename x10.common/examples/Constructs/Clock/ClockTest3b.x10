/**
 * Clock test for  barrier functions.
 * Alternate barrier version where parent activity terminates, and
 * finish is used to wait for the children
 * @author kemal 3/2005
 */
public class ClockTest3b {
	
	int val=0;
	static final int N=32;
	
	public boolean run() {
		
                finish async {
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
		}
		if (val !=2*N) {
			throw new Error();
		}
		return true;
	}
	
	public static void main(String args[]) {
		boolean b= (new ClockTest3b()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
	
}
