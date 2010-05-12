/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package ssca2;

import x10.util.*;

import x10.compiler.Native;
import x10.io.File;
import x10.io.FileWriter;
import x10.io.IOException;

/**
  * Computes the betweeness_centrality indices for all the vertices in a distributed graph.
  * <p>
  * For every vertex, computation happens in two phases. In the first phase, BFS traversal is performed
  * starting from the vertex. During this phase, the depth of each vertex visited is recorded. Also, 
  * the predecessors for each vertex are recorded.  
  * <p>
  * In the second phase, the graph is traversed in the backward direction to that of the BFS. During the traversal,
  * quantitiies for each vertex are updated using the following recurrence relation: TODO.
  * BetweenessCentrality of a vertex, bc(v) is simply obtained by computing the running sum of del(v).
   

  * @author: Ganesh Bikshandi
  */

class BetweenessCentrality {

     public static type UVDSQuad= Quad[types.VERT_T, types.VERT_T, types.LONG_T, types.DOUBLE_T];
     public static type VDSTriplet =  Triplet[types.VERT_T, types.DOUBLE_T, types.DOUBLE_T];

     //PlaceLocal storge for program variables for each vertex
     global val BC: PlaceLocalHandle[Array[types.DOUBLE_T](1)];
     global val Sig: PlaceLocalHandle[Array[types.DOUBLE_T](1)];
     global  val Del: PlaceLocalHandle[Array[types.DOUBLE_T](1)];
     global val D: PlaceLocalHandle[Array[types.LONG_T](1)];
     global val Pred: PlaceLocalHandle[Array[GrowableRail[types.VERT_T]](1)];
     global val Frontier: PlaceLocalHandle[Rail[GrowableRail[types.VERT_T]]];

     // alltoall buffers , N_*: Input, L_*: output 
     // Not required for async version
     global val N_cent: PlaceLocalHandle[Rail[GrowableRail[VDSTriplet]]];
     global val L_cent: PlaceLocalHandle[GrowableRail[VDSTriplet]];
     global val N_bfs: PlaceLocalHandle[Rail[GrowableRail[UVDSQuad]]];
     global val L_bfs: PlaceLocalHandle[GrowableRail[UVDSQuad]];
     global val Visited: PlaceLocalHandle[Array[Boolean](1)];

     //Stack computed during BFS and used during Back Propagation
     global val S: PlaceLocalHandle[GrowableRail[types.VERT_T]];
     global val Count: PlaceLocalHandle[GrowableRail[types.INT_T]];

     //switch to use the async version
     global val use_async: Boolean;
     
     // the graph for which we need to compute the BC.
     global val pg: defs.pGraph;

