import x10.compiler.*;
import x10.util.OptionsParser;
import x10.util.Option;
import x10.lang.Math;
import x10.util.Random;
import x10.util.Stack;

public class UTS2 {

  private static val NORMALIZER = 2147483648.0;

  @NativeRep ("c++", "UTS__SHA1Rand", "UTS__SHA1Rand", null)
  @NativeCPPCompilationUnit ("sha1.c")
  @NativeCPPCompilationUnit ("UTS__SHA1Rand.cc")
  public static struct SHA1Rand {
    public def this (seed:Int) { }
    public def this (parent:SHA1Rand, spawn_number:Int) { }
    @Native ("c++", "UTS__SHA1Rand_methods::apply(#0)")
    public def apply () : Int = 0;
  }

  static class BinomialState {

    const STATE_PLACE_ZERO = 0; // the first place does not use a state machine, use this value as placeholder
    const STATE_STEALING = 1; // actively stealing work from other places
    const STATE_ARRESTED = 2; // no-longer stealing but still alive and could potentially steal again
    const STATE_DEATH_ROW = 3; // will soon be dead, will never steal again

    // places > 0 start off life stealing
      var state:Int = here == Place.FIRST_PLACE ? STATE_PLACE_ZERO : STATE_STEALING;

      val stack = new Stack[SHA1Rand]();

    // params that define the tree
      val q:Long, m:Int;

      val k:Int; // the number to be stolen

      var nodesCounter:UInt = 0;
      var stealsAttempted:UInt = 0;
      var stealDepth:UInt = 0;
      var stealsPerpetrated:UInt = 0;
      var stealsReceived:UInt = 0;
      var stealsSuffered:UInt = 0;
      val probe_rnd = new Random();
      public def this (q:Long, m:Int, k:Int) {
	  this.q = q; this.m = m; this.k = k;
      }

      public final def processSubtree (rng:SHA1Rand) {
	  val numChildren = (rng() < q) ? m : 0;
	  nodesCounter++;
	  /* Iterate over all the children and accumulate the counts */
	  for (var i:Int=0 ; i<numChildren ; ++i) {
	      val work = SHA1Rand(rng, i);
	      stack.push(work);
	  }
      }

      final def pop(k:Int) = ValRail.make[SHA1Rand](k, (int)=> stack.pop());
      
    public final def trySteal () : ValRail[SHA1Rand] {
      stealsReceived++;
      val length = stack.size();
      return 
	  (length >= k) 
	  ? pop(k)
	  : (length >= k/2) 
	  ? pop(k/2)
	  : (length > 1) 
	  ? pop(1)
	  : null;
    }

    public final def nonHomeMain (st:PLH) {
      while (state != STATE_DEATH_ROW) {
        while (state == STATE_STEALING) {
          attemptSteal(st);
          Runtime.probe(); // have to check that we've not been arrested
	  while (stack.size() > 0) {
	      val work = stack.pop();
	      processSubtree(work);
	      if (probe_rnd.nextDouble() < 0.01) {
		  Runtime.probe();
	      }
	  }
        }
        Runtime.probe(); // check that we've not been put on death row
      }
      // place > 0 now exits
    }

    public final def attemptSteal(st:PLH) {
      for (var pi:Int=0 ; pi<Place.MAX_PLACES ; ++pi) {
        val p = Place(pi);
        if (p==here) continue;
        stealsAttempted++;
        val steal_result = at (p) st().trySteal();
        if (steal_result!=null) {
          stealsPerpetrated++;
	  for (r in steal_result) {
	      processSubtree(r);
	  }
          return true;
        }
      }
      return false;
    }

