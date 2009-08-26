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

package x10me.opt.ir;

import x10me.opt.ir.operand.*;

/**
 * Class file for AttemptLong Instruction class.
 */
public final class AttemptLong extends Attempt {

  /**
    * Constructor for AttemptLong.
    *
    * @param result
    * @param address
    * @param offset
    * @param oldValue
    * @param newValue
    * @param field
    * @param guard
    */
  public AttemptLong (Operand result, Operand address, Operand offset, Operand oldValue, Operand newValue, FieldOperand field, Operand guard) {
    super (result, address, offset, oldValue, newValue, field, guard);
  }
  /**
    * Constructor for AttemptLong without option parameter.
    *
    * @param result
    * @param address
    * @param offset
    * @param oldValue
    * @param newValue
    * @param field
    */
  public AttemptLong (Operand result, Operand address, Operand offset, Operand oldValue, Operand newValue, FieldOperand field) {
    super (result, address, offset, oldValue, newValue, field, null);
  }

  /**
    * Return the name of the instruction.
    */
  public String nameOf() {
    return "AttemptLong";
  }

  @Override
  public char getOpcode() {
    return Operators.AttemptLong;
  }

  @Override
  public boolean isExplicitLoad() {
    return true;
  }

  @Override
  public boolean isImplicitLoad() {
    return true;
  }

  @Override
  public boolean isExplicitStore() {
    return true;
  }

  @Override
  public boolean isImplicitStore() {
    return true;
  }

  @Override
  public boolean isCompare() {
    return true;
  }

  @Override
  public boolean isRelease() {
    return true;
  }

}
