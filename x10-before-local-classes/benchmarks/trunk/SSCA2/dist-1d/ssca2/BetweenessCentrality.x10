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

import x10.compiler.NativeCPPInclude;

 @NativeCPPInclude("sprng.h") {}


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

class LinearArray[T] {

  val size: Int;
   val data: Rail[T];
   val ptr: Long;
   val dummy: T;

   public static def make[T] (r: Region(1), init:(Int)=>T)  = new LinearArray[T](r, init);

   public def this (r: Region(1)){
     size = r.size();
     data = Rail.make[T](size);
     val offset = r.min(0);
     ptr = 0;
     dummy = data(0);
     { @Native ("c++", "FMGL(ptr) = (x10_long) (FMGL(data)->raw() - offset);") {} }
   }

   public def this (r: Region(1), init:(Int)=>T) {
     size = r.size();
     data = Rail.make[T](size, init);
     val offset = r.min(0);
     ptr = 0;
     dummy = data(0);
     { @Native ("c++", "FMGL(ptr) = (x10_long) (FMGL(data)->raw() - offset);") {} }
   }

    @Native("c++", "((#2*) (#0)->FMGL(ptr))[#1]")
   public native safe def apply(i: Int): T  ;

/* {
     val a: T= dummy;
     {@Native ("c++", "a = ((FMGL(T)*) FMGL(ptr))[i];"){}}
      return a;
   } */

    @Native("c++", "((#3*) (#0)->FMGL(ptr))[#2] = #1;")
   public native safe def set(elem:T, i: Int): T;
/* {
     {@Native ("c++", "((FMGL(T)*) FMGL(ptr))[i] = elem;"){}}
     return elem;
   } */
};



class BetweenessCentrality {

      
class WrapRail[T] implements Indexable[Int,T], Settable[Int,T] {
   public var length: Int;
   private val data: Rail[T];

  public def this(size: Int) {
    length = 0;
    data= Rail.make[T](size);
   } 
   public def reset() {
     length = 0;
   } 

  public def add(val elem: T) {
     data(length++) = elem;
   }

  public def apply(i: Int) {
     return data(i);
  }

  public def set(elem: T, i: Int) {
      data(i) = elem;
      return data(i);
  }

};


     public static type UVDSQuad= Quad[VERT_T, VERT_T, types.LONG_T, types.DOUBLE_T];

     public static type VERT_T=types.VERT_T;

     public static NUM_PLACES = Place.MAX_PLACES;



     //PlaceLocal storge for program variables for each vertex
     global val BC: PlaceLocalHandle[LinearArray[types.DOUBLE_T]];
     global val Sig: PlaceLocalHandle[LinearArray[types.DOUBLE_T]];
     global  val Del: PlaceLocalHandle[LinearArray[types.DOUBLE_T]];
     global val D: PlaceLocalHandle[LinearArray[types.LONG_T]];
     global val Pred: PlaceLocalHandle[LinearArray[WrapRail[VERT_T]]];
     global val Frontier: PlaceLocalHandle[Rail[GrowableRail[VERT_T]]];

     // alltoall buffers , N_*: Input, L_*: output 
     // Not required for async version
     global val N_cent: PlaceLocalHandle[Rail[GrowableRail[Pair[VERT_T, Double]]]];
     global val L_cent: PlaceLocalHandle[GrowableRail[Pair[VERT_T,Double]]];
     global val N_bfs: PlaceLocalHandle[Rail[GrowableRail[UVDSQuad]]];
     global val L_bfs: PlaceLocalHandle[GrowableRail[UVDSQuad]];

     //Stack computed during BFS and used during Back Propagation
     global val S: PlaceLocalHandle[WrapRail[VERT_T]];
     global val Count: PlaceLocalHandle[WrapRail[types.INT_T]];

     //switch to use the async version
     global val use_async: Boolean;
     global val filter: Boolean;
    
 
     
     // the graph for which we need to compute the BC.
     global val pg: defs.pGraph;


     global val alltoallv_timer: PTimer;
     global val kernel4: PTimer;

