package ssca2;

import x10.compiler.Native;

class SSCA2 {

       public static def distribute (g: defs.graph) {

       val GLOBALS = at (defs.container) {defs.container.globals};
         val unique = Dist.makeUnique();
         val pg = new defs.pGraph(g.n, g.m, unique);
         val  gNumEdges = ValRail.make[types.LONG_T](g.numEdges.length, (i: Int)=>(g.numEdges as Rail[types.LONG_T])(i));

         val  gEndV = ValRail.make[types.VERT_T](g.endV.length, (i: Int)=>(g.endV as Rail[types.VERT_T])(i));
         val  gWeight = ValRail.make[types.WEIGHT_T](g.weight.length, (i: Int)=>(g.weight as Rail[types.WEIGHT_T])(i));

          finish for ((place) in unique) {
            finish async (Place.places(place)) {
              val pg_here = pg.restrict_here();

              val lo = gNumEdges(pg_here.vertices.min(0));
              x10.io.Console.OUT.println(pg_here.vertices + " " + lo);

              for ((i): Point in pg_here.vertices) {
                 pg_here.numEdges(i) = gNumEdges(i) - lo;


                 for ((j) in gNumEdges(i)..gNumEdges(i+1)-1) {
                   pg_here.endV.add(gEndV(j));
                   pg_here.weight.add(gWeight(j));
                 }
                }
             pg_here.numEdges(pg_here.vertices.max(0)+1) = gNumEdges(pg_here.vertices.max(0)+1) - lo;

             /* for ((i) in pg_here.vertices) {
                         val low = pg_here.numEdges(i);
                         val high = pg_here.numEdges(i+1);
                         for ((j) in  low..high-1) {
                                 x10.io.Console.OUT.println("[ " + i + " " + pg_here.endV(j) +  " " + pg_here.weight(j) + "] " );
                         }

             } */
            }
         }

        return pg;
        }
	
	public static def main (args:Rail[String]!): void {
		
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
		
		val genRet = genScaleData.compute();
		x10.io.Console.OUT.println("Time taken for scalable data generation is" + genRet.first);
		
		x10.io.Console.OUT.println("computeGraph begining execution");
		val compRet = computeGraph.compute(genRet.second);
		x10.io.Console.OUT.println("Time taken for kernel 1 is" + compRet.first);
		
		val getRet = getStartLists.compute(compRet.second);
		val tmp: Rail[defs.edge]! = getRet.second;
		x10.io.Console.OUT.println("Time taken for kernel 2 is" + getRet.first);
		
		val findRet = findSubGraphs.compute(compRet.second, getRet.second);
		x10.io.Console.OUT.println("Time taken for kernel 3 is" + findRet);
                  

                   val GLOBALS = at (defs.container) {defs.container.globals};
                val unique = Dist.makeUnique(); 
                val pg = distribute(compRet.second);
                val pg_real = new defs.pGraph(GLOBALS.N, GLOBALS.M, unique);


                /* x10.io.Console.OUT.println(genRet.second.startVertex + " " + genRet.second.endVertex + " " + genRet.second.weight);
                  val stream1 = genScaleData.init_sprng_wrapper(0, 1,  2387);
                  for ((i) in  0..GLOBALS.N-1){
                    x10.io.Console.OUT.println(i + " " + genScaleData.sprng_wrapper(stream1));
                  } */
                /* finish ateach ((place) in unique) {
                  val world: Comm! = Comm.WORLD();
                 val pg_here = pg.restrict_here();
                  val stream1 = genScaleData.init_sprng_wrapper(place, Place.MAX_PLACES,  2387);
                  val stream2 = genScaleData_dist.init_sprng_wrapper(place, Place.MAX_PLACES,  2387, pg_here.vertices.size());
                  for ((i) in  pg_here.vertices) {
                    x10.io.Console.OUT.println(i + " " + genScaleData.sprng_wrapper(stream1) + " " + genScaleData.sprng_wrapper(stream2));
                  }
                  //x10.io.Console.OUT.println(tmp0.etriplets);
                   computeGraph_dist.compute(GLOBALS, tmp0, pg_real, place, world); 
                 val pg_here2 = pg_real.restrict_here();
                  for((i) in pg_here1.vertices) {
                         x10.io.Console.OUT.println("[ " + pg_here1.numEdges(i) + " " + pg_here2.numEdges(i) + "] " );
                 }

                 for((i) in pg_here1.vertices) {
                         val lo0 = pg_here1.numEdges(i);
                         val hi0 = pg_here1.numEdges(i+1);
                         for ((j) in  lo0..hi0-1) {
                           x10.io.Console.OUT.println("[ " + i + " " + pg_here1.endV(j) +  " " + pg_here1.weight(j) + "] " );
                           x10.io.Console.OUT.println("[ " + i + " " + pg_here2.endV(j) +  " " + pg_here2.weight(j) + "] " );
                         }
                 } 
                 } */


                 finish ateach ((place) in unique) {

//                  { @Native("c++", "MPI_Init(NULL, NULL);") {} }
                  val world: Comm! = Comm.WORLD();
                  val tmp0: defs.graphSDG_dist! = genScaleData_dist.compute(GLOBALS, place, world); 
                  computeGraph_dist.compute(GLOBALS, tmp0, pg_real, place, world); 
                  val sourceList = getStartLists_dist.compute(pg, place, world); 
                  val tmp:Rail[defs.edge]! = sourceList as Rail[defs.edge]!;
    for ((i) in 0..sourceList.length-1) {
                          x10.io.Console.OUT.println("edge " + sourceList(i).e + "( " + sourceList(i).w + " ) " + " : " + "[ " + sourceList(i).startVertex + " , " + sourceList(i).endVertex + "]");
                 }   
	    	  findSubGraphs_dist.compute(pg, place, world, sourceList, GLOBALS.SubGraphPathLength);
 //                 { @Native("c++", "MPI_Finalize();") {} }
                } 
	}
	
};
