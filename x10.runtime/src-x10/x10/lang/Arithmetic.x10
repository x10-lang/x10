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
 * A set of common arithmetic operations.
 */
@NativeRep("c++", "x10::lang::Arithmetic< #T >*", "x10::lang::Arithmetic< #T >", null)
public interface Arithmetic[T] {
    /**
     * A unary plus operator.
     * A no-op.
     * @return the value of the current entity.
     */
    @Native("c++", "::x10::lang::Arithmetic< #T >::_m3____plus(#this)")
    operator + this: T;

    /**
     * A unary minus operator.
     * Negates the operand.
     * @return the negated value of the current entity.
     */
    @Native("c++", "::x10::lang::Arithmetic< #T >::_m5____minus(#this)")
    operator - this: T;

    /**
     * A binary plus operator.
     * Computes the result of the addition of the two operands.
     * @param that the other entity
     * @return the sum of the current entity and the other entity.
     */
    @Native("c++", "::x10::lang::Arithmetic< #T >::_m4____plus(#this, #that)")
    operator this + (that: T): T;

    /**
     * A binary minus operator.
     * Computes the result of the subtraction of the two operands.
     * @param that the other entity
     * @return the difference of the current entity and the other entity.
     */
    @Native("c++", "::x10::lang::Arithmetic< #T >::_m6____minus(#this, #that)")
    operator this - (that: T): T;

    /**
     * A binary multiply operator.
     * Computes the result of the multiplication of the two operands.
     * @param that the other entity
     * @return the product of the current entity and the other entity.
     */
    @Native("c++", "::x10::lang::Arithmetic< #T >::__times(#this, #that)")
    operator this * (that: T): T;

    /**
     * A binary divide operator.
     * Computes the result of the division of the two operands.
     * @param that the other entity
     * @return the quotient of the current entity and the other entity.
     */
    @Native("c++", "::x10::lang::Arithmetic< #T >::__over(#this, #that)")
    operator this / (that: T): T;
}
