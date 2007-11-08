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
package polyglot.ext.x10.types;

import java.util.List;

import polyglot.ast.Formal;
import polyglot.types.MethodInstance;
import polyglot.types.SemanticException;

/**
 * @author vj
 *
 */
public interface X10MethodInstance extends MethodInstance, X10ProcedureInstance {
	/**
	 * Does this method instance represent a method on a java class?
	 * @return
	 */
	boolean isJavaMethod();
	
	/**
	 * Is this a method in a safe class, or a method marked as safe?
	 * @return
	 */
	boolean isSafe();
	
	/**
	 * Return an instance of this, specialized with (a) any references
	 * to this in the dependent type of the result replaced by
	 * selfVar of thisType or an EQV of thisType (with propagation) 
	 * (b) any references to this in the dependent
	 * type T of an argument replaced by selfVar of thisType or an EQV
	 * at T, with no propagation.
	 * @param thisType
	 * @return
	 * @throws SemanticException 
	 */
	X10MethodInstance instantiateForThis(X10Type thisType) throws SemanticException;

}
