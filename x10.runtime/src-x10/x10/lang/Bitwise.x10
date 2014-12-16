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

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
 * A set of common bitwise operations.
 */
@NativeRep("c++", "x10::lang::Bitwise< #T >*", "x10::lang::Bitwise< #T >", null)
public interface Bitwise[T] {
    /**
     * A bitwise complement operator.
     * Computes a bitwise complement (NOT) of the operand.
     * @return the bitwise complement of the current entity.
     */
    @Native("c++", "::x10::lang::Bitwise< #T >::__tilde(#this)")
    operator ~ this: T;

    /**
     * A bitwise and operator.
     * Computes a bitwise AND of the two operands.
     * @param that the other entity
     * @return the bitwise AND of the current entity and the other entity.
     */
    @Native("c++", "::x10::lang::Bitwise< #T >::__ampersand(#this, #that)")
    operator this & (that: T): T;

    /**
     * A bitwise or operator.
     * Computes a bitwise OR of the two operands.
     * @param that the other entity
     * @return the bitwise OR of the current entity and the other entity.
     */
    @Native("c++", "::x10::lang::Bitwise< #T >::__bar(#this, #that)")
    operator this | (that: T): T;

    /**
     * A bitwise xor operator.
     * Computes a bitwise XOR of the two operands.
     * @param that the other entity
     * @return the bitwise XOR of the current entity and the other entity.
     */
    @Native("c++", "::x10::lang::Bitwise< #T >::__caret(#this, #that)")
    operator this ^ (that: T): T;

    /**
     * A bitwise left shift operator.
     * Computes the value of the left-hand operand shifted left by the value of the right-hand operand.
     * The shift count will be masked based on the size of the receiver so that only the bottom
     * k bits will be considered.
     * @param count the shift count
     * @return the current entity shifted left by count.
     */
    @Native("c++", "::x10::lang::Bitwise< #T >::__left(#this, #count)")
    operator this << (count: Long): T;

    /**
     * A bitwise right shift operator.
     * Computes the value of the left-hand operand shifted right by the value of the right-hand operand.
     * The shift count will be masked based on the size of the receiver so that only the bottom
     * k bits will be considered.
     * @param count the shift count
     * @return the current entity shifted right by count.
     */
    @Native("c++", "::x10::lang::Bitwise< #T >::__right(#this, #count)")
    operator this >> (count: Long): T;

    /**
     * A bitwise logical right shift operator (zero-fill).
     * Computes the value of the left-hand operand shifted right by the value of the right-hand operand,
     * filling the high bits with zeros.
     * The shift count will be masked based on the size of the receiver so that only the bottom
     * k bits will be considered.
     * @deprecated use the right-shift operator and unsigned conversions instead.
     * @param count the shift count
     * @return the current entity shifted right by count with high bits zero-filled.
     */
    @Native("c++", "::x10::lang::Bitwise< #T >::__unsigned_right(#this, #count)")
    operator this >>> (count: Long): T;
}

