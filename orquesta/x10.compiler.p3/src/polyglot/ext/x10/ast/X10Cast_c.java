/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.Collections;
import java.util.List;

import polyglot.ast.Cast_c;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.main.Report;
import polyglot.types.ClassDef;
import polyglot.types.MethodInstance;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.TypeChecker;

/**
 * Represent java cast operation.
 * (CastType) expression
 * This class is compliant with dependent type constraint.
 * If a dynamic cast is needed, then some code is generated to check 
 * instance's value, field, etc... are valid against declared type constraint.
 *
 * @author vcave
 *
 */
public class X10Cast_c extends Cast_c implements X10Cast, X10CastInfo {
    protected boolean primitiveType = false;
    protected boolean notNullRequired = false;
    protected boolean toTypeNullable = false;
    protected boolean convert;

    public X10Cast_c(Position pos, TypeNode castType, Expr expr, boolean convert) {
	super(pos, castType, expr);
	this.convert = convert;
    }
    
    public boolean convert() {
	return convert;
    }
    
    public Node typeCheck(TypeChecker tc) throws SemanticException {
	X10Cast_c n = (X10Cast_c) copy();

	Type toType = n.castType.type();
	Type fromType = n.expr.type();
	X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();

	n.primitiveType = false;
	n.toTypeNullable = false;
	n.notNullRequired = false;

	Expr result = n.type(toType);
	boolean conversionAllowed = false;
	
	if (ts.isBoolean(fromType) && ts.isBoolean(toType))
	    conversionAllowed = true;
	else if (ts.isNumeric(fromType) && ts.isNumeric(toType))
	    conversionAllowed = true;
	else if (ts.isValueType(fromType) && ts.isSubtype(ts.boxOf(Types.ref(fromType)), toType))
	    conversionAllowed = true;
	else if (ts.isValueType(toType) && ts.isSubtype(fromType, ts.boxOf(Types.ref(toType))))
	    conversionAllowed = true;
	else {
	    // Can convert if there is a static method toType.$convert(fromType)
	    try {
		MethodInstance mi = ts.findMethod(toType, ts.MethodMatcher(toType, "$convert", Collections.singletonList(fromType)), (ClassDef) null);
		if (mi.flags().isStatic() && mi.returnType().isSubtype(toType))
		    conversionAllowed = true;
	    }
	    catch (SemanticException e) {
	    }
	}

	if (convert) {
	    if (! conversionAllowed) {
		throw new SemanticException("Cannot convert expression of type \"" 
		                            + fromType + "\" to type \"" 
		                            + toType + "\".",
		                            position());
	    }
	}
	else {
	    if (ts.isValueType(fromType) && ts.isValueType(toType)) {
		if (! ts.typeBaseEquals(fromType, toType)) {
		    throw new SemanticException("Cannot coerce expression of type \"" 
		                                + fromType + "\" to type \"" 
		                                + toType + "\"" + (conversionAllowed ? "; use an explicit conversion with 'to'." : "."),
		                                position());
		}
	    }
	    
	    if (ts.isValueType(fromType) && ts.isBox(toType))
		throw new SemanticException("Cannot coerce expression of type \"" 
		                            + fromType + "\" to type \"" 
		                            + toType + "\"" + (conversionAllowed ? "; use an explicit conversion with 'to'." : "."),
		                            position());

	    if (ts.isBox(fromType) && ts.isValueType(toType))
		throw new SemanticException("Cannot coerce expression of type \"" 
		                            + fromType + "\" to type \"" 
		                            + toType + "\"" + (conversionAllowed ? "; use an explicit conversion with 'to'." : "."),
		                            position());

	    // check java cast is valid and dependent type constraint are meet
	    if (! ts.isCastValid(fromType, toType)) {
		throw new SemanticException("Cannot coerce expression of type \"" 
		                            + fromType + "\" to type \"" 
		                            + toType + "\".",
		                            position());
	    }
	}

	return n.type(toType);
    }

    public boolean isDepTypeCheckingNeeded() {
	return false;
    }

    public boolean notNullRequired() {
	return notNullRequired;
    }

    public boolean isPrimitiveCast() {
	return primitiveType;
    }

    public boolean isToTypeNullable() {
	return this.toTypeNullable;
    }

    public TypeNode getTypeNode() {
	return (TypeNode) this.castType().copy();
    }

    public void setToTypeNullable(boolean b) {
	this.toTypeNullable = b;
    }

    public void setPrimitiveCast(boolean b) {
	this.primitiveType = b;
    }

    public void setNotNullRequired(boolean b) {
	this.notNullRequired = b;
    }

    public String toString() {
	return expr.toString() + (convert ? " to " : " as ") + castType.toString();
    }
    
    @Override
    public List<Type> throwTypes(TypeSystem ts) {
	// 'e as T' can throw ClassCastException
	// 'e to T' cannot throw an exception
	if (convert)
	    return Collections.EMPTY_LIST;
        return super.throwTypes(ts);
    }
}
