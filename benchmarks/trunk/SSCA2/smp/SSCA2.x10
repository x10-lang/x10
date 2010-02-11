package ssca2;

class SSCA2 {

public static def main (args:Rail[String]!): void {

    /* Data structure for storing generated tuples in the 
     * Scalable Data Generation Stage -- see defs.h */

    /* The graph data structure -- see defs.h */
    val G: defs.graph;
  
    /* Kernel 2 output */
    val maxIntWtList: defs.edge;
    val maxIntWtListSize: types.INT_T;
    
    /* Kernel 3 output */
    val extractedSubGraphs: Rail[Rail[defs.V]];

    /* Kernel 4 output */
    val BC: Rail[types.DOUBLE_T];
    
    val time: types.DOUBLE_T;

    if (args.length != 2) {
        x10.io.Console.OUT.println("Usage: ./SSCA2 <No. of threads> <SCALE>");
        return;
    }

     val THREADS = Int.parseInt(args(0));
     assert THREADS > 0;

     util.x10_set_num_threads(THREADS);

     val SCALE = Int.parseInt(args(1));
    
     x10.io.Console.OUT.println("HPCS SSCA#2 Graph Analysis Benchmark v2.0");
     x10.io.Console.OUT.println("Running...");
//     val globals = new defs();
    defs.init(SCALE);

     x10.io.Console.OUT.println("# of processors :" + THREADS);
     x10.io.Console.OUT.println("SCALE :" + SCALE);

     x10.io.Console.OUT.println("Scalable Data Generator");
     x10.io.Console.OUT.println("genScalData() begining exectuion" );

     val SDGdata = genScaleData.compute();
}

};
