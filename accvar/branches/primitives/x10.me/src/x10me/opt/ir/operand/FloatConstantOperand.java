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
 * Represents a constant float operand.
 *
 * @see Operand
 */
public final class FloatConstantOperand extends ConstantOperand {

  /**
   * Value of this operand.
   */
  public float value;

  /**
   * Constructs a new float constant operand with the specified value.
   *
   * @param v value
   */
  public FloatConstantOperand(float v) {
    value = v;
  }

  /**
   * Return a new operand that is semantically equivalent to <code>this</code>.
   *
   * @return a copy of <code>this</code>
   */
  public Operand copy() {
    return new FloatConstantOperand(value);
  }

  /**
   * Return the {@link TypeReference} of the value represented by the operand.
   *
   * @return TypeReference.Float
   */
  public Type getType() {
    return KnownTypes.FLOAT_TYPE;
  }

  /**
   * Does the operand represent a value of the float data type?
   *
   * @return <code>true</code>
   */
  public boolean isFloat() {
    return true;
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
    return (op instanceof FloatConstantOperand) && (value == ((FloatConstantOperand) op).value);
  }

  /**
   * Returns the string representation of this operand.
   *
   * @return a string representation of this operand.
   */
  public String toString() {
    return Float.toString(value);
  }
}
