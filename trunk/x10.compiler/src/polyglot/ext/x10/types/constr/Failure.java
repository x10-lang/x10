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
package polyglot.ext.x10.types.constr;

import polyglot.types.SemanticException;
import polyglot.util.Position;

/**
 * @author vj
 *
 */
public class Failure extends SemanticException {

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
	 * @param position
	 */
	public Failure(Position position) {
		super(position);
	}

	/**
	 * @param m
	 */
	public Failure(String m) {
		super(m);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param m
	 * @param cause
	 */
	public Failure(String m, Throwable cause) {
		super(m, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param m
	 * @param position
	 */
	public Failure(String m, Position position) {
		super(m, position);
		// TODO Auto-generated constructor stub
	}

}
