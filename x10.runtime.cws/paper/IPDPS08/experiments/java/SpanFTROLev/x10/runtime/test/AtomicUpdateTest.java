package x10.runtime.test;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;



public class AtomicUpdateTest {
	
	public AtomicUpdateTest() {
		super();
		
	}
	volatile int a;
	volatile int b;
	AtomicIntegerFieldUpdater aU = AtomicIntegerFieldUpdater.newUpdater(AtomicUpdateTest.class, "a");
	AtomicIntegerFieldUpdater bU = AtomicIntegerFieldUpdater.newUpdater(AtomicUpdateTest.class, "b");
	
	static int N;
	
	public void runTest() {
		new TwoThreadTest() {
			public void run1() {
				for (int i=1; i < N; i++) {
					int x = a;
					bU.addAndGet(AtomicUpdateTest.this, 1);
				}
			}
			public void run2() {
				for (int i=1; i < N; i++) {
					int x = b;
					aU.addAndGet(AtomicUpdateTest.this, x+1);
				}
			}
			public void reportResult(double t) {
				System.out.println((2*N) + " atomic reads and " + (2*N) + " updates took " 
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
		
		AtomicUpdateTest at = new AtomicUpdateTest();
		at.runTest();
		
		
	}
	
}
