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
 * Class file for Call Instruction class.
 */
public final class Call extends CallParent {

  /**
    * Constructor for Call.
    *
    * @param result
    * @param address
    * @param method
    * @param guard
    * @param numVarOps of the number of variable arguments.
    */
  public Call (Operand result, Operand address, MethodOperand method, Operand guard, int numVarOps) {
    super (result, address, method, guard, numVarOps);
  }
  /**
    * Constructor for Call without option parameter.
    *
    * @param result
    * @param address
    * @param method
    * @param numVarOps of the number of variable arguments.
    */
  public Call (Operand result, Operand address, MethodOperand method, int numVarOps) {
    super (result, address, method, null, numVarOps);
  }

  /**
    * Return the name of the instruction.
    */
  public String nameOf() {
    return "Call";
  }

  @Override
  public char getOpcode() {
    return Operators.Call;
  }

  @Override
  public boolean isCall() {
    return true;
  }

  @Override
  public boolean isImplicitLoad() {
    return true;
  }

  @Override
  public boolean isImplicitStore() {
    return true;
  }

  @Override
  public boolean isPEI() {
    return true;
  }

  @Override
  public boolean isGCPoint() {
    return true;
  }

  @Override
  public boolean isDynamicLinkPoint() {
    return true;
  }

}
