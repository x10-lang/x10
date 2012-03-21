/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */

package pagerank;

import x10.io.Console;
import x10.util.Timer;
//
import x10.matrix.Debug;
//
import x10.matrix.Matrix;
import x10.matrix.DenseMatrix;
import x10.matrix.blas.DenseMatrixBLAS;
//

/**
 * 
 * Sequential implementation of page rank algorithm based on GML dense/sparse 
 * matrix.
 */
public class SeqPageRank {

	val rowG:Int;
	val colP:Int;

	public val iteration:Int;
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
					it:Int, nz:Double) {
		rowG = g.M;  colP = p.N;
		//
		G = g as DenseMatrix(rowG, rowG); 
		P = p as DenseMatrix(rowG, colP); 
		E = e as DenseMatrix(rowG, colP); 
		U = u as DenseMatrix(colP, rowG);
		//
		iteration = it;
		nzDensity = nz;
		//
		GP = DenseMatrix.make(rowG, colP);
		UP = DenseMatrix.make(colP, colP);
	}
	
	public def run():DenseMatrix {
		Debug.flushln("Start sequential PageRank");
		for (var i:Int=0; i<iteration; i++) {
			GP.mult(G, P).scale(alpha);			
			//DenseMultBLAS.comp(G, P, GP);
			//GP.scale(alpha);
			//
			P.mult(E, UP.mult(U, P)).scale(1-alpha).cellAdd(GP);
			//DenseMultBLAS.comp(U, P, UP);
			//DenseMultBLAS.comp(E, UP, P);
			//P.scale(1-alpha);
			//P.cellAdd(GP);
		}
		Debug.flushln("Sequential PageRank completes");
		return P;
	}
		
}