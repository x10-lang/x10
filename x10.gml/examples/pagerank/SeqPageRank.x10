/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2016.
 */

import x10.matrix.DenseMatrix;
import x10.matrix.Vector;

/**
 * Sequential implementation of page rank algorithm based on GML dense/sparse 
 * matrix.
 */
public class SeqPageRank {
	val rowG:Long;

    public val tolerance:Float;
	val iterations:Long;
	val alpha = 0.85 as ElemType;

	// Input and output data
	public val G:DenseMatrix(rowG, rowG);
	public val P:Vector(rowG);
	//public val U:Vector(rowG);
	
	// Temp data and matrix 
	val GP:Vector(rowG);
	
	public def this(g:DenseMatrix, it:Long, tolerance:Float) {
		rowG = g.M;
		G = g as DenseMatrix(rowG, rowG); 
		P = Vector.make(rowG, 1.0);
		//U = u as Vector(rowG);

		iterations = it;
        this.tolerance = tolerance;

		GP = Vector.make(rowG);
	}
	
	public def run():Vector {
		var iter:Long = 0;
        var maxDelta:ElemType = 1.0f as ElemType;
        while ((iterations > 0 && iter < iterations) 
            || (iterations <= 0 && maxDelta > tolerance)) {
			GP.mult(G, P).scale(alpha);
            //val teleport = U.dot(P) * (1-alpha); // personalized
            val teleport = 1.0f-alpha;
            GP.cellAdd(teleport);
            maxDelta = 0.0 as ElemType;
            for (i in 0..(GP.M-1)) {
                maxDelta = Math.max(maxDelta, Math.abs(GP(i) - P(i)));
            }
            GP.copyTo(P);
            iter++;
		}
		return P;
	}
}
