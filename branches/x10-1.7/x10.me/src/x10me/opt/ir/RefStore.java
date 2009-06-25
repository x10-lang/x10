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
 * Class file for RefStore Instruction class.
 */
public final class RefStore extends Store {

  /**
    * Constructor for RefStore.
    *
    * @param value
    * @param address
    * @param offset
    * @param field
    * @param guard
    */
  public RefStore (Operand value, Operand address, Operand offset, FieldOperand field, Operand guard) {
    super (value, address, offset, field, guard);
  }
  /**
    * Constructor for RefStore without option parameter.
    *
    * @param value
    * @param address
    * @param offset
    * @param field
    */
  public RefStore (Operand value, Operand address, Operand offset, FieldOperand field) {
    super (value, address, offset, field, null);
  }

  /**
    * Return the name of the instruction.
    */
  public String nameOf() {
    return "RefStore";
  }

  @Override
  public char getOpcode() {
    return Operators.RefStore;
  }

  @Override
  public boolean isExplicitStore() {
    return true;
  }

  @Override
  public boolean isImplicitStore() {
    return true;
  }

}
