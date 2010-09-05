/**
 * 
 * @author Mohsen Vakilian
 * 
 */
public class Main {

	public static void main(String[] args) {
		Node node1 = new Node(1, null);
		Node node2 = new Node(2, node1);
		swapNodeShouldSucceed(node2);
	}

	public static void swapNodeShouldSucceed(Node node) {
		if (node != null) {
			node.swapByNext();
		}
	}

}
