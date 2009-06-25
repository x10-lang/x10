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

package x10me.opt.ir.operand;

import x10me.opt.ir.Instruction;

/**
 * Simular to java.util.Enumeration except that next returns an Operand.  
 */
public class OperandEnumeration {
  protected final Instruction instr;
  protected int i;
  protected final int end;

  /**
   * Create an iteration for the operands in INSTR with COUNT elements 
   * starting at operand START.
   */ 
  public OperandEnumeration(Instruction instr, int start, int count) {
    this.instr = instr;
    this.i = start;
    this.end = start + count;
  }

  /**
   * Return true if the enumeration has more elements.
   */
  public boolean hasMoreElements () {
    return i < this.end;
  }

  /**
   * Return the next Operand.
   */
  public Operand next() {
    if (i == this.end)
      throw new java.util.NoSuchElementException("OperandEnumerator");

    return this.instr.getOperand (this.i++);
  }
}

