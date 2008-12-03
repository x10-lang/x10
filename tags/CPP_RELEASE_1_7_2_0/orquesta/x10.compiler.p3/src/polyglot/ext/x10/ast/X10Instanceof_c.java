/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import polyglot.ast.Expr;
import polyglot.ast.Instanceof_c;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;

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

	protected boolean toTypeNullable = false;
	protected boolean notNullRequired = false;
	
	public X10Instanceof_c(Position pos, Expr expr, TypeNode compareType) {
    	super(pos,expr,compareType);
	}
 
    /** Type check the expression. */
    public Node typeCheck(ContextVisitor tc) throws SemanticException {
        X10Instanceof_c n = (X10Instanceof_c) copy();
        Type toType = n.compareType().type();
        Type fromType = n.expr().type();

        n.toTypeNullable = false;
        n.notNullRequired = false;
        
        if (! tc.typeSystem().isCastValid(fromType, toType)) {
            throw new SemanticException(
                      "Left operand of \"instanceof\", " + fromType + ", must be castable to "
                      + "the right operand " + toType + ".");
        }
        
        X10TypeSystem xts = (X10TypeSystem) tc.typeSystem();
        
        n.toTypeNullable = !xts.isValueType(n.compareType.type());

        // is conversion from a nullable type to a non nullable one.        
        // not Null is required if toType is notNullable or toType is nullable but has constraints
        n.notNullRequired = !n.toTypeNullable;

        return n.type(tc.typeSystem().Boolean());
	}

    public boolean isDepTypeCheckingNeeded() {
    	return false;
    }
    
	public boolean isPrimitiveCast() {
		return false;
	}

	/**
	 * Always return false as if we are dealing with a non nullable
	 * then the (null instanceof T) code generated will return false.   
	 */
	public boolean notNullRequired() {
		return this.notNullRequired;
	}

	public boolean isToTypeNullable() {
		return this.toTypeNullable;
	}

    public TypeNode getTypeNode() {
    	return this.compareType();
}
}
