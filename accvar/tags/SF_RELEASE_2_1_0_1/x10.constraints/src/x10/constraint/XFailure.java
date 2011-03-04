/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.constraint;


/**
 * A representation of failure. Thrown when the constraint becomes inconsistent.
 * @author vj
 *
 */
public class XFailure extends Exception {
	Object etc;
	
	/**
	 * 
	 */
	public XFailure() {
		super();
	}

	/**
	 * @param cause
	 */
	public XFailure(Throwable cause) {
		super(cause);
	}

	/**
	 * @param etc
	 */
	public XFailure(Object etc) {
		this.etc = etc;
	}

	/**
	 * @param m
	 */
	public XFailure(String m) {
		super(m);
	}

	/**
	 * @param m
	 * @param cause
	 */
	public XFailure(String m, Throwable cause) {
		super(m, cause);
	}

	/**
	 * @param m
	 * @param etc
	 */
	public XFailure(String m, Object etc) {
		super(m);
		this.etc = etc;
	}

}
