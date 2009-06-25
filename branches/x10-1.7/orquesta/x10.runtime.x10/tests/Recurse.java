import x10.generics.Parameters;
import x10.generics.Synthetic;
import x10.runtime.Runtime;
import x10.runtime.X10RuntimeClassloader;

@Parameters({"T"})
public class Recurse implements Runnable {
	public static class T {};
	public Recurse() { }
	@Synthetic public Recurse(Class T) { this(); }
	@Synthetic public static boolean instanceof$(Object o, String constraint) { assert(false); return true; }
	public static boolean instanceof$(Object o, String constraint, boolean b) { /*check constraint*/; return b; }
	public static Object cast$(Object o, String constraint) { /*check constraint*/; return (Recurse)o; }
	public Object nest(int n) {
		if (n == 0) return this;
		else return new Recurse(((X10RuntimeClassloader)Recurse.class.getClassLoader()).getClass("Recurse$$Recurse$T")).nest(n-1);
	}
	public void run() {
		System.out.println("Loaded and executed: "+getClass().getName());
		System.out.println(nest(5));
	}
}

