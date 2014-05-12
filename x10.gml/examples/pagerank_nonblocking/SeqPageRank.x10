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
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;

/**
 * Sequential implementation of page rank algorithm based on GML dense/sparse 
 * matrix.
 */
public class SeqPageRank {
	val rowG:Long;
	val colP:Long;

	val iterations:Long;
	val nzDensity:Double;
	val alpha:Double= 0.85;

	// Input and output data
	public val G:DenseMatrix(rowG, rowG);
	public val P:DenseMatrix(rowG, colP);
	public val E:DenseMatrix(rowG, colP);
	public val U:DenseMatrix(colP, rowG);
	
	// Temp data and matrix 
	val GP:DenseMatrix(rowG, colP);
	val UP:DenseMatrix(colP, colP);
	
	public def this(g:DenseMatrix, p:DenseMatrix, 
					e:DenseMatrix, u:DenseMatrix, 
					it:Long, nz:Double) {
		rowG = g.M;  colP = p.N;
		G = g as DenseMatrix(rowG, rowG); 
		P = p as DenseMatrix(rowG, colP); 
		E = e as DenseMatrix(rowG, colP); 
		U = u as DenseMatrix(colP, rowG);

		iterations = it;
		nzDensity = nz;

		GP = DenseMatrix.make(rowG, colP);
		UP = DenseMatrix.make(colP, colP);
	}
	
	public def run():DenseMatrix {
		Debug.flushln("Start sequential PageRank");
		for (1..iterations) {
			GP.mult(G, P).scale(alpha);			
			P.mult(E, UP.mult(U, P)).scale(1-alpha).cellAdd(GP);
		}
		Debug.flushln("Sequential PageRank completes");
		return P;
	}
}
