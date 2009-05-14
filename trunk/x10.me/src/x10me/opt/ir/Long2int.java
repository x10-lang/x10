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
 * Class file for Long2int Instruction class.
 */
public final class Long2int extends Unary {

  /**
    * Constructor for Long2int.
    *
    * @param result
    * @param val
    */
  public Long2int (Operand result, Operand val) {
    super (result, val);
  }

  /**
    * Return the name of the instruction.
    */
  public String nameOf() {
    return "Long2int";
  }

  @Override
  public char getOpcode() {
    return Operators.Long2int;
  }

}
