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
 * Class file for BoundsCheck Instruction class.
 */
public final class BoundsCheck extends BoundsCheckParent {

  /**
    * Constructor for BoundsCheck.
    *
    * @param guardResult
    * @param ref
    * @param index
    * @param guard
    */
  public BoundsCheck (RegisterOperand guardResult, ArrayOperand ref, Operand index, Operand guard) {
    super (guardResult, ref, index, guard);
  }

  /**
    * Return the name of the instruction.
    */
  public String nameOf() {
    return "BoundsCheck";
  }

  @Override
  public char getOpcode() {
    return Operators.BoundsCheck;
  }

  @Override
  public boolean isPEI() {
    return true;
  }

  @Override
  public boolean isGCPoint() {
    return true;
  }

}
