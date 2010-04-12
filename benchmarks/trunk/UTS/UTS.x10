import x10.compiler.*;
import x10.util.OptionsParser;
import x10.util.Option;
import x10.lang.Math;
import x10.util.Random;
import x10.util.Box;

public class UTS {

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

  public static def geometric (shapeFunction:Int, /* 0..3*/
                               rootBranchingFactor:Int, /* self-expln */
                               treeDepth:Int, /* cut off after this depth */
                               parentDepth:Int, /* depth of the parent */
                               rng:SHA1Rand /* random number generator */
                              ): Int {
    /* compute branching factor at this node */
    var curNodeBranchingFactor:Double;

    if (0 == parentDepth) { /* root node */
      curNodeBranchingFactor = rootBranchingFactor;
    } else { /* calculate the branching factor for this node */
      if (0 == shapeFunction) { /* Exponential decrease */
        val tmpLogOne = -1.0 * Math.log (rootBranchingFactor as Double);
        val tmpLogTwo = Math.log (treeDepth as Double);
        curNodeBranchingFactor = rootBranchingFactor  * 
                                 Math.pow (parentDepth as Double, 
                                      tmpLogOne/tmpLogTwo);
      } else if (1 == shapeFunction) { /* Cyclic */
        if (parentDepth > (5*treeDepth)) {
          curNodeBranchingFactor = 0.0;
        } else {
          val TWO = 2.0;
          val PI = 3.141592653589793;
          val exponent = Math.sin (TWO*PI*(parentDepth as Double)/
                                     (treeDepth as Double));

          curNodeBranchingFactor = Math.pow (rootBranchingFactor, 
                                             exponent);
        }
      } else if (2 == shapeFunction) { /* Fixed */
        curNodeBranchingFactor = (parentDepth < treeDepth) ? 
                                    rootBranchingFactor : /* true */
                                    0; /* false */
      } else if (3 == shapeFunction) { /* Linear --- default */
        curNodeBranchingFactor = rootBranchingFactor *
                                 (1.0 - (parentDepth as Double)/
                                        (treeDepth as Double));
      } else {
        curNodeBranchingFactor = 0;
        Console.OUT.println ("Unknown shape function for geometric UTS");
      }
    }

    /* Now, calculate the number of children */
    val probForCurNodeBranchingFactor = 1.0 / (1.0 + curNodeBranchingFactor);
    val randomNumber = rng() as Double;
    val normalizedRandomNumber = randomNumber / NORMALIZER;
    val numChildren = Math.floor ((Math.log (1-normalizedRandomNumber)) /
                                  (Math.log 
                                (1-probForCurNodeBranchingFactor))) as Int;

    /* Iterate over all the children and accumulate the counts */
    var nodes:Int = 1;
    for ((i) in 1..(numChildren)) {
      nodes += geometric (shapeFunction,
                          rootBranchingFactor,
                          treeDepth,
                          parentDepth+1,
                          SHA1Rand (rng, i));
    }

    return nodes;
  }






  static class BinomialState {

    const STATE_PLACE_ZERO = 0; // the first place does not use a state machine, use this value as placeholder
    const STATE_STEALING = 1; // actively stealing work from other places
    const STATE_ARRESTED = 2; // no-longer stealing but still alive and could potentially steal again
    const STATE_DEATH_ROW = 3; // will soon be dead, will never steal again

    // places > 0 start off life stealing
    var state:Int = here == Place.FIRST_PLACE ? STATE_PLACE_ZERO : STATE_STEALING;

    var working:Boolean = false;

    // this guy can be stolen
    var work: SHA1Rand = SHA1Rand(0);
    var workValid:Boolean = false; // whether or not the above field contains meaningful data

    // params that define the tree
    val q:Long, m:Int;

    var nodesCounter:UInt = 0;
    var stealsAttempted:UInt = 0;
    var stealDepth:UInt = 0;
    var stealsPerpetrated:UInt = 0;
    var stealsReceived:UInt = 0;
    var stealsSuffered:UInt = 0;

    val probe_rnd = new Random();

    val depthCap:Int;

    //var prefix:String = "    ";

    public def this (q:Long, m:Int, depthCap:Int) {
      this.q = q; this.m = m; this.depthCap = depthCap;
    }

    public final def processSubtree (rng:SHA1Rand) {
      working = true;
      processSubtreeR(rng);
      workValid = false;
      working = false;
    }

    public final def processSubtreeR (rng:SHA1Rand) {
      //Console.OUT.println(here+prefix+" o "+rng());
      val numChildren = (rng() < q) ? m : 0;
      nodesCounter++;

      /* Iterate over all the children and accumulate the counts */
      //Console.OUT.println(here+prefix+" Iterating over children");
      for (var i:Int=0 ; i<numChildren ; ++i) {
        //workValid = false;
        //Activity.sleep(100);
        work = SHA1Rand(rng, i);
        workValid = true;
        if (probe_rnd.nextDouble() < 0.01) {
          //Console.OUT.println(here+prefix+" Allowing a steal");
          Runtime.probe();
          //Console.OUT.println(here+prefix+" Steal opportunity over");
        }
        if (workValid) {
          //val p = prefix;
          //prefix = p + "    ";
          processSubtreeR(work);
          //prefix = p;
        }
      }

    }

