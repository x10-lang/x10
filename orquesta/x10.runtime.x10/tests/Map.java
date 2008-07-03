import x10.generics.Parameters;
import x10.generics.Synthetic;
import x10.runtime.Runtime;

@Parameters({"K","V"})
public class Map implements Runnable {
	public class K {};
	public class V {};
	public static K EMPTY;
	public static V NONE;
	public K key;
	public V value;
	public Map() { }
	@Synthetic public Map(Class K, Class V) { this(); }
	public Map(int i) { }
	@Synthetic public Map(Class K, Class V, int i) { this(i); }
	@Synthetic public static boolean instanceof$(Object o, String constraint) { assert(false); return true; }
	public static boolean instanceof$(Object o, String constraint, boolean b) { /*check constraint*/; return b; }
	public static Object cast$(Object o, String constraint) { /*check constraint*/; return (Map)o; }
	private boolean keq(K k1, K k2) {
		if ((boolean)(k1 == k2))
			return true;
		return false;
	}
	public V get(K k) {
		if (keq(k, key))
			return value;
		return NONE;
	}
	public void put(K k, V v) {
		key = k;
		value = v;
	}
	public void remove(K k) {
		key = EMPTY;
	}
	public void run() {
		System.out.println("Loaded and executed: "+getClass().getName());
		Map m = new Map(int.class, double.class, 3);
		K k = (Map.K)Runtime.coerce((int)5);
		m.put(k, (Map.V)Runtime.coerce((double)7.0));
		System.out.println("Created map m of type"+m.getClass().getName());
		System.out.println("\tretrieved m{5}="+m.get(k));
	}
}
