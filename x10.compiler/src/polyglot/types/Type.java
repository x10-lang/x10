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

import polyglot.util.CodeWriter;
import x10.types.Annotated;
import x10.types.X10ClassType;

/**
 * A <code>Type</code> is the base type of all classes which represent
 * types.
 */
public interface Type extends Qualifier, Annotated, Named
{
    public void equals(Type t);
    
    /**
     * Return a string into which to translate the type.
     * @param c A resolver in which to lookup this type to determine if
     * the type is unique in the given resolver.
     */
    String translate(Resolver c);

    /**
     * Return an array of this type.
     */
    Type arrayOf();

    /**
     * Return a <code>dims</code>-array of this type.
     */
    Type arrayOf(int dims);

    /**
     * Cast the type to a class type, or null.
     */
    X10ClassType toClass();

    /**
     * Cast the type to a null type, or null.
     */
    NullType toNull();

    /**
     * Cast the type to a reference type, or null.
     */
    ObjectType toReference();

    /**
     * Cast the type to a primitive type, or null.
     */
    JavaPrimitiveType toPrimitive();

    /**
     * Cast the type to an array type, or null.
     */
    JavaArrayType toArray();

    /**
     * Return true if this type is equivalent to t.
     * Usually this is the same as equalsImpl(TypeObject), but that
     * method should return true only if the types are
     * <i>structurally equal</i>.
     * @param t Type to compare to
     * @param context TODO
     * @return True if this type is equivalent to t.
     */
    boolean typeEquals(Type t, Context context);
    
    /**
     * Return true if this type is a subtype of <code>ancestor</code>.
     * @param context TODO
     */
    boolean isSubtype(Type ancestor, Context context);

    /**
     * Return true if this type can be cast to <code>toType</code>.
     * @param context TODO
     */
    boolean isCastValid(Type toType, Context context);

    /**
     * Return true if a value of this type can be assigned to a variable of
     * type <code>toType</code>.
     * @param context TODO
     */
    boolean isImplicitCastValid(Type toType, Context context);

    /**
     * Return true a literal <code>value</code> can be converted to this type.
     * @param context TODO
     */
    boolean numericConversionValid(Object value, Context context);

    /**
     * Return true a literal <code>value</code> can be converted to this type.
     * @param context TODO
     */
    boolean numericConversionValid(long value, Context context);

    /**
     * Return true if a primitive type.
     */
    boolean isJavaPrimitive();

    /**
     * Return true if void.
     */
    boolean isVoid();

    /**
     * Return true if boolean.
     */
    boolean isBoolean();

    /**
     * Return true if char.
     */
    boolean isChar();

    /**
     * Return true if byte.
     */
    boolean isByte();

    /**
     * Return true if short.
     */
    boolean isShort();

    /**
     * Return true if int.
     */
    boolean isInt();

    /**
     * Return true if long.
     */
    boolean isLong();

    /**
     * Return true if float.
     */
    boolean isFloat();

    /**
     * Return true if double.
     */
    boolean isDouble();

    /**
     * Return true if UByte
     */
    boolean isUByte();

    /**
     * Return true if UShort
     */
    boolean isUShort();

    /**
     * Return true if UInt
     */
    boolean isUInt();

    /**
     * Return true if ULong
     */
    boolean isULong();

    /**
     * Return true if int, short, byte, or char.
     */
    boolean isIntOrLess();

    /**
     * Return true if long, int, short, byte, or char.
     */
    boolean isLongOrLess();

    /**
     * Return true if double, float, long, int, short, byte, or char.
     */
    boolean isNumeric();

    /**
     * Return true if long, int, short, or byte.
     */
    boolean isSignedNumeric();

    /**
     * Return true if ulong, uint, ushort, or ubyte.
     */
    boolean isUnsignedNumeric();

    /**
     * Return true if a reference type.
     */
    boolean isReference();

    /**
     * Return true if a null type.
     */
    boolean isNull();

    /**
     * Return true if an array type.
     */
    boolean isArray();

    /**
     * Return true if a class type.
     */
    boolean isClass();

    /**
     * Return true if a subclass of Throwable.
     */
    boolean isThrowable();

    /**
     * Return true if an unchecked exception.
     */
    boolean isUncheckedException();

    /**
     * Return true if the types can be compared; that is, if they have
     * the same type system.
     */
    boolean isComparable(Type t);
    
    /**
     * Return true if the type is Any
     */
    boolean isAny();
    
    /**
     * Return true if the type is a type parameter
     */
    boolean isParameterType();

    /**
     * Return true if the type is String
     */
    boolean isString();
    
    /**
     * Return true if the type is Rail
     */
    boolean isRail();

    /**
     * Return true if the type is Runtime
     */
    boolean isRuntime();


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
    String toString();
    
    void print(CodeWriter w);
}
