import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author Mohsen Vakilian
 * 
 */
public class CollectionAdditionCommutativityChecker {

	public static void main(String[] args) {
		testSetInsertionCommutativity();
		testListInsertionCommutativity();
	}

	public static void testListInsertionCommutativity() {
		List<Integer> l = new ArrayList<Integer>();
		l.add(1);
		l.add(3);
		l.add(5);
		areTheseListAppendsCommutative(l, 2, 3);
	}

	public static void testSetInsertionCommutativity() {
		Set<Integer> s = new HashSet<Integer>();
		s.add(1);
		s.add(3);
		s.add(5);
		areTheseSetInsertionsCommutative(s, 2, 3);
	}

	public static void areTheseSetInsertionsCommutative(Set<Integer> set,
			Integer n1, Integer n2) {
		Set<Integer> set1 = new HashSet<Integer>(set);
		Set<Integer> set2 = new HashSet<Integer>(set);

		insert(set1, n1, n2);
		insert(set2, n2, n1);

		assert set1.equals(set2);
	}

	public static void areTheseListAppendsCommutative(List<Integer> list,
			Integer n1, Integer n2) {
		List<Integer> list1 = new ArrayList<Integer>(list);
		List<Integer> list2 = new ArrayList<Integer>(list);

		insert(list1, n1, n2);
		insert(list2, n2, n1);

		assert list1.equals(list2);
	}

	public static void insert(Collection<Integer> set, Integer n1, Integer n2) {
		set.add(n1);
		set.add(n2);
	}

}
