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

public interface HasResult {
  /**
   * Get the operand called result from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called result
   */
  public Operand getResult();

  /**
   * Get the operand called result from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called result
   */
  public Operand getClearResult();

  /**
   * Set the operand called result in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public void setResult(Operand o);

  /**
   * Does this instruction have a non-null
   * operand named result?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named result or <code>false</code>
   *         if it does not.
   */
  public boolean hasResult();
}
