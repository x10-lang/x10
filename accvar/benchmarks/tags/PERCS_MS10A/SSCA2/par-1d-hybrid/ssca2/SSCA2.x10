package ssca2;

import x10.compiler.Native;
import x10.util.HashMap;
import x10.util.GrowableRail;

class SSCA2 {
	
	
	public static def main (args:Rail[String]!): void {
		
		if (args.length < 3) {
			x10.io.Console.OUT.println("Usage: ./SSCA2 <SCALE>");
			return;
		}
		
		
		val SCALE = Int.parseInt(args(0));
		
		val DUMP = args.length > 1 ?  Boolean.parseBoolean(args(1)) : false;
		
		val SERIAL_GRAPH_GEN = args.length > 2 ? Boolean.parseBoolean(args(2)) : false;
		
		val FILTER = args.length > 3 ? Boolean.parseBoolean(args(3)): false;
		val CUTSHORT = args.length > 4 ? Boolean.parseBoolean(args(4)) : false;
		
		val NOSELF = args.length > 5 ? Boolean.parseBoolean(args(5)) : true;
		val ALLGATHER = args.length > 6 ?  Boolean.parseBoolean(args(6)) : false;
		
		val USE_ASYNC = args.length > 7 ?  Boolean.parseBoolean(args(7)) : false;
		
		val THREADS = args.length > 8 ?  Int.parseInt(args(8)) : 1;
		
		x10.io.Console.OUT.println( SERIAL_GRAPH_GEN + " " + " " + FILTER + " " + CUTSHORT + " " +  USE_ASYNC + " " + NOSELF + " "  + ALLGATHER);
		
		x10.io.Console.OUT.println("HPCS SSCA#2 Graph Analysis Benchmark v2.0");
		x10.io.Console.OUT.println("Running...");
		//     val globals = new Defs();
		Defs.init(SCALE, CUTSHORT, THREADS);
		
		x10.io.Console.OUT.println("# of places:" + Place.MAX_PLACES);
		x10.io.Console.OUT.println("# of threads/places:" + THREADS);
		x10.io.Console.OUT.println("SCALE :" + SCALE);
		
		x10.io.Console.OUT.println("Scalable Data Generator");
		x10.io.Console.OUT.println("GenScalData() begining exectuion" );
		
		
		val GLOBALS = at (Defs.container) {Defs.container.globals};
		val unique = Dist.makeUnique(); 
		
		val kernel0 = PTimer.make("kernel0");
		val  kernel1 = PTimer.make("kernel1");
		val  kernel2 = PTimer.make("kernel2");
		val  kernel3 = PTimer.make("kernel3");
		
		
		
		val pg = Defs.pGraph(GLOBALS.N, GLOBALS.M, unique);
		if (SERIAL_GRAPH_GEN == true) {
			
			
			kernel0.start();
			val edges = GenScaleDataSeq.compute(NOSELF);
			kernel0.stop();
			
			val M_p = GLOBALS.M / Place.MAX_PLACES;
			val Edges = PlaceLocalHandle.make[Rail[Types.EDGE_T]](unique, ()=>Rail.make[Types.EDGE_T](M_p, (i:Int)=>edges(here.id*M_p+i)));
			
			finish ateach ((place) in unique) {
				val world: Comm! = Comm.WORLD();
			kernel1.start();
			ComputeGraph.compute(Edges(), pg, place, world);
			kernel1.stop();
			world.barrier();
			Runtime.deallocObject(Edges());
			}
			Runtime.deallocObject(edges);
			
		}else {
			val RVertices = PlaceLocalHandle.make[HashMap[Types.VERT_T, Types.VERT_T]](unique, ()=>new HashMap[Types.VERT_T, Types.VERT_T]());
			val Out_pairs = PlaceLocalHandle.make[GrowableRail[Types.UVPair]](unique, ()=>new GrowableRail[Types.UVPair](0));
			finish ateach ((place) in unique) {
				val world: Comm! = Comm.WORLD();
			kernel0.start();
			val edges = GenScaleData.compute(GLOBALS, place, world, RVertices, Out_pairs, NOSELF, ALLGATHER);
			kernel0.stop();
			world.barrier();
			
			kernel1.start();
			ComputeGraph.compute(edges as Rail[Types.EDGE_T]!, pg, place, world); 
			kernel1.stop();
			world.barrier();
			Runtime.deallocObject(edges);
			}
		}
		
		
		finish ateach ((place) in unique) {
			
			val world: Comm! = Comm.WORLD();
		kernel2.start();
		val sourceList = GetStartLists.compute(pg, place, world); 
		kernel2.stop();
		world.barrier();
		
		/* val tmp:Rail[Defs.edge]! = sourceList as Rail[Defs.edge]!;
		 * 
		 * for ((i) in 0..sourceList.length-1) {
			 * x10.io.Console.OUT.println("edge " + sourceList(i).e + "( " + sourceList(i).w + " ) " + " : " + "[ " + sourceList(i).startVertex + " , " + sourceList(i).endVertex + "]");
		 * }  */  
		 kernel3.start();
		 FindSubGraphs.compute(pg, world, sourceList, GLOBALS.SubGraphPathLength, GLOBALS.nthreads);
		 kernel3.stop();
		 world.barrier();
		}
		
		val bc = USE_ASYNC ? new BetweenessAsync(pg, FILTER, SCALE, GLOBALS.K4Approx) : new BetweenessAlltoall(pg, FILTER, SCALE, GLOBALS.K4Approx);
		bc.compute(GLOBALS);
		if (DUMP) bc.dump();
		
		PTimer.printDetailed();
		PStat.printDetailed();
	}
	
};
