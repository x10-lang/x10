package ssca2;

import x10.compiler.Native;
import x10.util.HashMap;
import x10.util.GrowableRail;

class SSCA2 {

       public static def distribute (g: defs.graph) {

       val GLOBALS = at (defs.container) {defs.container.globals};
         val unique = Dist.makeUnique();
         val pg = defs.pGraph(g.n, g.m, unique);
         val  gNumEdges = ValRail.make[types.LONG_T](g.numEdges.length, (i: Int)=>(g.numEdges as Rail[types.LONG_T])(i));

         val  gEndV = ValRail.make[types.VERT_T](g.endV.length, (i: Int)=>(g.endV as Rail[types.VERT_T])(i));
         val  gWeight = ValRail.make[types.WEIGHT_T](g.weight.length, (i: Int)=>(g.weight as Rail[types.WEIGHT_T])(i));

          finish for ((place) in unique) {
            finish async (Place.places(place)) {
              val pg_here = pg.restrict_here();

              val lo = gNumEdges(pg_here.vertices.min(0));
              //x10.io.Console.OUT.println(pg_here.vertices + " " + lo);

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

		if (args.length < 3) {
			x10.io.Console.OUT.println("Usage: ./SSCA2 <No. of threads> <SCALE>");
			return;
		}
		
		val THREADS = Int.parseInt(args(0));
		assert THREADS > 0;
		
		util.x10_set_num_threads(THREADS);
		
		val SCALE = Int.parseInt(args(1));

		val DUMP = args.length > 2 ?  Boolean.parseBoolean(args(2)) : false;

		val SERIAL_GRAPH_GEN = args.length > 3 ? Boolean.parseBoolean(args(3)) : false;

		val FILTER = args.length > 4 ? Boolean.parseBoolean(args(4)): false;
		val CUTSHORT = args.length > 5 ? Boolean.parseBoolean(args(5)) : false;

		val NOSELF = args.length > 6 ? Boolean.parseBoolean(args(6)) : true;
		val ALLGATHER = args.length > 7 ?  Boolean.parseBoolean(args(7)) : false;
	
		val USE_ASYNC = args.length > 8 ?  Boolean.parseBoolean(args(8)) : false;

                x10.io.Console.OUT.println( SERIAL_GRAPH_GEN + " " + " " + FILTER + " " + CUTSHORT + " " +  USE_ASYNC + " " + NOSELF + " "  + ALLGATHER);
	
		x10.io.Console.OUT.println("HPCS SSCA#2 Graph Analysis Benchmark v2.0");
		x10.io.Console.OUT.println("Running...");
		//     val globals = new defs();
		defs.init(SCALE, CUTSHORT);
		
		x10.io.Console.OUT.println("# of places:" + Place.MAX_PLACES);
		x10.io.Console.OUT.println("# of threads/place:" + THREADS);
		x10.io.Console.OUT.println("SCALE :" + SCALE);
		
		x10.io.Console.OUT.println("Scalable Data Generator");
		x10.io.Console.OUT.println("genScalData() begining exectuion" );
	

                val GLOBALS = at (defs.container) {defs.container.globals};
                val unique = Dist.makeUnique(); 
  
                 val kernel0 = PTimer.make("kernel0");
                 val  kernel1 = PTimer.make("kernel1");
                 val  kernel2 = PTimer.make("kernel2");
                 val  kernel3 = PTimer.make("kernel3");

                var pg_real: defs.pGraph;

                  
                if (SERIAL_GRAPH_GEN == true) {


                  kernel0.start();
		  val genRet = genScaleData.compute();
                  kernel0.stop();

                  kernel1.start();
		  val compRet = computeGraph.compute(genRet.second);
                  kernel1.stop();

		 pg_real = distribute(compRet.second);

                }else {
                 val RVertices = PlaceLocalHandle.make[HashMap[types.VERT_T, types.VERT_T]](unique, ()=>new HashMap[types.VERT_T, types.VERT_T]());
                 val Out_pairs = PlaceLocalHandle.make[GrowableRail[types.UVPair]](unique, ()=>new GrowableRail[types.UVPair](0));
                 pg_real = defs.pGraph(GLOBALS.N, GLOBALS.M, unique);
                 val pg_tmp = pg_real;
                 finish ateach ((place) in unique) {
                    val world: Comm! = Comm.WORLD();
                    kernel0.start();
                    val tmp0: defs.graphSDG_dist! = genScaleData_dist.compute(GLOBALS, place, world, RVertices, Out_pairs, NOSELF, ALLGATHER);
                    kernel0.stop();
                    world.barrier();

                    kernel1.start();
                    computeGraph_dist.compute(GLOBALS, tmp0, pg_tmp, place, world); 
                    kernel1.stop();
                    world.barrier();
                  }
                }

                 val pg = pg_real;


                 finish ateach ((place) in unique) {

                  val world: Comm! = Comm.WORLD();
                    kernel2.start();
                  val sourceList = getStartLists_dist.compute(pg, place, world); 
                    kernel2.stop();
                    world.barrier();

                  /* val tmp:Rail[defs.edge]! = sourceList as Rail[defs.edge]!;
    
for ((i) in 0..sourceList.length-1) {
                          x10.io.Console.OUT.println("edge " + sourceList(i).e + "( " + sourceList(i).w + " ) " + " : " + "[ " + sourceList(i).startVertex + " , " + sourceList(i).endVertex + "]");
                 }  */  
                    kernel3.start();
	    	  findSubGraphs_dist.compute(pg, place, world, sourceList, GLOBALS.SubGraphPathLength);
                    kernel3.stop();
                    world.barrier();
                }

               val bc = new BetweenessCentrality(pg_real, USE_ASYNC, FILTER, SCALE);
               bc.compute(GLOBALS);
               if (DUMP) bc.dump();

               PTimer.printDetailed();
	}
	
};
