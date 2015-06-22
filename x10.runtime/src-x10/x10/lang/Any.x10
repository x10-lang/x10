/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;
import x10.compiler.NoThisAccess;

/**
 * The top of the type hierarchy.
 * Implemented by all classes and structs.
 */
@NativeRep("java", "java.lang.Object", null, "x10.rtt.Types.ANY")
@NativeRep("c++", "x10::lang::Any*", "x10::lang::Any", null)
public interface Any {

    /**
     * Return the string representation of this entity.
     * 
     * <p> The method is a common method that is likely to be called within the 
     * body of atomic/when. So any programmer overriding this method should
     * ensure that operations that are illegal in atomic/when blocks (viz, 
     * the use of when, at, async etc) are not performed in the implementation 
     * of this method. That is, this method implementation should be "safe".
     *
     * @return a string representation of this entity.
     */
    // @Native("java", "((java.lang.Object)(#this)).toString()")
    @Native("java", "x10.rtt.Types.toString(#this)")
    @Native("c++", "::x10aux::to_string(#this)")
    def toString():String;

    /**
     * Return a string representation of the run-time type of this entity.
     * Should not be overridden.
     *
     * @return a string representation of the run-time type of this entity.
     */
    @Native("java", "x10.rtt.Types.typeName(#this)")
    @Native("c++", "::x10aux::type_name(#this)")
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
     * <p> The method is a common method that is likely to be called within the 
     * body of atomic/when. So any programmer overriding this method should
     * ensure that operations that are illegal in atomic/when blocks (viz, 
     * the use of when, at, async etc) are not performed in the implementation 
     * of this method. That is, the implementation of this method should be "safe".
     * 
     * <p> So, either the equality comparison
     * has to be based only on locally available information (highly desirable), 
     * or the implementation has
     * to ensure that the method is being invoked in the right place, and 
     * possibly throw an exception if it is not.
     *
     * @param that the given entity
     * @return true if this entity is equal to the given entity.
     */
    @Native("java", "((java.lang.Object)(#this)).equals(#that)")
    @Native("c++", "::x10aux::equals(#this,#that)")
    def equals(that:Any):Boolean;

    /**
     * Return the implementation-defined hash code of this entity.
     * The implementation should be pure, i.e., x.hashCode() should return the
     * same value on subsequent invocations, with an additional requirement that
     * if x.equals(y) is true, then x.hashCode() should equal y.hashCode().
     *
     * <p> The method is a common method that is likely to be called within the 
     * body of atomic/when. So any programmer overriding this method should
     * ensure that operations that are illegal in atomic/when blocks (viz, 
     * the use of when, at, async etc) are not performed in the implementation 
     * of this method. That is, the implementation of this method should be "safe".
     * 
     * <p> So, either the equality comparison 
     * has to be based only on locally available information (highly desirable), 
     * or the implementation has
     * to ensure that the method is being invoked in the right place, and 
     * possibly throw an exception if it is not.
     *
     * @return the hash code of this entity.
     */
    // @Native("java", "((java.lang.Object)(#this)).hashCode()")
    @Native("java", "x10.rtt.Types.hashCode(#this)")
    @Native("c++", "::x10aux::hash_code(#this)")
    def hashCode():Int;
}
public type Any(x:Any) = Any{self==x};

// vim:shiftwidth=4:tabstop=4:expandtab
