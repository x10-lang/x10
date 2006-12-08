package polyglot.ext.x10.types;

import polyglot.ext.x10.types.constr.Constraint;
import polyglot.types.ConstructorInstance;

public interface X10ConstructorInstance extends ConstructorInstance {
	Constraint depClause();
	void depClause(Constraint c);

}