     public  def computeInDegree(val pg: defs.pGraph) {

           val unique = Dist.makeUnique();
           val InDegree = PlaceLocalHandle.make[Rail[types.LONG_T]](unique, ()=>Rail.make[types.LONG_T](pg.restrict_here().vertices.size(), (i:Int)=>0));

         kernel4.start();
         finish ateach((p) in unique) {
            val world = Comm.WORLD();
            val pg_here = pg.restrict_here();
            val tmp = world.usort[VERT_T](pg_here.endV, (v:VERT_T)=>pg.owner_id(v));
            val inDegree = InDegree();
            val voffset = pg.restrict_here().vertices.min(0);
            for (var i : Int = 0 ; i < tmp.length(); i++) {
              inDegree(tmp(i)-voffset) += 1;
            } 
            /* for (var i : Int = inDegree.region.min(0); i <= inDegree.region.max(0); i++ ) 
               x10.io.Console.ERR.println((inDegree as Array[types.LONG_T](1)!)(i)); */
         }
         kernel4.stop();

         return InDegree;

      }

     /**
       * Constructor
       */
     public def this(pg: defs.pGraph, val use_async: Boolean, val filter: Boolean, val SCALE: types.INT_T) {



             alltoallv_timer = PTimer.make("alltoallv");
             kernel4 = PTimer.make("kernel4");

             val unique = Dist.makeUnique();


             val InDegree = computeInDegree(pg);

             BC = PlaceLocalHandle.make[LinearArray[types.DOUBLE_T]](unique, ()=>LinearArray.make[types.DOUBLE_T](pg.restrict_here().vertices, (i:Int)=>0d));
             Sig = PlaceLocalHandle.make[LinearArray[types.DOUBLE_T]](unique, ()=>LinearArray.make[types.DOUBLE_T](pg.restrict_here().vertices, (i:Int)=>0d));
             Del = PlaceLocalHandle.make[LinearArray[types.DOUBLE_T]](unique, ()=>LinearArray.make[types.DOUBLE_T](pg.restrict_here().vertices, (i:Int)=>0d));
             D = PlaceLocalHandle.make[LinearArray[types.LONG_T]](unique, ()=>LinearArray.make[types.LONG_T](pg.restrict_here().vertices, (i:Int)=>-1));
             Pred = PlaceLocalHandle.make[LinearArray[WrapRail[VERT_T]]](unique, ()=> LinearArray.make[WrapRail[VERT_T]](pg.restrict_here().vertices, (i:Int)=>new WrapRail[VERT_T]((InDegree()(i)))));

             Frontier = PlaceLocalHandle.make[Rail[GrowableRail[VERT_T]]](unique, ()=>Rail.make[GrowableRail[VERT_T]](2, (i:Int)=>new GrowableRail[VERT_T]()) as Rail[GrowableRail[VERT_T]]!);

             S = PlaceLocalHandle.make[WrapRail[VERT_T]](unique, ()=>new WrapRail[VERT_T](pg.chunkSize));
             Count = PlaceLocalHandle.make[WrapRail[types.INT_T]](unique, ()=>new WrapRail[types.INT_T](50));

             if (!use_async) {
                N_cent = PlaceLocalHandle.make[Rail[GrowableRail[Pair[VERT_T, Double]]]](unique, ()=>Rail.make[GrowableRail[Pair[VERT_T, Double]]](NUM_PLACES, (n:Int)=>new GrowableRail[Pair[VERT_T, Double]](0)));
               L_cent =  PlaceLocalHandle.make[GrowableRail[Pair[VERT_T, Double]]](unique, ()=>new GrowableRail[Pair[VERT_T,Double]](0));

                N_bfs = PlaceLocalHandle.make[Rail[GrowableRail[UVDSQuad]]](unique, ()=>Rail.make[GrowableRail[UVDSQuad]](NUM_PLACES,(n:Int)=>new GrowableRail[UVDSQuad](0)));
                L_bfs = PlaceLocalHandle.make[GrowableRail[UVDSQuad]](unique, ()=>new GrowableRail[UVDSQuad](10));

             }else {
                N_cent = PlaceLocalHandle.make[Rail[GrowableRail[Pair[VERT_T, Double]]]](unique, ()=>null);
               L_cent =  PlaceLocalHandle.make[GrowableRail[Pair[VERT_T, Double]]](unique, ()=>null);

                N_bfs = PlaceLocalHandle.make[Rail[GrowableRail[UVDSQuad]]](unique, ()=>null);
                L_bfs = PlaceLocalHandle.make[GrowableRail[UVDSQuad]](unique, ()=>null);

            }


             this.filter = filter;
             this.use_async = use_async;
             this.pg = pg;
     }


