/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.matrix.comm;

/**
 * This struct is used to pack information of sparse matrix from the source place.
 * The data fields are transferred to the local place where the remote array copy uses 
 * to start copy data from the remote place to here.
 *
 */
protected struct DenseRemoteSourceInfo {
	public val valbuf:GlobalRail[Double];
	public val offset:Long;
	public val length:Long;
	
	public def this(vlu:GlobalRail[Double], off:Long, len:Long) {
		valbuf = vlu;	
		offset = off; 
		length = len;
	}

	public def this(vlu:Rail[Double], off:Long, len:Long) {
		valbuf = new GlobalRail[Double](vlu as Rail[Double]{self!=null});	
		offset = off; 
		length = len;
	}
}
