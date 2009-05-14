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

public interface HasVariableOperands {
  /**
   * How many variable-length operands 
   * does this instruction have?
   * @return the number of operands this instruction has.
   */
  public int getNumberOf();

  /**
   * Change the number of variable length operands that may be stored in
   * this instruction to numVarOps.
   * @param numVarOps the new number of variable length operands 
   *        that may be stored in the instruction
   */
  public void resize(int numVarOps);
}
