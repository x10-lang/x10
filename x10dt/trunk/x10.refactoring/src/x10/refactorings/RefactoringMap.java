package x10.refactorings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RefactoringMap<K extends RefactoringPosition,V> implements Map<K,V> {

	private ArrayList<Entry<K,V>> internalSet;
	private HashMap<K, Integer> keySet;
	private HashMap<V, Integer> valueSet;
	
	public RefactoringMap() {
		internalSet = new ArrayList<Entry<K,V>>();
		keySet = new HashMap<K, Integer>();
		valueSet = new HashMap<V, Integer>();
	}
	
	public RefactoringMap(int initSize){
		internalSet = new ArrayList<Entry<K,V>>(initSize);
		keySet = new HashMap<K, Integer>(initSize);
		valueSet = new HashMap<V, Integer>(initSize);
	}
	
	public void clear() {
		internalSet.clear();		
	}

	public boolean containsKey(Object key) {
		return keySet.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return valueSet.containsKey(value);
	}

	public Set<Map.Entry<K, V>> entrySet() {
		HashSet<Map.Entry<K,V>> retval = new HashSet<Map.Entry<K,V>>();
		retval.addAll(internalSet);
		return retval;
	}

	public Collection<? extends Map.Entry<K,V>> entryCollection() {
		return internalSet;
	}
	
	public V get(Object key) {
		for (Entry<K, V> e : internalSet) {
			if (e.key.equals(key))
				return e.value;
		}
		return null;
	}

	public boolean isEmpty() {
		return internalSet.isEmpty();
	}

	public Set<K> keySet() {
		return keySet.keySet();
	}

	public void putAll(Map<? extends K, ? extends V> t) {
		for (Map.Entry<? extends K, ? extends V> e : t.entrySet()) {
			K key = e.getKey();
			V value = e.getValue();
			putVoid(key, value);
		}
	}

	public V remove(Object key) {
		if (keySet.containsKey(key)) {
			Entry<K,V> removeEntry = null;
			int index = 0;
			for (Entry<K,V> e : internalSet) {
				index++;
				if (e.key.equals(key)) {
					removeEntry = e;
					break;
				}
			}
			if (removeEntry != null) {
				internalSet.remove(index);
				int keynum = keySet.remove(removeEntry.key);
				int valnum = valueSet.remove(removeEntry.value);
				if (keynum != 1)
					keySet.put(removeEntry.key, keynum-1);
				if (valnum != 1)
					valueSet.put(removeEntry.value, valnum-1);
				return removeEntry.value;
			}
		}
		return null;
	}

	public int size() {
		return internalSet.size();
	}

	public Collection<V> values() {
		// TODO Auto-generated method stub
		return valueSet.keySet();
	}
	
	
	public <T extends K> Collection<V> getAll(T key){
		ArrayList<V> resultList = new ArrayList<V>();
		for (Map.Entry<K,V> e : internalSet){
			if (key.equals(e.getKey()))
				resultList.add(e.getValue());
		}
		return resultList;
	}
	
	public V put(K key, V value){
		V oldVal = get(key);
		putVoid(key, value);
		return oldVal;
	}

	public void putVoid(K key, V value){
		// For the application of mapping a given position to multiple values,
		// this guard is correct. However, this does not allow a value to be
		// associated with multiple keys.
		if (!valueSet.containsKey(value)){
			Entry<K, V> new_e = new Entry<K,V>(key, value);
			internalSet.add(new_e);
			if (!keySet.containsKey(key))
				keySet.put(key, 1);
			else
				keySet.put(key, keySet.get(key)+1);
			if (!valueSet.containsKey(value))
				valueSet.put(value, 1);
			else
				valueSet.put(value, valueSet.get(value)+1);
		}
	}

	static class Entry<K,V> implements Map.Entry<K,V> {
        final K key;
        V value;

        /**
         * Create new entry.
         */
        Entry(K k, V v) {
            value = v;
            key = k;
        }

        public K getKey() {
            return key; /*ibm@98545*/
        }

        public V getValue() {
            return value;
        }
    
        public V setValue(V newValue) {
	    V oldValue = value;
            value = newValue;
            return oldValue;
        }
    
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry e = (Map.Entry)o;
            Object k1 = getKey();
            Object k2 = e.getKey();
            if (k1 == k2 || (k1 != null && k1.equals(k2))) {
                Object v1 = getValue();
                Object v2 = e.getValue();
                if (v1 == v2 || (v1 != null && v1.equals(v2))) 
                    return true;
            }
            return false;
        }
    
        public int hashCode() {
            return (key==null ? 0 : key.hashCode()) ^
                   (value==null   ? 0 : value.hashCode()); /*ibm@98545*/
        }
    
        public String toString() {
            return getKey() + "=" + getValue();
        }

    }
	
	public String toString() {
		return internalSet.toString();
	}
}
