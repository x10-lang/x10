package ssca2;

import x10.util.*;
public class computeGraph_dist  {

	
	public static def compute (GLOBALS: runtime_consts, SDGdata:defs.graphSDG_dist!, pg:defs.pGraph, pid: Int, world: Comm!) {

                val pg_here = pg.restrict_here();	
                val ledges = world.usort[types.UVWTriplet](SDGdata.etriplets, (i:types.UVWTriplet)=>pg.owner(i.first).id);
                //x10.io.Console.ERR.println(pg_here.vertices + " " + ledges);
			
		val degree: Array[types.LONG_T](1) = new Array[types.LONG_T](pg_here.vertices, (pt:Point(1))=>0);
		val pos = Rail.make[types.LONG_T](ledges.length(), (i:Int)=>0);

		for ((i) in  0..ledges.length()-1) {
			val u = ledges(i);
			pos(i) = degree(u.first); 
                       degree(u.first) = degree(u.first) + 1;
		}


                //x10.io.Console.ERR.println("point 1\n");
	
                val lo = pg_here.vertices.min(0);
                val hi = pg_here.vertices.max(0);

                pg_here.numEdges(lo) = 0;
		for ((i) in  lo+1..hi+1) {
		  pg_here.numEdges(i) = pg_here.numEdges(i-1) + degree(i-1);

                 /* dummy add -- should be removed */
                  for ((j) in 0..degree(i-1)-1) {
                    pg_here.endV.add(-1);
                    pg_here.weight.add(-1);
                  }
                }

                //x10.io.Console.ERR.println("point 2\n");
		
                 for ((i) in 0..ledges.length()-1) {
                   val u = ledges(i).first;
                   val j = pg_here.numEdges(u) + pos(i);
                   pg_here.endV(j) = ledges(i).second;
                   pg_here.weight(j) = ledges(i).third;
                 }

		
                //x10.io.Console.ERR.println("point 3\n");
		 /* for ((i) in 0..ledges.length()-1){
			  x10.io.Console.ERR.println("[ " + ledges(i) + "] ");
		 }
		 
		 for((i) in pg_here.vertices) {
			 x10.io.Console.ERR.println("[ " + pg_here.numEdges(i) + "] " );
		 }
		 
		 for((i) in pg_here.vertices) {
			 val lo0 = pg_here.numEdges(i);
			 val hi0 = pg_here.numEdges(i+1);
			 for ((j) in  lo0..hi0-1) {
			   x10.io.Console.ERR.println("[ " + i + " " + pg_here.endV(j) +  " " + pg_here.weight(j) + "] " );
			 }
		 } */ 
		
	}
};
