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

/**
 * Class file for IrPrologue Instruction class.
 */
public final class IrPrologue extends Prologue {

  /**
    * Constructor for IrPrologue.
    *
    * @param numVarOps of the number of variable arguments.
    */
  public IrPrologue (int numVarOps) {
    super (numVarOps);
  }

  /**
    * Return the name of the instruction.
    */
  public String nameOf() {
    return "IrPrologue";
  }

  @Override
  public char getOpcode() {
    return Operators.IrPrologue;
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
