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

	static final class BinomialState {
		var thief:Int; 
	    val width:Int;
	    var lifelines:Long=0L;
	    var lifelineNodes:Long=0L;
	    val stack = new Stack[SHA1Rand]();
	    
	    val q:Long, m:Int, k:Int, nu:Int;
	    val logEvents:Boolean;
	    val myRandom = new Random();
	    
	    var nodesCounter:Long = 0L;
	    var stealsAttempted:Long = 0L;
	    var stealsPerpetrated:Long = 0L;
	    var stealsReceived:Long = 0L;
	    var stealsSuffered:Long = 0L;
	    var nodesGiven:Long = 0L;
	    var nodesReceived:Long = 0L;
	    var lastTimeStamp:Long=-1L;
	    var timeAlive:Long = 0L;
	    var timeDead:Long=0L;
	    var chainDepth:Int=0, maxDepth:Int=0;

	/** Initialize the state. Executed at all places when executing the 
	 PlaceLocalHandle.make command in main (of UTS).
	 */
	public def this (q:Long, m:Int, k:Int, nu:Int, w:Int, e:Boolean) {
		this.q = q; this.m = m; this.k=k; this.nu=nu;
		width=w;
		thief= -1;
		logEvents=e;
	}
	def event(s:String) {
		event(logEvents, s);
	}
	def event(verbose:Boolean, s:String) {
		if (verbose)
		Console.OUT.println("[Place(" + here.id+"), at " 
				    + System.nanoTime() + "] " + s);
	}

	/** Check if the current node (governed by the SHA1Rand state) has any
	 children. If so, push it onto the local stack.
	 */
	final def processSubtree (rng:SHA1Rand) {
		processSubtree(rng, (rng() < q) ? m : 0);
	}

	/** Same as above, but called when we have already made a decision that
	 there are going to be this "numChildren" children. Its useful to 
	 split the functionality from the function (processSubtree) above as 
	 in some cases (for eg., processing the root node), we just want to 
	 directly create children instead of basing the decision off of a 
	 random number state.
	 */
	final def processSubtree (rng:SHA1Rand, numChildren:Int) {
		nodesCounter++;
		// Iterate over all the children and push on stack
		for (var i:Int=0 ; i<numChildren ; ++i) 
			stack.push(SHA1Rand(rng, i));
	}

	/** Return an array of "k" elements popped from the local stack.
	 * 
	 */
	final def pop(k:Int) = ValRail.make[SHA1Rand](k, (int)=> stack.pop());

	/** Go through each element in the stack, process it (generate its
	 children, and add them to the stack) until there is nothing left
	 on the stack. At this point, attempt to steal. If nothing can be 
	 stolen, terminate for now. Also, after processing a particular 
	 number of nodes, check if there are any outstanding messages to
	 handle and also, distribute a chunk of the local stack (work) to 
	 our lifeline buddy.
	 */
	def processStack(st:PLH) {
		var count:Int=0;
	    event("Starting main loop.");
	    while (stack.size() > 0) {
		   processSubtree(stack.pop());
		   if ((count++ & nu) == 0) {
			  Runtime.probe();	
			  distribute(st, 1);
		   }
	    }
	    event("Processed stack.");
	    val loot = attemptSteal(st);
	    if (loot != null) 
	    	processLoot(st, loot, false, 1);
	    event("Finished main loop.");
	}

	/** If our buddy has requested a lifeline, and we have ample supply 
	 of nodes, give him half (i.e, launch a remote async).
	 */
	def distribute(st:PLH, depth:Int) {
		if (thief >= 0) {
		    event("Distributing to " + thief);
			val loot = trySteal(thief);
			if (loot != null) {
				async (Place(thief))
				  st().launch(st, loot, depth);
				thief = -1;
			}
		}
	}

	/** This is the code invoked locally by each node when there are no 
	 more nodes left on the stack. In other words, this function is 
	 the basis of all pull-based stealing. The push based stealing 
	 happens through the lifeline system. First, we attempt to get 
	 work from randomly chosen neighbors (for a certain number of 
	 tries). If we are not successful, we invoke our lifeline system.
	 */
	def attemptSteal(st:PLH):ValRail[SHA1Rand] {
		event("Attempt steal");
		val P = Place.MAX_PLACES;
		if (P == 1) return null;
		val p = here.id;
		for (var i:Int=0; i < width; i++) {
			var q_:Int = 0;
		    while((q_ =  myRandom.nextInt(P)) == p) ;
		    val q = q_;
		    stealsAttempted++;
		event("Stealing from " + q);
		    val loot = at (Place(q)) st().trySteal(p);
		    if (loot != null) {
		    	event("Steal succeeded with " + 
			      (loot == null ? 0 : loot.length()) + " items");
			  return loot;
		    }
		}
		event("No loot; establishing lifeline.");
		// resigned to make a lifeline steal.
		    val lifeline = (p+1) % P;
		    val loot = at(Place(lifeline)) st().trySteal(p); 
		    event("Lifeline steal result " + 
			  (loot==null ? 0 : loot.length()));
		    return loot;
	}

	/** Invoked to process stolen work. It can either be invoked 
	 synchronously by a place that was successful in stealing from
	 another node, or through a launch from a buddy.
	 */
	def processLoot(st:PLH, loot:ValRail[SHA1Rand], lifeline:Boolean, depth:Int) {
		if (lifeline) {
			lifelines ++;
			lifelineNodes += loot.length();
		} else {
			stealsPerpetrated++;
			nodesReceived += loot.length();
		}
		for (r in loot) processSubtree(r);

		distribute(st, depth+1);
		processStack(st);
	}

	/** Try to steal from the local stack --- invoked by either a 
	 theif at a remote place using asyncs (during attemptSteal) 
	 or by the owning place itself when it wants to give work to 
	 a fallen buddy.
	 */
	def trySteal (p:Int) : ValRail[SHA1Rand] {
		stealsReceived++;
		val length = stack.size();
		if (length <= 2) {
		    if (here.id == (p+1)% Place.MAX_PLACES) {
			thief = p;
			event("Established lifeline donee " + thief);
		    }
		    event("Returning null");
		    return null;
		}
		val numSteals = length/2;
		stealsSuffered++;
		nodesGiven += numSteals;
		val result = stack.pop(numSteals);
		event("Steal result is  " + result);
		return result;
	}

	def launch(st:PLH, loot:ValRail[SHA1Rand], depth:Int) {
		event("Launched at depth " + depth);
		val time = System.nanoTime();
		if (lastTimeStamp > 0) {
			timeDead += (time - lastTimeStamp);
		}
		lastTimeStamp= time;
		chainDepth=depth;
		maxDepth = max(chainDepth, maxDepth);
		processLoot(st, loot, true, depth);
		timeAlive = System.nanoTime()-lastTimeStamp;
		event("Finished launch.");
	}
	/** Called only for the root node. Processes all the children of 
	 the root node and then proceeds to divide these children up 
	 evenly amongst all the places. This is the bootstrap mechanism
	 for distributed UTS.
	 */
	def main (st:PLH, b0:Int, rng:SHA1Rand) {
		val P=Place.MAX_PLACES;
		event("Start main finish");
		lastTimeStamp = System.nanoTime();
		finish {
			event("Launch main");
			processSubtree(rng, b0);
			val lootSize = stack.size()/P;
			for (var pi:Int=1 ; pi<P ; ++pi) {
				val loot = pop(lootSize);
				async (Place(pi))
				  st().launch(st, loot, 1);
			}
			processStack(st);
			event("Finish main");
		} 
		event("End main finish");
	}
	}

	static def abs(i:Float)  = i < 0.0F ? -i : i;
	static def absMax(i:Float, j:Float) = abs(i) < abs(j) ? j : i;
	static def max(i:Long, j:Long) = i < j  ? j : i;
	static def max(i:Int, j:Int) = i < j  ? j : i;
	
	static def stats(st:PLH, time:Long, verbose:Boolean) {
	    Console.OUT.println("Stats:");
		val P = Place.MAX_PLACES;
		var nodeSum_:Long=0L;
		var stolenSum_:Long=0;
		var steals_:Long=0;
		var ll_:Long=0, llN_:Long=0;
		// needs to be cleaned up.
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
				val ta = st().timeAlive, td = st().timeDead;
				Console.OUT.println("\t max launch depth=" + st().maxDepth);
				Console.OUT.println("\t time alive = " + ta + "(" + 
						    ((ta+td)==0 ? "INF%)" : ((100*ta)/(ta+td) + "%)")));
			}

	Console.OUT.println("Overhead::\n\t" + stolenSum + " total nodes stolen."); 
	val theftEfficiency = (stolenSum*1.0)/steals;
	Console.OUT.println("\t" + safeSubstring("" + steals, 0,6)+ " direct steals."); 
	Console.OUT.println("\t" + safeSubstring("" + theftEfficiency, 0,8)+ " nodes stolen per attempt."); 
	Console.OUT.println("\t" + ll_ + " lifeline steals.");
	Console.OUT.println("\t" + safeSubstring("" + (1.0*llN_)/ll_, 0,8) + " nodes stolen/lifeline steal.");
	Console.OUT.println("\t" + safeSubstring("" + balance, 0,6) + "% absmax imbalance.");
	Console.OUT.println("Performance = "+nodeSum+"/"+safeSubstring("" + (time/1E9), 0,6)
			+"="+ safeSubstring("" + (nodeSum/(time/1E3)), 0, 6) + "M nodes/s");

	}

