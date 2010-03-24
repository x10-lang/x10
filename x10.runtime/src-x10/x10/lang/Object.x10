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
public class Object (
        @Native("java", "x10.lang.Place.place(x10.core.Ref.home(#0))")
        @Native("c++", "x10::lang::Place_methods::place((#0)->location)")
        home: Place) 
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
     * Note that the method is global and safe, so the implementations cannot
     * spawn activities at other places.  So, either the equality comparison
     * has to be based on only global information, or the implementation has
     * to ensure that the home location of this object and the given entity is
     * 'here', and possibly throw an exception if not.
     *
     * @param that the given entity
     * @return true if this object is equal to the given entity.
     */
    @Native("java", "((Object)#0).equals(#1)")
    @Native("c++", "(#0)->equals(#1)")
    public global safe native def equals(that:Any#): boolean;

    /**
     * Return the implementation-defined hash code of this object.
     * The implementation should be pure, i.e., x.hashCode() should return the
     * same value on subsequent invocations, with an additional invariant that
     * if x.equals(y) is true, then x.hashCode() should equal y.hashCode().
     *
     * Note that the method is global and safe, so the implementations cannot
     * spawn activities at other places.  So, either the equality comparison
     * has to be based on only global information, or the implementation has
     * to ensure that the home location of this object is 'here', and
     * possibly throw an exception if not.
     *
     * @return the hash code of this object.
     */
    @Native("java", "((Object)#0).hashCode()")
    @Native("c++", "(#0)->hashCode()")
    public global safe native def hashCode() : Int;

    /**
     * Return the string representation of this object.
     *
     * Note that the method is global and safe, so the implementations cannot
     * spawn activities at other places.  So, either the string representation
     * has to include only global information, or the implementation has to
     * ensure that the home location of the object is 'here', and possibly
     * throw an exception if not.
     *
     * @return a string representation of this object.
     */
    @Native("java", "((Object)#0).toString()")
    @Native("c++", "(#0)->toString()")
    public global safe native def toString() : String;

    /**
     * Return true if the home location of this object is the given place.
     *
     * @param p The given place
     * @return true if the home location of this object is p.
     */
    @Native("java", "x10.core.Ref.at(#0, #1.id)")
    @Native("c++", "(#0)->at(#1)")
    public property safe def at(p:Place) = home==p;

    /**
     * Return true if this object is in the same home location as the given Object.
     *
     * @param r The given Object
     * @return true if the home location of this object is the same as the home location of r.
     */
    @Native("java", "x10.core.Ref.at(#0, #1)")
    @Native("c++", "(#0)->at(#1)")
    public property safe def at(r:Any#) = home==r.home;
    
    /**
     * Return a string representation of the run-time type of this object.
     *
     * @return a string representation of the run-time type of this object.
     */
    @Native("java", "x10.core.Ref.typeName(#0)")
    @Native("c++", "x10aux::type_name(#0)")
    public global safe native final def typeName():String;
}
