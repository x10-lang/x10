/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types;

import polyglot.types.ConstructorDef;
import polyglot.types.ConstructorInstance;
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
	
	/** Return the constraint on properties, if any,
	 * obtained from the return type of the call
	 * to super in the body of this constructor. 
	 * @return
	 */
	XConstraint supClause();

}