      private global def compute_bc_alltoall (val source: VERT_T, world:Comm!) {
          val N = N_bfs(); 
          val L = L_bfs();

           val pred = Pred() ;
               val del = Del();
               val sig = Sig();
               val d = D();
               val bc = BC();
               val s = S();
               val count = Count();
               val pg_here = pg.restrict_here() as defs.pGraphLocal!;

              //x10.io.Console.ERR.println("point 1");


           val src_owner = pg.owner_id(source);
           if (src_owner ==here.id)  {s.add(source);count.add(0); count.add(1); d(source ) = 0; sig(source) = 1;}
            else { count.add(0); count.add(0);}

            val endV = pg_here.endV;
            val numEdges = pg_here.numEdges;
            val weight = pg_here.weight;

            var l: Int = 1;
            while (true) {

              for (var i: Int = 0; i < NUM_PLACES; i++) 
                 (N(i) as GrowableRail[UVDSQuad]!).setLength(0);

              val start = count(l-1);
              val end = count(l)-1;
              val flength =  end-start+1;
              //x10.io.Console.ERR.println("flength" + flength);
              val LSize = NUM_PLACES == 1 ? flength: world.sum(flength);
              //x10.io.Console.ERR.println("LSize " + LSize);
              //world.barrier();
              if (LSize == 0) break;

                L.setLength(0);
             count.add(count(l));
              for (var i: Int = start; i <=end; i++) {
                val vertex = s(i);

                val lo = numEdges(vertex);
                val hi = numEdges(vertex+1)-1;

                //for ((k) in  lo..hi) 
                  for (var k: Int = lo; k <= hi; k++) {

                  if (((weight(k) & 7) ==0) && filter) continue;

                  val neighbor = endV(k); //Rename neighbor to v; vertext to u
                  if (NUM_PLACES == 1){
                    if (d(neighbor) == -1) {
                      s.add(neighbor);
                      count(l+1)++;
                      d(neighbor) = d(vertex) + 1;
                      sig(neighbor) = sig(vertex);
                       (pred(neighbor) as WrapRail[VERT_T]!).add(vertex);
                    }  else if (d(neighbor) == d(vertex) + 1) {
                           sig(neighbor) += sig(vertex);
                          (pred(neighbor) as WrapRail[VERT_T]!).add(vertex);
                    }

                  } else { 
                    val owner = pg.owner_id(neighbor);   
                    (N(owner) as GrowableRail[UVDSQuad]!).add(UVDSQuad(vertex, neighbor, d(vertex), sig(vertex))); }
                }
              }

                 alltoallv_timer.start();
	      if (NUM_PLACES > 1) world.alltoallv[UVDSQuad](N,L);
                 alltoallv_timer.stop();

              //world.barrier();

                   
             for (var i: Int = 0; i < L.length(); i++) {
               val t = L(i);
               val w = t.second;
               val v = t.first;
              if(d(w) == -1) {
               s.add(w);
                count(l+1)++;
               d(w)  = t.third + 1;
               sig(w) = t.fourth;
               (pred(w) as WrapRail[VERT_T]!).add(v);

             } else if (d(w) ==  t.third+1) {
                  sig(w) += t.fourth;
                 (pred(w) as WrapRail[VERT_T]!).add(v);
             }
             } 
             l++; 
            } 

               val c_length = count.length;

                val N2 = N_cent();
                val L2 = L_cent();

                for (var level: Int = c_length-1; level > 1; level--) {
                 val start = count(level-1);
                 val end = count(level)-1;

                 val tsum = NUM_PLACES == 1 ? end-start+1 : world.sum(end-start+1);
                 //world.barrier();
                 if (tsum == 0)  continue;

                 //for ((k) in 0..NUM_PLACES-1)  
                  for (var k: Int = 0; k < NUM_PLACES; k++) 
                   N2(k).setLength(0);

                 //for ((j) in start..end) 
                 L2.setLength(0);
                 for (var j: Int = start; j <= end; j++) {
                 val w = s(j);

                val pred_w  = pred(w);
                val pred_length = (pred_w as WrapRail[VERT_T]!).length;
                  for (var k: Int = 0; k < pred_length; k++) {
                  val v = pred_w(k);
                  val del_w = del(w);
                  val sig_w = sig(w);
                  if (NUM_PLACES == 1) {
                     del(v) = del(v) + sig(v)*(((1.0+del_w) as double)/sig_w);
                  }
                  else{
                       val owner = pg.owner_id(v);
                       N2(owner).add(Pair[VERT_T, Double](v, ((1.0+del_w) as double) / sig_w));
                   }

                 }
                }

                 alltoallv_timer.start();
                 if (NUM_PLACES > 1) world.alltoallv[Pair[VERT_T, Double]](N2, L2);
                 alltoallv_timer.stop();

                  for (var k: Int = 0; k <L2.length(); k++) {
                    val v = L2(k).first;
                    val factor = L2(k).second;
                    del(v) = del(v) + sig(v)*factor;
                 }
                  for (var j: Int = start; j <= end; j++) {
                   val w = s(j);
                   bc(w) += del(w);
                 }
          }
      }


