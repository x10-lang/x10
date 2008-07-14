@x10.generics.Parameters({"T"})
public class List$$float implements Runnable {
	public class T {};
	float x;
	public float get() { return x; }
	public void set(float v) { x = v; }
	public void run() {
		System.out.println("Loaded and executed: "+getClass().getName());
	}
}
