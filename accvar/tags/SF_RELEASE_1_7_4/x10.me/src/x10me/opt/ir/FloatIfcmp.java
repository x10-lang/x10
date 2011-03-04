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
 * Class file for FloatIfcmp Instruction class.
 */
public final class FloatIfcmp extends IfCmp {

  /**
    * Constructor for FloatIfcmp.
    *
    * @param guardResult
    * @param val1
    * @param val2
    * @param cond
    * @param target
    * @param branchProfile
    */
  public FloatIfcmp (RegisterOperand guardResult, Operand val1, Operand val2, ConditionOperand cond, BranchOperand target, BranchProfileOperand branchProfile) {
    super (guardResult, val1, val2, cond, target, branchProfile);
  }

  /**
    * Return the name of the instruction.
    */
  public String nameOf() {
    return "FloatIfcmp";
  }

  @Override
  public char getOpcode() {
    return Operators.FloatIfcmp;
  }

  @Override
  public boolean isBranch() {
    return true;
  }

  @Override
  public boolean isConditionalBranch() {
    return true;
  }

}
