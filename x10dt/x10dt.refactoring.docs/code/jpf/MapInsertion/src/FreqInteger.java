import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Mohsen Vakilian
 * 
 */
public class FreqInteger {

	Map<Integer, Integer> m = new HashMap<Integer, Integer>();

	void accum(Integer k, Integer v) {
		if (!m.containsKey(k)) {
			m.put(k, 0);
		}
		m.put(k, m.get(k) + v);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((m == null) ? 0 : m.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FreqInteger other = (FreqInteger) obj;
		if (m == null) {
			if (other.m != null)
				return false;
		} else if (!m.equals(other.m))
			return false;
		return true;
	}

}
