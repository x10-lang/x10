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

public interface X10ConstructorInstance extends ConstructorInstance {
	Constraint depClause();
	void depClause(Constraint c);

}
