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

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
 * Boolean is a logical data type, with two values: 'true' and 'false'.
 * All of the normal logical operations are defined on Boolean.
 * There are also static methods that define conversions to and from String,
 * as well as some Boolean constants.
 */
@NativeRep("java", "boolean", null, "x10.rtt.Type.BOOLEAN")
@NativeRep("c++", "x10_boolean", "x10_boolean", null)
public final struct Boolean {

    /**
     * A logical complement operator.
     * Computes a logical complement of the given Boolean.
     * @param x the given Boolean
     * @return the logical complement of the given Boolean.
     */
    @Native("java", "(!(#1))")
    @Native("c++",  "(!(#1))")
    public native static safe operator ! (x:Boolean): Boolean;

    /**
     * A logical and operator (not short-circuiting).
     * Computes a logical AND of the given Boolean and the other Boolean.
     * Evaluates both arguments.
     * @param x the given Boolean
     * @param y the other Boolean
     * @return the logical AND of the given Boolean and the other Boolean.
     */
    @Native("java", "((#1) & (#2))")
    @Native("c++",  "((x10_boolean) (((#1) ? 1 : 0) & ((#2) ? 1 : 0)))")
    public native static safe operator (x:Boolean) & (y:Boolean): Boolean;

    /**
     * A logical or operator (not short-circuiting).
     * Computes a logical OR of the given Boolean and the other Boolean.
     * Evaluates both arguments.
     * @param x the given Boolean
     * @param y the other Boolean
     * @return the logical OR of the given Boolean and the other Boolean.
     */
    @Native("java", "((#1) | (#2))")
    @Native("c++",  "((x10_boolean) (((#1) ? 1 : 0) | ((#2) ? 1 : 0)))")
    public native static safe operator (x:Boolean) | (y:Boolean): Boolean;

    /**
     * A logical xor operator.
     * Computes a logical XOR of the given Boolean and the other Boolean.
     * @param x the given Boolean
     * @param y the other Boolean
     * @return the logical XOR of the given Boolean and the other Boolean.
     */
    @Native("java", "((#1) ^ (#2))")
    @Native("c++",  "((x10_boolean) (((#1) ? 1 : 0) ^ ((#2) ? 1 : 0)))")
    public native static safe operator (x:Boolean) ^ (y:Boolean): Boolean;


    /**
     * A constant holding the Boolean value 'true'.
     */
    @Native("java", "true")
    @Native("c++", "true")
    public const TRUE = true;

    /**
     * A constant holding the Boolean value 'false'.
     */
    @Native("java", "false")
    @Native("c++", "false")
    public const FALSE = false;


    /**
     * Returns a String representation of this Boolean.
     * @return a string representation of this Boolean.
     */
    @Native("java", "java.lang.Boolean.toString(#0)")
    @Native("c++", "x10aux::to_string(#0)")
    public global safe native def toString(): String;

    /**
     * @deprecated use {@link #parse(String)} instead
     */
    @Native("java", "java.lang.Boolean.parseBoolean(#1)")
    @Native("c++", "x10aux::boolean_utils::parseBoolean(#1)")
    public native static def parseBoolean(String): Boolean;

    /**
     * Parses the String argument as a Boolean.
     * The Boolean returned represents the value 'true' if the String argument
     * is not null and is equal, ignoring case, to the string "true".
     * @param s the String containing the Boolean representation to be parsed
     * @return the Boolean represented by the String argument.
     */
    @Native("java", "java.lang.Boolean.parseBoolean(#1)")
    @Native("c++", "x10aux::boolean_utils::parseBoolean(#1)")
    public native static def parse(s:String): Boolean;


    /**
     * Return true if the given entity is a Boolean, and this Boolean is equal
     * to the given entity.
     * @param x the given entity
     * @return true if this Boolean is equal to the given entity.
     */
    //FIXME Java: use equalsequals()?
    @Native("java", "((((#2) instanceof boolean) && #1 == ((boolean)#2)) || (((#2) instanceof Boolean) && #1 == ((Boolean) #2).booleanValue()))")
    @Native("c++", "x10aux::equals(#0,#1)")
    public global safe native def equals(x:Any):Boolean;

    /**
     * Returns true if this Boolean is equal to the given Boolean.
     * @param x the given Boolean
     * @return true if this Boolean is equal to the given Boolean.
     */
    @Native("java", "x10.rtt.Equality.equalsequals(#0, #1)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public global safe native def equals(x:Boolean):Boolean;

    // These operations are built-in.  Declaring them will prevent the
    // short-circuiting behavior.

//    @Native("java", "((#0) && (#1))")
//    @Native("c++",  "((x10_boolean) (((#1) ? 1 : 0) && ((#2) ? 1 : 0)))")
//    public native static safe operator (x:Boolean) && (y:Boolean): Boolean;
//
//    @Native("java", "((#0) || (#1))")
//    @Native("c++",  "((x10_boolean) (((#1) ? 1 : 0) || ((#2) ? 1 : 0)))")
//    public native static safe operator (x:Boolean) || (y:Boolean): Boolean;
}