     /**
       * Constructor
       */
     public def this(pg: defs.pGraph, val use_async: Boolean) {

             val unique = Dist.makeUnique();

             BC = PlaceLocalHandle.make[Array[types.DOUBLE_T](1)](unique, ()=>new Array[types.DOUBLE_T](pg.restrict_here().vertices, (p: Point(1))=>0d) as Array[types.DOUBLE_T](1)!);
             Sig = PlaceLocalHandle.make[Array[types.DOUBLE_T](1)](unique, ()=>new Array[types.DOUBLE_T](pg.restrict_here().vertices, (p: Point(1))=>0.0d) as Array[types.DOUBLE_T](1)!);
             Del = PlaceLocalHandle.make[Array[types.DOUBLE_T](1)](unique, ()=>new Array[types.DOUBLE_T](pg.restrict_here().vertices, (p: Point(1))=>0.0d) as Array[types.DOUBLE_T](1)!);
             D = PlaceLocalHandle.make[Array[types.LONG_T](1)](unique, ()=>new Array[types.LONG_T](pg.restrict_here().vertices, (p: Point(1))=>-1) as Array[types.LONG_T](1)!);

             Pred = PlaceLocalHandle.make[Array[GrowableRail[types.VERT_T]](1)](unique, ()=>new Array[GrowableRail[types.VERT_T]](pg.restrict_here().vertices, (p:Point(1))=>new GrowableRail[types.VERT_T]()) as Array[GrowableRail[types.VERT_T]](1)!);

             Frontier = PlaceLocalHandle.make[Rail[GrowableRail[types.VERT_T]]](unique, ()=>Rail.make[GrowableRail[types.VERT_T]](2, (i:Int)=>new GrowableRail[types.VERT_T]()) as Rail[GrowableRail[types.VERT_T]]!);

             S = PlaceLocalHandle.make[GrowableRail[types.VERT_T]](unique, ()=>new GrowableRail[types.VERT_T](0));
             Count = PlaceLocalHandle.make[GrowableRail[types.INT_T]](unique, ()=>new GrowableRail[types.INT_T](0));
             Visited  = PlaceLocalHandle.make[Array[Boolean](1)](unique, ()=>new Array[Boolean](pg.restrict_here().vertices, (p: Point(1))=>false));

             if (!use_async) {
                N_cent = PlaceLocalHandle.make[Rail[GrowableRail[VDSTriplet]]](unique, ()=>Rail.make[GrowableRail[VDSTriplet]](Place.MAX_PLACES, (n:Int)=>new GrowableRail[VDSTriplet](0)));
               L_cent =  PlaceLocalHandle.make[GrowableRail[VDSTriplet]](unique, ()=>new GrowableRail[VDSTriplet](0));

                N_bfs = PlaceLocalHandle.make[Rail[GrowableRail[UVDSQuad]]](unique, ()=>Rail.make[GrowableRail[UVDSQuad]](Place.MAX_PLACES,(n:Int)=>new GrowableRail[UVDSQuad](0)));
                L_bfs = PlaceLocalHandle.make[GrowableRail[UVDSQuad]](unique, ()=>new GrowableRail[UVDSQuad](0));

             }else {
                N_cent = PlaceLocalHandle.make[Rail[GrowableRail[VDSTriplet]]](unique, ()=>null);
               L_cent =  PlaceLocalHandle.make[GrowableRail[VDSTriplet]](unique, ()=>null);

                N_bfs = PlaceLocalHandle.make[Rail[GrowableRail[UVDSQuad]]](unique, ()=>null);
                L_bfs = PlaceLocalHandle.make[GrowableRail[UVDSQuad]](unique, ()=>null);

            }

             this.use_async = use_async;
             this.pg = pg;
     }


       private global def compute_back_async(place: Int, world:Comm!) {
              //x10.io.Console.ERR.println("inside back_async");
               val pred = Pred() as Array[GrowableRail[types.VERT_T]](1)!;
               val del = Del() as Array[types.DOUBLE_T](1)!;
               val sig = Sig() as Array[types.DOUBLE_T](1)!;
               val d = D() as Array[types.LONG_T](1)!;
               val bc = BC() as Array[types.DOUBLE_T](1)!;

               val s = S();
               val count = Count();
               val c_length = count.length();

                for (var l: Int = c_length-1; l >0; l--) {
                 val start = count(l-1);
                 val end = count(l)-1;

                 val tsum = world.sum(end-start+1);
                 //world.barrier();
                 if (tsum == 0)  continue;

                 for ((j) in start..end) {
                 val w = s(j);

                val pred_w  = pred(w);
                val pred_length = (pred(w) as GrowableRail[types.VERT_T]!).length();
                for ((k) in  0..pred_length-1) {
                  val v = (pred_w as GrowableRail[types.VERT_T]!)(k);
                  val del_w = del(w);
                  val sig_w = sig(w);
                  val owner = pg.owner(v);
                  finish async (owner) {
                     val del = Del() as Array[types.DOUBLE_T](1)!;
                     val sig = Sig() as Array[types.DOUBLE_T](1)!;
                     atomic del(v) = del(v) + sig(v)*(((1.0+del_w) as double)/sig_w);
                     //x10.io.Console.ERR.println("del " + i + " " +  v + " " + del(v));
                  } 

                 }
                 bc(w) += del(w);
                }
              }
           }