private static def safeSubstring(str:String, start:int, end:int) = str.substring(start, Math.min(end, str.length()));
	
	public static def main (args : Rail[String]!) {
		try {
			val opts = new OptionsParser(args, null,
					[Option("t", "", "Tree type 0: BIN, 1: GEO, 2: HYBRID"),
					 Option("b", "", "Root branching factor"),
					 Option("r", "", "Root seed (0 <= r <= 2^31"),
					 Option("a", "", "Tree shape function"),
					 Option("d", "", "Tree depth"),
					 Option("s", "", "Sequential"),
					 Option("e", "", "Event logs, default 0 (no)."),
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
			val e = opts("-e", 0)==1;

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
					"   w=" + w +
					"   n=" + nu +
					"   q=" + q);

			val qq = (q*NORMALIZER) as Long;

			if (seq != 0) {
				var time:Long = System.nanoTime();
			val nodes = new SeqBinomialState(qq, mf).main(b0, SHA1Rand(r));
			time = System.nanoTime() - time;
			Console.OUT.println("Performance = "+nodes+"/"+(time/1E9)+"="+ (nodes/(time/1E3)) + "M nodes/s");
			} else {
				val st = PlaceLocalHandle.make[BinomialState](Dist.makeUnique(), 
						()=>new BinomialState(qq, mf,k,nu, w, e));
				Console.OUT.println("Starting...");
				var time:Long = System.nanoTime();
				st().main(st, b0, SHA1Rand(r));
				time = System.nanoTime() - time;
				Console.OUT.println("Starting...");

				stats(st, time, verbose);
			}
			Console.OUT.println("--------");

		} catch (e:Throwable) {
			e.printStackTrace(Console.ERR);
		}
	}
}

// vim: ts=2:sw=2:et
