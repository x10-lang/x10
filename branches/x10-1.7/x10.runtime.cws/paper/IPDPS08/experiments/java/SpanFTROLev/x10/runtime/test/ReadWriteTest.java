package x10.runtime.test;


public class ReadWriteTest {
	
	public ReadWriteTest() {
		super();
		
	}
	static int a;
	static  int b;
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
				System.out.println((2*N) + " reads and " + (2*N) + " writes took " 
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
			System.out.println("Usage: java ReadWriteTest <threads>");
			return;
		}
		ReadWriteTest v = new ReadWriteTest();
		v.runTest();
		
		
	}
	
}
