@x10.generics.Parameters({"T"})
public class List$$double implements Runnable {
	public class T {};
	double x;
	public double get() { return x; }
	public void set(double v) { x = v; }
	public void run() {
		System.out.println("Loaded and executed: "+getClass().getName());
	}
}
