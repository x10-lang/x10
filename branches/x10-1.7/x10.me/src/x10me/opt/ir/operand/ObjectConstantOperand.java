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

import x10me.types.Address;
import x10me.types.Type;

/**
 * Represents a constant object operand (for example, from an
 * initialized static final).
 *
 * @see Operand
 */
public class ObjectConstantOperand extends ConstantOperand {

  /**
   * The non-null object value
   */
  public final Object value;
  public final Type type;
  
  /**
   * Construct a new object constant operand
   *
   * @param v the object constant
   * @param i JTOC offset of the object constant
   */
  public ObjectConstantOperand(Object v, Type t) {
    assert v != null;
    assert t != null;
    value = v;
    type = t;
  }

  /**
   * Return a new operand that is semantically equivalent to <code>this</code>.
   *
   * @return a copy of <code>this</code>
   */
  public Operand copy() {
    return new ObjectConstantOperand(value, type);
  }

  /**
   * Return the {@link TypeReference} of the value represented by the operand.
   *
   * @return type reference for type of object
   */
  public Type getType() {
    return type;
  }

  /**
   * Does the operand represent a value of the reference data type?
   *
   * @return <code>true</code>
   */
  public final boolean isRef() {
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
    return (op instanceof ObjectConstantOperand) && value.equals(((ObjectConstantOperand) op).value);
  }

  /**
   * Returns the string representation of this operand.
   *
   * @return a string representation of this operand.
   */
  public String toString() {
    return "object \"" + value + "\"";
  }

  public Address toAddress() {
    // TODO Auto-generated method stub
    return null;
  }
}
