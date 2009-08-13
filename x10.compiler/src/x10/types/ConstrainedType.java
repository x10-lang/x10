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
import x10.constraint.XConstraint;
import x10.constraint.XConstraint_c;

public interface ConstrainedType extends ObjectType, X10NamedType, X10ThisVar {
	Ref<? extends Type> baseType();
	ConstrainedType baseType(Ref<? extends Type> baseType);
	
	Ref<XConstraint> constraint();
	ConstrainedType constraint(Ref<XConstraint> constraint);
	
	XConstraint getRealXClause();
	//void setRealXClause(XConstraint c, SemanticException error);
	
	void checkRealClause() throws SemanticException;
	
	//XConstraint realX();
}
