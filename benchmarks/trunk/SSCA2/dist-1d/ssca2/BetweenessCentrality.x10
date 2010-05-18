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
  * starting from the given source vertex (source).  During BFS, For each vertex v, its child "w" is visited only if d(w)==-1 or d(w)=d(v)+1.
  * (d(v) is depth of a vertex from a "source" vertex). The # of shortest paths for w (sig(w)) is also updated while vising w.
  * and the predecessors of w are stored. Additionally, the visited vertices are "stacked" in S. 
  * <p>
  * In the second phase, the graph is traversed in the backward direction to that of the BFS. During the traversal,
  * for each vertex w, and for all of its predecessor v, del(v) is updated as follows:
  *       del (v) = del(v) + sig(v)/sig(w)*(1+del(w).
  *
  * BetweenessCentrality of a vertex, bc(v), is simply obtained by computing the running sum of del(v).
  * 
  *
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
     global val filter: Boolean;
     
     // the graph for which we need to compute the BC.
     global val pg: defs.pGraph;


     public static  def computeInDegree(val pg: defs.pGraph) {

           val unique = Dist.makeUnique();
           val InDegree = PlaceLocalHandle.make[Array[types.LONG_T](1)](unique, ()=>new Array[types.LONG_T](pg.restrict_here().vertices, (p: Point(1))=>0) as Array[types.LONG_T](1)!);
         finish ateach((p) in unique) {
            val world = Comm.WORLD();
            val pg_here = pg.restrict_here();
            val tmp = world.usort[types.VERT_T](pg_here.endV, (v:types.VERT_T)=>pg.owner_id(v));
            val inDegree = InDegree();
            for (var i : Int = 0 ; i < tmp.length(); i++) {
              (inDegree as Array[types.LONG_T](1)!)(tmp(i)) += 1;
            } 
            /* for (var i : Int = inDegree.region.min(0); i <= inDegree.region.max(0); i++ ) 
               x10.io.Console.ERR.println((inDegree as Array[types.LONG_T](1)!)(i)); */
         }
         return InDegree;

      }

     /**
       * Constructor
       */
     public def this(pg: defs.pGraph, val use_async: Boolean, val filter: Boolean, val SCALE: types.INT_T) {




             val unique = Dist.makeUnique();


             val InDegree = computeInDegree(pg);

             BC = PlaceLocalHandle.make[Array[types.DOUBLE_T](1)](unique, ()=>new Array[types.DOUBLE_T](pg.restrict_here().vertices, (p: Point(1))=>0d) as Array[types.DOUBLE_T](1)!);
             Sig = PlaceLocalHandle.make[Array[types.DOUBLE_T](1)](unique, ()=>new Array[types.DOUBLE_T](pg.restrict_here().vertices, (p: Point(1))=>0.0d) as Array[types.DOUBLE_T](1)!);
             Del = PlaceLocalHandle.make[Array[types.DOUBLE_T](1)](unique, ()=>new Array[types.DOUBLE_T](pg.restrict_here().vertices, (p: Point(1))=>0.0d) as Array[types.DOUBLE_T](1)!);
             D = PlaceLocalHandle.make[Array[types.LONG_T](1)](unique, ()=>new Array[types.LONG_T](pg.restrict_here().vertices, (p: Point(1))=>-1) as Array[types.LONG_T](1)!);

             Pred = PlaceLocalHandle.make[Array[GrowableRail[types.VERT_T]](1)](unique, ()=>new Array[GrowableRail[types.VERT_T]](pg.restrict_here().vertices, (p:Point(1))=>new GrowableRail[types.VERT_T]()) as Array[GrowableRail[types.VERT_T]](1)!);


             finish ateach ((p) in unique) {

                val pred = Pred();
                val inDegree = InDegree();
                for (var v: Int = pred.region.min(0); v <= pred.region.max(0); v++) 
                    (pred as Array[GrowableRail[types.VERT_T]](1)!)(v).setLength((inDegree as Array[types.VERT_T](1)!)(v));
             }

             Frontier = PlaceLocalHandle.make[Rail[GrowableRail[types.VERT_T]]](unique, ()=>Rail.make[GrowableRail[types.VERT_T]](2, (i:Int)=>new GrowableRail[types.VERT_T]()) as Rail[GrowableRail[types.VERT_T]]!);

             S = PlaceLocalHandle.make[GrowableRail[types.VERT_T]](unique, ()=>new GrowableRail[types.VERT_T](pg.chunkSize));
             Count = PlaceLocalHandle.make[GrowableRail[types.INT_T]](unique, ()=>new GrowableRail[types.INT_T](SCALE));
             Visited  = PlaceLocalHandle.make[Array[Boolean](1)](unique, ()=>new Array[Boolean](pg.restrict_here().vertices, (p: Point(1))=>false));

             if (!use_async) {
                N_cent = PlaceLocalHandle.make[Rail[GrowableRail[VDSTriplet]]](unique, ()=>Rail.make[GrowableRail[VDSTriplet]](Place.MAX_PLACES, (n:Int)=>new GrowableRail[VDSTriplet](0)));
               L_cent =  PlaceLocalHandle.make[GrowableRail[VDSTriplet]](unique, ()=>new GrowableRail[VDSTriplet](0));

                N_bfs = PlaceLocalHandle.make[Rail[GrowableRail[UVDSQuad]]](unique, ()=>Rail.make[GrowableRail[UVDSQuad]](Place.MAX_PLACES,(n:Int)=>new GrowableRail[UVDSQuad](0)));
                L_bfs = PlaceLocalHandle.make[GrowableRail[UVDSQuad]](unique, ()=>new GrowableRail[UVDSQuad](10));

             }else {
                N_cent = PlaceLocalHandle.make[Rail[GrowableRail[VDSTriplet]]](unique, ()=>null);
               L_cent =  PlaceLocalHandle.make[GrowableRail[VDSTriplet]](unique, ()=>null);

                N_bfs = PlaceLocalHandle.make[Rail[GrowableRail[UVDSQuad]]](unique, ()=>null);
                L_bfs = PlaceLocalHandle.make[GrowableRail[UVDSQuad]](unique, ()=>null);

            }


             this.filter = filter;
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

                for (var l: Int = c_length-1; l > 1; l--) {
                 val start = count(l-1);
                 val end = count(l)-1;

                 val tsum = Place.MAX_PLACES == 1 ? end-start+1 : world.sum(end-start+1);
                 //world.barrier();
                 if (tsum == 0)  continue;

                 //for ((k) in 0..Place.MAX_PLACES-1)  
                  for (var k: Int = 0; k < Place.MAX_PLACES; k++) 
                   (N(k) as GrowableRail[VDSTriplet]!).setLength(0);

                 //for ((j) in start..end) 
                 L.setLength(0);
                 for (var j: Int = start; j <= end; j++) {
                 val w = s(j);

                val pred_w  = pred(w);
                val pred_length = (pred(w) as GrowableRail[types.VERT_T]!).length();
                //for ((k) in  0..pred_length-1) 
                  for (var k: Int = 0; k < pred_length; k++) {
                  val v = (pred_w as GrowableRail[types.VERT_T]!)(k);
                  val del_w = del(w);
                  val sig_w = sig(w);
                  val owner = pg.owner_id(v);
                  if (owner == here.id) {
                     del(v) = del(v) + sig(v)*(((1.0+del_w) as double)/sig_w);
                  }
                  else N(owner).add(VDSTriplet(v, del_w, sig_w));

                 }
                }

                 if (Place.MAX_PLACES > 1) world.alltoallv[VDSTriplet](N, L);

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
           val visited =  Visited() as Array[Boolean](1)!;
           for ((i) in vertices) visited(i) =false;

              //x10.io.Console.ERR.println("inside bfs_async");

           val L0 = frontier(0) as GrowableRail[types.VERT_T]!;
           if (pg.owner_id(source) ==here.id)  {L0.add(source); d(source ) = 0; sig(source) = 1;}
           
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
                   val L_next = Frontier()((cur_phase+1)%2) as GrowableRail[types.VERT_T]!;
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

           val src_owner = pg.owner_id(source);
           if (src_owner ==here.id)  {s.add(source);count.add(0); count.add(1); d(source ) = 0; sig(source) = 1;}
            else { count.add(0); count.add(0);}

            var l: Int = 1;
            while (true) {

              //for ((i) in 0..Place.MAX_PLACES-1) 
              for (var i: Int = 0; i < Place.MAX_PLACES; i++) 
                 (N(i) as GrowableRail[UVDSQuad]!).setLength(0);

              val start = count(l-1);
              val end = count(l)-1;
              val flength =  end-start+1;
              //x10.io.Console.ERR.println("flength" + flength);
              val LSize = Place.MAX_PLACES == 1 ? flength: world.sum(flength);
              //x10.io.Console.ERR.println("LSize " + LSize);
              //world.barrier();
              if (LSize == 0) break;

                L.setLength(0);
              //for ((i) in 0..flength-1) {
             count.add(count(l));
              for (var i: Int = start; i <=end; i++) {
                val vertex = s(i);

                val lo = pg_here.numEdges(vertex);
                val hi = pg_here.numEdges(vertex+1)-1;

                //for ((k) in  lo..hi) 
                  for (var k: Int = lo; k <= hi; k++) {

                  if (((pg_here.weight(k) & 7) ==0) && filter) continue;

                  val neighbor = pg_here.endV(k); //Rename neighbor to v; vertext to u
                  val owner = pg.owner_id(neighbor);
                  //if (neighbor == vertex) continue;
                  if (owner == here.id) { 
                    if (d(neighbor) == -1) {
                      s.add(neighbor);
                      count(l+1) ++;
                      d(neighbor) = d(vertex) + 1;
                      sig(neighbor) = sig(vertex);
                       (pred(neighbor) as GrowableRail[types.VERT_T]!).add(vertex);
                    }  else if (d(neighbor) == d(vertex) + 1) {
                           sig(neighbor) += sig(vertex);
                          (pred(neighbor) as GrowableRail[types.VERT_T]!).add(vertex);
       
                    }

                  } else { (N(owner) as GrowableRail[UVDSQuad]!).add(UVDSQuad(vertex, neighbor, d(vertex), sig(vertex))); }

                }
              }

	      if (Place.MAX_PLACES > 1) world.alltoallv[UVDSQuad](N,L);
              //world.barrier();

                   
             //for ((i) in 0..L.length()-1) {
             for (var i: Int = 0; i < L.length(); i++) {
               val t = L(i);
               val w = t.second;
               val v = t.first;
              if(d(w) == -1) {
               s.add(w);
                count(l+1)++;
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
      public global def compute (val GLOBALS: runtime_consts) {

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

           val numV = 1<<GLOBALS.K4Approx;

           finish ateach((p) in unique) {
               val world: Comm! = Comm.WORLD();

               //x10.io.Console.ERR.println("world");

               /* place locals accessed by asyncs */
               val pred = Pred() as Array[GrowableRail[types.VERT_T]](1)!;
               val del = Del() as Array[types.DOUBLE_T](1)!;
               val sig = Sig() as Array[types.DOUBLE_T](1)!;
               val d = D() as Array[types.LONG_T](1)!;
               val bc = BC() as Array[types.DOUBLE_T](1)!;
               val s = S();
               val count = Count();
               val pg_here = pg.restrict_here() as defs.pGraphLocal!;
      
               var num_traversals: Int = 0;

               //x10.io.Console.ERR.println("point 1");
                 for (var k: Int = pred.region.min(0); k <= pred.region.max(0); k++) {
                        pred(k).setLength(0);
                        bc(k) = 0.0d;
                        sig(k) = 0.0d;
                        del(k) = 0.0d;
                        d(k) = -1;
                 }
               kernel4.start();

           for (var i: Int = 0; i < n; i++ ) {

               //x10.io.Console.ERR.println("point 2");

               //val new_vertices = NewVertices(here.id);
               //val a = world.sum(pg.owner_id(i) ==here.id ? new_vertices as rail[types.UVPair]!)(i % chunkSize) 
               //val startVertex = world.sum(pg.owner_id(i) ==here.id ? (new_vertices as Rail[types.UVPair]!)(i % chunkSize).second : 0); //actually a broadcast
               val startVertex = world.sum(pg.owner_id(i) ==here.id ? ((pg_here.numEdges(i+1) - pg_here.numEdges(i)) > 0 ? i : -1) : 0); //actually a broadcast
         
               if (startVertex==-1) continue;

               num_traversals++;

               if (num_traversals == numV + 1) break;

               //x10.io.Console.ERR.println("main " + i);

               s.setLength(0);
               count.setLength(0);
               //x10.io.Console.ERR.println("bfs");
               //bfs.start();
               //compute_bfs_alltoall(p, world,  startVertex, pg_here);
                  val source = startVertex;

          val N = N_bfs() as Rail[GrowableRail[UVDSQuad]]!;
          val L = L_bfs();

              //x10.io.Console.ERR.println("point 1");

           val vertices = pg_here.vertices;

           val src_owner = pg.owner_id(source);
           if (src_owner ==here.id)  {s.add(source);count.add(0); count.add(1); d(source ) = 0; sig(source) = 1;}
            else { count.add(0); count.add(0);}

            var l: Int = 1;
            while (true) {

              //for ((i) in 0..Place.MAX_PLACES-1) 
              for (var i0: Int = 0; i0 < Place.MAX_PLACES; i0++) 
                 (N(i0) as GrowableRail[UVDSQuad]!).setLength(0);

              val start = count(l-1);
              val end = count(l)-1;
              val flength =  end-start+1;
              //x10.io.Console.ERR.println("flength" + flength);
              val LSize = Place.MAX_PLACES == 1 ? flength: world.sum(flength);
              //x10.io.Console.ERR.println("LSize " + LSize);
              //world.barrier();
              if (LSize == 0) break;

                L.setLength(0);
              //for ((i) in 0..flength-1) {
             count.add(count(l));
              for (var i1: Int = start; i1 <=end; i1++) {
                val vertex = s(i1);

                val lo = pg_here.numEdges(vertex);
                val hi = pg_here.numEdges(vertex+1)-1;

                //for ((k) in  lo..hi) 
                  for (var k: Int = lo; k <= hi; k++) {

                  if (((pg_here.weight(k) & 7) ==0) && filter) continue;

                  val neighbor = pg_here.endV(k); //Rename neighbor to v; vertext to u
                  val owner = pg.owner_id(neighbor);
                  //if (neighbor == vertex) continue;
                  if (owner == here.id) { 
                    if (d(neighbor) == -1) {
                      s.add(neighbor);
                      count(l+1) ++;
                      d(neighbor) = d(vertex) + 1;
                      sig(neighbor) = sig(vertex);
                       (pred(neighbor) as GrowableRail[types.VERT_T]!).add(vertex);
                    }  else if (d(neighbor) == d(vertex) + 1) {
                           sig(neighbor) += sig(vertex);
                          (pred(neighbor) as GrowableRail[types.VERT_T]!).add(vertex);
                    }

                  } else { (N(owner) as GrowableRail[UVDSQuad]!).add(UVDSQuad(vertex, neighbor, d(vertex), sig(vertex))); }

                }
              }

	      if (Place.MAX_PLACES > 1) world.alltoallv[UVDSQuad](N,L);
              //world.barrier();

                   
             //for ((i) in 0..L.length()-1) {
             for (var i2: Int = 0; i2 < L.length(); i2++) {
               val t = L(i2);
               val w = t.second;
               val v = t.first;
              if(d(w) == -1) {
               s.add(w);
                count(l+1)++;
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

        

               //bfs.stop();
               //x10.io.Console.ERR.println("back");
               world.barrier();
                //back.start();
               compute_back_alltoall(p, world);
               /* val c_length = count.length();
                val N2 = N_cent();
                val L2 = L_cent();

      
                for (var l0: Int = c_length-1; l0 > 1; l0--) {
                 val start = count(l0-1);
                 val end = count(l0)-1;

                 val tsum = Place.MAX_PLACES == 1 ? end-start+1 : world.sum(end-start+1);
                 //world.barrier();
                 if (tsum == 0)  continue;

                 //for ((k) in 0..Place.MAX_PLACES-1)  
                  for (var k: Int = 0; k < Place.MAX_PLACES; k++) 
                   (N2(k) as GrowableRail[VDSTriplet]!).setLength(0);

                 //for ((j) in start..end) 
                 L2.setLength(0);
                 for (var j: Int = start; j <= end; j++) {
                 val w = s(j);

                val pred_w  = pred(w);
                val pred_length = (pred(w) as GrowableRail[types.VERT_T]!).length();
                //for ((k) in  0..pred_length-1) 
                  for (var k: Int = 0; k < pred_length; k++) {
                  val v = (pred_w as GrowableRail[types.VERT_T]!)(k);
                  val del_w = del(w);
                  val sig_w = sig(w);
                  val owner = pg.owner_id(v);
                  if (owner == here.id) {
                     del(v) = del(v) + sig(v)*(((1.0+del_w) as double)/sig_w);
                  }
                  else N2(owner).add(VDSTriplet(v, del_w, sig_w));

                 }
                }

                 if (Place.MAX_PLACES > 1) world.alltoallv[VDSTriplet](N2, L2);

                  //for ((k) in 0..L2.length()-1) 
                  for (var k: Int = 0; k <L2.length(); k++) {
                    val v = L2(k).first;
                    val del_w = L2(k).second;
                    val sig_w = L2(k).third;
                    del(v) = del(v) + sig(v)*(((1.0+del_w) as double)/sig_w);
                 }
                 //for ((j) in start..end) 
                  for (var j: Int = start; j <= end; j++) {
                   val w = s(j);
                   bc(w) += del(w);
                 }
               }*/ 
                //back.stop();
               /* for ((k) in pg_here.vertices)  */
                /* for (var k: Int = pg_here.vertices.min(0); k <= pg_here.vertices.max(0); k++) {
                        pred(k).setLength(0);
                        del(k) = 0;
               //         sig(k) = 0;
                        d(k) = -1;
               } */

               /* for ((k) in 0..s.length()-1)  */
                 for (var k: Int = 0; k < s.length(); k++) {
                        //x10.io.Console.ERR.println("w: " + s(k));
                        pred(s(k)).setLength(0);
                        del(s(k)) = 0.0d;
                        sig(s(k)) = 0.0d;
                        d(s(k)) = -1;
               }

            world.barrier();     
          } 
               kernel4.stop();

        }

     }

      public global def dump() {
         //write the output to a file for verification
          val unique = Dist.makeUnique();
          finish for((p) in unique) {
               finish async (Place.places(p)) {
               val bc = BC() as Array[types.DOUBLE_T](1)!;
                for ((a) in bc.region) {
                 x10.io.Console.OUT.println("BC: " + a + " " + bc(a));
                }
             }
         }

    }
}
	
