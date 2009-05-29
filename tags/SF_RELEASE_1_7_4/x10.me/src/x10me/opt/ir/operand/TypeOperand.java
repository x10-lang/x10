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

import x10me.types.Type;

/**
 * A TypeOperand represents a type.
 * Used in checkcast, instanceof, new, etc.
 * It will contain either a RVMType (if the type can be resolved
 * at compile time) or a TypeReference (if the type cannot be resolved
 * at compile time).
 *
 * @see Operand
 * @see RVMType
 * @see TypeReference
 */
public final class TypeOperand extends Operand {

  /**
   * A type
   */
  private final Type type;

  /**
   * Create a new type operand with the specified type reference
   */
  public TypeOperand(Type t) {
    type = t;
  }

  /**
   * Return the {@link Type} of the value represented by the operand.
   *
   * @return TypeReference.Type
   */
  public Type getType() {
    return type;
  }

  /**
   * Return a new operand that is semantically equivalent to <code>this</code>.
   *
   * @return a copy of <code>this</code>
   */
  public Operand copy() {
    return new TypeOperand(type);
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
    if (op instanceof TypeOperand) {
      TypeOperand that = (TypeOperand) op;
      return type == that.type;
    } else {
      return false;
    }
  }

  /**
   * Returns the string representation of this operand.
   *
   * @return a string representation of this operand.
   */
  public String toString() {
    return type.toString();
  }
}
