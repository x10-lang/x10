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
	*       del (v) = del(v) + sig(v)/sig(w)*(1+del(w)).
	* 
	* BetweenessCentrality of a vertex, bc(v), is simply obtained by computing the running sum of del(v).
	* 
	* 
	* @author: Ganesh Bikshandi
	*/
	
	
	abstract class BetweenessCentrality {
		
		
		public static type UVDSQuad= Quad[VERT_T, VERT_T, Types.LONG_T, Types.DOUBLE_T];
		
		public static type VERT_T=Types.VERT_T;
		
		public static NUM_PLACES = Place.MAX_PLACES;
		
		
		//PlaceLocal storge for program variables for each vertex
		global val BC: PlaceLocalHandle[LinearArray[Types.DOUBLE_T]];
		global val Sig: PlaceLocalHandle[LinearArray[Types.DOUBLE_T]];
		global  val Del: PlaceLocalHandle[LinearArray[Types.DOUBLE_T]];
		global val D: PlaceLocalHandle[LinearArray[Types.LONG_T]];
		global val Pred: PlaceLocalHandle[LinearArray[WrapRail[VERT_T]]];
		global val Frontier: PlaceLocalHandle[GrowableRail[VERT_T]];
		
		//Stack computed during BFS and used during Back Propagation
		global val S: PlaceLocalHandle[WrapRail[VERT_T]];
		global val Count: PlaceLocalHandle[WrapRail[Types.INT_T]];
		
		//switch to use the async version
		global val filter: Boolean;
		
		
		// the graph for which we need to compute the BC.
		global val pg: Defs.pGraph;
		
		global val kernel4: PTimer;
		
		
		public  def computeInDegree(val pg: Defs.pGraph) {
			
			val unique = Dist.makeUnique();
			val InDegree = PlaceLocalHandle.make[Rail[Types.LONG_T]](unique, ()=>Rail.make[Types.LONG_T](pg.restrict_here().vertices.size(), (i:Int)=>0));
			
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
					 * x10.io.Console.ERR.println((inDegree as Array[Types.LONG_T](1)!)(i)); */
			}
			kernel4.stop();
			
			return InDegree;
			
		}
		
		/**
		 * Constructor
		 */
		 public def this(pg: Defs.pGraph,  val filter: Boolean, val SCALE: Types.INT_T, val K4Approx: Types.INT_T) {
			
			
			val ntraversals = 1 << K4Approx; 
			kernel4 = PTimer.make("kernel4");
			
			val unique = Dist.makeUnique();
			
			
			val InDegree = computeInDegree(pg);
			
			BC = PlaceLocalHandle.make[LinearArray[Types.DOUBLE_T]](unique, ()=>LinearArray.make[Types.DOUBLE_T](pg.restrict_here().vertices, (i:Int)=>0d));
			Sig = PlaceLocalHandle.make[LinearArray[Types.DOUBLE_T]](unique, ()=>LinearArray.make[Types.DOUBLE_T](pg.restrict_here().vertices, (i:Int)=>0d));
			Del = PlaceLocalHandle.make[LinearArray[Types.DOUBLE_T]](unique, ()=>LinearArray.make[Types.DOUBLE_T](pg.restrict_here().vertices, (i:Int)=>0d));
			D = PlaceLocalHandle.make[LinearArray[Types.LONG_T]](unique, ()=>LinearArray.make[Types.LONG_T](pg.restrict_here().vertices, (i:Int)=>-1));
			Pred = PlaceLocalHandle.make[LinearArray[WrapRail[VERT_T]]](unique, ()=> LinearArray.make[WrapRail[VERT_T]](pg.restrict_here().vertices, (i:Int)=>new WrapRail[VERT_T]((InDegree()(i)))));
			
			Frontier = PlaceLocalHandle.make[GrowableRail[VERT_T]](unique, ()=>new GrowableRail[VERT_T]());
			
			S = PlaceLocalHandle.make[WrapRail[VERT_T]](unique, ()=>new WrapRail[VERT_T](pg.chunkSize));
			Count = PlaceLocalHandle.make[WrapRail[Types.INT_T]](unique, ()=>new WrapRail[Types.INT_T](50));
			
			
			
			this.filter = filter;
			this.pg = pg;
		}
		
		
		public abstract  global def compute_bc (source:Types.VERT_T, world: Comm!, nthreads: Int) : void;
		
		
		/**
		 * compute the Betweneess Centrality Metric 
		 */ 
		 public global def compute (val GLOBALS: Consts) {
			
			val  bfs = PTimer.make("bfs");
			val  back = PTimer.make("back");
			
			val seed =  2387;
			val stream = GenScaleDataSeq.init_sprng_wrapper(0, 1, seed);
			
			
			val n = pg.N;
			val chunkSize = pg.N / NUM_PLACES;
			val unique = Dist.makeUnique();
			
			// TODO: parallelize this
			val Srcs = Rail.make[Types.VERT_T](n, (i:Int)=>i);
			for (var i: Int=0; i < n; i++) {
				val j = (n*GenScaleDataSeq.sprng_wrapper(stream)) as Types.VERT_T;
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
			val pg_here = pg.restrict_here() as Defs.pGraphLocal!;
			
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
				//val a = world.sum(pg.owner_id(i) ==here.id ? new_vertices as rail[Types.UVPair]!)(i % chunkSize) 
				//val startVertex = world.sum(pg.owner_id(i) ==here.id ? (new_vertices as Rail[Types.UVPair]!)(i % chunkSize).second : 0); //actually a broadcast
				val startVertex = world.sum(here.id == 0 ? Srcs(i) : 0);//actually a broadcast
				val outDegree = world.sum(pg.owner_id(startVertex) ==here.id ? pg_here.numEdges(startVertex+1) - pg_here.numEdges(startVertex) : 0); 
				
				//x10.io.Console.ERR.println("start: " + startVertex);
				if (outDegree==0) continue;
				
				num_traversals++;
				
				if (num_traversals == numV + 1) break;
				
				//x10.io.Console.ERR.println("main " + i);
				
				s.reset();
				count.reset();
				
				compute_bc(startVertex, world, GLOBALS.nthreads);
				
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
	
