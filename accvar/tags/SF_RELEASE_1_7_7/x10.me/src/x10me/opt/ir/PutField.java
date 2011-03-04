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
 * Class file for PutField Instruction class.
 */
public final class PutField extends PutFieldParent {

  /**
    * Constructor for PutField.
    *
    * @param value
    * @param ref
    * @param offset
    * @param field
    * @param guard
    */
  public PutField (Operand value, Operand ref, Operand offset, FieldOperand field, Operand guard) {
    super (value, ref, offset, field, guard);
  }

  /**
    * Return the name of the instruction.
    */
  public String nameOf() {
    return "PutField";
  }

  @Override
  public char getOpcode() {
    return Operators.PutField;
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
