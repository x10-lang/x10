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

package x10.matrix.comm;

import x10.matrix.ElemType;
/**
 * This struct is used to pack information of sparse matrix from the destination place.
 * The data fields are transferred to the local place where the remote array copy uses 
 * to start copy data from here to the remote place. 
 *
 */
protected struct DenseRemoteDestInfo {
	public val valbuf:GlobalRail[ElemType];
	public val offset:Long;
	
	public def this(vlu:GlobalRail[ElemType], off:Long) {
		valbuf = vlu;	
		offset = off; 
	}

	public def this(vlu:Rail[ElemType], off:Long) {
		valbuf = new GlobalRail[ElemType](vlu as Rail[ElemType]{self!=null});	
		offset = off; 
	}
}
