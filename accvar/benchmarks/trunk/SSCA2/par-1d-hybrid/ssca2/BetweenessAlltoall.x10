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

class BetweenessAlltoall extends BetweenessCentrality {
	
	
	public static type UVDSQuad= Quad[VERT_T, VERT_T, Types.LONG_T, Types.DOUBLE_T];
	
	public static type VERT_T=Types.VERT_T;
	
	public static NUM_PLACES = Place.MAX_PLACES;
	
	
	// Not required for async version
	global val N_cent: PlaceLocalHandle[Rail[GrowableRail[Pair[VERT_T, Double]]]];
	global val L_cent: PlaceLocalHandle[GrowableRail[Pair[VERT_T,Double]]];
	global val N_bfs: PlaceLocalHandle[Rail[GrowableRail[UVDSQuad]]];
	global val L_bfs: PlaceLocalHandle[GrowableRail[UVDSQuad]];
	
	
	//timers and statistics
	global val alltoallv_timer: PTimer;
	global val kernel4: PTimer;
	global val L_size: PStat;
	global val S_size: PStat;
	global val L_size_avg: PStat;
	global val S_size_avg: PStat;
	global val Levels: PStat;
	
	public def this(pg: Defs.pGraph,  val filter: Boolean, val SCALE: Types.INT_T, val K4Approx: Types.INT_T) {
		
		super(pg, filter, SCALE, K4Approx);
		
		val unique = Dist.makeUnique(); 
		val ntraversals = 1 << K4Approx; 
		alltoallv_timer = PTimer.make("alltoallv");
		kernel4 = PTimer.make("kernel4");
		L_size = PStat.make("L_size", (a: Double, b:Double)=>Math.max(a,b));
		S_size = PStat.make("S_size", (a: Double, b:Double)=>Math.max(a,b));
		L_size_avg = PStat.make("L_size_avg", (a: Double, b:Double)=>a + b / ntraversals);
		S_size_avg = PStat.make("S_size_avg", (a: Double, b:Double)=>a + b /  ntraversals);
		Levels = PStat.make("Levels", (a: Double, b:Double)=>a + b / ntraversals);
		
		
		N_cent = PlaceLocalHandle.make[Rail[GrowableRail[Pair[VERT_T, Double]]]](unique, ()=>Rail.make[GrowableRail[Pair[VERT_T, Double]]](NUM_PLACES
				, (n:Int)=>new GrowableRail[Pair[VERT_T, Double]](0)));
		L_cent =  PlaceLocalHandle.make[GrowableRail[Pair[VERT_T, Double]]](unique, ()=>new GrowableRail[Pair[VERT_T,Double]](0));
		
		N_bfs = PlaceLocalHandle.make[Rail[GrowableRail[UVDSQuad]]](unique, ()=>Rail.make[GrowableRail[UVDSQuad]](NUM_PLACES,(n:Int)=>new GrowableRail[UVDSQuad](0)));
		L_bfs = PlaceLocalHandle.make[GrowableRail[UVDSQuad]](unique, ()=>new GrowableRail[UVDSQuad](10));
		
	}
	
	
	public global def compute_bc (val source: VERT_T, world:Comm!, ntheads: Int): void {
		val N = N_bfs(); 
		val L = L_bfs();
		
		val pred = Pred() ;
		val del = Del();
		val sig = Sig();
		val d = D();
		val bc = BC();
		val s = S();
		val count = Count();
		val pg_here = pg.restrict_here() as Defs.pGraphLocal!;
		
		//x10.io.Console.ERR.println("point 1");
		
		
		var lsize: Long = 0l;
		var ssize: Long = 0l;
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
						if (neighbor == vertex) continue;
						
						//x10.io.Console.ERR.println("phase " + l + " " + neighbor + " " + d(neighbor) + " " + sig(neighbor) );
						
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
							(N(owner) as GrowableRail[UVDSQuad]!).add(UVDSQuad(vertex, neighbor, d(vertex), sig(vertex))); 
						}
					}
			}
			
			alltoallv_timer.start();
			if (NUM_PLACES > 1) world.alltoallv[UVDSQuad](N,L);
			alltoallv_timer.stop();
			
			lsize += L.length();
			L_size.event_count(L.length());
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
			ssize += count(l+1) - count(l) + 1;
			S_size.event_count(count(l+1) - count(l) + 1);
			l++; 
		} 
		Levels.event_count(l-1);
		//x10.io.Console.OUT.println(lsize);
		L_size_avg.event_count(lsize/(l-1));
		S_size_avg.event_count(ssize/(l-1));
		
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
}


