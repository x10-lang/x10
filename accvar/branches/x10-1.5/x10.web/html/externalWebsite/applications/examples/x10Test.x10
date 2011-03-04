//package harness;
import java.util.Random;

/**
 * Test harness abstract class.
 * FIXME: remove the ugly hack of relying on Java Threads
 */
public abstract class x10Test  extends x10.lang.Object {

	/** some utility functions **/
	
	public boolean powerOf2(int a_int){
		assert a_int>0; //int(:self>0) is not supported yet.
		int i=(int)Math.abs(a_int);
		/*if (i==0) return false;
		else{
			if (i!=(pow2(log2(i)))) return false;
		}*/
		return (i==0)? false: (i!=(pow2(log2(i))))? false: true;
	}

	public  int log2(int a_int){
		return (int)(Math.log(a_int)/Math.log(2));
	}

	public  int pow2(int a_int){
		return (int)Math.pow(2,a_int);
	}

	/**
	 * The body of the test.
	 * @return true on success, false on failure
	 */

	protected abstract boolean run();

	public void executeAsync() {
		final boolean b[] = new boolean[] { false };
		Thread timer = startTimeoutTimer();
		try {
			finish async(this) b[0] = this.run();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		timer.interrupt();
		reportResult(b[0]);
	}

	public void execute() {
		boolean b = false;
		Thread timer = startTimeoutTimer();
		try {
			finish b = this.run();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		timer.interrupt();
		reportResult(b);
	}

	public static final String PREFIX = "++++++ ";
	public static void success() {
		System.out.println(PREFIX+"Test succeeded.");
		x10.lang.Runtime.setExitCode(0);
	}
	public static void failure() {
		System.out.println(PREFIX+"Test failed.");
		x10.lang.Runtime.setExitCode(1);
	}
	protected static void reportResult(boolean b) {
		if (b) success(); else failure();
	}

	/**
	 * Check if a given condition is true, and throw an error if not.
	 */
	public static safe void chk(boolean b) {
		if (!b) throw new Error();
	}

	/**
	 * Check if a given condition is true, and throw an error with a given
	 * message if not.
	 */
	public static safe void chk(boolean b, String s) {
		if (!b) throw new Error(s);
	}

	private Random myRand = new Random(1L);

	/**
	 * Return a random integer between lb and ub (inclusive)
	 */
	protected int ranInt(int lb, int ub) {
		return lb + myRand.nextInt(ub-lb+1);
	}

	/**
	 * Start the timeout timer for the number of seconds specified in the
	 * "x10test.timeout" system property (default is 300).
	 * @return the timer thread.
	 */
	private static Thread startTimeoutTimer() {
		final int seconds = java.lang.Integer.getInteger("x10test.timeout", 300).intValue();
		// Cannot use async -- have to force a real Thread
		Thread timer = new Thread(new Runnable() {
			public void run() {
				if (x10.lang.Runtime.sleep(seconds*1000)) {
					x10.lang.Runtime.exit(128);
				}
			}
		});
		timer.start();
		return timer;
	}
}
