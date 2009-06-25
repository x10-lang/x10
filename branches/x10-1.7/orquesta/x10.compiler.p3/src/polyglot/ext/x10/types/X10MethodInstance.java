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

import polyglot.types.MethodDef;
import polyglot.types.MethodInstance;
import polyglot.types.Type;
import x10.constraint.XTerm;

/**
 * @author vj
 *
 */
public interface X10MethodInstance extends MethodInstance, X10ProcedureInstance<MethodDef>, X10Use<X10MethodDef> {
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
	
	// Use X10MethodInstance as the return type rather than X10MethodInstance
	MethodInstance returnType(Type returnType);
	
	XTerm body();
	X10MethodInstance body(XTerm body);

}
