import x10.compiler.*;
import x10.util.OptionsParser;
import x10.util.Option;
import x10.lang.Math;
import x10.util.Random;
import x10.util.Stack;

public class UTS {

	static type PLH = PlaceLocalHandle[BinomialState];
	static type PLH2= PlaceLocalHandle[BinomialState2];
	private static val NORMALIZER = 2147483648.0; // does not depend on input parameters

	@NativeRep ("c++", "UTS__SHA1Rand", "UTS__SHA1Rand", null)
	@NativeCPPCompilationUnit ("sha1.c")
	@NativeCPPCompilationUnit ("UTS__SHA1Rand.cc")
	public static struct SHA1Rand {
		public def this (seed:Int) { }
		public def this (parent:SHA1Rand, spawn_number:Int) { }
		@Native ("c++", "UTS__SHA1Rand_methods::apply(#0)")
		public def apply () : Int = 0;
	}

	static class SeqBinomialState {
		// params that define the tree
		val q:Long, m:Int;

		var nodesCounter:UInt = 0;
		public def this (q:Long, m:Int) {
			this.q = q; this.m = m; 
		}

		public final def processSubtree (rng:SHA1Rand) {
			processSubtree(rng, (rng() < q) ? m : 0);
		}
		public final def processSubtree (rng:SHA1Rand, numChildren:Int) {
			nodesCounter++;
			/* Iterate over all the children and push on stack. */
			for (var i:Int=0 ; i<numChildren ; ++i) 
				processSubtree(SHA1Rand(rng, i));
		}

		public final def main (b0:Int, rng:SHA1Rand) {
			processSubtree(rng, b0);
			Console.OUT.println(nodesCounter+" nodes. ");
			return nodesCounter;
		}
	}
	/*
	static class ImplicitStealBinomialState {
		// params that define the tree
		val q:Long, m:Int, k:Int;

		var nodesCounter:UInt = 0;
		public def this (q:Long, m:Int, k:Int) {
			this.q = q; this.m = m; this.k=k;
		}

		public final def processSubtree (st:PLH, rng:SHA1Rand) {
			processSubtree(rng, (rng() < q) ? m : 0);
		}
		public final def processSubtree (st: PLH, rng:SHA1Rand, numChildren:Int) {
			offer 1; 
			// Iterate over all the children and push on stack. 
			for (var i:Int=0 ; i<numChildren ; ++i) 
				@global async 
				   st().processSubtree(st, SHA1Rand(rng, i));
		}

		public final def main (st: PLH, b0:Int, rng:SHA1Rand) {
			val count = finish(Int.+) { processSubtree(st, rng, b0) };
			Console.OUT.println(nodesCounter+" nodes. ");
			return nodesCounter;
		}
	}
	*/
	
    
    static  class BinomialState {

	const STATE_PLACE_ZERO = 0; // the first place does not use a state machine, use this value as placeholder
	const STATE_STEALING = 1; // actively stealing work from other places
	const STATE_ARRESTED = 2; // no-longer stealing but still alive and could potentially steal again
	const STATE_DEATH_ROW = 3; // will soon be dead, will never steal again
	
	// places > 0 start off life stealing
	var state:Int = here == Place.FIRST_PLACE ? STATE_PLACE_ZERO : STATE_STEALING;

	val stack = new Stack[SHA1Rand]();

	// params that define the tree
	val q:Long, m:Int, k:Int, nu:Int;

  	var nodesCounter:Long = 0L;
    var stealsAttempted:Long = 0L;
    var stealsPerpetrated:Long = 0L;
    var stealsReceived:Long = 0L;
    var stealsSuffered:Long = 0L;
    var nodesGiven:Long = 0L;
    var nodesReceived:Long = 0L;
	
	val myRandom = new Random();
	public def this (q:Long, m:Int, k:Int, nu:Int) {
	    this.q = q; this.m = m; this.k=k; this.nu=nu;
	}

	final def processSubtree (rng:SHA1Rand) {
	    processSubtree(rng, (rng() < q) ? m : 0);
	}
	final def processSubtree (rng:SHA1Rand, numChildren:Int) {
	    nodesCounter++;
	    /* Iterate over all the children and push on stack. */
	    for (var i:Int=0 ; i<numChildren ; ++i) 
		stack.push(SHA1Rand(rng, i));
	}

	def processStack() {
		var count:Int=0;
		while (stack.size() > 0) {
		    processSubtree(stack.pop());
		    if (count++ % nu == 0) 
			Runtime.probe();
		}
	    }
		
