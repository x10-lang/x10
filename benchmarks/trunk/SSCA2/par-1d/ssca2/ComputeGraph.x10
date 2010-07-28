package ssca2;

import x10.util.*;
public class ComputeGraph { 
	
	
	public static def compute (edges: Rail[Types.EDGE_T]!, pg:Defs.pGraph, pid: Int, world: Comm!) {
		
		val pg_here = pg.restrict_here();	
		val ledges = world.usort[Types.EDGE_T](edges, (i:Types.EDGE_T)=>pg.owner_id(i.first));
		//x10.io.Console.ERR.println(pg_here.vertices + " " + ledges);
		
		val degree: LinearArray[Types.LONG_T](1) = new LinearArray[Types.LONG_T](pg_here.vertices, (i:Int)=>0);
		val pos = Rail.make[Types.LONG_T](ledges.length(), (i:Int)=>0);
		
		for (var i: Int=0; i < ledges.length();i++) {
			val e = ledges(i);
			pos(i) = degree(e.first); 
			degree(e.first) = degree(e.first) + 1;
		}
		
		
		//x10.io.Console.ERR.println("point 1\n");
		
		val lo = pg_here.vertices.min(0);
		val hi = pg_here.vertices.max(0);
		
		pg_here.numEdges(lo) = 0;
		for (var i : Int = lo+1; i <= (hi+1); i++) {
			pg_here.numEdges(i) = pg_here.numEdges(i-1) + degree(i-1);
		}
		
		//x10.io.Console.ERR.println("point 2\n");
		
		val nedgesLocal = pg_here.numEdges(hi+1);	
		pg_here.endV.setLength(nedgesLocal);
		pg_here.weight.setLength(nedgesLocal);
		
		for (var i: Int = 0; i < ledges.length(); i++) {
			val u = ledges(i).first;
			val j = pg_here.numEdges(u) + pos(i);
			pg_here.endV(j) = ledges(i).second;
			pg_here.weight(j) = ledges(i).third;
		}
		
		
		//x10.io.Console.ERR.println("point 3\n");
		/* for ((i) in 0..ledges.length()-1){
			 * x10.io.Console.ERR.println("[ " + ledges(i) + "] ");
		* }
		* 
		* for((i) in pg_here.vertices) {
			* x10.io.Console.ERR.println("[ " + pg_here.numEdges(i) + "] " );
		* }
		* 
		* for((i) in pg_here.vertices) {
			* val lo0 = pg_here.numEdges(i);
			* val hi0 = pg_here.numEdges(i+1);
			* for ((j) in  lo0..hi0-1) {
				* x10.io.Console.ERR.println("[ " + i + " " + pg_here.endV(j) +  " " + pg_here.weight(j) + "] " );
			* }
		* } */ 
		
		Runtime.deallocObject(degree);	
		Runtime.deallocObject(pos);
	}
};
