package x10.runtime.test;

public abstract class TwoThreadTest {

	public TwoThreadTest() {
		super();
		
		
	}
	public abstract void run1();
	public abstract void run2();
	public abstract void reportResult(double time);
	volatile boolean o1done = false, o2done=false;
	void runTest() {
		final Object o1 = new Object(), o2 = new Object();
		Thread t1 = new Thread(new Runnable() {
			public void run() {
				run1();
				synchronized (o1) {
					o1done=true;
					o1.notifyAll();
				}
			}
		});
		Thread t2 = new Thread(new Runnable() {
			public void run() {
				run2();
				synchronized (o2) {
					o2done=true;
					o2.notifyAll();
				}
			}
		});
		t1.setDaemon(true);
		t2.setDaemon(true);
		int NPS = 1000*1000*1000;
		try {
			long s = System.nanoTime();
			t1.start();
			t2.start();
			synchronized (o1) {
				while (! o1done)
				o1.wait();
			}
			synchronized (o2) {
				while (! o2done)
				o2.wait();
			}
			long t = System.nanoTime()-s;
			reportResult((((double) t)/NPS));
			
		} catch (InterruptedException z) {
			System.out.println("Test failed. Retry.");
		}
	}
	

}
