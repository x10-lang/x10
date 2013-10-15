/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */
package x10.matrix.block;

import x10.compiler.Inline;
import x10.util.StringBuilder;

import x10.matrix.Debug;
import x10.matrix.MathTool;

/**
 * This class represents meta-information of how matrix is partitioned into symmetric block matrices.
 */
public type SymGrid(bM:Long)=SymGrid{self.numRowBlocks==bM, self.numColBlocks==bM};
public type SymGrid(m:Long,bM:Long)=SymGrid{self.M==m,self.N==m,self.numRowBlocks==bM,self.numColBlocks==self.numRowBlocks};

public class SymGrid extends Grid{self.M==self.N,self.numRowBlocks==self.numColBlocks} {
	/**
	 * Create instance of symmetric partitioning of n x n matrix in nbks x nbks blocks 
	 */
	public def this(n:Long, nblks:Long) {
		super(n, nblks);
	}
	
	public def this(n:Long, Bs:Rail[Long]) {
		super(n, Bs);
	}

	public def equals(that:SymGrid) : Boolean {
		if (this == that) return true;
		if (!likeMe(that)) return false;

		return (match(this.rowBs, that.rowBs));
	}
	
	public static def isSymmetric(g:Grid):Boolean {
		var ret:Boolean = true;
		if (g.M!=g.N) return false;
		if (g.numRowBlocks!=g.numColBlocks) return false;
		
		for (var i:Long=0; i<g.numRowBlocks&&ret; i++)
			ret &=(g.rowBs(i)==g.colBs(i));
		return ret;
	}
	

	public def toString() : String {
		val strbld = new StringBuilder();
		strbld.add("Symmetric Partition "+M+" rows into "+numRowBlocks+" [");
		for (var i:Long=0; i<numColBlocks; i++, strbld.add(","))
			strbld.add(rowBs(i));
		strbld.add("]\n");
		return strbld.toString();
	}
}
