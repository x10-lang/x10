/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

package x10.matrix.comm;

/**
 * This struct is used to pack information of sparse matrix from the destination place.
 * The data fields are captured by the local place where the remote array copy uses 
 * to start copy data from here to the remote place. 
 *
 */
protected struct SparseRemoteDestInfo {
	public val idxbuf:RemoteArray[Int];
	public val valbuf:RemoteArray[Double];
	public val offset:Int;
	
	public def this(idx:RemoteArray[Int], vlu:RemoteArray[Double], off:Int) {
		idxbuf = idx; 
		valbuf = vlu;	
		offset = off; 
	}

	public def this(idx:Array[Int], vlu:Array[Double], off:Int) {
		idxbuf = new RemoteArray[Int](idx as Array[Int]{self!=null}); 
		valbuf = new RemoteArray[Double](vlu as Array[Double]{self!=null});	
		offset = off; 
	}
}