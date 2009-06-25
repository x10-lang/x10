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
 * Class file for GetStatic Instruction class.
 */
public final class GetStatic extends GetStaticParent {

  /**
    * Constructor for GetStatic.
    *
    * @param result
    * @param offset
    * @param field
    */
  public GetStatic (Operand result, Operand offset, FieldOperand field) {
    super (result, offset, field);
  }

  /**
    * Return the name of the instruction.
    */
  public String nameOf() {
    return "GetStatic";
  }

  @Override
  public char getOpcode() {
    return Operators.GetStatic;
  }

  @Override
  public boolean isExplicitLoad() {
    return true;
  }

  @Override
  public boolean isImplicitLoad() {
    return true;
  }

}
