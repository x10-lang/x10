/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.types;

import polyglot.ext.x10.types.constr.Constraint;
import polyglot.types.ConstructorInstance;
import polyglot.types.Type;

public interface X10ConstructorInstance extends ConstructorInstance, X10ProcedureInstance {
	/**
	 * Return the depclause associated with the returntype of the constructor.
	 * @return
	 */
	Constraint constraint();
	
	/**
	 * Set the returnType associated with this constructor instance.
	 * @param t
	 */
	void setReturnType(X10Type t);
	
	X10Type returnType();
	
	/** Return the constraint on properties, if any,
	 * obtained from the return type of the call
	 * to super in the body of this constructor. 
	 * @return
	 */
	Constraint supClause();
	
	/** Set the constraint on properties obtained from
	 * the return type of the call to super. Set when typechecking
	 * the code in the body of the constructor for which this is the constructor instance.
	 * 
	 * @param c
	 */
	void setSupClause(Constraint c);

}
