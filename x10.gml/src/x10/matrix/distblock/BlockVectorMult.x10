/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 */

package x10.matrix.distblock;

import x10.matrix.Vector;
import x10.matrix.VectorMult;
import x10.matrix.block.Grid;

/**
 * This class performs matrix-vector multiplication for block-partitioned matrices.
 */
public class BlockVectorMult  { 

	public static def comp(aSet:BlockSet, bV:Vector, cV:Vector, plus:Boolean):Vector(cV) =
		comp(aSet, bV, 0, cV, 0, plus);
	
	public static def comp(aSet:BlockSet, 
			bV:Vector, offsetB:Long, 
			cV:Vector, offsetC:Long, plus:Boolean):Vector(cV) {
		val itr = aSet.iterator();
		val grid = aSet.getGrid();
        var addToResult:Boolean = plus; // if false, first VectorMult will overwrite cV
		while (itr.hasNext()) {
			val ablk = itr.next();
			val rowOff = ablk.rowOffset - offsetC;
			val colOff = ablk.colOffset - offsetB;
			val mA = ablk.getMatrix();
			VectorMult.comp(mA, bV, colOff, cV, rowOff, addToResult);
            addToResult = true;
		}
		return cV;
	}
	
	public static def comp(bV:Vector, aSet:BlockSet, cV:Vector, plus:Boolean):Vector(cV) =
		comp(bV, 0, aSet, cV, 0, plus);
	
	public static def comp(bV:Vector, offsetB:Long, 
			aSet:BlockSet, cV:Vector, offsetC:Long, plus:Boolean):Vector(cV) {
		val itr = aSet.iterator();
		val grid = aSet.getGrid();
        var addToResult:Boolean = plus; // if false, first VectorMult will overwrite cV
		while (itr.hasNext()) {
			val ablk = itr.next();
			val rowOff = ablk.rowOffset - offsetB;
			val colOff = ablk.colOffset - offsetC;
			val mA = ablk.getMatrix();
			VectorMult.comp(bV, rowOff, mA, cV, colOff, addToResult);
            addToResult = true;
		}
		return cV;
	}
}
