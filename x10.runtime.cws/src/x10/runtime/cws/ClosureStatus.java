/*
 *
 * (C) Copyright IBM Corporation 2007
 *
 *  This file is part of X10 runtime. It is 
 *  governed by the licence under which 
 *  X10 is released.
 *
 */
package x10.runtime.cws;

/**
 * The status associated with a closure.
 * @author vj 5/18/2007
 *
 */
public enum ClosureStatus {

		RUNNING,
		SUSPENDED,
		RETURNING,
		READY,
		ABORTING,
		PASSTHROUGH
	}


