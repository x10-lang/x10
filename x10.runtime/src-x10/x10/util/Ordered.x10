/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.util;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
 * A set of common comparison operations.
 * Should be implemented by types that define a total order.
 */
@NativeRep("c++", "x10::util::Ordered< #T >*", "x10::util::Ordered< #T >", null)
public interface Ordered[T] {
    /**
     * A binary less-than operator.
     * Compares the two operands and returns true if the left-hand operand
     * appears strictly before the right-hand operand in the total order
     * defined by the type.
     * @param that the other entity
     * @return true if the current entity is strictly less than the other entity.
     */
    @Native("c++", "::x10::util::Ordered< #T >::__lt(#this, #that)")
    operator this <  (that: T): Boolean;

    /**
     * A binary greater-than operator.
     * Compares the two operands and returns true if the left-hand operand
     * appears strictly after the right-hand operand in the total order
     * defined by the type.
     * @param that the other entity
     * @return true if the current entity is strictly greater than the other entity.
     */
    @Native("c++", "::x10::util::Ordered< #T >::__gt(#this, #that)")
    operator this >  (that: T): Boolean;

    /**
     * A binary less-than-or-equal-to operator.
     * Compares the two operands and returns true if the left-hand operand
     * appears before or at the same location as the right-hand operand in
     * the total order defined by the type.
     * @param that the other entity
     * @return true if the current entity is less than or equal to the other entity.
     */
    @Native("c++", "::x10::util::Ordered< #T >::__le(#this, #that)")
    operator this <= (that: T): Boolean;

    /**
     * A binary greater-than-or-equal-to operator.
     * Compares the two operands and returns true if the left-hand operand
     * appears after or at the same location as the right-hand operand in
     * the total order defined by the type.
     * @param that the other entity
     * @return true if the current entity is greater than or equal to the other entity.
     */
    @Native("c++", "::x10::util::Ordered< #T >::__ge(#this, #that)")
    operator this >= (that: T): Boolean;
}
