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
 * Represents a constant int operand.
 *
 * @see Operand
 */
public final class IntConstantOperand extends ConstantOperand {

  /**
   * Constant 0, can be copied as convenient
   */
  public static final IntConstantOperand zero = new IntConstantOperand(0);

  /**
   * Value of this operand.
   */
  public final int value;

  /**
   * Constructs a new int constant operand with the specified value.
   * Type will be determined by value.
   *
   * @param v value
   */
  public IntConstantOperand(int v) {
    value = v;
  }

  /**
   * Return the {@link TypeReference} of the value represented by
   * the operand. For int constants we speculate on the type
   * dependenent on the constant value.
   *
   * @return a speculation on the type of the value represented by the
   * operand.
   */
  public Type getType() {
    if ((value == 0) || (value == 1)) {
      return KnownTypes.BOOLEAN_TYPE;
    } else if (-128 <= value && value <= 127) {
      return KnownTypes.BYTE_TYPE;
    } else if (-32768 <= value && value <= 32767) {
      return KnownTypes.SHORT_TYPE;
    } else {
      return KnownTypes.INT_TYPE;
    }
  }

  /**
   * Does the operand represent a value of an int-like data type?
   *
   * @return <code>true</code>
   */
  public boolean isIntLike() {
    return true;
  }

  /**
   * Does the operand represent a value of an int data type?
   *
   * @return <code>true</code>
   */
  public boolean isInt() {
    return true;
  }

  /**
   * Return a new operand that is semantically equivalent to <code>this</code>.
   *
   * @return a copy of <code>this</code>
   */
  public Operand copy() {
    return new IntConstantOperand(value);
  }

  /**
   * Return the lower 8 bits (as an int) of value
   */
  public int lower8() {
    return (value & 0xFF);
  }

  /**
   * Return the lower 16 bits (as an int) of value
   */
  public int lower16() {
    return (value & 0xFFFF);
  }

  /**
   * Return the upper 16 bits (as an int) of value
   */
  public int upper16() {
    return (value >>> 16);
  }

  /**
   * Return the upper 24 bits (as an int) of value
   */
  public int upper24() {
    return (value >>> 8);
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
    return (op instanceof IntConstantOperand) && (value == ((IntConstantOperand) op).value);
  }

  public boolean equals(Object o) {
    return (o instanceof IntConstantOperand) && (value == ((IntConstantOperand) o).value);
  }

  public int hashCode() {
    return value;
  }

  /**
   * Returns the string representation of this operand.
   *
   * @return a string representation of this operand.
   */
  public String toString() {
    if (value > 0xffff || value < -0xffff) {
      return "0x" + Integer.toHexString(value);
    } else {
      return Integer.toString(value);
    }
  }
}
