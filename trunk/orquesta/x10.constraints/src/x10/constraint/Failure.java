/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/**
 * 
 */
package x10.constraint;


/**
 * @author vj
 *
 */
public class Failure extends Exception {
	Object etc;
	
	/**
	 * 
	 */
	public Failure() {
		super();
	}

	/**
	 * @param cause
	 */
	public Failure(Throwable cause) {
		super(cause);
	}

	/**
	 * @param etc
	 */
	public Failure(Object etc) {
		this.etc = etc;
	}

	/**
	 * @param m
	 */
	public Failure(String m) {
		super(m);
	}

	/**
	 * @param m
	 * @param cause
	 */
	public Failure(String m, Throwable cause) {
		super(m, cause);
	}

	/**
	 * @param m
	 * @param etc
	 */
	public Failure(String m, Object etc) {
		super(m);
		this.etc = etc;
	}

}
