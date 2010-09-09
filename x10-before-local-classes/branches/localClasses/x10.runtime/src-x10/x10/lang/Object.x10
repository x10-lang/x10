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
 * The base class for all reference classes.
 */
@NativeRep("java", "java.lang.Object", null, null)
@NativeRep("c++", "x10aux::ref<x10::lang::Object>", "x10::lang::Object", null)
public class Object 
        implements Any 
{
    /**
     * Default constructor.
     */
    public native def this();

    /**
     * Return true if the given entity is an Object, and this object is equal
     * to the given entity in an implementation-dependent way.
     * The usual properties of symmetry, commutativity, and purity should
     * apply, i.e., x.equals(x) should always be true; if x.equals(y) is true,
     * then so should y.equals(x) be; and x.equals(y) should return the same
     * value on subsequent invocations.
     *
     * Note that the method is safe, so the implementations cannot
     * spawn activities at other places.  So, either the equality comparison
     * has to be based on only information available at the current place, 
     * or the implementation has to throw an exception if the information is not available.
     *
     * @param that the given entity
     * @return true if this object is equal to the given entity.
     */
    @Native("java", "((Object)#0).equals(#1)")
    @Native("c++", "(#0)->equals(#1)")
    public safe native def equals(that:Any): boolean;

    /**
     * Return the implementation-defined hash code of this object.
     * The implementation should be pure, i.e., x.hashCode() should return the
     * same value on subsequent invocations, with an additional invariant that
     * if x.equals(y) is true, then x.hashCode() should equal y.hashCode().
     *
     * Note that the method is safe, so the implementations cannot
     * spawn activities at other places.  So, either the equality comparison
     * has to be based on only information available at the current place, 
     * or the implementation has to throw an exception if the information is not available.
     *
     * @return the hash code of this object.
     */
    @Native("java", "((Object)#0).hashCode()")
    @Native("c++", "(#0)->hashCode()")
    public safe native def hashCode() : Int;

    /**
     * Return the string representation of this object.
     *
     * Note that the method is safe, so the implementations cannot
     * spawn activities at other places.  So, either the string representation
     * has to be based on only information available at the current place, 
     * or the implementation has to throw an exception if it cannot perform the toString
     * operation with the avaiable information.
     *
     * @return a string representation of this object.
     */
    @Native("java", "((Object)#0).toString()")
    @Native("c++", "(#0)->toString()")
    public safe native def toString() : String;

    /**
     * Return a string representation of the run-time type of this object.
     *
     * @return a string representation of the run-time type of this object.
     */
    @Native("java", "x10.core.Ref.typeName(#0)")
    @Native("c++", "x10aux::type_name(#0)")
    public safe native final def typeName():String;
}