	    final def pop(k:Int) = ValRail.make[SHA1Rand](k, (int)=> stack.pop());

	    def trySteal () : ValRail[SHA1Rand] {
		stealsReceived++;
		val length = stack.size();
		val numSteals = 
		    (k > 0 ? (k < length ?  k : (k/2 < length ? k/2 :0))
		     : length/2);
		if (length <= 2 || numSteals==0) {
		    return null;
		}
		stealsSuffered++;
		nodesGiven += numSteals;
		return pop(numSteals);
	    }
		
	    def attemptSteal(st:PLH) {
		val P = Place.MAX_PLACES;
		val start = myRandom.nextInt(P);
		for (var pi:Int=0 ; pi <P ; ++pi) {
		    val p = Place((start+pi)%P);
		    if (p==here) continue;
		    stealsAttempted++;
		    val steal_result = at (p) st().trySteal();
		    if (steal_result!=null) {
			stealsPerpetrated++;
			nodesReceived += steal_result.length();
			var count:Int=0;
			for (r in steal_result) {
			    processSubtree(r);
			    if (count++ % nu == 0) 
				Runtime.probe();
			}
			return true;
		    }
		}
		return false;
	    }

	    def nonHomeMain (st:PLH) {
		while (state != STATE_DEATH_ROW) {
		    while (state == STATE_STEALING) {
			attemptSteal(st);
			Runtime.probe(); // have to check for arrest.
			processStack();
		    }
		    // We are not in STATE_STEALING.
		    // So we must have processed a message that forced us into 
		    // STATE_ARRESTED. Keep processing messages until you you 
		    // get the killer message or a re-awakening message.
		    Runtime.probe(); // check that we've not been put on death row
		}
		// place > 0 now exits
	    }	
	    
	    def main (st:PLH, b0:Int, rng:SHA1Rand) {
		val P=Place.MAX_PLACES;
		finish {
		    for (var pi:Int=1 ; pi<P ; ++pi) 
			async (Place(pi)) 
			    st().nonHomeMain(st);

		    // Initialize the work.
		    processSubtree(rng, b0);

		    STEAL_LOOP:
		    while (true) {
			processStack();
			// Place 0 ran out of work *BUT* there 
			// may be work elsewhere that was stolen, 
			// so try to steal some back
			if (attemptSteal(st)) 
			    continue STEAL_LOOP;

			// no work, suspect global quiescence
			// the rest of this loop body is relatively slow
			// but should be executed rarely.
			// ask everyone to stop stealing (synchronous)
			for (var pi:Int=1 ; pi<P ; ++pi) {
			    at (Place(pi)) {
				val this_ = st();
				assert this_.state==STATE_STEALING;
				//  if (!this_.working)
				this_.state = STATE_ARRESTED;
			    }
			}
			// check noone has any work (synchronous)
			for (var pi:Int=1 ; pi<P ; ++pi) {
			    val p = Place(pi);
			    if (at (p) st().state != STATE_ARRESTED) {
				// Discovered work. Restart everyone.
				for (var pi2:Int=1 ; pi2<P ; ++pi2) {
				    val p2 = Place(pi2);
				    at (p2) {
					st().state = STATE_STEALING;
				    }
				}
				continue STEAL_LOOP;
			    }
			}
			for (var pi:Int=1 ; pi<P ; ++pi) {
			    val p = Place(pi);
			    at (p) {
				val this_ = st();
				assert this_.state == STATE_ARRESTED;
				this_.state = STATE_DEATH_ROW;
			    }
			}
			break;
		    } // STEAL_LOOP
		} // finish
	    }
	}