      private global def compute_back_alltoall (place: Int, world:Comm!) {
               val pred = Pred() as Array[GrowableRail[types.VERT_T]](1)!;
               val del = Del() as Array[types.DOUBLE_T](1)!;
               val sig = Sig() as Array[types.DOUBLE_T](1)!;
               val d = D() as Array[types.LONG_T](1)!;
               val bc = BC() as Array[types.DOUBLE_T](1)!;

               val s = S();
               val count = Count();
               val c_length = count.length();

                val N = N_cent();
                val L = L_cent();

                for (var l: Int = c_length-1; l >0; l--) {
                 val start = count(l-1);
                 val end = count(l)-1;

                 val tsum = world.sum(end-start+1);
                 //world.barrier();
                 if (tsum == 0)  continue;

                 //for ((k) in 0..Place.MAX_PLACES-1)  
                  for (var k: Int = 0; k < Place.MAX_PLACES; k++) 
                   (N(k) as GrowableRail[VDSTriplet]!).setLength(0);

                 //for ((j) in start..end) {
                 for (var j: Int = start; j <= end; j++) {
                 val w = s(j);

                val pred_w  = pred(w);
                val pred_length = (pred(w) as GrowableRail[types.VERT_T]!).length();
                //for ((k) in  0..pred_length-1) {
                  for (var k: Int = 0; k < pred_length; k++) {
                  val v = (pred_w as GrowableRail[types.VERT_T]!)(k);
                  val del_w = del(w);
                  val sig_w = sig(w);
                  val owner = pg.owner(v);
                  N(owner.id).add(VDSTriplet(v, del_w, sig_w));

                 }
                }

                 world.alltoallv[VDSTriplet](N, L);

                  //for ((k) in 0..L.length()-1) {
                  for (var k: Int = 0; k <L.length(); k++) {
                    val v = L(k).first;
                    val del_w = L(k).second;
                    val sig_w = L(k).third;
                    del(v) = del(v) + sig(v)*(((1.0+del_w) as double)/sig_w);
                 }
                 //for ((j) in start..end) {
                  for (var j: Int = start; j <= end; j++) {
                   val w = s(j);
                   bc(w) += del(w);
                 }
          }
      }

      private global def compute_bfs_async (place: Int, world:Comm!,  source:types.VERT_T, pg_here: defs.pGraphLocal!) {

           val d = D() as Array[types.LONG_T](1)!;
           val sig = Sig() as Array[types.DOUBLE_T](1)!;
           val frontier = Frontier();

           val s = S();
           val  count = Count();
           val vertices = pg_here.vertices;
           val visited =  Visited() as Array[Boolean](1);
           for ((i) in vertices) visited(i) =false;

              //x10.io.Console.ERR.println("inside bfs_async");

           val L0 = frontier(0) as GrowableRail[types.VERT_T]!;
           if (pg.owner(source) == here)  {L0.add(source); d(source ) = 0; sig(source) = 1;}
           
            var phase: Int = 0;
            count.add(0);
            while (true) {

 
              val L = frontier(phase%2) as GrowableRail[types.VERT_T]!;

              val flength = L.length();
              val LSize = world.sum(flength);
              //x10.io.Console.ERR.println("size " + flength + " " + LSize);
              //world.barrier();
              if (LSize == 0) break;

              count.add(count(phase));
              finish for ((i) in 0..flength-1) {
                val v = L(i);

                if (visited(v) == true) continue;

               if (phase >0 ) {
                 s.add(v);
                 count(phase+1)++;
               }
                visited(v) = true;
                val lo = pg_here.numEdges(v);
                val hi = pg_here.numEdges(v+1)-1;


                for ((k) in  lo..hi) {
                  val w = pg_here.endV(k);
                  if (w == v) continue;
                  val owner = pg.owner(w);

                   val sig_v = sig(v);
                   val d_v = d(v);
                  val cur_phase = phase;
                    
                 ////x10.io.Console.ERR.println("phase " + cur_phase + " " + d_v + " " + sig_v);
             
                  async (owner) {
                    val d = D() as Array[types.LONG_T](1)!;
                     val sig = Sig() as Array[types.DOUBLE_T](1)!;
                   val L_next = Frontier()((cur_phase+1)%2);
                   val pred = Pred() as Array[GrowableRail[types.VERT_T]](1)!;
                   atomic {
                   L_next.add(w);
                   if (d(w)==-1) {
                     d(w) = d_v + 1;
                     sig(w) = sig_v;
                     pred(w).add(v);
                   } else if (d(w) == d_v+1) {
                      sig(w) += sig_v;
                      pred(w).add(v);
                    }
                  }
                  }
                }
              }
              L.setLength(0);
            //  world.barrier();

             phase++; 
            }
}