    public final def main (st:PLH, b0:Int, rng:SHA1Rand) : UInt {
      finish {
        for (var pi:Int=1 ; pi<Place.MAX_PLACES ; ++pi) {
          async (Place(pi)) st().nonHomeMain(st);
        }

        // add root node
        //Console.OUT.println(here+" ROOT");
        nodesCounter++;

        // Iterate over all the children and accumulate the counts
        for (var i:Int=0 ; i<b0 ; ++i) {
          processSubtree(SHA1Rand(rng, i));
        }

        //Console.OUT.println(here+": All work completed or stolen.");

        // Place 0 ran out of work *BUT* there may be work elsewhere that was stolen, so try to steal some back

        STEAL_LOOP:
        while (true) {

          if (attemptSteal(st)) {
            continue STEAL_LOOP;
          }

          //Console.OUT.println(here+": Could not steal work back from places > 0.");

          // no work, suspect global quiescence
          // the rest of this loop body is relatively slow but should be executed rarely.

          //Console.OUT.println(here+": Telling everyone else to stop stealing.");
          // ask everyone to stop stealing (synchronous)
          for (var pi:Int=1 ; pi<Place.MAX_PLACES ; ++pi) {
            at (Place(pi)) {
              val this_ = st();
              assert this_.state==STATE_STEALING;
            //  if (!this_.working)
                this_.state = STATE_ARRESTED;
            }
          }

          //Console.OUT.println(here+": Checking global quiescence.");
          // check noone has any work (synchronous)
          for (var pi:Int=1 ; pi<Place.MAX_PLACES ; ++pi) {
            val p = Place(pi);
            if (at (p) st().state != STATE_ARRESTED) {
              //Console.OUT.println(here+": Discovered there is still work to do, restarting everyone.");
              for (var pi2:Int=1 ; pi2<Place.MAX_PLACES ; ++pi2) {
                val p2 = Place(pi2);
                at (p2) {
                  val this_ = st();
                  this_.state = STATE_STEALING;
                }
              }
              continue STEAL_LOOP;
            }
          }

          //Console.OUT.println(here+": Shutting everyone else down.");
          for (var pi:Int=1 ; pi<Place.MAX_PLACES ; ++pi) {
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

      // Globally terminated, accumulate results:
      // collective reduce using +
      val nodeSum = new Cell[UInt](0);
      val everyone = Dist.makeUnique();
      finish ateach (i in everyone) {
        val there = here;
        val nodes = st().nodesCounter;
        val sa = st().stealsAttempted;
        val ss = st().stealsSuffered;
        val sr = st().stealsReceived;
        val sp = st().stealsPerpetrated;
        val sd = st().stealDepth;
        at (nodeSum) {
          val pc = sa==0U ? "NaN" : ""+((100U*sp)/sa);
          Console.OUT.println(there+": "+
			      nodes+" nodes,  "+
			      sp+"/"+sa+"="+pc+"% successful steals (AvgDepth "+((sd as Double)/sp)+")  ("+ss+"/"+sr+"="+((ss as Double)/sr)+"% suffered)");
          atomic {
            nodeSum(nodeSum()+nodes);
          }
        }
      }

      return nodeSum();
    }

  }

  static type PLH = PlaceLocalHandle[BinomialState];

  public static def main (args : Rail[String]!) {
    try {
      val opts = new OptionsParser(args, 
      null,
      [Option("t", "", "Tree type 0: BIN, 1: GEO, 2: HYBRID"),

       Option("b", "", "Root branching factor"),
       Option("r", "", "Root seed (0 <= r <= 2^31"),

       Option("a", "", "Tree shape function"),
       Option("d", "", "Tree depth"),

       Option("q", "", "BIN: probability of a non-leaf node"),
       Option("m", "", "BIN: number of children for non-leaf node"),

       Option("f", "", "Hybrid switch-over depth "),

       Option("k", "", "Number of items to steal "),

       Option("D", "", "Depth (performance tweak, default 5)")]);

      val tree_type:Int = opts ("-t", 0);

      val b0 = opts ("-b", 4);
      val r:Int = opts ("-r", 0);

      // geometric options
      val geo_tree_shape_fn:Int = opts ("-a", 0);
      val geo_tree_depth:Int = opts ("-d", 6);

      // binomial options
      val q:Double = opts ("-q", 15.0/64.0);
      val mf:Int = opts ("-m", 4);
      val k:Int = opts ("-k", mf);
      
      // hybrid options
      val geo_to_bin_shift_depth_ratio:Double = opts ("-f", 0.5);

      Console.OUT.println("--------");
      Console.OUT.println("Places = "+Place.MAX_PLACES);
      Console.OUT.println("b0 = " + b0 +
			  "   r = " + r +
			  "   m = " + mf +
			  "   q = " + q);


      val qq = (q*NORMALIZER) as Long;
        
      val st = PlaceLocalHandle.make[BinomialState](Dist.makeUnique(), 
						    ()=>new BinomialState(qq, mf, k));
      var time:Long = System.nanoTime();
      val nodes = st().main(st, b0, SHA1Rand(r));
      time = System.nanoTime() - time;
      Console.OUT.println("Performance = "+nodes+"/"+(time/1E9)+"="+ (nodes/(time/1E3)) + "M nodes/s");
      Console.OUT.println("--------");

    } catch (e:Throwable) {
      e.printStackTrace(Console.ERR);
    }
  }
}

// vim: ts=2:sw=2:et