    static final class BinomialState2 extends BinomialState {
		var thief:Int; 
		val width:Int;
        var lifelines:Long=0L;
        var lifelineNodes:Long=0L;
		public def this (q:Long, m:Int, k:Int, nu:Int, w:Int) {
			super(q,m,k,nu);
			width=w;
			thief= -1;
		}
		def processStack(st:PLH2) {
			var count:Int=0;
		    while (stack.size() > 0) {
		    	processSubtree(stack.pop());
		    	if ((count++ & nu) == 0) {
		    		Runtime.probe();	
		    		distribute(st);
		    	}
		    }
		    val loot = attemptSteal(st);
		    if (loot != null)
		    	processLoot(st, loot, false);
		}
		def distribute(st:PLH2) {
			if (thief >= 0) {
    			val loot = trySteal(thief);
    			if (loot != null) {
    				async (Place(thief)) 
    				st().processLoot(st, loot,true);
    				thief = -1;
    			}
    		}
		}
		def attemptSteal(st:PLH2):ValRail[SHA1Rand] {
			val P = Place.MAX_PLACES;
			if (P == 1) return null;
			val p = here.id;
			for (var i:Int=0; i < width; i++) {
				var q_:Int = 0;
				while((q_ =  myRandom.nextInt(P)) == p) ;
				val q = q_;
				stealsAttempted++;
				val loot = at (Place(q)) st().trySteal(p);
				if (loot != null) {
					return loot;
				}
			}
			// resigned to make a lifeline steal.
			val lifeline = (p+1) % P;
			return at(Place(lifeline)) st().trySteal(p); 
		}
		def processLoot(st:PLH2, loot:ValRail[SHA1Rand], lifeline:boolean) {
			if (lifeline) {
				lifelines ++;
				lifelineNodes += loot.length();
			} else {
				stealsPerpetrated++;
				nodesReceived += loot.length();
			}
            for (r in loot) 
			   processSubtree(r);
        	distribute(st);
            processStack(st);
        }
		//def trySteal (p:Int)=trySteal(p, false);
		def trySteal (p:Int) : ValRail[SHA1Rand] {
			stealsReceived++;
			val length = stack.size();
			if (length <= 2) {
				if (here.id == (p+1)% Place.MAX_PLACES) //lifeline
					thief = p;
				return null;
			}
			val numSteals = length/2; // isLifeline? (4*length)/5 : length/2;
			stealsSuffered++;
			nodesGiven += numSteals;
			return pop(numSteals);
		}
		def main (st:PLH2, b0:Int, rng:SHA1Rand) {
			val P=Place.MAX_PLACES;
			finish {
				processSubtree(rng, b0);
				val lootSize = stack.size()/P;
				for (var pi:Int=1 ; pi<P ; ++pi) {
					val loot = pop(lootSize);
					async (Place(pi)) 
					  st().processLoot(st, loot, true);
				}
				processStack(st);
			} 
		}
	}

    static def abs(i:Float)  = i < 0.0F ? -i : i;
    static def absMax(i:Float, j:Float) = abs(i) < abs(j) ? j : i;
    static def stats(st:PLH2, time:Long, verbose:Boolean) {
	val P = Place.MAX_PLACES;
	var nodeSum_:Long=0L;
	var stolenSum_:Long=0;
	var steals_:Long=0;
	var ll_:Long=0, llN_:Long=0;
	for ((i) in 0..P-1) {
		val there = Place(i);
	    nodeSum_ += at (there) st().nodesCounter;
	    stolenSum_ += at (there) st().nodesReceived;
	    steals_ += at (there) st().stealsPerpetrated;
	    ll_ += at (there) st().lifelines;
	    llN_ += at (there) st().lifelineNodes;
	}
	val nodeSum = nodeSum_;
	val stolenSum = stolenSum_;
	val steals = steals_;
	val idealRatio = 1.0/P;
	var balance:Float = 0.0F;
	for ((i) in 0..P-1) {
		val there = Place(i);
		val nodes = at (there) st().nodesCounter;
		val ratio = (1.0*nodes)/nodeSum;
		val iBalance = ((100.0*(ratio-idealRatio))/idealRatio) as Float;
		balance = absMax(balance, iBalance);
	}
	if (verbose)
	for ((i) in 0..P-1) at (Place(i)) {
	    val there = here;
	    val nodes = st().nodesCounter;
	    val sa = st().stealsAttempted;
	    val ss = st().stealsSuffered;
	    val sr = st().stealsReceived;
	    val sp = st().stealsPerpetrated;
	    val pc = sa==0L ? "NaN" : "" + ((100*sp)/sa);
	    val pr = sr==0L ? "NaN" : "" + ((100*ss)/sr);
	    val nr = st().nodesReceived;
	    val ns = st().nodesGiven;
	    Console.OUT.println(there+": processed " + nodes + " nodes.");
	    val ratio = (1.0*nodes)/nodeSum;
	    val ratioS = (""+ratio).substring(0,6);
	    val imbalance = (100.0*(ratio-idealRatio))/idealRatio;
	    val imbalanceS = (""+ imbalance).substring(0,6);
	    Console.OUT.println("\t " + st().lifelines + " lifeline steals received "  
				+ st().lifelineNodes + " (total nodes).");
	    Console.OUT.println("\t " + ratioS + " ratio, "  
				+ imbalanceS + "% balance.");
	    Console.OUT.println("\t" + sp+"/"+sa+"="
				+ pc +"% successful steals, received " 
				+ nr + " nodes.");
	    Console.OUT.println("\t" + ss+"/"+sr+"="
				+ pr + "% suffered, gave " 
				+ ns + " nodes.");
	}
	Console.OUT.println("Overhead::\n\t" + stolenSum + " total nodes stolen."); 
	val theftEfficiency = (stolenSum*1.0)/steals;
	Console.OUT.println("\t" + ("" + steals).substring(0,6)+ " direct steals."); 
	Console.OUT.println("\t" + ("" + theftEfficiency).substring(0,8)+ " nodes stolen per attempt."); 
	Console.OUT.println("\t" + ll_ + " lifeline steals.");
	Console.OUT.println("\t" + ("" + (1.0*llN_)/ll_).substring(0,8) + " nodes stolen/lifeline steal.");
	Console.OUT.println("\t" + ("" + balance).substring(0,6) + "% absmax imbalance.");
	Console.OUT.println("Performance = "+nodeSum+"/"+("" + (time/1E9)).substring(0,6)
			+"="+ ("" + (nodeSum/(time/1E3))).substring(0,6) + "M nodes/s");

    }
	
