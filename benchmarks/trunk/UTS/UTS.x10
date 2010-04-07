import x10.compiler.*;
import x10.util.*;

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

  public static def binomial (prob:int, num:int, rng:SHA1Rand) : int {
    val children = rng() < prob ? num : 0;
    var nodes:int = 1;
    for ((i) in 0..(children-1)) {
      val rng2 = SHA1Rand(rng, i);
      nodes += binomial(prob, num, rng2);
    }
    return nodes;
  }


  public static def main (args : Rail[String]!) {
    try {
      val opts = new OptionsParser(args, 
              [Option("g", "--granularity", "compute granularity: number of rng_spawns per node")],
              [Option("t", "tree_type", "tree type 0: BIN, 1: GEO, 2: HYBRID"),
               Option("b", "root_branching_factor","")]);
       val tree_type:int = 0;   
       val root_branch_factor:double = 4.0;
       val root_seed:int = 0;
       val geo_tree_shape_fn:int = 0;
       val geo_tree_depth:int = 6;
       val bin_non_leaf_prob:double = 15.0/64.0;
       val bin_num_child_non_leaf:int = 4;
       val geo_to_bin_shift_depth_ratio:double = 0.5;
       val compute_granularity:boolean = true;

       Console.OUT.println ("Num nodes = "+ 
            binomial((bin_non_leaf_prob * Int.MAX_VALUE) as int, 
                bin_num_child_non_leaf, 
                SHA1Rand(root_seed)));

    } catch (e:Throwable) {
      e.printStackTrace(Console.ERR);
    }
  }
}
