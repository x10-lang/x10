@x10.generics.Parameters({"T"})
public class List$$long implements Runnable {
	public class T {};
	long x;
	public long get() { return x; }
	public void set(long v) { x = v; }
	public void run() {
		System.out.println("Loaded and executed: "+getClass().getName());
	}
}
