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
					 Option("w", "", "Number of thieves to send out, less 1. (Default 0, so 1 thief will be sent out."),
           Option("l", "", "Lifeline method: 0 for linear, 1 for hypercube, 2 for sparse chunked, 3 for sparse embedding -- in which case also enter dimension"),
           Option("z", "", "Dimension of the sparse hypercube")
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

      // Figure out what kind of connectivity is needed.
      val l:Int = opts ("-l", 3);
      val z:Int = opts ("-z", 1);

			Console.OUT.println("--------");
			Console.OUT.println("Places="+Place.MAX_PLACES);
			Console.OUT.println("b0=" + b0 +
					"   r=" + r +
					"   m=" + mf +
					"   s=" + seq +
					"   w=" + w +
					"   n=" + nu +
					"   q=" + q +
          "   l=" + l + 
          "   z=" + z +
          (l==3 ? "base=" + NetworkGenerator.findW(Place.MAX_PLACES, z) : "")
           );

			val qq = (q*NORMALIZER) as Long;

			if (seq != 0) {
				var time:Long = System.nanoTime();
			val nodes = new SeqBinomialState(qq, mf).main(b0, SHA1Rand(r));
			time = System.nanoTime() - time;
			Console.OUT.println("Performance = "+nodes+"/"+(time/1E9)+"="+ (nodes/(time/1E3)) + "M nodes/s");
			} else {
        // Generate the lifelineNetwork
        val lifelineNetwork:ValRail[ValRail[Int]] = 
          (0==l) ? NetworkGenerator.generateRing(Place.MAX_PLACES) :
          (1==l) ? NetworkGenerator.generateHyperCube(Place.MAX_PLACES):
          (2==l) ? NetworkGenerator.generateChunkedGraph (Place.MAX_PLACES, z):
                NetworkGenerator.generateSparseEmbedding (Place.MAX_PLACES, z);
                        

				val st = PlaceLocalHandle.make[BinomialState](Dist.makeUnique(), 
	    ()=>new BinomialState(qq, mf,k,nu, w, e, l, lifelineNetwork(here.id)));
				Console.OUT.println("Starting...");
				var time:Long = System.nanoTime();
				st().main(st, b0, SHA1Rand(r));
				time = System.nanoTime() - time;
				Console.OUT.println("Finished.");
				st().counter.stats(st, time, verbose);
			}
			Console.OUT.println("--------");

		} catch (e:Throwable) {
			e.printStackTrace(Console.ERR);
		}
	}
}

// vim: ts=2:sw=2:et
