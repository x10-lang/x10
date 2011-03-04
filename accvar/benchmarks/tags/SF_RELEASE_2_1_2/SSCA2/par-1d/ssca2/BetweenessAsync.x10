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

class BetweenessAsync extends BetweenessCentrality { 
	
	
	public def this(pg: Defs.pGraph, val filter: Boolean, val SCALE: Types.INT_T, val K4Approx: Types.INT_T) {
		super(pg, filter, SCALE, K4Approx);
	}
	
	public global def compute_bc( source:Types.VERT_T, world:Comm!,  nthreads: Types.INT_T): void {
		
		val pred = Pred() as LinearArray[WrapRail[Types.VERT_T]](1)!;
		val del = Del() as LinearArray[Types.DOUBLE_T](1)!;
		val bc = BC() as LinearArray[Types.DOUBLE_T](1)!;
		val d = D() as LinearArray[Types.LONG_T](1)!;
		val sig = Sig() as LinearArray[Types.DOUBLE_T](1)!;
		
		val pg_here = pg.restrict_here();
		val s = S();
		val  count = Count();
		
		//x10.io.Console.ERR.println("inside bfs_async");
		
		val src_owner = pg.owner_id(source); 
		if (src_owner ==here.id)  {s.add(source);count.add(0); count.add(1); d(source ) = 0; sig(source) = 1;}
		else { count.add(0); count.add(0);}
		
		
		val endV = pg_here.endV;
		val numEdges = pg_here.numEdges;
		val weight = pg_here.weight;
		
		
		var phase: Int = 1;
		while (true) {
			
			val start = count(phase-1);
			val end = count(phase)-1;
			
			val flength = end - start + 1;
			val LSize = world.sum(flength);
			//x10.io.Console.ERR.println("size " + flength + " " + LSize);
			//world.barrier();
			if (LSize == 0) break;
			
			count.add(count(phase));
			finish for (var i: Int = start; i <= end; i++) {
				val v = s(i);
				
				val lo = numEdges(v);
				val hi = numEdges(v+1)-1;
				
				
				
				for ((k) in  lo..hi) {
					if (((weight(k) & 7) ==0) && filter) continue;
					val w = endV(k);
					if (w == v) continue;
					val owner = pg.owner(w);
					
					val sig_v = sig(v);
					val d_v = d(v);
					val cur_phase = phase;
					
					//x10.io.Console.ERR.println("phase " + cur_phase + " " + w + " " +  d(w) + " " + sig(w));
					
					async (owner) {
						val d_here = D() as LinearArray[Types.LONG_T](1)!;
						val sig_here = Sig() as LinearArray[Types.DOUBLE_T](1)!;
						val pred_here = Pred() as LinearArray[WrapRail[Types.VERT_T]](1)!;
						val s_here = S();
						val count_here = Count();
						atomic {
							if (d_here(w)==-1) {
								s_here.add(w);
								count_here(cur_phase+1)++;
								d_here(w) = d_v + 1;
								sig_here(w) = sig_v;
								pred_here(w).add(v);
							} else if (d_here(w) == d_v+1) {
								sig_here(w) += sig_v;
								pred_here(w).add(v);
							}
						}
					}
				}
			}
			world.barrier();
			
			phase++; 
		}
		
		
		val c_length = count.length;
		
		for (var l: Int = c_length-1; l > 1; l--) {
			val start = count(l-1);
			val end = count(l)-1;
			
			val tsum = world.sum(end-start+1);
			//world.barrier();
			if (tsum == 0)  continue;
			
			for ((j) in start..end) {
				val w = s(j);
				
				val pred_w  = pred(w);
				val pred_length = (pred(w) as WrapRail[Types.VERT_T]!).length;
				finish for ((k) in  0..pred_length-1) {
					val v = (pred_w as WrapRail[Types.VERT_T]!)(k);
					val del_w = del(w);
					val sig_w = sig(w);
					val owner = pg.owner(v);
					async (owner) {
						val del_here = Del() as LinearArray[Types.DOUBLE_T](1)!;
						val sig_here = Sig() as LinearArray[Types.DOUBLE_T](1)!;
						atomic del_here(v) = del_here(v) + sig_here(v)*(((1.0+del_w) as double)/sig_w);
						//x10.io.Console.ERR.println("del " + i + " " +  v + " " + del(v));
					}
					
				}
				bc(w) += del(w);
			}
		}
		
	}
	
	
}

