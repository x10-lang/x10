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

package x10.types;

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
