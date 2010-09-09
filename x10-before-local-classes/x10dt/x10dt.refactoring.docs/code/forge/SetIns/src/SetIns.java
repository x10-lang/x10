import java.util.Set;

import edu.mit.csail.sdg.annotations.Returns;
import edu.mit.csail.sdg.annotations.Throws;

/**
 * 
 * @author Mohsen Vakilian
 * 
 */
public class SetIns {

	@Throws("Exception : false")
	@Returns("true")
	public boolean isSetInsCommutative(Set<Integer> s1, Set<Integer> s2,
			Integer a, Integer b) {
		if (s1 != s2 && s1.equals(s2)) {
			s1.add(a);
			s1.add(b);

			s2.add(b);
			s2.add(a);

			return s1.equals(s2);
		} else
			return true;
	}
}
