import edu.mit.csail.sdg.annotations.Ensures;
import edu.mit.csail.sdg.annotations.Invariant;
import edu.mit.csail.sdg.annotations.Modifies;
import edu.mit.csail.sdg.annotations.Pure;
import edu.mit.csail.sdg.annotations.Requires;
import edu.mit.csail.sdg.annotations.Returns;

/**
 * 
 * @author Mohsen Vakilian
 * 
 */
public class SwapNodes {

	@Requires("no (l1.head.*next - null) & (l2.head.*next - null)")
	// @Throws("Exception : false")
	@Returns("true")
	@Modifies("Node.next")
	public static boolean idIsSwapNodeCommutative(List l1, List l2, int id1,
			int id2) {
		// l1 != l2 is not sufficient. They should be recursively disjoint.
		if (l1 != null && l2 != null && l1 != l2 && l1.equals(l2)) {
			l1.swapByNext(id1);
			l1.swapByNext(id2);

			l2.swapByNext(id2);
			l2.swapByNext(id1);

			return (l1.equals(l2));
		}
		return true;
	}

	public static void main(String[] args) {
		Node n1 = new Node(-3, null);
		Node n2 = new Node(-3, null);

		List l1 = new List(n1);
		List l2 = new List(n2);

		System.out.println(idIsSwapNodeCommutative(l1, l2, -3, -1));

		System.out.println(n2.next);
	}
}

// @Invariant({ "no (this & this.^next)", "no n in Node | n.id = (n.^next).id"
// })
@Invariant({ "no (this & this.^next)" })
class Node {

	@Invariant("no (this.(Node @ id) & (this.^next).(Node @ id))")
	int id;

	Node next;

	@Ensures({ "no (this & (this.^next))",
			"no (this.(Node @ id) & (this.^next).(Node @ id))" })
	@Modifies("this.id, this.next")
	public Node(int id, Node next) {
		super();
		this.id = id;
		this.next = next;
	}

	// @Modifies("this.next, this.next.next")
	@Modifies("Node.next")
	public Node swapByNext() {
		if (this.next != null) {
			Node t = this.next;
			this.next = t.next;
			t.next = this;
			return t;
		}
		return this;
	}

	@Pure
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((next == null) ? 0 : next.hashCode());
		return result;
	}

	@Pure
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Node))
			return false;
		Node other = (Node) obj;
		if (id != other.id)
			return false;
		if (next == null) {
			if (other.next != null)
				return false;
		} else if (!next.equals(other.next))
			return false;
		return true;
	}

	// @Helper
	// public boolean directEquals(Object obj) {
	// if (this == obj)
	// return true;
	// if (obj == null)
	// return false;
	// if (!(obj instanceof Node))
	// return false;
	// Node other = (Node) obj;
	// if (id != other.id)
	// return false;
	// if (next == null) {
	// if (other.next != null)
	// return false;
	// }
	// return true;
	// }
	//
	// @Helper
	// @Override
	// public boolean equals(Object obj) {
	// Node thisCurNode = this;
	// Object otherCurObject = obj;
	//
	// while (thisCurNode != null) {
	// if (!thisCurNode.directEquals(otherCurObject)) {
	// return false;
	// }
	// Node otherCurNode = (Node) otherCurObject;
	// thisCurNode = thisCurNode.next;
	// otherCurObject = otherCurNode.next;
	// }
	//
	// return otherCurObject == null;
	// }

}

class List {

	@Invariant("this.head != null")
	Node head;

	@Modifies("this.head")
	@Ensures("this.head != null")
	public List(Node head) {
		super();
		this.head = head;
	}

	@Pure
	public Node find(int id) {
		Node curNode = head;

		while (curNode != null) {
			if (curNode.id == id)
				return curNode;
			curNode = curNode.next;
		}

		return null;
	}

	@Modifies("Node.next")
	public void swapByNext(int id) {
		Node n = find(id);
		if (n != null) {
			n.swapByNext();
		}
	}

	@Pure
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((head == null) ? 0 : head.hashCode());
		return result;
	}

	@Pure
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof List))
			return false;
		List other = (List) obj;
		if (head == null) {
			if (other.head != null)
				return false;
		} else if (!head.equals(other.head))
			return false;
		return true;
	}

}
