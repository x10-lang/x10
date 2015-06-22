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

/**
 * This interface imposes a total ordering on the entities of each type that implements it.
 *
 * Lists of objects that implement this interface can be sorted automatically by the sort()
 * method of the List class.  Objects that implement this interface can be used as keys in
 * a sorted map or as elements in a sorted set, without the need to specify a comparator.
 *
 * @param T the type of entities that this entity may be compared to
 */
@NativeRep("java", "java.lang.Comparable<#T$box>", null, "x10.rtt.ParameterizedType.make(x10.rtt.Types.COMPARABLE, #T$rtt)")
@NativeRep("c++", "x10::lang::Comparable< #T >*", "x10::lang::Comparable< #T >", null)
public interface Comparable[T] {
    /**
     * Compare this entity with the given entity in the total order defined by the type.
     * Return a negative integer if this entity precedes the given entity in the total order,
     * a positive integer if this entity succeeds the given entity in the total order, and
     * zero if they are equal.
     * The implementation should be pure and symmetric, i.e., x.compareTo(y) should return
     * the same value on subsequent invocations and x.compareTo(y) being negative should
     * mean that y.compareTo(x) is positive, with an additional invariant that
     * if x.equals(y) is true, then x.compareTo(y) should return 0.
     *
     * @param that the given entity
     * @return a negative integer, zero, or a positive integer if this entity is less than, equal
     * to, or greater than the given entity.
     */
    // XTENLANG-2723 (revert r21635)
    // // N.B. stop passing rtt to java raw class's methods
    // @Native("java", "#this.compareTo(#that)")
    @Native("java", "#this.compareTo(#that)")
    @Native("c++", "::x10::lang::Comparable< #T >::compareTo(#this, #that)")
    def compareTo(that:T):Int;
}

// vim:shiftwidth=4:tabstop=4:expandtab
