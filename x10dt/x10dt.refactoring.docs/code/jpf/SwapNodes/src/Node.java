import gov.nasa.jpf.symbc.Symbolic;

/**
 * 
 * @author Mohsen Vakilian
 * 
 */
public class Node {

	@Symbolic("true")
	int elem;

	@Symbolic("true")
	Node next;

	public Node(int elem, Node next) {
		super();
		this.elem = elem;
		this.next = next;
	}

	public Node swapByNext() {
		if (this.next != null)
			if (this.elem > this.next.elem) {
				Node t = this.next;
				this.next = t.next;
				t.next = this;
				return t;
			}
		return this;
	}

}