/*
 *
 * (C) Copyright IBM Corporation 2006-2008
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
	 * Is this a method in a safe class, or a method marked as safe?
	 * @return
	 */
	boolean isSafe();
	
	// Use X10MethodInstance as the return type rather than X10MethodInstance
	MethodInstance returnType(Type returnType);
	
	/** Type to use in a RHS context rather than the return type. */
	Type rightType();
	
	XTerm body();
	X10MethodInstance body(XTerm body);

}
