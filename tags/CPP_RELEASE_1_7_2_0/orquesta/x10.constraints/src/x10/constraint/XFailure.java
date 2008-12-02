/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.constraint;


/**
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
