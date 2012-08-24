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

package safe.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;
import x10.compiler.Opaque;

/**
 * Boolean is a logical data type, with two values: 'true' and 'false'.
 * All of the normal logical operations are defined on Boolean.
 * There are also static methods that define conversions to and from String,
 * as well as some Boolean constants.
 */
@NativeRep("java", "boolean", null, "x10.rtt.Types.BOOLEAN")
@NativeRep("c++", "x10_boolean", "x10_boolean", null)
public struct Boolean implements Comparable[Boolean] {

    /**
     * A logical complement operator.
     * Computes a logical complement of this Boolean.
     * @return the logical complement of this Boolean.
     */
    @Native("java", "!(#this)")
    @Native("c++",  "!(#0)")
    public native operator ! this: Boolean{self == B.not(this)};
    
    @Opaque("not")
	public static native property def not(x: Boolean) :Boolean; 
    

    /**
     * A logical and operator (not short-circuiting).
     * Computes a logical AND of this Boolean and the other Boolean.
     * Evaluates both arguments.
     * @param x the other Boolean
     * @return the logical AND of this Boolean and the other Boolean.
     */
    @Native("java", "((#this) & (#x))")
    @Native("c++",  "((x10_boolean) (((#0) ? 1 : 0) & ((#1) ? 1 : 0)))")
    public native operator this & (x:Boolean): Boolean{self == B.and(this, x)};

    @Opaque("and")
	public static native property def and(x: Boolean, y:Boolean) :Boolean; 
	
    /**
     * A logical or operator (not short-circuiting).
     * Computes a logical OR of this Boolean and the other Boolean.
     * Evaluates both arguments.
     * @param x the other Boolean
     * @return the logical OR of this Boolean and the other Boolean.
     */
    @Native("java", "((#this) | (#x))")
    @Native("c++",  "((x10_boolean) (((#0) ? 1 : 0) | ((#1) ? 1 : 0)))")
    public native operator this | (x:Boolean): Boolean {self == B.or(this, x)};

    @Opaque("or")
	public static native property def or(x: Boolean, y:Boolean) :Boolean; 

    /**
     * A logical xor operator.
     * Computes a logical XOR of this Boolean and the other Boolean.
     * @param x the other Boolean
     * @return the logical XOR of this Boolean and the other Boolean.
     */
    @Native("java", "((#this) ^ (#x))")
    @Native("c++",  "((x10_boolean) (((#0) ? 1 : 0) ^ ((#1) ? 1 : 0)))")
    public native operator this ^ (x:Boolean): Boolean {self == B.xor(this, x)};

    @Opaque("xor")
	public static native property def xor(x: Boolean, y:Boolean) :Boolean; 


    /**
     * A constant holding the Boolean value 'true'.
     */
    @Native("java", "true")
    @Native("c++", "true")
    property static native TRUE(): Boolean {self == B.asBoolean(true)};

    /**
     * A constant holding the Boolean value 'false'.
     */
    @Native("java", "false")
    @Native("c++", "false")
    property static native FALSE(): Boolean {self == B.asBoolean(false)}; 


    /**
     * Returns a String representation of this Boolean.
     * @return a string representation of this Boolean.
     */
    @Native("java", "java.lang.Boolean.toString(#this)")
    @Native("c++", "x10aux::to_string(#0)")
    public native def toString(): String;

    /**
     * @deprecated use {@link #parse(String)} instead
     */
    @Native("java", "java.lang.Boolean.parseBoolean(#s)")
    @Native("c++", "x10aux::boolean_utils::parseBoolean(#1)")
    public native static def parseBoolean(s:String): Boolean;

    /**
     * Parses the String argument as a Boolean.
     * The Boolean returned represents the value 'true' if the String argument
     * is not null and is equal, ignoring case, to the string "true".
     * @param s the String containing the Boolean representation to be parsed
     * @return the Boolean represented by the String argument.
     */
    @Native("java", "java.lang.Boolean.parseBoolean(#s)")
    @Native("c++", "x10aux::boolean_utils::parseBoolean(#1)")
    public native static def parse(s:String): Boolean;


    /**
     * Return true if the given entity is a Boolean, and this Boolean is equal
     * to the given entity.
     * @param x the given entity
     * @return true if this Boolean is equal to the given entity.
     */
    //FIXME Java: use equalsequals()?
    @Native("java", "((((#x) instanceof boolean) && #this == ((boolean)#x)) || (((#x) instanceof Boolean) && #this == ((Boolean) #x).booleanValue()))")
    @Native("c++", "x10aux::equals(#0,#1)")
    public native def equals(x:Any):x10.lang.Boolean;

    /**
     * Returns true if this Boolean is equal to the given Boolean.
     * @param x the given Boolean
     * @return true if this Boolean is equal to the given Boolean.
     */
    @Native("java", "x10.rtt.Equality.equalsequals(#this, #x)")
    @Native("c++", "x10aux::equals(#0,#1)")
    public native def equals(x:Boolean):x10.lang.Boolean {self == B.asX10Boolean(B.sequals(this, x))};
    
    @Opaque("sequals")
	public static native property def sequals(x: Boolean, y:Boolean) : Boolean; 
    @Opaque("sequals")
	public static native property def sequals(x: Boolean, y:Any) : Boolean; 

    /**
    * Returns a negative Int, zero, or a positive Int if this Boolean is less than, equal
    * to, or greater than the given Boolean.
    * @param x the given Boolean
    * @return a negative Int, zero, or a positive Int if this Boolean is less than, equal
    * to, or greater than the given Boolean.
    */
    @Native("java", "x10.rtt.Equality.compareTo(#this, #x)")
    @Native("c++", "x10aux::boolean_utils::compareTo(#0, #1)")
    public native def compareTo(x:Boolean):Int;

    // These operations are built-in.  Declaring them will prevent the
    // short-circuiting behavior.

    @Native("java", "((#x) && (#y))")
    @Native("c++",  "((x10_boolean) (((#1) ? 1 : 0) && ((#2) ? 1 : 0)))")
    public native static operator (x:Boolean) && (y:Boolean): Boolean {self == B.and(x,y)};

    @Native("java", "((#x) || (#y))")
    @Native("c++",  "((x10_boolean) (((#1) ? 1 : 0) || ((#2) ? 1 : 0)))")
    public native static operator (x:Boolean) || (y:Boolean): Boolean {self == B.or(x,y)};
    
    // TODO: move coercion the other way to x10.lang.Boolean class
    @Opaque("asX10Boolean")
    public static native property def asX10Boolean(x: Boolean): x10.lang.Boolean; 

    @Opaque("asBoolean")
    public static native property def asBoolean(x: x10.lang.Boolean): Boolean {Boolean.asX10Boolean(self) == x}; 

    /**
    * Coerce a given x10.lang.Boolean to an safe.lang.Boolean.
    * @param x the given x10.lang.Boolean
    * @return the given x10.lang.Boolean converted to an safe.lang.Boolean.
    */
    @Native("java", "((Boolean)#x)")
    @Native("c++",  "((x10_boolean) (#1))")
    public native static operator (x:x10.lang.Boolean): Boolean {self == B.asBoolean(x)};
    
    public native operator this() : x10.lang.Boolean {self == B.asX10Boolean(this)}; 
}

public type Boolean(b:Boolean) = Boolean{self==b};
public type B = Boolean; 
