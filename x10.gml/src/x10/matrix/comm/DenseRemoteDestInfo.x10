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
 * The data fields are transferred to the local place where the remote array copy uses 
 * to start copy data from here to the remote place. 
 *
 */
protected struct DenseRemoteDestInfo {
	public val valbuf:RemoteArray[Double];
	public val offset:Int;
	
	public def this(vlu:RemoteArray[Double], off:Int) {
		valbuf = vlu;	
		offset = off; 
	}

	public def this(vlu:Array[Double], off:Int) {
		valbuf = new RemoteArray[Double](vlu as Array[Double]{self!=null});	
		offset = off; 
	}
}