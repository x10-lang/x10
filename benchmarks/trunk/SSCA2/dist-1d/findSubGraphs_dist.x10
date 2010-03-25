package ssca2;

import x10.util.*;

import x10.compiler.Native;

import x10.util.concurrent.atomic.AtomicBoolean;

class findSubGraphs_dist  {

      public static def compute (g: defs.graph, sourceList: Rail[defs.edge]!) {

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



/*         val Ms = PlaceLocalHandle.make[Rail[GrowableRail[types.VERT_T]!]](unique, ()=>Rail.make[GrowableRail[types.VERT_T]!](Place.MAX_PLACES, (i:Int)=>new GrowableRail[types.VERT_T](0)));
         val Ns = PlaceLocalHandle.make[Rail[GrowableRail[types.VERT_T]!]](unique, ()=>Rail.make[GrowableRail[types.VERT_T]!](Place.MAX_PLACES, (i:Int)=>new GrowableRail[types.VERT_T](0))); */

         finish ateach ((p) in unique) {
           @Native("c++", "MPI_Init(NULL, NULL);") {}
         }

       for ((elem) in 0..sourceList.length-1) {
         val srcEdge = sourceList(elem);

         finish ateach ((p) in unique) {


           val world: Comm! = Comm.WORLD();
           val pg_here = pg.restrict_here();
           val vertices = pg_here.vertices;
           val visited = Array.make[Boolean](vertices);
            var lcount: Int= 0;
           for ((i) in vertices) visited(i) = false;
           val L = Rail.make[Rail[types.VERT_T]!](GLOBALS.SubGraphPathLength, (i:Int)=>Rail.make[types.VERT_T](0));

            val source = srcEdge.endVertex;   
            if (pg.owner(source) == here)  {
            L(0) = Rail.make[types.VERT_T](1);L(0)(0)= source; }
            if (pg.owner(srcEdge.startVertex) == here) { visited(srcEdge.startVertex) = true; lcount++; }

            var l: Int = 0;
            while (l <= GLOBALS.SubGraphPathLength) {

             //val M = Rail.make[Rail[types.VERT_T]!](Place.MAX_PLACES, (i:Int)=>Rail.make[types.VERT_T](0));
             val N = Rail.make[GrowableRail[types.VERT_T]!](Place.MAX_PLACES, (i:Int)=>new GrowableRail[types.VERT_T](0));

              val frontier: Rail[types.VERT_T]! = L(l);
              val flength = frontier.length();
              val frontierSize = world.sum(flength);
              //x10.io.Console.OUT.println("frontier Size " + frontierSize);
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
                  //x10.io.Console.OUT.println("neibhor " + neighbor + " " + owner);
                  N(owner.id).add(neighbor);
                }
              }

             if (l == GLOBALS.SubGraphPathLength)  break;

       /*      for ((place)  in unique) {
               val N_q : GrowableRail[types.VERT_T]! = N(place);
               val mlength = N_q.length();
               for ((i) in 0..mlength-1) {
                   x10.io.Console.OUT.println ("N: " + N_q(i)); 
                }
             } */

              L(l+1) = world.alltoallv(N);
  
                
             /* val M = L(l+1);
             val mlength = M.length();
             for ((i) in 0..mlength-1) {
                 x10.io.Console.OUT.println ("M: " + M(i)); 
             } */



             l = l  + 1;
            }

            val count = world.sum(lcount);

            if (here.id == 0)
             x10.io.Console.OUT.println("Search from <" + srcEdge.startVertex + " ," + srcEdge.endVertex + " > " + "number of vertices visited: " + count);
         }         
      }
         finish ateach ((p) in unique) {
           @Native("c++", "MPI_Finalize();") {}
         }
}
}
	
