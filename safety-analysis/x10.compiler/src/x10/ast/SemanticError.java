/**
 * 
 */
/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.ast;

import polyglot.types.SemanticException;
import polyglot.util.Position;

/**
 * SemanticErrors should result in an error message being displayed to the user. Some SemanticExceptions,
 * such as MissingDependencyException are caught internally by various goals. SemanticErrors should not be
 * caughty by the goal execution mechanism and should not be prevented from terminating compilation.
 * 
 * @author vj
 *
 */
public class SemanticError extends SemanticException {

	/**
	 * 
	 */
	public SemanticError() {
		super();
		
	}

	/**
	 * @param cause
	 */
	public SemanticError(Throwable cause) {
		super(cause);
		
	}

	/**
	 * @param position
	 */
	public SemanticError(Position position) {
		super(position);
		
	}

	/**
	 * @param m
	 */
	public SemanticError(String m) {
		super(m);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param m
	 * @param cause
	 */
	public SemanticError(String m, Throwable cause) {
		super(m, cause);
		
	}

	/**
	 * @param m
	 * @param position
	 */
	public SemanticError(String m, Position position) {
		super(m, position);
		
	}

}
