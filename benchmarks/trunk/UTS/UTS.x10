import x10.compiler.*;
import x10.util.OptionsParser;
import x10.util.Option;

public class UTS {

  @NativeRep ("c++", "UTS__SHA1Rand", "UTS__SHA1Rand", null)
  @NativeCPPInclude ("sha1_rand.hpp")
  @NativeCPPCompilationUnit ("sha1.c")
  public static struct SHA1Rand {
    public def this (seed:int) { }

    public def this (parent:SHA1Rand, spawn_number:int) { }

    @Native ("c++", "UTS__SHA1Rand_methods::apply(#0)")
    public def apply () : int = 0;
  }

  public static def binomial (q:double, m:int, rng:SHA1Rand) : int {
    val NORMALIZER = 2147483648.0;
    val randomNumber = rng() as double;
    val normalizedRandomNumber = randomNumber / NORMALIZER;
    val numChildren = (normalizedRandomNumber > q) ? m : 0;
    var nodes:int = 1;

    /** 
     * Debug to see if there is any branch that terminates
    if (0 == numChildren) {
      Console.OUT.println ("Terminating this branch");
    }
    */

    for ((i) in 1..(numChildren)) {
      val rng2 = SHA1Rand(rng, i);
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
