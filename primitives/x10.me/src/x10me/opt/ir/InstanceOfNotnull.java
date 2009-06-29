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
 * Class file for InstanceOfNotnull Instruction class.
 */
public final class InstanceOfNotnull extends InstanceOfParent {

  /**
    * Constructor for InstanceOfNotnull.
    *
    * @param result
    * @param type
    * @param ref
    * @param guard
    */
  public InstanceOfNotnull (Operand result, TypeOperand type, Operand ref, Operand guard) {
    super (result, type, ref, guard);
  }
  /**
    * Constructor for InstanceOfNotnull without option parameter.
    *
    * @param result
    * @param type
    * @param ref
    */
  public InstanceOfNotnull (Operand result, TypeOperand type, Operand ref) {
    super (result, type, ref, null);
  }

  /**
    * Return the name of the instruction.
    */
  public String nameOf() {
    return "InstanceOfNotnull";
  }

  @Override
  public char getOpcode() {
    return Operators.InstanceOfNotnull;
  }

}
