@x10.generics.Parameters({"T"})
public class List implements Runnable {
	public class T {};
	T x;
	public T get() { return x; }
	public void set(T v) { x = v; }
	public void run() {
		System.out.println("Loaded and executed: "+getClass().getName());
		T v = get();
		set(v);
		System.out.println("\twith "+v);
	}
}
