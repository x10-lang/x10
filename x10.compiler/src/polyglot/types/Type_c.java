/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * This file was originally derived from the Polyglot extensible compiler framework.
 *
 *  (C) Copyright 2000-2007 Polyglot project group, Cornell University
 *  (C) Copyright IBM Corporation 2007-2015.
 */

package polyglot.types;

import java.util.List;

import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.TypedList;
import x10.types.X10ClassType;


/**
 * Abstract implementation of a <code>Type</code>.  This implements most of
 * the "isa" and "cast" methods of the type and methods which just dispatch to
 * the type system.
 */
public abstract class Type_c extends TypeObject_c implements Type
{
    private static final long serialVersionUID = -876728129439491724L;
    List<Type> annotations;
    /** Used for deserializing types. */
    protected Type_c() { }
    
    /** Creates a new type in the given a TypeSystem. */
    public Type_c(TypeSystem ts) {
        this(ts, null, null);
    }

    /** Creates a new type in the given a TypeSystem at a given position. */
    public Type_c(TypeSystem ts, Position pos, Position errorPos) {
        super(ts, pos, errorPos);
    }

    public List<Type> annotations() {
    	return annotations;
    }
    public Type annotations(List<Type> annotations) {
    	Type_c result = (Type_c) copy();
    	result.annotations= TypedList.copyAndCheck(annotations, Type.class, true);
    	return result;
    }
    /**
     * Return a string into which to translate the type.
     * @param c A resolver in which to lookup this type to determine if
     * the type is unique in the given resolver.
     */
    public abstract String translate(Resolver c);

    public boolean isType() { return true; }
    public boolean isPackage() { return false; }
    public Type toType() { return this; }
    public Package toPackage() { return null; }

    /* To be filled in by subtypes. */
    public boolean isJavaPrimitive() { return false; }
    public boolean isReference() { return false; } 
    public boolean isNull() { return false; }
    public boolean isClass() { return false; }
    public boolean isArray() { return false; }
    
    public final boolean isAny() { return ts.isAny(this); }
    public final boolean isParameterType() { return ts.isParameterType(this); }
    public final boolean isString() { return ts.isString(this); }
    public final boolean isRail() { return ts.isRail(this); }
    public final boolean isRuntime() { return ts.isRuntime(this); }

    public final boolean isNumeric() { return ts.isNumeric(this); }
    public final boolean isSignedNumeric() { return ts.isSignedNumeric(this); }
    public final boolean isUnsignedNumeric() { return ts.isUnsignedNumeric(this); }
    public final boolean isIntOrLess() { return ts.isIntOrLess(this); }
    public final boolean isLongOrLess() { return ts.isLongOrLess(this); }
    public final boolean isVoid() { return ts.isVoid(this); }
    public final boolean isBoolean() { return ts.isBoolean(this); }
    public final boolean isChar() { return ts.isChar(this); }
    public final boolean isByte() { return ts.isByte(this); }
    public final boolean isShort() { return ts.isShort(this); }
    public final boolean isInt() { return ts.isInt(this); }
    public final boolean isLong() { return ts.isLong(this); }
    public final boolean isFloat() { return ts.isFloat(this); }
    public final boolean isDouble() { return ts.isDouble(this); }
    public final boolean isUByte() { return ts.isUByte(this); }
    public final boolean isUShort() { return ts.isUShort(this); }
    public final boolean isUInt() { return ts.isUInt(this); }
    public final boolean isULong() { return ts.isULong(this); }

    public Name name() { return null; }
    public QName fullName() {
        Name n = name();
        if (n == null)
            return null;
        return QName.make(null, n);
    }
    public boolean isGloballyAccessible() { return true; }

    /**
     * Return true if a subclass of Throwable.
     */
    public final boolean isThrowable() {
        return ts.isThrowable(this);
    }


    /**
     * Return true if an unchecked exception.
     */
    public final boolean isUncheckedException() {
	    return ts.isUncheckedException(this);
    }
    
    /** Returns a non-null iff isClass() returns true. */
    public X10ClassType toClass() {
	return null;
    }

    /** Returns a non-null iff isNull() returns true. */
    public NullType toNull() {
	return null;
    }

    /** Returns a non-null iff isReference() returns true. */
    public ObjectType toReference() {
	return null;
    }

    /** Returns a non-null iff isPrimitive() returns true. */
    public JavaPrimitiveType toPrimitive() {
	return null;
    }

    /** Returns a non-null iff isArray() returns true. */
    public JavaArrayType toArray() {
	return null;
    }

    /**
     * Return a <code>dims</code>-array of this type.
     */
    public Type arrayOf(int dims) {
	return ts.arrayOf(this, dims); 
    }  

    /**
     * Return an array of this type.
     */
    public Type arrayOf() {
	return ts.arrayOf(this);
    }  
    
    public final boolean typeEquals(Type t, Context context) {
	    return ts.typeEquals(this, t, context);
    }
    
    public final void typeEqualsImpl(Type t) { }
    public final void isSubtypeImpl(Type t) { }
    public final void descendsFromImpl(Type t) { }
    public final void isCastValidImpl(Type t) { }
    public final void isImplicitCastValidImpl(Type t) { }
    public final void numericConversionValidImpl(long t) { }
    public final void numericConversionValidImpl(Object t) { }
    
    /**
     * Return true if this type is a subtype of <code>ancestor</code>.
     */
    public final boolean isSubtype(Type t, Context context) {
	return ts.isSubtype(this, t, context);
    }
    
    /**
     * Return true if this type can be cast to <code>toType</code>.
     */
    public final boolean isCastValid(Type toType, Context context) {
	    return ts.isCastValid(this, toType, context);
    }
    
    /**
     * Return true if a value of this type can be assigned to a variable of
     * type <code>toType</code>.
     */
    public final boolean isImplicitCastValid(Type toType, Context context) {
	    return ts.isImplicitCastValid(this, toType, context);
    }

    /**
     * Return true a literal <code>value</code> can be converted to this type.
     * This method should be removed.  It is kept for backward compatibility.
     */
    public final boolean numericConversionValid(long value, Context context) {
	    return ts.numericConversionValid(this, value, context);
    }
    
    /**
     * Return true a literal <code>value</code> can be converted to this type.
     */
    public final boolean numericConversionValid(Object value, Context context) {
	    return ts.numericConversionValid(this, value, context);
    }
    
    /**
     * Return true if the types can be compared; that is, if they have
     * the same type system.
     */
    public boolean isComparable(Type t) {
	return t.typeSystem() == ts;
    }

    /**
     * Yields a string representing this type.  The string is that obtained
     * by calling typeToString(), with the annotations (if any) appended.
     * 
     * <p> Subclasses should implement typeToString() to provide a representation
     * of the underlying type.
     * 
     */
  
    public final String toString() {
    	if (annotations != null) {
    		StringBuilder sb = new StringBuilder();
    		sb.append(typeToString());
    		for (Type ct : annotations) {
    			sb.append("@");
    			sb.append(ct);
    			sb.append(" ");
    		}

    		return sb.toString();
    	}
    	return typeToString();
    }

    /**
     * Yields a string representing this type.  The string
     * should be consistent with equality.  That is,
     * if this.equals(anotherType), then it should be
     * that this.toString().equals(anotherType.toString()).
     *
     * The string does not have to be a legal Java identifier.
     * It is suggested, but not required, that it be an
     * easily human readable representation, and thus useful
     * in error messages and generated output.
     * 
     */
    abstract public String typeToString();
    
    public void print(CodeWriter w) {
	w.write(toString());
    }
}