    // use box so we can encode 'could not steal' with null
    public final def trySteal () : Box[SHA1Rand] {
      stealsReceived++;
      if (workValid) {
        workValid = false;
        stealsSuffered++;
        //Console.OUT.println(here+": got mugged of "+work());
        return new Box[SHA1Rand](work);
      }
      return null;
    }

    public final def nonHomeMain (st:PLH) {
      while (state != STATE_DEATH_ROW) {
        while (state == STATE_STEALING) {
          attemptSteal(st);
          Runtime.probe(); // have to check that we've not been arrested
        }
        Runtime.probe(); // check that we've not been put on death row
      }
      // place > 0 now exits
    }

    // original implementation
    public final def attemptSteal(st:PLH) {
      for (var pi:Int=0 ; pi<Place.MAX_PLACES ; ++pi) {
        val p = Place(pi);
        if (p==here) continue;
        stealsAttempted++;
        val steal_result = at (p) st().trySteal();
        if (steal_result!=null) {
          stealsPerpetrated++;
          processSubtree(steal_result());
          return true;
        }
      }
      return false;
    }

    /*
    // place hopping code (synchronous)
    public final def attemptStealHop(st:PLH, depth:Int, home:Place, continuation:(Box[SHA1Rand], Int)=>Void) {
      if (depth>depthCap) { at (home) { continuation(null,0); } return; }
      val p_ = Place(probe_rnd.nextInt(Place.MAX_PLACES-1));
      val p = p_==home ? p_.next() : p_;
      at (p) {
        val steal_result = st().trySteal();
        if (steal_result != null) {
          at (home) {
            continuation(steal_result, depth);
          }
        } else {
          st().attemptStealHop(st, depth+1, home, continuation);
        }
      }
    }

    public final def attemptSteal(st:PLH) {
      stealsAttempted++;
      val r = new Cell[Boolean](false);
      val continuation = (s:Box[SHA1Rand], depth:Int) => {
        if (s!=null) {
          stealsPerpetrated++;
          stealDepth+=depth;
          processSubtree(s());
          r(true);
        } else {
          r(false);
        }
      };
      attemptStealHop(st, 0, here, continuation);
      return r();
    } */

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
              if (!this_.working)
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
      val all_nodes = new Cell[UInt](0);
      val everyone = Dist.makeUnique();
      finish ateach (i in everyone) {
        val there = here;
        val nodes = st().nodesCounter;
        val sa = st().stealsAttempted;
        val ss = st().stealsSuffered;
        val sr = st().stealsReceived;
        val sp = st().stealsPerpetrated;
        val sd = st().stealDepth;
        at (all_nodes) {
          val pc = sa==0U ? "NaN" : ""+((100U*sp)/sa);
          Console.OUT.println(there+": "+nodes+" nodes,  "+sp+"/"+sa+"="+pc+"% successful steals (AvgDepth "+((sd as Double)/sp)+")  ("+ss+"/"+sr+"="+((ss as Double)/sr)+"% suffered)");
          atomic {
            all_nodes(all_nodes()+nodes);
          }
        }
      }

      return all_nodes();
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

       Option("D", "", "Depth (performance tweak, default 5)")]);

      val tree_type:Int = opts ("-t", 0);
       
      val root_branch_factor = opts ("-b", 4);
      val root_seed:Int = opts ("-r", 0);

      // geometric options
      val geo_tree_shape_fn:Int = opts ("-a", 0);
      val geo_tree_depth:Int = opts ("-d", 6);

      // binomial options
      val bin_non_leaf_prob:Double = opts ("-q", 15.0/64.0);
      val bin_num_child_non_leaf:Int = opts ("-m", 4);
      
      // hybrid options
      val geo_to_bin_shift_depth_ratio:Double = opts ("-f", 0.5);

      val depth = opts ("-D", 5);

      Console.OUT.println("--------");
      Console.OUT.println("Places = "+Place.MAX_PLACES);
      Console.OUT.println("b0 = "+root_branch_factor+"   q = "+bin_non_leaf_prob+"   m = "+bin_num_child_non_leaf+"   r = "+root_seed);

      val q = (bin_non_leaf_prob*NORMALIZER) as Long;
        
      val st = PlaceLocalHandle.make[BinomialState](Dist.makeUnique(), ()=>new BinomialState(q, bin_num_child_non_leaf, depth));
      var time:Long = System.nanoTime();
      val nodes = st().main(st, root_branch_factor, SHA1Rand(root_seed));
      time = System.nanoTime() - time;
      Console.OUT.println("Performance = "+nodes+"/"+(time/1E9)+"="+ (nodes/(time/1E3)) + "M nodes/s");
      Console.OUT.println("--------");

    } catch (e:Throwable) {
      e.printStackTrace(Console.ERR);
    }
  }
}

// vim: ts=2:sw=2:et
