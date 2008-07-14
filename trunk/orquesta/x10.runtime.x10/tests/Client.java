public class Client implements Runnable {
	public void run() {
		System.out.println("Loaded and executed: "+getClass().getName());
		List$$int li = new List$$int();
		li.set(5);
		int i = li.get();
		System.out.println("\tgot int "+i);
		List$$float lf = new List$$float();
		lf.set(2.0f);
		float f = lf.get();
		System.out.println("\tgot float "+f);
		List$$double ld = new List$$double();
		ld.set(3.0);
		double d = ld.get();
		System.out.println("\tgot double "+d);
		List$$long ll = new List$$long();
		ll.set(4L);
		long l = ll.get();
		System.out.println("\tgot long "+l);
	}
}
