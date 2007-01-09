/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//LIMITATION:
//This test case will not meet expectations. It is a limitation of the current release.
import harness.x10Test;

/**
 * All non-nullable fields, array elements and local variables should
 * be explicitly initialized. This test case should fail at compile
 * time. This test case tests what would happen if implicit
 * initializations were required.
 *
 * @author kemal 4/2005
 */
public class InitNonNullable_MustFailCompile extends x10Test {

	public boolean run() {
		nullable<BoxedNode> bn = new BoxedNode();
		chk(bn != null);
		Node tmp1 = (Node)(bn.val);
		chk(bn.val.data == 19);
		Node tmp2 = (Node)(bn.val.next);
		chk(bn.val.next.data == 19);
		Node tmp3 = (Node)(bn.val.next.next);
		chk(bn.val.next.next.data == 19);
		Node tmp4 = (Node)(bn.val.next.next.next);
		chk(bn.val.next.next.next.data == 19);

		nullable<BoxedNodeArray> bna = new BoxedNodeArray();
		chk(bna != null);
		Node[.] tmp5 = (Node[[0:9]->here])(bna.val);
		Node tmp6 = (Node)(bna.val[9]);
		chk(bna.val[9].data == 19);
		Node tmp7 = (Node)(bna.val[9].next);
		chk(bna.val[9].next.data == 19);
		final Node[.] A = new Node[[0:9]->here];
		Node tmp8 = (Node)(A[2]);
		chk(A[2].data == 19);
		Node tmp9 = (Node)(A[2].next);
		chk(A[2].next.data == 19);
		return true;
	}

	public static void main(String[] args) {
		new InitNonNullable_MustFailCompile().execute();
	}

	static class Node {
		int data;
		Node next;
		public Node() {
			data = 19;
		}
	}

	static class BoxedNode {
		public Node val;
	}

	static class BoxedNodeArray {
		public Node[[0:9]->here] val;
	}
}

