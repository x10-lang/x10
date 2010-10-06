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
import x10.compiler.NoThisAccess;

/**
 * The top of the type hierarchy.
 * Implemented by all classes and structs.
 * 
 * Restriction: The types in Any cannot use "here". (In the current implementation,
 * using "here" in a type, e.g. def at(p:Object!):boolean, would cause an infinite
 * recursion. See PlaceChecker.pushHereTerm.)
 *
 * @author vj 12/14/09
 */
@NativeRep("java", "java.lang.Object", null, "x10.rtt.Types.ANY")
@NativeRep("c++", "x10aux::ref<x10::lang::Any>", "x10::lang::Any", null)
public interface Any {

    /**
     * Return the string representation of this entity.
     *
     * Note that the method is safe, so the implementations cannot
     * spawn activities at other places.  So, either the string representation
     * has to include only global information, or the implementation has to
     * ensure that the home location of the entity is 'here', and possibly
     * throw an exception if not.
     *
     * @return a string representation of this entity.
     */
    @Native("java", "((java.lang.Object)(#0)).toString()")
    @Native("c++", "x10aux::to_string(#0)")
    def toString():String;

    /**
     * Return a string representation of the run-time type of this entity.
     * Should not be overridden.
     *
     * @return a string representation of the run-time type of this entity.
     */
    @Native("java", "x10.rtt.Types.typeName(#0)")
    @Native("c++", "x10aux::type_name(#0)")
    @NoThisAccess
    def typeName():String;

    /**
     * Return true if this entity is equal to the given entity in an
     * implementation-dependent way.
     * The usual properties of symmetry, commutativity, and purity should
     * apply, i.e., x.equals(x) should always be true; if x.equals(y) is true,
     * then so should y.equals(x) be; and x.equals(y) should return the same
     * value on subsequent invocations.
     *
     * Note that the method is safe, so the implementations cannot
     * spawn activities at other places.  So, either the equality comparison
     * has to be based on only global information, or the implementation has
     * to ensure that the home location of the entities is 'here', and
     * possibly throw an exception if not.
     *
     * @param that the given entity
     * @return true if this entity is equal to the given entity.
     */
    @Native("java", "((java.lang.Object)(#0)).equals(#1)")
    @Native("c++", "x10aux::equals(#0,#1)")
    def equals(that:Any):Boolean;

    /**
     * Return the implementation-defined hash code of this entity.
     * The implementation should be pure, i.e., x.hashCode() should return the
     * same value on subsequent invocations, with an additional invariant that
     * if x.equals(y) is true, then x.hashCode() should equal y.hashCode().
     *
     * Note that the method is global and safe, so the implementations cannot
     * spawn activities at other places.  So, either the equality comparison
     * has to be based on only global information, or the implementation has
     * to ensure that the home location of this entity is 'here', and
     * possibly throw an exception if not.
     *
     * @return the hash code of this entity.
     */
    @Native("java", "((java.lang.Object)(#0)).hashCode()")
    @Native("c++", "x10aux::hash_code(#0)")
    def hashCode():Int;
}

// vim:shiftwidth=4:tabstop=4:expandtab
