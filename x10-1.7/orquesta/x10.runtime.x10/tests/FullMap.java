import x10.generics.Parameters;
import x10.generics.InstantiateClass;
import x10.generics.Synthetic;
import x10.runtime.Runtime;

@Parameters({"K","V"})
public class FullMap implements Runnable {
	public static class K {};
	public static class V {};
	public V NONE;
	@Parameters({"K","V"})
	public static class Entry {
		public static class K {};
		public static class V {};
		public K key;
		public V value;
		@InstantiateClass({"FullMap$Entry$K", "FullMap$Entry$V"})
		public Entry next;
		public Entry(K k, V v, @InstantiateClass({"FullMap$Entry$K", "FullMap$Entry$V"}) Entry n) { key = k; value = v; next = n; }
		@Synthetic public Entry(Class K, Class V, K k, V v, @InstantiateClass({"FullMap$Entry$K", "FullMap$Entry$V"}) Entry n) { this(k, v, n); }
	}
	@InstantiateClass({"FullMap$K", "FullMap$V"})
	Entry/*[K,V]*/[] table = (Entry[])Runtime.newarray$(new Entry/*[K,V]*/[10], K.class, V.class);
	private int hash(K k) { return 0; }
	private boolean keq(K k1, K k2) {
		if (k1 == k2)
			return true;
		return false;
	}
	public V get(K k) {
		int h = hash(k);
		int h2 = h % table.length;
		for (Entry e = table[h2]; e != null; e = e.next) {
			if (keq(k, (FullMap.K)Runtime.coerce((FullMap.Entry.K)e.key)))
				return (FullMap.V)Runtime.coerce((FullMap.Entry.V)e.value);
		}
		return NONE;
	}
	public void put(K k, V v) {
		int h = hash(k);
		int h2 = h % table.length;
		for (Entry e = table[h2]; e != null; e = e.next) {
			if (keq(k, (FullMap.K)Runtime.coerce((FullMap.Entry.K)e.key))) {
				e.value = (FullMap.Entry.V)Runtime.coerce((FullMap.V)v);
				return;
			}
		}
		Entry e = new Entry(K.class, V.class, (FullMap.Entry.K)Runtime.coerce((FullMap.K)k), (FullMap.Entry.V)Runtime.coerce((FullMap.V)v), table[h2]);
		table[h2] = e;
	}
	public void remove(K k) {
		int h = hash(k);
		int h2 = h % table.length;
		Entry prev = null;
		Entry e = table[h2];
		for (; e != null; e = e.next) {
			if (keq(k, (FullMap.K)Runtime.coerce((FullMap.Entry.K)e.key))) {
				if (prev == null)
					table[h2].next = e.next;
				else
					prev.next = e.next;
			}
		}
		prev = e;
	}
	public void run() {
		System.out.println("Loaded and executed: "+getClass().getName());
	}
}
