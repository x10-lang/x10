import x10.compiler.*;
import x10.util.OptionsParser;
import x10.util.Option;
import x10.lang.Math;

public class UTS {

  private static val NORMALIZER = 2147483648.0;

  @NativeRep ("c++", "UTS__SHA1Rand", "UTS__SHA1Rand", null)
  @NativeCPPInclude ("sha1_rand.hpp")
  @NativeCPPCompilationUnit ("sha1.c")
  public static struct SHA1Rand {
    public def this (seed:int) { }

    public def this (parent:SHA1Rand, spawn_number:int) { }

    @Native ("c++", "UTS__SHA1Rand_methods::apply(#0)")
    public def apply () : int = 0;
  }

  public static def geometric (shapeFunction:int, /* 0..3*/
                               rootBranchingFactor:int, /* self-expln */
                               treeDepth:int, /* cut off after this depth */
                               parentDepth:int, /* depth of the parent */
                               rng:SHA1Rand /* random number generator */
                              ): int {
    /* compute branching factor at this node */
    var curNodeBranchingFactor:double;

    if (0 == parentDepth) { /* root node */
      curNodeBranchingFactor = rootBranchingFactor;
    } else { /* calculate the branching factor for this node */
      if (0 == shapeFunction) { /* Exponential decrease */
        val tmpLogOne = -1.0 * Math.log (rootBranchingFactor as double);
        val tmpLogTwo = Math.log (treeDepth as double);
        curNodeBranchingFactor = rootBranchingFactor  * 
                                 Math.pow (parentDepth as double, 
                                      tmpLogOne/tmpLogTwo);
      } else if (1 == shapeFunction) { /* Cyclic */
        if (parentDepth > (5*treeDepth)) {
          curNodeBranchingFactor = 0.0;
        } else {
          val TWO = 2.0;
          val PI = 3.141592653589793;
          val exponent = Math.sin (TWO*PI*(parentDepth as double)/
                                     (treeDepth as double));

          curNodeBranchingFactor = Math.pow (rootBranchingFactor, 
                                             exponent);
        }
      } else if (2 == shapeFunction) { /* Fixed */
        curNodeBranchingFactor = (parentDepth < treeDepth) ? 
                                    rootBranchingFactor : /* true */
                                    0; /* false */
      } else if (3 == shapeFunction) { /* Linear --- default */
        curNodeBranchingFactor = rootBranchingFactor *
                                 (1.0 - (parentDepth as double)/
                                        (treeDepth as double));
      } else {
        curNodeBranchingFactor = 0;
        Console.OUT.println ("Unknown shape function for geometric UTS");
      }
    }

    /* Now, calculate the number of children */
    val probForCurNodeBranchingFactor = 1.0 / (1.0 + curNodeBranchingFactor);
    val randomNumber = rng() as double;
    val normalizedRandomNumber = randomNumber / NORMALIZER;
    val numChildren = Math.floor ((Math.log (1-normalizedRandomNumber)) /
                                  (Math.log 
                                (1-probForCurNodeBranchingFactor))) as int;

    /* Iterate over all the children and accumulate the counts */
    var nodes:int = 1;
    for ((i) in 1..(numChildren)) {
      nodes += geometric (shapeFunction,
                          rootBranchingFactor,
                          treeDepth,
                          parentDepth+1,
                          SHA1Rand (rng, i));
    }

    return nodes;
  }

  public static def binomial (q:double, m:int, rng:SHA1Rand) : int {
    val randomNumber = rng() as double;
    val normalizedRandomNumber = randomNumber / NORMALIZER;
    val numChildren = (normalizedRandomNumber > q) ? m : 0;
    var nodes:int = 1;

    /* Iterate over all the children and accumulate the counts */
    for ((i) in 1..(numChildren)) {
      nodes += binomial(q, m, SHA1Rand(rng, i));
    }

    return nodes;
  }


  public static def main (args : Rail[String]!) {
    try {
      val opts = new OptionsParser(args, 
      [Option("g", "", "Number of rng_spawns per node")],
      [Option("t", "", "Tree type 0: BIN, 1: GEO, 2: HYBRID"),
       Option("b", "", "Root branching factor"),
       Option("r", "", "Root seed (0 <= r <= 2^31"),
       Option("q", "", "BIN: probability of a non-leaf node"),
       Option("m", "", "BIN: number of children for non-leaf node")]);

       val tree_type:int = opts ("-t", 0);
       
       //val root_branch_factor:double = opts ("-b", 4.0);
       val root_branch_factor:double = 4.0;
       val root_seed:int = opts ("-r", 0);

       //val geo_tree_shape_fn:int = opts ("-a", 0);
       //val geo_tree_depth:int = opts ("-d", 6);

       //val bin_non_leaf_prob:double = opts ("-q", 15.0/64.0);
       val bin_non_leaf_prob:double = 15.0/64.0;
       val bin_num_child_non_leaf:int = opts ("-m", 4);
      
       //val geo_to_bin_shift_depth_ratio:double = opts ("-f", 0.5);
       val geo_to_bin_shift_depth_ratio:double = 0.5;
       //val compute_granularity:boolean = opts ("-g", 1);
       val compute_granularity:boolean = true;

       Console.OUT.println ("Num nodes = "+ 
            binomial(bin_non_leaf_prob,
                     bin_num_child_non_leaf, 
                     SHA1Rand(root_seed)));

    } catch (e:Throwable) {
      e.printStackTrace(Console.ERR);
    }
  }
}
