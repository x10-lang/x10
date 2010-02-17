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

import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
import polyglot.types.Ref;
import polyglot.types.Type;
import x10.constraint.XConstraint;

public interface X10ConstructorInstance extends ConstructorInstance, X10ProcedureInstance<ConstructorDef>, X10Use<X10ConstructorDef> {
	/**
	 * Return the depclause associated with the returntype of the constructor.
	 * @return
	 */
	XConstraint constraint();
	
	Type returnType();
	ConstructorInstance returnType(Type retType);
	Ref<? extends Type> returnTypeRef();
	ConstructorInstance returnTypeRef(Ref<? extends Type> returnType);
	
	/** Return the constraint on properties, if any,
	 * obtained from the return type of the call
	 * to super in the body of this constructor. 
	 * @return
	 */
	XConstraint supClause();
	
	X10ConstructorInstance toRefCI();

}
