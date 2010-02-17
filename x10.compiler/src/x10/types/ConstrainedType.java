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