      private global def compute_bfs_alltoall (place: Int, world: Comm!, source:types.VERT_T, pg_here:defs.pGraphLocal!) {

              //x10.io.Console.ERR.println("enter alltoall");
          val N = N_bfs() as Rail[GrowableRail[UVDSQuad]]!;
          val L = L_bfs();

              //x10.io.Console.ERR.println("point 1");

           val d = D() as Array[types.LONG_T](1)!;
           val sig = Sig() as Array[types.DOUBLE_T](1)!;
           val pred = Pred() as Array[GrowableRail[types.VERT_T]](1)!;
 
           val s = S(); //rename to Stack
           val  count = Count();

           val vertices = pg_here.vertices;
           val visited = Visited() as Array[Boolean](1);
           for ((i) in vertices) visited(i) =false;

           if (pg.owner(source) == here)  {L.add(UVDSQuad(-1, source, 0, 0.0)); d(source ) = 0; sig(source) = 1;}

            var l: Int = 0;
            count.add(0);
            while (true) {

              //for ((i) in 0..Place.MAX_PLACES-1) 
              for (var i: Int = 0; i < Place.MAX_PLACES; i++) 
                 (N(i) as GrowableRail[UVDSQuad]!).setLength(0);

              val flength = L.length();
              //x10.io.Console.ERR.println("flength" + flength);
              val LSize = world.sum(flength);
              //x10.io.Console.ERR.println("LSize " + LSize);
              //world.barrier();
              if (LSize == 0) break;

              count.add(count(l));
              //for ((i) in 0..flength-1) {
              for (var i: Int = 0; i < flength; i++) {
                val vertex = L(i).second;

                if (visited(vertex) == true) continue;

               if (l >0 ) {
                 s.add(vertex);
                 count(l+1)++;
               }
                visited(vertex) = true;
                val lo = pg_here.numEdges(vertex);
                val hi = pg_here.numEdges(vertex+1)-1;


                //for ((k) in  lo..hi) {
                  for (var k: Int = lo; k <= hi; k++) {
                  val neighbor = pg_here.endV(k); //Rename neighbor to v; vertext to u
                  val owner = pg.owner(neighbor);
                  if (neighbor == vertex) continue;
                  (N(owner.id) as GrowableRail[UVDSQuad]!).add(UVDSQuad(vertex, neighbor, d(vertex), sig(vertex)));
                }
              }

	      world.alltoallv[UVDSQuad](N,L);
              //world.barrier();

                   
             //for ((i) in 0..L.length()-1) {
             for (var i: Int = 0; i < L.length(); i++) {
               val t = L(i);
               val w = t.second;
               val v = t.first;
              if(d(w) == -1) {
               d(w)  = t.third + 1;
               sig(w) = t.fourth;
               (pred(w) as GrowableRail[types.VERT_T]!).add(v);

             } else if (d(w) ==  t.third+1) {
                  sig(w) += t.fourth;
                 (pred(w) as GrowableRail[types.VERT_T]!).add(v);
             }
             } 
             l++; 
            }

         }
        

