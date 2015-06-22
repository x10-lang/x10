/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2014.
 */

import x10.matrix.util.Debug;
import x10.matrix.DenseMatrix;
import x10.matrix.Vector;

/**
 * Sequential implementation of page rank algorithm based on GML dense/sparse 
 * matrix.
 */
public class SeqPageRank {
	val rowG:Long;

	val iterations:Long;
	val alpha = 0.85 as ElemType;

	// Input and output data
	public val G:DenseMatrix(rowG, rowG);
	public val P:Vector(rowG);
	public val U:Vector(rowG);
	
	// Temp data and matrix 
	val GP:Vector(rowG);
	
	public def this(g:DenseMatrix, p:Vector, 
					u:Vector, 
					it:Long) {
		rowG = g.M;
		G = g as DenseMatrix(rowG, rowG); 
		P = p as Vector(rowG); 
		U = u as Vector(rowG);

		iterations = it;

		GP = Vector.make(rowG);
	}
	
	public def run():Vector {
		for (1..iterations) {
			GP.mult(G, P).scale(alpha);
            val teleport = U.dot(P) * (1-alpha);
			P.map(GP, (x:Double)=>x+teleport);
		}
		return P;
	}
}