    public static def main (args : Rail[String]!) {
	try {
	    val opts = 
		new OptionsParser(args, 
				  null,
				  [Option("t", "", "Tree type 0: BIN, 1: GEO, 2: HYBRID"),
				   
				   Option("b", "", "Root branching factor"),
				   Option("r", "", "Root seed (0 <= r <= 2^31"),
				   
				   Option("a", "", "Tree shape function"),
				   Option("d", "", "Tree depth"),
				   Option("s", "", "Sequential"),
				   Option("q", "", "BIN: probability of a non-leaf node"),
				   Option("m", "", "BIN: number of children for non-leaf node"),
				   
				   
				   Option("k", "", "Number of items to steal; default 0. If 0, steal half. "),
				   Option("v", "", "Verbose, default 0 (no)."),
				   Option("n", "", "Number of nodes to process before probing."),
				   Option("w", "", "Number of thieves to send out, less 1. (Default 0, so 1 thief will be sent out.)")
				   ]);
				   

	    
	    val tree_type:Int = opts ("-t", 0);
	    
	    val b0 = opts ("-b", 4);
	    val seq = opts("-s", 0);
	    val r:Int = opts ("-r", 0);
	    val verbose = opts("-v",0)==1;
	    val nu:Int = opts("-n",200);
	    val w:Int = opts("-w", 0);

	    // geometric options
	    val geo_tree_shape_fn:Int = opts ("-a", 0);
	    val geo_tree_depth:Int = opts ("-d", 6);

	    // binomial options
	    val q:Double = opts ("-q", 15.0/64.0);
	    val mf:Int = opts ("-m", 4);
	    val k:Int = opts ("-k", 0);

	    // hybrid options
	    val geo_to_bin_shift_depth_ratio:Double = opts ("-f", 0.5);

	    Console.OUT.println("--------");
	    Console.OUT.println("Places="+Place.MAX_PLACES);
	    Console.OUT.println("b0=" + b0 +
				"   r=" + r +
				"   m=" + mf +
				"   s=" + seq +
				"   q=" + q);

	    val qq = (q*NORMALIZER) as Long;

	    if (seq != 0) {
		var time:Long = System.nanoTime();
		val nodes = new SeqBinomialState(qq, mf).main(b0, SHA1Rand(r));
		time = System.nanoTime() - time;
		Console.OUT.println("Performance = "+nodes+"/"+(time/1E9)+"="+ (nodes/(time/1E3)) + "M nodes/s");
	    } else {
		val st = PlaceLocalHandle.make[BinomialState2](Dist.makeUnique(), 
							      ()=>new BinomialState2(qq, mf,k,nu, w));
		var time:Long = System.nanoTime();
		st().main(st, b0, SHA1Rand(r));
		time = System.nanoTime() - time;
		stats(st, time, verbose);
	    }
	    Console.OUT.println("--------");
	    
	} catch (e:Throwable) {
	    e.printStackTrace(Console.ERR);
	}
    }
}

// vim: ts=2:sw=2:et