      /**
        * compute the Betweneess Centrality Metric 
        */ 
      public global def compute () {

          val  kernel4 = PTimer.make("kernel4");
          val  bfs = PTimer.make("bfs");
          val  back = PTimer.make("back");

           val n = pg.N;
           val chunkSize = pg.N / Place.MAX_PLACES;
           val unique = Dist.makeUnique();

           // TODO: Permutation
           /* val NewVertices = DistArray.make[GrowableRail[types.UVPair]](unique);
           finish ateach((p) in unique) {
               val world: Comm! = Comm.WORLD();
               NewVertices(here.id) =  util.random_permute_vertices(pg.restrict_here().vertices, n, Comm.WORLD());
           } */

           //x10.io.Console.ERR.println("inside bc..");

           finish ateach((p) in unique) {
               val world: Comm! = Comm.WORLD();

               //x10.io.Console.ERR.println("world");

               /* place locals accessed by asyncs */
               val pred = Pred() as Array[GrowableRail[types.VERT_T]](1);
               val del = Del() as Array[types.DOUBLE_T](1)!;
               val sig = Sig() as Array[types.DOUBLE_T](1)!;
               val d = D() as Array[types.LONG_T](1)!;
               val bc = BC() as Array[types.DOUBLE_T](1)!;
               val s = S();
               val count = Count();
               val pg_here = pg.restrict_here() as defs.pGraphLocal!;

               //x10.io.Console.ERR.println("point 1");
           for ((i) in 0..n-1) {



               kernel4.start();
               //x10.io.Console.ERR.println("point 2");

               //val new_vertices = NewVertices(here.id);
               //val a = world.sum(pg.owner(i) == here ? new_vertices as rail[types.UVPair]!)(i % chunkSize) 
               //val startVertex = world.sum(pg.owner(i) == here ? (new_vertices as Rail[types.UVPair]!)(i % chunkSize).second : 0); //actually a broadcast
               val startVertex = world.sum(pg.owner(i) == here ? i : 0); //actually a broadcast

               x10.io.Console.ERR.println("main " + i);

               s.setLength(0);
               count.setLength(0);
               if (use_async){
               bfs.start();
               compute_bfs_async(p, world, startVertex, pg_here);
               bfs.stop();
               world.barrier();
                back.start();
                compute_back_async(p, world);
                back.stop();
              } else {
               //x10.io.Console.ERR.println("bfs");
               bfs.start();
               compute_bfs_alltoall(p, world,  startVertex, pg_here);
               bfs.stop();
               //x10.io.Console.ERR.println("back");
               world.barrier();
                back.start();
               compute_back_alltoall(p, world);
                back.stop();
              }

               for ((k) in pg_here.vertices) {
                        pred(k).setLength(0);
                        del(k) = 0;
               //         sig(k) = 0;
                        d(k) = -1;
               }
               /* for ((k) in 0..s.length()-1) {
                        pred(s(k)).setLength(0);
                        del(s(k)) = 0;
                  //      sig(s(k)) = 0;
                        d(s(k)) = -1;
               } */

               kernel4.stop();
            world.barrier();     
          } 

        }


         //write the output to a file for verification
         try {

          finish ateach((p) in unique) {
             val world = Comm.WORLD();

             for ((i) in unique ) {
               world.barrier();
               if (i !=here.id) continue;
               val printer = new FileWriter(new File("BC.txt"));
               val bc = BC() as Array[types.DOUBLE_T](1)!;
                for ((a) in bc.region) {
                 val s = a.toString() + " " + bc(a).toString();
                 x10.io.Console.OUT.println(s);
                }
             }
         }

       } catch (e: IOException ) {

       } 
    }
}
	
