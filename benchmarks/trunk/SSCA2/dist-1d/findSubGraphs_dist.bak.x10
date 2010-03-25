package ssca2;

import x10.util.*;


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

             for ((i) in pg_here.vertices) {
                         val low = pg_here.numEdges(i);
                         val high = pg_here.numEdges(i+1);
                         for ((j) in  low..high-1) {
                                 x10.io.Console.OUT.println("[ " + i + " " + pg_here.endV(j) +  " " + pg_here.weight(j) + "] " );
                         }

             }
            }
         }


         val srcEdge = sourceList(0);

/*         val Ms = PlaceLocalHandle.make[Rail[GrowableRail[types.VERT_T]!]](unique, ()=>Rail.make[GrowableRail[types.VERT_T]!](Place.MAX_PLACES, (i:Int)=>new GrowableRail[types.VERT_T](0)));
         val Ns = PlaceLocalHandle.make[Rail[GrowableRail[types.VERT_T]!]](unique, ()=>Rail.make[GrowableRail[types.VERT_T]!](Place.MAX_PLACES, (i:Int)=>new GrowableRail[types.VERT_T](0))); */

         finish ateach ((p) in unique) {
           val world: Comm! = Comm.WORLD();
           val pg_here = pg.restrict_here();
           val vertices = pg_here.vertices;
           val visited = Array.make[Boolean](vertices);
           for ((i) in vertices) visited(i) = true;
           val L = Rail.make[GrowableRail[types.VERT_T]!](GLOBALS.SubGraphPathLength, 
                                                    (i:Int)=>new GrowableRail[types.VERT_T](0));
            val source = srcEdge.endVertex;   
            if (pg.owner(source) == here) L(0).add(source);
            if (pg.owner(srcEdge.startVertex) == here) visited(srcEdge.startVertex) = true;

            var l: Int = 0;
            while (l < GLOBALS.SubGraphPathLength) {

              val N = Ns();
              val M = Ms();

              val frontier: GrowableRail[types.VERT_T]! = L(l);
              val flength = frontier.length();
              val frontierSize = world.sum(flength);

              if (frontierSize == 0) break;

              for ((i) in 0..flength-1) {
                val vertex = frontier(i);
                if (pg.owner(vertex) != here) continue;

                if (visited(vertex) == true) continue;
 
                visited(vertex) = true;
                val lo = pg_here.numEdges(vertex);
                val hi = pg_here.numEdges(vertex+1)-1;
                for ((k) in  lo..hi) {
                  val neighbor = pg_here.endV(k);
                  val owner = pg.owner(neighbor);
                  ((N as Rail[GrowableRail[types.VERT_T]!]!)(owner.id) as GrowableRail[types.VERT_T!]).add(pg_here.endV(neighbor));
                }
              }

              //Comm.alltoallv(N, M); 
              finish for ((i) in unique) {
                val N_i: GrowableRail[types.VERT_T]! = N(i);
                val nlength = N_i.length();
                val tmp = ValRail.make[types.VERT_T](nlength, (k:Int)=>(N_i as GrowableRail[types.VERT_T]!)(k));
                val there = here.id;
                async (Place.places(i)) { 
                  val M = Ms();
                  val M_there: GrowableRail[types.VERT_T]! =  M(there);
                  val mlength = M_there.length();
                  for ((j) in 0..mlength) {
                    M_there(j) = tmp(j);   
                  }
                }
              }
   
             for ((place)  in unique) {
               val M_q : GrowableRail[types.VERT_T]! = M(place);
               val mlength = M_q.length();
               for ((i) in 0..mlength-1) {
                   val vertex = M_q(i);
                   L(l+1).add(vertex);
               }
             }

             l = l  + 1;
            }
         }         
      }
}
	
