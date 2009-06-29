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
 * Class file for MustImplementInterface Instruction class.
 */
public final class MustImplementInterface extends TypeCheck {

  /**
    * Constructor for MustImplementInterface.
    *
    * @param result
    * @param ref
    * @param type
    * @param guard
    */
  public MustImplementInterface (Operand result, Operand ref, TypeOperand type, Operand guard) {
    super (result, ref, type, guard);
  }
  /**
    * Constructor for MustImplementInterface without option parameter.
    *
    * @param result
    * @param ref
    * @param type
    */
  public MustImplementInterface (Operand result, Operand ref, TypeOperand type) {
    super (result, ref, type, null);
  }

  /**
    * Return the name of the instruction.
    */
  public String nameOf() {
    return "MustImplementInterface";
  }

  @Override
  public char getOpcode() {
    return Operators.MustImplementInterface;
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
