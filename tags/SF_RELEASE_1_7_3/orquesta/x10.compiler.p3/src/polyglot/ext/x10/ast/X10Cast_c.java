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

import polyglot.ast.Call;
import polyglot.ast.Cast_c;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.main.Report;
import polyglot.types.ClassDef;
import polyglot.types.MethodInstance;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;

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
    protected boolean convert;

    public X10Cast_c(Position pos, TypeNode castType, Expr expr, boolean convert) {
	super(pos, castType, expr);
	this.convert = convert;
    }
    
    public boolean isConversion() {
	return convert;
    }
    
    public Node typeCheck(ContextVisitor tc) throws SemanticException {
	X10Cast_c n = (X10Cast_c) copy();

	Type toType = n.castType.type();
	Type fromType = n.expr.type();
	X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();

	boolean conversionAllowed = false;
	boolean coercionAllowed = false;
	
	MethodInstance converter = null;
	
	if (ts.isBoolean(fromType) && ts.isBoolean(toType))
	    conversionAllowed = true;
	else if (ts.isNumeric(fromType) && ts.isNumeric(toType))
	    conversionAllowed = true;
	else if (ts.isValueType(fromType) && ts.isSubtype(ts.boxOf(Types.ref(fromType)), toType))
	    conversionAllowed = true;
	else if (ts.isValueType(toType) && ts.isSubtype(fromType, ts.boxOf(Types.ref(toType))))
	    conversionAllowed = true;
	else if (ts.isValueType(toType) && ts.descendsFrom(fromType, toType))
	    conversionAllowed = true;
	else {
	    // Can convert if there is a static method toType.$convert(fromType)
	    try {
		MethodInstance mi = ts.findMethod(toType, ts.MethodMatcher(toType, Name.make("$convert"), Collections.singletonList(fromType)), (ClassDef) null);
		if (mi.flags().isStatic() && X10TypeMixin.baseType(mi.returnType()).isSubtype(X10TypeMixin.baseType(toType))) {
		    conversionAllowed = true;
		    converter = mi;
		}
	    }
	    catch (SemanticException e) {
	    }
	}
	
	coercionAllowed = true;
	
	if (! ts.isCastValid(fromType, toType))
	    coercionAllowed = false;

	if (coercionAllowed)
	    conversionAllowed = true;
	
	if (convert) {
	    if (! conversionAllowed) {
		throw new SemanticException("Cannot convert expression of type \"" 
		                            + fromType + "\" to type \"" 
		                            + toType + "\".",
		                            position());
	    }
	    
	    if (converter != null) {
		NodeFactory nf = tc.nodeFactory();
		MethodInstance mi = converter;
		Position p = position();
		Expr e = expr;
		
		// Do the conversion.
		Call c = nf.Call(p, nf.CanonicalTypeNode(p, toType), nf.Id(p, mi.name()), e);
		c = c.methodInstance(mi);
		c = (Call) c.type(mi.returnType());
		
		// Now, do a coercion if needed to check any additional constraints on the type.
		if (! mi.returnType().isSubtype(toType)) {
		    X10Cast_c n1 = (X10Cast_c) copy();
		    n1.expr = c;
		    n1.convert = false;
		    return n1.type(toType);
		}
		else {
		    return c;
		}
	    }
	}
	else {
	    if (! coercionAllowed) {
		throw new SemanticException("Cannot coerce expression of type \"" 
		                            + fromType + "\" to type \"" 
		                            + toType + "\"" + (conversionAllowed ? "; use an explicit conversion with 'to'." : "."),
		                            position());
	    }
	}

	return n.type(toType);
    }

    public boolean isDepTypeCheckingNeeded() {
	return false;
    }

    public boolean notNullRequired() {
	Type fromType = expr.type();
	Type toType = castType.type();
	X10TypeSystem ts = (X10TypeSystem) fromType.typeSystem();
	return ts.isReferenceType(fromType) && ts.isValueType(toType);
    }

    public boolean isToTypeNullable() {
	Type toType = castType.type();
	X10TypeSystem ts = (X10TypeSystem) toType.typeSystem();
	return ts.isReferenceType(toType) || ts.isInterfaceType(toType);
    }

    public TypeNode getTypeNode() {
	return (TypeNode) this.castType().copy();
    }

    public String toString() {
	return expr.toString() + (convert ? " to " : " as ") + castType.toString();
    }
    
    @Override
    public List<Type> throwTypes(TypeSystem ts) {
	// 'e as T' and 'e to T' can throw ClassCastException
        return super.throwTypes(ts);
    }
}
