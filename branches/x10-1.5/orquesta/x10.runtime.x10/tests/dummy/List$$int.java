@x10.generics.Parameters({"T"})
public class List$$int implements Runnable {
	public class T {};
	int x;
	public int get() { return x; }
	public void set(int v) { x = v; }
	public void run() {
		System.out.println("Loaded and executed: "+getClass().getName());
	}
}
