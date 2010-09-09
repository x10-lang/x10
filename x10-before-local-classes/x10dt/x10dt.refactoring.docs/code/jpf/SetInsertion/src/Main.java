import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Mohsen Vakilian
 *
 */
public class Main {

	public static void main(String[] args) {
		int a = 1;
		int b = 2;
		setInsertionShouldBeCommutative(a, b);
	}

	public static void setInsertionShouldBeCommutative(int a, int b) {
		Set<Integer> set1 = new HashSet<Integer>();
		set1.add(a);
		set1.add(b);

		Set<Integer> set2 = new HashSet<Integer>();
		set2.add(b);
		// set2.add(a);

		assert set1.equals(set2);

		if (!set1.equals(set2))
			throw new RuntimeException("!set1.equals(set2)");
	}
}
