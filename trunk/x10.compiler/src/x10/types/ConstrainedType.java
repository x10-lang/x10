/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.types;

import java.util.List;

import polyglot.ast.Expr;
import polyglot.types.ObjectType;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.StructType;
import polyglot.types.Type;
import x10.types.constraints.CConstraint;

public interface ConstrainedType extends ObjectType, X10NamedType, Proto, X10Struct, X10ThisVar {
	Ref<? extends Type> baseType();
	ConstrainedType baseType(Ref<? extends Type> baseType);
	
	Ref<CConstraint> constraint();
	ConstrainedType constraint(Ref<CConstraint> constraint);
	
	CConstraint getRealXClause();
	
	void checkRealClause() throws SemanticException;
	
	
	void ensureSelfBound();
}
