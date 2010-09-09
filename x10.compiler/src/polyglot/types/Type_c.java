/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import polyglot.util.CodeWriter;
import polyglot.util.Position;

/**
 * Abstract implementation of a <code>Type</code>.  This implements most of
 * the "isa" and "cast" methods of the type and methods which just dispatch to
 * the type system.
 */
public abstract class Type_c extends TypeObject_c implements Type
{
    /** Used for deserializing types. */
    protected Type_c() { }
    
    /** Creates a new type in the given a TypeSystem. */
    public Type_c(TypeSystem ts) {
        this(ts, null);
    }

    /** Creates a new type in the given a TypeSystem at a given position. */
    public Type_c(TypeSystem ts, Position pos) {
        super(ts, pos);
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
    public boolean isPrimitive() { return false; }
    public boolean isReference() { return false; } 
    public boolean isNull() { return false; }
    public boolean isClass() { return false; }
    public boolean isArray() { return false; }
    
    public final boolean isNumeric() { return ts.isNumeric(this); }
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
    public ClassType toClass() {
	return null;
    }

    /** Returns a non-null iff isNull() returns true. */
    public NullType toNull() {
	return null;
    }

    /** Returns a non-null iff isReference() returns true. */
    public ReferenceType toReference() {
	return null;
    }

    /** Returns a non-null iff isPrimitive() returns true. */
    public PrimitiveType toPrimitive() {
	return null;
    }

    /** Returns a non-null iff isArray() returns true. */
    public ArrayType toArray() {
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
     * Yields a string representing this type.  The string
     * should be consistent with equality.  That is,
     * if this.equals(anotherType), then it should be
     * that this.toString().equals(anotherType.toString()).
     *
     * The string does not have to be a legal Java identifier.
     * It is suggested, but not required, that it be an
     * easily human readable representation, and thus useful
     * in error messages and generated output.
     */
    public abstract String toString();

    public void print(CodeWriter w) {
	w.write(toString());
    }
}
