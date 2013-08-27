public class RBTree {
	private static val RED=true,BLACK=false;
	
	private static class Node {
		var key:Int;            
		var value:Int;          
		var left:Node;
		var right:Node; 
		var parent:Node;
		var color:Boolean;      
		
		def this(key:int, value:int) {
			this.key=key;
			this.value=value;
			this.color=BLACK;
		
			this.left = null;
			this.right = null;
			this.parent = null;
		}
		
	}
	
	private static def parentOf(n:Node) {
		if (n != null) {
			return n.parent;
		} 
		return null;
	}
	
	private static def leftOf(n:Node) {
		if (n != null) {
			return n.left;
		} 
		return null;
	}
	
	private static def rightOf(n:Node) {
		if (n != null) {
			return n.right;
		} 
		return null;
	}

	private static def colorOf(n:Node) {
		if (n != null) {
			return n.color;
		} 
		return BLACK;
	}
	
	private static def setColor(n:Node, color:boolean) {
		if (n != null) {
			n.color = color;
		} 
	}
	
	
	private static class Tree {
		var root:Node;
		
		def this() {
			this.root = null;
		}
		
	}
	
	private static val n_null:Node = new Node(-1, -1);
	private val t:Tree;
	
	def this() {
		this.t = new Tree();
		
	}
	
	private def less(a:Int, b:Int):Boolean = a < b; 
	private def eq(a:Int, b:Int):Boolean = a == b; 
	
	private def lookup(key:int) {
		var n:Node = t.root; 
		while (n != null) { 
			if (eq(key, n.key)) { 
				return n;
			} else if (less(key, n.key)) {
				n = n.left;
			} else {
				n = n.right;
			} 
		}
		return null; 
	}

	private def rotateLeft(x:Node) {
		var r:Node = x.right;
		var rl:Node = r.left; 
		
		x.right = rl;
		if (rl != null) { 
			rl.parent = x;	
		} 
		var xp:Node = x.parent;
		r.parent = xp; 
		 
		if (xp == null) {
			t.root = r;
		} else if (xp.left == x) { 
			xp.left = r; 
		} else {
			xp.right = r;
		}
		r.left = x;
		x.parent = r; 
	}

	private def rotateRight (x:Node) {
		var l:Node = x.left;
		var lr:Node = l.right;
		x.left = lr; 
		 
		if (lr != null) {
			lr.parent = x;
		}
		
		var xp:Node = x.parent; 
		l.parent = xp;
		if (xp == null) {
			t.root = l;
		} else if (xp.right == x) {
			xp.right = l;
		} else { 
			xp.left = l;
		}
		l.right = x;
		x.parent = l;
	}
	
	private def fixAfterInsertion(xc:Node) {
		var x:Node = xc;
		x.color = RED; 

		while (x != null && x != t.root) { 
			var xp:Node = x.parent; 
			if (xp.color != RED) break ; 

			if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
				var y:Node = rightOf(parentOf(parentOf(x)));
				if (colorOf(y) == RED) {
					RBTree.setColor(RBTree.parentOf(x), BLACK);
					RBTree.setColor(y, BLACK);
					RBTree.setColor(RBTree.parentOf(RBTree.parentOf(x)), RED);
					x = parentOf(parentOf(x));
				} else {
					if (x == RBTree.rightOf(RBTree.parentOf(x))) {
						x = RBTree.parentOf(x);
						rotateLeft(x);
					}
					RBTree.setColor(RBTree.parentOf(x), BLACK);
					RBTree.setColor(RBTree.parentOf(RBTree.parentOf(x)), RED);
					if (RBTree.parentOf(RBTree.parentOf(x)) != null)
						rotateRight(RBTree.parentOf(RBTree.parentOf(x)));
				}
			} else {
				var y:Node = RBTree.leftOf(RBTree.parentOf(RBTree.parentOf(x)));
				if (RBTree.colorOf(y) == RED) {
					RBTree.setColor(RBTree.parentOf(x), BLACK);
					RBTree.setColor(y, BLACK);
					RBTree.setColor(RBTree.parentOf(RBTree.parentOf(x)), RED);
					x = RBTree.parentOf(RBTree.parentOf(x));
				} else {
					if (x == leftOf(parentOf(x))) {
						x = parentOf(x);
						rotateRight(x);
					}
					setColor(parentOf(x),  BLACK);
					setColor(parentOf(parentOf(x)), RED);
					if (parentOf(parentOf(x)) != null)
						rotateLeft(parentOf(parentOf(x)));
				}
			}
		}
		var ro:Node = t.root; 
		if (ro.color != BLACK) {
			ro.color = BLACK ; 
		}
	}
	
	private def insert(key:int, value:int) { 
		var n:Node;
		var nt:Node;
		var nt_l:Node;
		var nt_r:Node;
		
		//Console.OUT.println("insert start\n");
		nt = t.root;
		if (nt == null) {
			n = new Node(key, value);
			t.root = n;
			//Console.OUT.println("insert root initialized\n");
			return null;
		}
		
		while (true) {
			if (eq(key, nt.key)) {
				//Console.OUT.println("insert found\n");
				return nt;
			} else if (less(key, nt.key)) {
				nt_l = nt.left;
				if (nt_l != null) {
					nt = nt_l;
				} else {
					n = new Node(key, value);
					n.parent = nt;
					nt.left = n;
					fixAfterInsertion(n);
					//Console.OUT.println("insert fixed 1\n");
					return null;
				}
			} else {
				nt_r = nt.right;
				if (nt_r != null) {
					nt = nt_r;
				} else {
					n = new Node(key, value);
					n.parent = nt;
					nt.right = n;
					fixAfterInsertion(n);
					//Console.OUT.println("insert fixed 2\n");
					return null;
				}
			}
		}
	}
	
	public def put(key:int, value:int) {
		var n:Node;
		
		n = insert(key, value);
		if (n != null) {
			n.value = value;
			return false;
		}
		return true;
		 
	}
	
	private def successor(t:Node) {
		if (t == null) {
			return null;
		} else if (t.right != null) {
			var p:Node = t.right; 
			while (p.left != null) { 
				p = p.left;  
			}
			return p;
		} else {
			var p:Node = t.parent;
			var ch:Node = t;
			while (p != null && ch == p.right) {
				ch = p;
				p = p.parent; 
			}
			return p;
		}
	}
	
	private def fixAfterDeletion(xc:Node) {
		var x:Node = xc;
		while (x != t.root && RBTree.colorOf(x) == BLACK) { 
			if (x == RBTree.leftOf(RBTree.parentOf(x))) {
				var sib:Node = RBTree.rightOf(RBTree.parentOf(x));
				if (RBTree.colorOf(sib) == RED) {
					RBTree.setColor(sib, BLACK);
					RBTree.setColor(RBTree.parentOf(x), RED);
					rotateLeft(RBTree.parentOf(x));
					sib = RBTree.rightOf(RBTree.parentOf(x));
				}

				if (RBTree.colorOf(RBTree.leftOf(sib)) == BLACK && RBTree.colorOf(RBTree.rightOf(sib)) == BLACK) {
					RBTree.setColor(sib,  RED);
					x = RBTree.parentOf(x);
				} else {
					if (RBTree.colorOf(RBTree.rightOf(sib)) == BLACK) {
						RBTree.setColor(RBTree.leftOf(sib), BLACK);
						RBTree.setColor(sib, RED);
						rotateRight(sib);
						sib = RBTree.rightOf(RBTree.parentOf(x));
					}
					RBTree.setColor(sib, RBTree.colorOf(RBTree.parentOf(x)));
					RBTree.setColor(RBTree.parentOf(x), BLACK);
					RBTree.setColor(RBTree.rightOf(sib), BLACK);
					rotateLeft(RBTree.parentOf(x));
					// TODO: consider break ...
					x = t.root; 
				}
			} else { // symmetric
				var sib:Node = RBTree.leftOf(RBTree.parentOf(x));

				if (colorOf(sib) == RED) {
					RBTree.setColor(sib, BLACK);
					RBTree.setColor(RBTree.parentOf(x), RED);
					rotateRight(RBTree.parentOf(x));
					sib = RBTree.leftOf(RBTree.parentOf(x));
				}

				if (RBTree.colorOf(RBTree.rightOf(sib)) == BLACK &&
						RBTree.colorOf(RBTree.leftOf(sib)) == BLACK) {
					RBTree.setColor(sib,  RED);
					x = RBTree.parentOf(x);
				} else {
					if (RBTree.colorOf(RBTree.leftOf(sib)) == BLACK) {
						RBTree.setColor(RBTree.rightOf(sib), BLACK);
						RBTree.setColor(sib, RED);
						rotateLeft(sib);
						sib = RBTree.leftOf(RBTree.parentOf(x));
					}
					RBTree.setColor(sib, RBTree.colorOf(RBTree.parentOf(x)));
					RBTree.setColor(RBTree.parentOf(x), BLACK);
					RBTree.setColor(leftOf(sib), BLACK);
					rotateRight(parentOf(x));
					// TODO: consider break ...
					x = t.root; 
				}
			}
		}

		if (x != null && x.color != BLACK) {
			x.color = BLACK; 
		}
	}

	private def delete (n:Node) { 
		if (n.left != null && n.right != null) {
			var s:Node = successor(n);
			n.key = s.key;
			n.value = s.value;
		}
		
		var replacement:Node = n.left != null ? n.left : n.right;
		
		if (replacement != null) {
			replacement.parent = n.parent;
			 
			var pp:Node = n.parent; 
			if (pp == null) {
				t.root = replacement; 
			} else if (n == pp.left) { 
				pp.left = replacement; 
			} else {
				pp.right = replacement; 
			}
			n.left = null;
			n.right = null;
			n.parent = null;
			
			if (n.color == BLACK) {
				fixAfterDeletion(replacement);
			}
		} else if (n.parent == null) {
			t.root = null; 
		} else { 
			if (n.color == BLACK) {
				fixAfterDeletion(n);
			}
			var pp:Node = n.parent; 
			if (pp != null) {
				if (n == pp.left) {
					pp.left = null; 
				} else if (n == pp.right) {
					pp.right = null; 
				}
				n.parent = null; 
			}
		}
		return n; 
	}
	
	public def delete(key:int) {
		var n:Node = lookup(key);
		if (n != null) {
			n = delete(n);
			return true;
		}
		return false;
	}

	public def contains(key:int) {
		var n:Node = lookup(key);

		return n != null;
	}

}