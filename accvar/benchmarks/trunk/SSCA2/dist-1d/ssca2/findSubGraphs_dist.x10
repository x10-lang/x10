package ssca2;

import x10.util.*;

import x10.compiler.Native;

import x10.util.concurrent.AtomicBoolean;

class findSubGraphs_dist  {

      public static def compute (pg: defs.pGraph, place: Int, world: Comm!, sourceList: Rail[defs.edge]!, SubGraphPathLength: Int) {


       val L = new GrowableRail[types.VERT_T](0);
       val N = Rail.make[GrowableRail[types.VERT_T]](Place.MAX_PLACES, (i:Int)=>new GrowableRail[types.VERT_T](0));

       for ((elem) in 0..sourceList.length-1) {
        val srcEdge = sourceList(elem);

           val pg_here = pg.restrict_here();
           val vertices = pg_here.vertices;
           val visited = new Array[Boolean](vertices, (p: Point(1))=>false);
           var lcount: Int= 0;

            if (pg.owner(srcEdge.startVertex) == here) { visited(srcEdge.startVertex) = true; lcount++; }
           val source = srcEdge.endVertex;  

           L.setLength(0);
           if (pg.owner(source) == here) { L.add(source); }
            var l: Int = 0;
            while (l <= SubGraphPathLength) {
              for ((i) in 0..Place.MAX_PLACES-1) N(i).setLength(0); 
              val frontier: GrowableRail[types.VERT_T]! = L;
              val flength = frontier.length();
//              x10.io.Console.OUT.println("frontier Size " + flength);
              val frontierSize = world.sum(flength);
  //            x10.io.Console.OUT.println("frontier Size " + frontierSize);
              if (frontierSize == 0) break;

              for ((i) in 0..flength-1) {
                val vertex = frontier(i);
               if (pg.owner(vertex) != here) continue;

                if (visited(vertex) == true) continue;
 
                visited(vertex) = true;
                val lo = pg_here.numEdges(vertex);
                val hi = pg_here.numEdges(vertex+1)-1;
                  //x10.io.Console.OUT.println("lo..hi " + lo + " " + hi);

                lcount++;

                for ((k) in  lo..hi) {
                  val neighbor = pg_here.endV(k);
                  val owner = pg.owner(neighbor);
  //                x10.io.Console.OUT.println("neibhor " + neighbor + " " + owner);
                  N(owner.id).add(neighbor);
                }
              }

             if (l == SubGraphPathLength)  break;


              world.alltoallv[types.VERT_T](N, L);
  
                
             l = l  + 1;
            }

            val count = world.sum(lcount);

            if (place == 0)
             x10.io.Console.OUT.println("Search from <" + srcEdge.startVertex + " ," + srcEdge.endVertex + " > " + "number of vertices visited: " + count);
             world.barrier();
         }         
}
}
	
