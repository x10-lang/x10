
public class List {
	
	private static class Node {		
		var key:int;            
		var value:int;          
		var count:int;
		
		var is_next_remote:boolean;
		var next:Node;
		var next_remote:GlobalRef[Node];
		
		var is_prev_remote:boolean;
		var prev:Node;
		var prev_remote:GlobalRef[Node];
		
		def this(key:int, value:int) {
			this.key=key;
			this.value=value;
			this.count = 0;
			
			this.is_next_remote = false;
			this.next = null;
			this.next_remote = GlobalRef[Node](null); 
			
			this.is_prev_remote = false;
			this.prev = null;
			this.prev_remote = GlobalRef[Node](null);
		}
	}
	
	private static struct ScanRet {
		val node_prev_ref:GlobalRef[Node];
		val node_ref:GlobalRef[Node];
		val count:int;
		val is_found:boolean;
		
		def this(node_prev_ref:GlobalRef[Node], node_ref:GlobalRef[Node], count:int, is_found:boolean) {
			this.node_prev_ref = node_prev_ref;
			this.node_ref = node_ref;
			this.count = count;
			this.is_found = is_found;
		}
	}
	
	var head_node:Node;
	
	def this() {
		this.head_node = null;
		
	}
	
	private static def scan(op_type:int, start_node:Node, key:int, value:int):ScanRet {
		var res:boolean = false;
		var node:Node = start_node;
		var node_prev:Node = null;
		var count:int = 0;
		var is_found:boolean = false;
		
		while (node != null) {
			count++;
			
			//Console.OUT.println("["+here.id+"] cur_key="+node.key+" search_key="+key);
			
			if (key == node.key) {
				val node_prev_ref = GlobalRef[Node](node_prev);
				val node_ref = GlobalRef[Node](node);
				is_found = true;
				if (op_type == 1) {
					node.value = value;
				}
				return ScanRet(node_prev_ref, node_ref, count, is_found);
				
			} else {
				if (key < node.key) {
					val node_prev_ref = GlobalRef[Node](node_prev);
					val node_ref = GlobalRef[Node](node);
					return ScanRet(node_prev_ref, node_ref, count, is_found);
				}
				
				if (node.is_next_remote) {
					val node_local = node;
					val ret = at (node.next_remote) { List.scan(op_type, node_local.next_remote(), key, value) };
					return ret;
				} else {
					node_prev = node;
					node = node.next;
					
					var j:int = 0;
					for (var k:int = 0; k < 1000; k++) {
						j++;
					}
				}
			}
		}
		
		val node_prev_ref = GlobalRef[Node](node_prev);
		val node_ref = GlobalRef[Node](node);
		if (op_type == 1) {
			if (node != null) {
				if (node.key == key) {
					node.value = value;
				} else {
					insert(node, key, value);
				}
			} else { // node == null				
				insert_tail(node_prev, count, key, value);
			}
		}
		
		return ScanRet(node_prev_ref, node_ref, count, is_found);
	}
	
	public def contains(key:int) {
		val ret:ScanRet;
		ret = scan(0, this.head_node, key, key);
		return ret.is_found;
	}
	
	public def put(key:int, value:int) {
		val ret:ScanRet;
		
		if (this.head_node == null) {
			insert_head(key, value);
		}
		
		ret = List.scan(1, this.head_node, key, value);
		
		/*at (ret.node_ref) {
			val node = ret.node_ref();
			if (node != null) {
				if (node.key == key) {
					node.value = value;
				} else {
					insert(node, key, value);
				}
			} else { // node == null
				val node_prev = ret.node_prev_ref();
				insert_tail(node_prev, ret.count, key, value);
			}
		}*/
		
	}
	
	public def delete(key:int) {
		return;
	}
	
	private def insert_head(key:int, value:int) {
		val new_node = new Node(key, value);
		this.head_node = new_node;
		this.head_node.is_next_remote = false;
		this.head_node.is_prev_remote = false;
		this.head_node.prev = null;
		this.head_node.next = null;
	}
	
	private static def insert_tail(node_tail:Node, count:int, key:int, value:int) {
		
		if (count < 50) {
			val new_node = new Node(key, value);
			node_tail.next = new_node;
			new_node.prev = node_tail;

		} else {
			val node_tail_ref = GlobalRef[Node](node_tail);
			at (here.next()) {
				val new_node = new Node(key, value);
				new_node.is_prev_remote = true;
				new_node.prev_remote = node_tail_ref;
				val new_node_ref = GlobalRef[Node](new_node);
				at (node_tail_ref) {
					node_tail_ref().is_next_remote = true;
					node_tail_ref().next_remote = new_node_ref;
				}
			}
		}
		
	}
	
	private static def insert(cur_node:Node, key:int, value:int) {
		val new_node = new Node(key, value);
		var prev_node:Node;
		
		if (cur_node.is_prev_remote) {
			val prev_node_remote:GlobalRef[Node] = cur_node.prev_remote;
			
			new_node.next = cur_node;
			new_node.prev_remote = prev_node_remote;
			new_node.is_next_remote = false;
			new_node.is_prev_remote = true;
			
			cur_node.prev = new_node;
			
			val new_node_ref = GlobalRef[Node](new_node);
			at (prev_node_remote) {
				prev_node_remote().next_remote = new_node_ref;
			}
			
		} else {
			prev_node = cur_node.prev;
			
			new_node.next = cur_node;
			new_node.prev = prev_node;
			new_node.is_next_remote = false;
			new_node.is_prev_remote = false;
			
			cur_node.prev = new_node;
			prev_node.next = new_node;
			
		}
		
	}
	
	public def print_list() {
		Console.OUT.println("--------------------------------");
		List.print(this.head_node);
		Console.OUT.println();
		Console.OUT.println("--------------------------------");
		Console.OUT.println();
	}
	
	public def print_back() {
		Console.OUT.println("--------------------------------");
		List.print_reversed(this.head_node);
		Console.OUT.println();
		Console.OUT.println("--------------------------------");
		Console.OUT.println();
	}
    private static def print_reversed(head_node:Node) {
		var node_prev:Node = null;
		var node:Node = head_node;
		
		while (node != null) {

			if (node.is_next_remote) {
				val node_local = node;
				at (node.next_remote) {
					List.print_reversed(node_local.next_remote());
					return;
				}
			}
			node_prev = node;
			node = node.next;	
		}
		
		print_backwards(node_prev);
		
	}
	
	private static def print_backwards(tail_node:Node) {
		var node:Node = tail_node;
		
		while (node != null) {
			
			Console.OUT.print("[("+here.id+") "+node.key+", "+node.value+"] ");
			
			if (node.is_prev_remote) {
				val node_local = node;
				at (node.prev_remote) {
					List.print_backwards(node_local.prev_remote());
					return;
				}
			}
			node = node.prev;
			
		}
	}
	
	private static def print(head_node:Node) {
		var node:Node = head_node;
		
		while (node != null) {
			
			Console.OUT.print("[("+here.id+") "+node.key+", "+node.value+"] ");
			
			if (node.is_next_remote) {				
				val node_local = node;
				at (node.next_remote) {
					List.print(node_local.next_remote());
					return;
				}
			}
			node = node.next;
			
		}
		
	}
	
}