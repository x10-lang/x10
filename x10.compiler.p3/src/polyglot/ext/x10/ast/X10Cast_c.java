/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package polyglot.ext.x10.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import polyglot.ast.Call;
import polyglot.ast.Cast_c;
import polyglot.ast.Expr;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.Precedence;
import polyglot.ast.TypeNode;
import polyglot.ext.x10.types.BoxType;
import polyglot.ext.x10.types.TypeProperty;
import polyglot.ext.x10.types.X10ClassType;
import polyglot.ext.x10.types.X10Type;
import polyglot.ext.x10.types.X10TypeMixin;
import polyglot.ext.x10.types.X10TypeSystem;
import polyglot.main.Report;
import polyglot.types.ClassDef;
import polyglot.types.MethodInstance;
import polyglot.types.ObjectType;
import polyglot.types.SemanticException;
import polyglot.types.Name;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.ContextVisitor;
import x10.constraint.XConstraint;

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
    protected ConversionType convert;

    public X10Cast_c(Position pos, TypeNode castType, Expr expr, ConversionType convert) {
	super(pos, castType, expr);
	this.convert = convert;
    }
    
    public boolean isConversion() {
	return convert != ConversionType.COERCION;
    }
    
    public ConversionType conversionType() {
        return convert;
    }
    
    public X10Cast conversionType(ConversionType convert) {
        X10Cast_c n = (X10Cast_c) copy();
        n.convert = convert;
        return n;
    }
    
    @Override
    public Precedence precedence() {
        switch (convert) {
        case COERCION:
        case PRIMITIVE:
        case TRUNCATION:
            return Precedence.CAST;
        case BOXING:
        case UNBOXING:
        case CALL:
            return Precedence.LITERAL;
        default:
            return Precedence.UNKNOWN;
        }
    }
    
    public Node typeCheck(ContextVisitor tc) throws SemanticException {
	Type toType = castType.type();
	Type fromType = expr.type();
	X10TypeSystem ts = (X10TypeSystem) tc.typeSystem();
	X10NodeFactory nf = (X10NodeFactory) tc.nodeFactory();

	// Handle some boxing coercions stepwise.
	if (convert != ConversionType.COERCION) {
            // Box[V] to Box[W]
            // -->
            // Box[V] to V to W to Box[W]
	    if (ts.isBox(fromType) && ts.isBox(toType)) {
	        // Reboxing a value.  Unbox and then box again.
	        BoxType fromBox = (BoxType) X10TypeMixin.baseType(fromType);
	        BoxType toBox = (BoxType) X10TypeMixin.baseType(toType);
	        Position p = position();
	        X10Cast unboxed = (X10Cast) nf.X10Cast(p, nf.CanonicalTypeNode(p, fromBox.arg()), expr, true).disambiguate(tc).typeCheck(tc).checkConstants(tc);
	        X10Cast coerced = (X10Cast) nf.X10Cast(p, nf.CanonicalTypeNode(p, toBox.arg()), unboxed, true).disambiguate(tc).typeCheck(tc).checkConstants(tc);
	        return this.expr(coerced).typeCheck(tc);
	    }

            // V to Box[W]
	    // -->
	    // V to W to Box[W]
	    if (ts.isValueType(fromType) && ts.isBox(toType)) {
	        // Boxing a value type.  First coerce to the boxed type, then box.
	        BoxType toBox = (BoxType) X10TypeMixin.baseType(toType);
	        Position p = position();
	        if (! ts.typeEquals(fromType, toBox.arg())) {
	            X10Cast coerced = (X10Cast) nf.X10Cast(p, nf.CanonicalTypeNode(p, toBox.arg()), expr, true).disambiguate(tc).typeCheck(tc).checkConstants(tc);
	            return this.expr(coerced).typeCheck(tc);
	        }
	    }
	    
	    // Box[V] to W
	    // -->
	    // Box[V] to V to W
	    if (ts.isValueType(toType) && ts.isBox(fromType)) {
	        // Unboxing a value type.  First coerce to the boxed type, then unbox.
	        BoxType fromBox = (BoxType) X10TypeMixin.baseType(fromType);
	        Position p = position();
	        if (! ts.typeEquals(fromBox.arg(), toType)) {
	            X10Cast unboxed = (X10Cast) nf.X10Cast(p, nf.CanonicalTypeNode(p, fromBox.arg()), expr, true).disambiguate(tc).typeCheck(tc).checkConstants(tc);
	            return this.expr(unboxed).typeCheck(tc);
	        }
	    }
	    
	    // V to R (Box[V] <: R)
	    // -->
	    // V to Box[V] to R
	    if (ts.isValueType(fromType)) {
	        Type boxOfFrom = null;
	        
	        // Don't create a box of an anonymous class
	        if (fromType instanceof X10ClassType) {
	            X10ClassType fromCT = (X10ClassType) fromType;
	            if (fromCT.isAnonymous()) {
	                if (fromCT.superClass() != null)
	                    boxOfFrom = ts.boxOf(Types.ref(fromCT.superClass()));
	                else if (fromCT.interfaces().size() > 0)
	                    boxOfFrom = ts.boxOf(Types.ref(fromCT.interfaces().get(0)));
	                else
	                    boxOfFrom = ts.Object();
	            }
	        }

	        if (boxOfFrom == null)
	            boxOfFrom = ts.boxOf(Types.ref(fromType));

	        if (ts.isSubtype(boxOfFrom, toType)) {
	            if (! ts.typeEquals(boxOfFrom, toType)) {
	                Position p = position();
	                X10Cast boxed = (X10Cast) nf.X10Cast(p, nf.CanonicalTypeNode(p, boxOfFrom), expr, true).disambiguate(tc).typeCheck(tc).checkConstants(tc);
	                return this.expr(boxed).typeCheck(tc);
	            }
	        }
	    }
	    
	    List<Type> c = ts.converterChain(fromType, toType);
	    if (c.size() > 2) {
	        Position p = position();
	        Expr n = expr;
	        for (int i = 1; i < c.size()-1; i++) {
	            n = (X10Cast) nf.X10Cast(p, nf.CanonicalTypeNode(p, c.get(i)), n, true).disambiguate(tc).typeCheck(tc).checkConstants(tc);
	        }
	        return this.expr(n).typeCheck(tc);
	    }
	}
	
	boolean coercionAllowed = false;
	ConversionType conversionType = convert == ConversionType.COERCION ? convert : ConversionType.UNKNOWN_CONVERSION;
	
	MethodInstance converter = null;
	
	if (ts.isBoolean(fromType) && ts.isBoolean(toType)) {
	    conversionType = ConversionType.PRIMITIVE;
	}
	else if (ts.isNumeric(fromType) && ts.isNumeric(toType)) {
	    conversionType = ConversionType.PRIMITIVE;
	}
	else if (ts.isValueType(fromType) && ts.typeEquals(ts.boxOf(Types.ref(fromType)), toType)) {
	    conversionType = ConversionType.BOXING;
	}
	else if (ts.isValueType(toType) && ts.typeEquals(fromType, ts.boxOf(Types.ref(toType)))) {
	    conversionType = ConversionType.UNBOXING;
	}
	else if (ts.isValueType(toType) && ts.descendsFrom(fromType, toType)) {
	    conversionType = ConversionType.TRUNCATION;
	}
	else if (ts.isSubtype(fromType, toType)) {
	    conversionType = ConversionType.COERCION;
	}
	else {
	    // Can convert if there is a static method toType.$convert(fromType)
	    if (converter == null) {
	        try {
	            MethodInstance mi = ts.findMethod(toType, ts.MethodMatcher(toType, Name.make("$convert"), Collections.singletonList(fromType), false), (ClassDef) null);
	            if (mi.flags().isStatic() && X10TypeMixin.baseType(mi.returnType()).isSubtype(X10TypeMixin.baseType(toType))) {
	                converter = mi;
	            }
	        }
	        catch (SemanticException e) {
	        }
	    }

	    if (converter != null) {
	        conversionType = ConversionType.CALL;
	    }
	}
	
	coercionAllowed = true;
	
	if (! ts.isCastValid(fromType, toType))
	    coercionAllowed = false;

	if (coercionAllowed)
	    if (conversionType == ConversionType.UNKNOWN_CONVERSION) 
	        conversionType = ConversionType.COERCION;
	
	if (convert != ConversionType.COERCION) {
	    if (conversionType == ConversionType.UNKNOWN_CONVERSION) {
		throw new SemanticException("Cannot convert expression of type \"" 
		                            + fromType + "\" to type \"" 
		                            + toType + "\".",
		                            position());
	    }
	    
	    if (converter != null) {
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
		    n1.convert = ConversionType.COERCION;
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
		                            + toType + "\"" + (conversionType != ConversionType.UNKNOWN_CONVERSION ? "; use an explicit conversion with 'to'." : "."),
		                            position());
	    }
	    
	    conversionType = ConversionType.COERCION;
	}

	X10Cast_c n = (X10Cast_c) copy();
	n.convert = conversionType;
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
	return expr.toString() + (isConversion() ? " to " : " as ") + castType.toString();
    }
    
    @Override
    public List<Type> throwTypes(TypeSystem ts) {
	// 'e as T' and 'e to T' can throw ClassCastException
        return super.throwTypes(ts);
    }
}
