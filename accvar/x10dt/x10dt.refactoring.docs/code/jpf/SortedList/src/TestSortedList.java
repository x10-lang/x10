import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Mohsen Vakilian
 * 
 */
public class TestSortedList {

	LinkedList l1, l2;

	@Before
	public void setup() {
		l1 = new LinkedList(new Node(1));
		l1.append(new Node(2));
		l2 = new LinkedList(new Node(3));
		l1.append(new Node(4));
	}

	@Test
	public void l1Combinedl2ShouldBeSorted() {
		LinkedList l1l2 = l1.combine(l2);
		Assert.assertTrue(l1l2.isSorted());
	}

	@Test
	public void l2Combinedl1ShouldBeSorted() {
		LinkedList l2l1 = l2.combine(l1);
		Assert.assertFalse(l2l1.isSorted());
	}
}
