@x10.generics.Parameters({"K","V"})
public class Map implements Runnable {
	public class K {};
	public class V {};
	public static K EMPTY;
	public static V NONE;
	public K key;
	public V value;
	public Map() { }
	public Map(Class V, Class K) { }
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
	}
}
