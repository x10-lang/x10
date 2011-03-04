import x10.generics.Parameters;
import x10.generics.Synthetic;
import x10.runtime.Runtime;

@Parameters({"T"})
public class List implements Runnable {
	public static class T {};
	public List() { }
	@Synthetic public List(Class T) { this(); }
	@Synthetic public static boolean instanceof$(Object o, String constraint) { assert(false); return true; }
	public static boolean instanceof$(Object o, String constraint, boolean b) { /*check constraint*/; return b; }
	public static Object cast$(Object o, String constraint) { /*check constraint*/; return (List)o; }
	T x;
	public T get() { return x; }
	public void set(T v) { x = v; }
	public void run() {
		System.out.println("Loaded and executed: "+getClass().getName());
		T v = get();
		set(v);
		System.out.println("\twith "+v);
		List l1 = new List(Runnable.class);
		boolean x1 = Runtime.instanceof$(List.instanceof$(l1, "int{self<2}"), int.class);
		System.out.println("\t"+l1.getClass().getName()+" "+(x1?"is":"isn't")+" an instance of List$$int");
		List l = new List(int.class);
		System.out.println("Created a "+l.getClass().getName());
		boolean x = Runtime.instanceof$(List.instanceof$(l, "int{self<2}"), int.class);
		// -- aload_3
		// -- ldc "int{self<2}"
		// -- invokestatic List$int.instanceof$(Object, String)
		// -- ldc int.class
		// -- invokestatic Runtime.instanceof$(boolean, Class)
		//aload_3
		//ldc "int{self<2}"
		//dup_x1							+1
		//instanceof List$$int						+3
		//invokestatic List$$int.instanceof$(Object, String, boolean)
		//nop
		//XX ldc int.class						-2(3)
		//XX invokestatic Runtime.instanceof$(boolean, Class);		-3
		System.out.println("\t"+l.getClass().getName()+" "+(x?"is":"isn't")+" an instance of List$$int");
		boolean y = Runtime.instanceof$(List.instanceof$(l, null), int.class);
		System.out.println("\t"+l.getClass().getName()+" "+(y?"is":"isn't")+" an instance of List$$int");
		boolean z = Runtime.instanceof$(List.instanceof$(l, "float{self<2}"), float.class);
		System.out.println("\t"+l.getClass().getName()+" "+(z?"is":"isn't")+" an instance of List$$float");
		boolean w = Runtime.instanceof$(List.instanceof$(l, null), T.class);
		System.out.println("\t"+l.getClass().getName()+" "+(w?"is":"isn't")+" an instance of "+getClass().getName());
		List m = (List)Runtime.cast$(List.cast$(l, "int{self<2}"), int.class);
		// -- aload_3
		// -- ldc "int{self<2}"
		// -- invokestatic List$$int.cast$(Object, String)
		// -- ldc int.class
		// -- invokestatic Runtime.cast$(Object, Class)
		// -- checkcast List
		//aload_3
		//ldc "int{self<2}"
		//invokestatic List$$int.cast$(Object, String)
		//nop*
		//XX ldc int.class						-2(3)
		//XX invokestatic Runtime.cast$(Object, Class)			-3
		//XX checkcast List						-3
		System.out.println("\t"+l.getClass().getName()+" was casted to List$$int");
		try {
			List n = (List)Runtime.cast$(List.cast$(l, "float{self<2.0}"), float.class);
			System.out.println("\t"+l.getClass().getName()+" was casted to List$$float");
		} catch (ClassCastException e) {
			System.out.println("\t"+l.getClass().getName()+" was not casted to List$$float");
		}
		try {
			List o = (List)Runtime.cast$(List.cast$(l, null), T.class);
			System.out.println("\t"+l.getClass().getName()+" was casted to "+getClass().getName());
		} catch (ClassCastException e) {
			System.out.println("\t"+l.getClass().getName()+" was not casted to "+getClass().getName());
		}
		l.set((List.T)Runtime.coerce((int)3));
		System.out.println("\twith "+l.get());
		T[] p = new T[2];
		p[0] = get();
		System.out.println("Created an array [0]="+p[0]+"; [1]="+p[1]);
		T[] q = new T[] {get(), get()};
		System.out.println("Created an array with {"+q[0]+","+q[1]+"}");
		T[][] r = new T[][] {{get()}, {get()}};
		System.out.println("Created an array with {{"+r[0][0]+"},{"+r[1][0]+"}}");
		T[][][] s = new T[3][2][1];
		System.out.println("Created an array with {"+s[0]+","+s[1]+","+s[2]+"}");
		List/*[T]*/[][][] t = (List[][][])Runtime.newarray$(new List/*[T]*/[3][2][1], T.class);
		System.out.println("Created an array with {"+t[0]+","+t[1]+","+t[2]+"}");
		List l2 = new List(int[].class);
		boolean x2 = Runtime.instanceof$(List.instanceof$(l2, "int[]{self.length<2}"), int[].class);
		System.out.println("\t"+l2.getClass().getName()+" "+(x2?"is":"isn't")+" an instance of List$$int[]");
	}
}
