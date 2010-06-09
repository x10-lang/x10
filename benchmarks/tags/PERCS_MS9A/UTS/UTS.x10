import x10.compiler.*;
import x10.util.OptionsParser;
import x10.util.Option;
import x10.lang.Math;
import x10.util.Random;
import x10.util.Stack;

public class UTS {

	static type PLH= PlaceLocalHandle[BinomialState];
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
    
  static final class BinomialState {
		var thief:Int; 
		val width:Int;
    var lifelines:Long=0L;
    var lifelineNodes:Long=0L;
		val stack = new Stack[SHA1Rand]();
		val q:Long, m:Int, k:Int, nu:Int;
		val myRandom = new Random();
    var nodesCounter:Long = 0L;
    var stealsAttempted:Long = 0L;
    var stealsPerpetrated:Long = 0L;
    var stealsReceived:Long = 0L;
    var stealsSuffered:Long = 0L;
    var nodesGiven:Long = 0L;
    var nodesReceived:Long = 0L;
  
    // Initialize the state. Executed at all places when executing the 
    // PlaceLocalHandle.make command in main (of UTS).
    public def this (q:Long, m:Int, k:Int, nu:Int, w:Int) {
		  this.q = q; this.m = m; this.k=k; this.nu=nu;
			width=w;
			thief= -1;
		}
  
    // Check if the current node (governed by the SHA1Rand state) has any
    // children. If so, push it onto the local stack.
		final def processSubtree (rng:SHA1Rand) {
		  processSubtree(rng, (rng() < q) ? m : 0);
		}
  
    // Same as above, but called when we have already made a decision that
    // there are going to be this "numChildren" children. Its useful to 
    // split the functionality from the function (processSubtree) above as 
    // in some cases (for eg., processing the root node), we just want to 
    // directly create children instead of basing the decision off of a 
    // random number state.
		final def processSubtree (rng:SHA1Rand, numChildren:Int) {
		  nodesCounter++;
		  // Iterate over all the children and push on stack
		  for (var i:Int=0 ; i<numChildren ; ++i) 
			stack.push(SHA1Rand(rng, i));
		}
  
    // Return an array of "k" elements popped from the local stack.
		final def pop(k:Int) = ValRail.make[SHA1Rand](k, (int)=> stack.pop());
  
    // Go through each element in the stack, process it (generate its
    // children, and add them to the stack) until there is nothing left
    // on the stack. At this point, attempt to steal. If nothing can be 
    // stolen, terminate for now. Also, after processing a particular 
    // number of nodes, check if there are any outstanding messages to
    // handle and also, distribute a chunk of the local stack (work) to 
    // our lifeline buddy.
		def processStack(st:PLH) {
			var count:Int=0;
			while (stack.size() > 0) {
			  processSubtree(stack.pop());
			  if ((count++ & nu) == 0) {
			    Runtime.probe();	
			    distribute(st);
			  }
			}
			val loot = attemptSteal(st);
			if (loot != null) processLoot(st, loot, false);
		}
  
    // If our buddy has requested a lifeline, and we have ample supply 
    // of nodes, give him half (i.e, launch a remote async).
		def distribute(st:PLH) {
			if (thief >= 0) {
      	val loot = trySteal(thief);
      	if (loot != null) {
      		async (Place(thief)) 
      		st().processLoot(st, loot,true);
      		thief = -1;
      	}
      }
		}
  
    // This is the code invoked locally by each node when there are no 
    // more nodes left on the stack. In other words, this function is 
    // the basis of all pull-based stealing. The push based stealing 
    // happens through the lifeline system. First, we attempt to get 
    // work from randomly chosen neighbors (for a certain number of 
    // tries). If we are not successful, we invoke our lifeline system.
		def attemptSteal(st:PLH):ValRail[SHA1Rand] {
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
  
    // Invoked to process stolen work. It can either be invoked 
    // synchronously by a place that was successful in stealing from
    // another node, or by a buddy (or root node) to jumpstart this 
    // current place again using async.
		def processLoot(st:PLH, loot:ValRail[SHA1Rand], lifeline:boolean) {
			if (lifeline) {
				lifelines ++;
				lifelineNodes += loot.length();
			} else {
				stealsPerpetrated++;
				nodesReceived += loot.length();
			}
      for (r in loot) processSubtree(r);
  
      distribute(st);
      processStack(st);
    }
  
    // Try to steal from the local stack --- invoked by either a 
    // theif at a remote place using asyncs (during attemptSteal) 
    // or by the owning place itself when it wants to give work to 
    // a fallen buddy.
		def trySteal (p:Int) : ValRail[SHA1Rand] {
			stealsReceived++;
			val length = stack.size();
			if (length <= 2) {
				if (here.id == (p+1)% Place.MAX_PLACES) thief = p;
				return null;
			}
			val numSteals = length/2;
			stealsSuffered++;
			nodesGiven += numSteals;
			return stack.pop(numSteals);
    }
  
    // Called only for the root node. Processes all the children of 
    // the root node and then proceeds to divide these children up 
    // evenly amongst all the places. This is the bootstrap mechanism
    // for distributed UTS.
		def main (st:PLH, b0:Int, rng:SHA1Rand) {
			val P=Place.MAX_PLACES;
			finish {
				processSubtree(rng, b0);
				val lootSize = stack.size()/P;
				for (var pi:Int=1 ; pi<P ; ++pi) {
				  val loot = pop(lootSize);
					async (Place(pi)) st().processLoot(st, loot, true);
				}
				processStack(st);
			} 
		}
  }

  static def abs(i:Float)  = i < 0.0F ? -i : i;
  static def absMax(i:Float, j:Float) = abs(i) < abs(j) ? j : i;
  static def stats(st:PLH, time:Long, verbose:Boolean) {
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
	    val opts = new OptionsParser(args, null,
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
		val st = PlaceLocalHandle.make[BinomialState](Dist.makeUnique(), 
							      ()=>new BinomialState(qq, mf,k,nu, w));
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
