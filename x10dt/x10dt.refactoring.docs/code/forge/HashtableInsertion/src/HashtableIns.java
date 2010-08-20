import java.util.Hashtable;
import edu.mit.csail.sdg.annotations.Returns;
import edu.mit.csail.sdg.annotations.Throws;

/**
 * 
 * @author Mohsen Vakilian
 * 
 */
public class HashtableIns {

	public void accum(Hashtable m, int k, int v) {
		if (!m.containsKey(k)) {
			m.put(k, 0);
		}
		m.put(k, (Integer) m.get(k) + v);
	}

	@Throws("Exception : false")
	@Returns("true")
	public boolean isHashtableAccumCommutative(Hashtable m1, int k1, int v1,
			Hashtable m2, int k2, int v2) {
		if (m1 != m2 && m1.equals(m2)) {
			accum(m1, k1, v1);
			accum(m1, k2, v2);

			accum(m2, k2, v2);
			accum(m2, k1, v2);

			return m1.equals(m2);
		} else
			return true;
	}

}
