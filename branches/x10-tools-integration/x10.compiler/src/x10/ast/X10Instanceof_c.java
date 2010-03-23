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

package x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Instanceof_c;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.errors.Errors;
import x10.types.X10TypeMixin;
import x10.types.X10TypeSystem;

/**
 * Represent java instanceof operation.
 * expression instanceof TargetType
 * This class is compliant with dependent type constraint.
 * If a dynamic check is needed then some code is generated to check declared 
 * type constraint are met by the instance.
 *
 * @author vcave
 *
 */
public class X10Instanceof_c extends Instanceof_c implements X10Instanceof, X10CastInfo{

	public X10Instanceof_c(Position pos, Expr expr, TypeNode compareType) {
    	super(pos,expr,compareType);
	}
 
    /** Type check the expression. */
    public Node typeCheck(ContextVisitor tc) throws SemanticException {
        X10Instanceof_c n = (X10Instanceof_c) copy();
        Type toType = n.compareType().type();
        Type fromType = n.expr().type();

        X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();

        if (! xts.isCastValid(fromType, toType, tc.context())) {
            throw new Errors.InstanceofError(fromType, toType, position());
        }
        
        X10TypeMixin.checkMissingParameters(toType);

        return n.type(xts.Boolean());
	}


    public TypeNode getTypeNode() {
    	return this.compareType();
}
}
