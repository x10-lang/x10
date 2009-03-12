//LIMITATION:
//This test case will not meet expectations. It is a limitation of the current release.
/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
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

	public def run(): boolean = {
		var bn: BoxedNode = new BoxedNode();
		chk(bn != null);
		var tmp1: Node = (bn.val) as Node;
		chk(bn.val.data == 19);
		var tmp2: Node = (bn.val.next) as Node;
		chk(bn.val.next.data == 19);
		var tmp3: Node = (bn.val.next.next) as Node;
		chk(bn.val.next.next.data == 19);
		var tmp4: Node = (bn.val.next.next.next) as Node;
		chk(bn.val.next.next.next.data == 19);

		var bna: BoxedNodeArray = new BoxedNodeArray();
		chk(bna != null);
		var tmp5: Array[Node] = (bna.val) as Array[Node];
		var tmp6: Node = (bna.val(9)) as Node;
		chk(bna.val(9).data == 19);
		var tmp7: Node = (bna.val(9).next) as Node;
		chk(bna.val(9).next.data == 19);
		val A: Array[Node] = new Array[Node]([0..9]->here) as Array[Node];
		var tmp8: Node = (A(2)) as Node;
		chk(A(2).data == 19);
		var tmp9: Node = (A(2).next) as Node;
		chk(A(2).next.data == 19);
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new InitNonNullable_MustFailCompile().execute();
	}

	static class Node {
		var data: int;
		var next: Node;
		public def this(): Node = {
			data = 19;
		}
	}

	static class BoxedNode {
		public var val: Node;
	}

	static class BoxedNodeArray {
		public var val: Array[Node];
	}
}
