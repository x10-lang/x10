/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file was derived from code developed by the
 *  Jikes RVM project (http://jikesrvm.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  See the COPYRIGHT.txt file distributed with this work for information
 *  regarding copyright ownership.
 */

package x10me.opt.ir.operand;

import x10me.types.KnownTypes;
import x10me.types.Type;

/**
 * Represents a constant long operand.
 *
 * @see Operand
 */
public final class LongConstantOperand extends ConstantOperand {

  /**
   * Constant 0, can be copied as convenient
   */
  public static final LongConstantOperand zero =
    new LongConstantOperand(0);

  /**
   * Value of this operand.
   */
  public long value;

   /**
   * Constructs a new long constant operand with the specified value.
   *
   * @param v value
   */
  public LongConstantOperand(long v) {
    value = v;
  }

  /**
   * Return the {@link TypeReference} of the value represented by the operand.
   *
   * @return TypeReference.Long
   */
  public Type getType() {
    return KnownTypes.LONG_TYPE;
  }

  /**
   * Does the operand represent a value of the long data type?
   *
   * @return <code>true</code>
   */
  public boolean isLong() {
    return true;
  }

  /**
   * Return the lower 32 bits (as an int) of value
   */
  public int lower32() {
    return (int)value;
  }

  /**
   * Return the upper 32 bits (as an int) of value
   */
  public int upper32() {
    return (int)(value >>> 32);
  }

  /**
   * Return a new operand that is semantically equivalent to <code>this</code>.
   *
   * @return a copy of <code>this</code>
   */
  public Operand copy() {
    return new LongConstantOperand(value);
  }

  /**
   * Are two operands semantically equivalent?
   *
   * @param op other operand
   * @return   <code>true</code> if <code>this</code> and <code>op</code>
   *           are semantically equivalent or <code>false</code>
   *           if they are not.
   */
  public boolean similar(Operand op) {
    return (op instanceof LongConstantOperand) && (value == ((LongConstantOperand) op).value);
  }

  /**
   * Returns the string representation of this operand.
   *
   * @return a string representation of this operand.
   */
  public String toString() {
    return Long.toString(value) + "L";
  }
}
