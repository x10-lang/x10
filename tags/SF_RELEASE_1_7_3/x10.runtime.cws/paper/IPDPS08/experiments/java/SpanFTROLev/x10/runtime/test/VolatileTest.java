package x10.runtime.test;


public class VolatileTest {
	
	public VolatileTest() {
		super();
		
	}
	static volatile int a;
	static volatile int b;
	static int N;
	
	void runTest() {
		new TwoThreadTest() {
			public void run1() {
				for (int i=1; i < N; i++) {
					int x = a;
					b = x+1;
				}
			}
			public void run2() {
				for (int i=1; i < N; i++) {
					int x = b;
					a = x+1;
				}
			}
			public void reportResult(double t) {
				System.out.println((2*N) + " volatile reads and " + (2*N) + " writes took " 
						+ t + " sec.");
			}
		}.runTest();
	}
	public static void main(String[] args) {
		
		try {
			N = Integer.parseInt(args[0]);
			System.out.println("Number of iterations=" + N);
		}
		catch (Exception e) {
			System.out.println("Usage: java VolatileTest <threads>");
			return;
		}
		VolatileTest v = new VolatileTest();
		v.runTest();
		
		
	}
	
}
