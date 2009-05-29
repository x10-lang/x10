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
 * Class file for GuardCondMove Instruction class.
 */
public final class GuardCondMove extends CondMove {

  /**
    * Constructor for GuardCondMove.
    *
    * @param result
    * @param val1
    * @param val2
    * @param cond
    * @param trueValue
    * @param falseValue
    */
  public GuardCondMove (Operand result, Operand val1, Operand val2, ConditionOperand cond, Operand trueValue, Operand falseValue) {
    super (result, val1, val2, cond, trueValue, falseValue);
  }

  /**
    * Return the name of the instruction.
    */
  public String nameOf() {
    return "GuardCondMove";
  }

  @Override
  public char getOpcode() {
    return Operators.GuardCondMove;
  }

  @Override
  public boolean isCompare() {
    return true;
  }

}
