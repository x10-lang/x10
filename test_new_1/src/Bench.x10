
import x10.util.Random;
import x10.io.Console;
import x10.compiler.Transaction;

public class Bench {
    
	public static val num_of_ops = new Array[long](10);
	
	public static def execute(tree_obj:RBTree, tree_size:int, ins_part:int, del_part:int, n_ops:int, n_threads:int, is_stm:boolean) {
		for (var i:int=0; i < tree_size; i++) {
			tree_obj.put(i, i);
		}
		
		for (var i:int=0; i < tree_size; i++) {
			if (!tree_obj.contains(i)) {
				Console.OUT.println("ERROR: not contains key="+i);
			}
		}
		
		Console.OUT.println("STARTING...");
		finish for (var k:int=0; k < n_threads; k++)
		{
			async {
				Runtime.initTMThread();
				
				val uniq_id = Runtime.getTMThreadUniqId();
				
				val rnd = new Random();
				var prob:int;
				var key:int;
				var value:int;
				var num_of_ins:long = 0;
				var num_of_del:long = 0;
				var num_of_lookup:long = 0;
				var checksum:long = 0;
				
				for (var j:int=0; j < n_ops/n_threads; j++) {
					prob = rnd.nextInt() % 100;
					if (prob < 0) {
						prob *= -1;
					}
					key = rnd.nextInt() % tree_size;
					if (key < 0) {
						key *= -1;
					}
					value = rnd.nextInt() % tree_size;
					if (value < 0) {
						value *= -1;
					}
					
					if (is_stm) {
						if (prob < ins_part) {
							//Console.OUT.println("INSERT");
							@Transaction
							atomic {	
								//tree_obj(key) = value;
								tree_obj.put(key, value);
							}
							num_of_ins++;
						} else if (prob < (ins_part + del_part)) {
							//Console.OUT.println("DELETE");
							@Transaction
							atomic {
								tree_obj.delete(key);
							}
							num_of_del++;
						} else {
							//Console.OUT.println("LOOKUP");
							@Transaction
							atomic {
								tree_obj.contains(key);
							}
							num_of_lookup++;
						}
					} else {
						if (prob < ins_part) {
							atomic {
								//tree_obj(key) = value;
								tree_obj.put(key, value);
							}
							num_of_ins++;
						} else if (prob < (ins_part + del_part)) {
							atomic {
								tree_obj.delete(key);
							}
							num_of_del++;
						} else {
							atomic {
								tree_obj.contains(key);
							}
							num_of_lookup++;
						}
					}
				}
				
				Runtime.finishTMThread();
				
				atomic {
					Bench.num_of_ops(0) += num_of_ins;
					Bench.num_of_ops(1) += num_of_del;
					Bench.num_of_ops(2) += num_of_lookup;
				}
			}
		}
	}
	public static def main(args: Array[String]) {
    	if (args.size != 6) {
    		Console.OUT.println("Usage: Bench RBTreeSize inserts deletes nOps nThreads IsStm");
    		return;
    	}
    	val RB_SIZE = int.parse(args(0));
    	val INS_PART = int.parse(args(1));
    	val DEL_PART = int.parse(args(2));
    	val N_OPS = int.parse(args(3));
    	val N_THREADS = int.parse(args(4));
    	val isSTM = (int.parse(args(5)) == 1)? true : false;
    	
    	Runtime.initTMSystem();
    	
    	//val tree = new LLRBTreeSetInt();
    	val tree = new RBTree();
    	
    	execute(tree, RB_SIZE, INS_PART, DEL_PART, N_OPS, N_THREADS, isSTM);
    	
    	Console.OUT.println("num_of_inserts: " + Bench.num_of_ops(0));
    	Console.OUT.println("num_of_deletes: " + Bench.num_of_ops(1));
    	Console.OUT.println("num_of_lookups: " + Bench.num_of_ops(2));
    	
    	/*if (ok) {
    		Console.OUT.println("Test ok.");
    	} else {
    		Console.OUT.println("Test failed.");
    	}*/
    	
    	Runtime.finishTMSystem();
    	
    }
}