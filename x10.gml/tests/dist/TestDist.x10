/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;

import x10.matrix.block.Grid;
import x10.matrix.dist.DistMatrix;

public class TestDist extends x10Test {
	public val nzp:Double;
	public val M:Long;
	public val N:Long;
	public val K:Long;	

    public def this(args:Rail[String]) {
		M = args.size > 0 ? Long.parse(args(0)):4;
		nzp = args.size > 1 ?Double.parse(args(1)):0.5;
		N = args.size > 2 ? Long.parse(args(2)):M+1;
		K = args.size > 3 ? Long.parse(args(3)):M+2;	
	}

	public def run():Boolean {
		var status:Boolean=true;
		val grid = Grid.make(M, N, Place.numPlaces());
		val m1  = DistMatrix.makeDense(grid);
		m1.initRandom();
		val m2 = m1.clone(); 
 		val m3 = m1 - m2;
 		status= m3.equals(0.0);
// 		
// 		val m4 = DistMatrix.makeDense(grid);
// 		val m5 = m4.clone();
// 		val m6 = (m4 + m5) - (m5 + m4);
// 		status &= m6.equals(0.0);
		
		return status;
	}

    public static def main(args:Rail[String]) {
		new TestDist(args).execute();
	}
} 
