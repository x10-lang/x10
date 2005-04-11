/**
 * Clock test for  barrier functions
 *
 * foreach loop body represented with a method
 *
 * @author kemal 3/2005
 */
//import x10.runtime.Clock; // workaround for registration bug
public class ClockTest4 {

	int val=0;
	static final int N=32;

	public boolean run() {
      		final clock c = clock.factory.clock();
		
			foreach (point [i]: 1:(N-1)) clocked(c) {
				// TODO: inner task must be auto-registered
				//((Clock)c).register();
				//delay(5000);
				foreachBody(i,c);
			}
		//delay(5000); // everyone should be registered first
		foreachBody(0,c);
		int temp2;
		atomic {temp2=val;}
		if (temp2!=0) {
			throw new Error();
		}
		return true;
	}

	void foreachBody(final int i, final clock c) {
			now(c) {async(here) {atomic val+=i;}}
			next;
			int temp;
			atomic {temp=val;}
			if (temp != N*(N-1)/2) {
				throw new Error();
			}
			next;
			now(c) {async(here) {atomic val-=i;}}
			next;
	}

	static void delay(int millis) {
		try{Thread.sleep(millis);}
		catch(InterruptedException e) {}
	}

	public static void main(String args[]) {
		boolean b= (new ClockTest4()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}
