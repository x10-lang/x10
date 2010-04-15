package ssca2;

import x10.util.*;

import x10.compiler.Native;

import x10.util.concurrent.atomic.AtomicBoolean;

class findSubGraphs_dist  {

      public static def compute (pg: defs.pGraph, place: Int, world: Comm!, sourceList: Rail[defs.edge]!, SubGraphPathLength: Int) {


       for ((elem) in 0..sourceList.length-1) {
         val srcEdge = sourceList(elem);

           val pg_here = pg.restrict_here();
           val vertices = pg_here.vertices;
           val visited = new Array[Boolean](vertices, (p: Point(1))=>false);
           var lcount: Int= 0;

           val source = srcEdge.endVertex;   
           val fsize_first = pg.owner(source) == here ? 1 : 0;
           var L: Rail[types.VERT_T]! = Rail.make[types.VERT_T](fsize_first, (i:Int)=>source);

            if (pg.owner(srcEdge.startVertex) == here) { visited(srcEdge.startVertex) = true; lcount++; }

            var l: Int = 0;
            while (l <= SubGraphPathLength) {

             val N = Rail.make[GrowableRail[types.VERT_T]!](Place.MAX_PLACES, (i:Int)=>new GrowableRail[types.VERT_T](0));

              val frontier: Rail[types.VERT_T]! = L;
              val flength = frontier.length();
              val frontierSize = world.sum(flength);
              //x10.io.Console.OUT.println("frontier Size " + frontierSize);
              if (frontierSize == 0) break;

              for ((i) in 0..flength-1) {
                val vertex = frontier(i);
               //if (pg.owner(vertex) != here) continue;

                if (visited(vertex) == true) continue;
 
                visited(vertex) = true;
                val lo = pg_here.numEdges(vertex);
                val hi = pg_here.numEdges(vertex+1)-1;
                  //x10.io.Console.OUT.println("lo..hi " + lo + " " + hi);

                lcount++;

                for ((k) in  lo..hi) {
                  val neighbor = pg_here.endV(k);
                  val owner = pg.owner(neighbor);
                  //x10.io.Console.OUT.println("neibhor " + neighbor + " " + owner);
                  N(owner.id).add(neighbor);
                }
              }

             if (l == SubGraphPathLength)  break;

       /*      for ((place)  in unique) {
               val N_q : GrowableRail[types.VERT_T]! = N(place);
               val mlength = N_q.length();
               for ((i) in 0..mlength-1) {
                   x10.io.Console.OUT.println ("N: " + N_q(i)); 
                }
             } */

              L = world.alltoallv[types.VERT_T](N);
  
                
             /* val M = L(l+1);
             val mlength = M.length();
             for ((i) in 0..mlength-1) {
                 x10.io.Console.OUT.println ("M: " + M(i)); 
             } */



             l = l  + 1;
            }

            val count = world.sum(lcount);

            if (place == 0)
             x10.io.Console.OUT.println("Search from <" + srcEdge.startVertex + " ," + srcEdge.endVertex + " > " + "number of vertices visited: " + count);
             world.barrier();
         }         
}
}
	
