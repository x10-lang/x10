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
 * Class file for IntIfcmp2 Instruction class.
 */
public final class IntIfcmp2 extends IfCmp2 {

  /**
    * Constructor for IntIfcmp2.
    *
    * @param guardResult
    * @param val1
    * @param val2
    * @param cond1
    * @param target1
    * @param branchProfile1
    * @param cond2
    * @param target2
    * @param branchProfile2
    */
  public IntIfcmp2 (RegisterOperand guardResult, Operand val1, Operand val2, ConditionOperand cond1, BranchOperand target1, BranchProfileOperand branchProfile1, ConditionOperand cond2, BranchOperand target2, BranchProfileOperand branchProfile2) {
    super (guardResult, val1, val2, cond1, target1, branchProfile1, cond2, target2, branchProfile2);
  }

  /**
    * Return the name of the instruction.
    */
  public String nameOf() {
    return "IntIfcmp2";
  }

  @Override
  public char getOpcode() {
    return Operators.IntIfcmp2;
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
