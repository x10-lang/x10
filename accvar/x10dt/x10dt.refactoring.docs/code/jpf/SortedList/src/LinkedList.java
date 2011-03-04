/**
 * 
 * @author Mohsen Vakilian
 * 
 */
public class LinkedList {

	Node head;
	Node last;

	public LinkedList(Node n) {
		this.head = this.last = n;
	}

	public void append(Node n) {
		last.next = n;
		last = n;
	}

	public void accum(int v) {
		Node newNode = new Node(v);
		if (head == last)
			append(newNode);
		else {
			head.next = newNode;
			last = newNode;
		}
	}

	public boolean isSorted() {
		return head == last || head.v < last.v;
	}

	public LinkedList combine(LinkedList ll) {
		LinkedList result = new LinkedList(head);
		if (head.next != null) {
			result.accum(head.next.v);
		}
		if (!result.isSorted()) {
			return result;
		}
		result.accum(ll.head.v);
		if (!result.isSorted()) {
			return result;
		}

		if (ll.head.next != null) {
			result.accum(ll.head.next.v);
		}
		return result;
	}
}