      /**
        * compute the Betweneess Centrality Metric 
        */ 
      public global def compute (val GLOBALS: runtime_consts) {

          val  bfs = PTimer.make("bfs");
          val  back = PTimer.make("back");
 
          val seed =  2387;
          val stream = genScaleData.init_sprng_wrapper(0, 1, seed);

 
           val n = pg.N;
           val chunkSize = pg.N / NUM_PLACES;
           val unique = Dist.makeUnique();

           // TODO: parallelize this
             val Srcs = Rail.make[types.VERT_T](n, (i:Int)=>i);
             for (var i: Int=0; i < n; i++) {
                 val j = (n*genScaleData.sprng_wrapper(stream)) as types.VERT_T;
                 val tmp = Srcs(i);
                 Srcs(i) = Srcs(j);
                 Srcs(j) = tmp;
             }
               
           //x10.io.Console.ERR.println("inside bc..");

           val numV = 1<<GLOBALS.K4Approx;

           finish ateach((p) in unique) {
               val world: Comm! = Comm.WORLD();

               //x10.io.Console.ERR.println("world");

               /* place locals accessed by asyncs */
               val pred = Pred();
               val del = Del();
               val sig = Sig();
               val d = D() ;
               val bc = BC();
               val s = S();
               val count = Count();
               val pg_here = pg.restrict_here() as defs.pGraphLocal!;
      
               var num_traversals: Int = 0;

               //x10.io.Console.ERR.println("point 1");
                 for (var k: Int = pg_here.vertices.region.min(0); k <= pg_here.vertices.region.max(0); k++) {
                        pred(k).reset();
                        bc(k) = 0.0d;
                        sig(k) = 0.0d;
                        del(k) = 0.0d;
                        d(k) = -1;
                 }
               kernel4.start();

           for (var i: Int = 0; i < n; i++ ) {


               //val new_vertices = NewVertices(here.id);
               //val a = world.sum(pg.owner_id(i) ==here.id ? new_vertices as rail[types.UVPair]!)(i % chunkSize) 
               //val startVertex = world.sum(pg.owner_id(i) ==here.id ? (new_vertices as Rail[types.UVPair]!)(i % chunkSize).second : 0); //actually a broadcast
               val startVertex = world.sum(here.id == 0 ? Srcs(i) : 0);//actually a broadcast
               val outDegree = world.sum(pg.owner_id(startVertex) ==here.id ? pg_here.numEdges(startVertex+1) - pg_here.numEdges(startVertex) : 0); 
         
               //x10.io.Console.ERR.println("start: " + startVertex);
               if (outDegree==0) continue;

               num_traversals++;

               if (num_traversals == numV + 1) break;

               //x10.io.Console.ERR.println("main " + i);

               s.reset();
               count.reset();

               compute_bc_alltoall(startVertex, world);

                 for (var k: Int = 0; k < s.length; k++) {
                        //x10.io.Console.ERR.println("w: " + s(k));
                        pred(s(k)).reset();
                        del(s(k)) = 0.0d;
                        sig(s(k)) = 0.0d;
                        d(s(k)) = -1;
               }

            world.barrier();     
          } 
               kernel4.stop();
        }
         val elapsed_time = kernel4.elapsed()*1e-9;
          x10.io.Console.OUT.println( "TEPS score for Kernel 4 is " +   ((7 * (GLOBALS.N as Long) * (1<<GLOBALS.K4Approx) as Long) as double)/elapsed_time + "\n");

     }

      public global def dump() {
         //write the output to a file for verification
          val unique = Dist.makeUnique();
          val pg_here =pg.restrict_here();
          finish for((p) in unique) {
               finish async (Place.places(p)) {
               val bc = BC();
                for ((a) in pg.restrict_here().vertices) {
                 x10.io.Console.ERR.println("BC: " + a + " " + bc(a));
                }
             }
         }

    }
}
	
