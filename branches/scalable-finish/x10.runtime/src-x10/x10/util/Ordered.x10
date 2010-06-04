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

package x10.util;

/**
 * A set of common comparison operations.
 * Should be implemented by types that define a total order.
 */
public interface Ordered[T] {
    /**
     * A binary less-than operator.
     * Compares the two operands and returns true if the left-hand operand
     * appears strictly before the right-hand operand in the total order
     * defined by the type.
     * @param that the other entity
     * @return true if the current entity is strictly less than the other entity.
     */
    global operator this <  (that: T): Boolean;
    /**
     * A binary greater-than operator.
     * Compares the two operands and returns true if the left-hand operand
     * appears strictly after the right-hand operand in the total order
     * defined by the type.
     * @param that the other entity
     * @return true if the current entity is strictly greater than the other entity.
     */
    global operator this >  (that: T): Boolean;
    /**
     * A binary less-than-or-equal-to operator.
     * Compares the two operands and returns true if the left-hand operand
     * appears before or at the same location as the right-hand operand in
     * the total order defined by the type.
     * @param that the other entity
     * @return true if the current entity is less than or equal to the other entity.
     */
    global operator this <= (that: T): Boolean;
    /**
     * A binary greater-than-or-equal-to operator.
     * Compares the two operands and returns true if the left-hand operand
     * appears after or at the same location as the right-hand operand in
     * the total order defined by the type.
     * @param that the other entity
     * @return true if the current entity is greater than or equal to the other entity.
     */
    global operator this >= (that: T): Boolean;
}
