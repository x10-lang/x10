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
 * Class file for ShortAstore Instruction class.
 */
public final class ShortAstore extends AStore {

  /**
    * Constructor for ShortAstore.
    *
    * @param value
    * @param array
    * @param index
    * @param field
    * @param guard
    */
  public ShortAstore (Operand value, Operand array, Operand index, FieldOperand field, Operand guard) {
    super (value, array, index, field, guard);
  }

  /**
    * Return the name of the instruction.
    */
  public String nameOf() {
    return "ShortAstore";
  }

  @Override
  public char getOpcode() {
    return Operators.ShortAstore;
  }

  @Override
  public boolean isExplicitStore() {
    return true;
  }

  @Override
  public boolean isImplicitStore() {
    return true;
  }

}
