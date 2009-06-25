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
 * Class file for YieldpointEpilogue Instruction class.
 */
public final class YieldpointEpilogue extends Empty {

  /**
    * Constructor for YieldpointEpilogue.
    *
    */
  public YieldpointEpilogue () {
    super ();
  }

  /**
    * Return the name of the instruction.
    */
  public String nameOf() {
    return "YieldpointEpilogue";
  }

  @Override
  public char getOpcode() {
    return Operators.YieldpointEpilogue;
  }

  @Override
  public boolean isGCPoint() {
    return true;
  }

  @Override
  public boolean isYieldPoint() {
    return true;
  }

}
