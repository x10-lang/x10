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
 * The CacheOp instruction class.
 *
 * The header comment for {@link Instruction} contains
 * an explanation of the role of these interior class in 
 * opt compiler's IR.
 */
public abstract class CacheOp extends Instruction {

  /**        
    * ref use Operand.
    */
  private Operand ref;

  /**
   * Get the operand called ref from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called ref
   */
  public final Operand getRef() {
    return ref;
  }

  /**
   * Get the operand called ref from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called ref
   */
  public final Operand getClearRef() {
    Operand tmp = ref;
    ref = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called ref in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setRef(Operand o) {
    if (o == null) {
      ref = null;
    } else {      
      assert o.instruction == null;
      ref = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named ref?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named ref or <code>false</code>
   *         if it does not.
   */
  public final boolean hasRef() {
    return ref != null;
  }


  /**
    * Constructor for CacheOp.
    *
    * @param ref
    */
  CacheOp (Operand ref) {
    this.ref = ref;
  }

  @Override
  public final int getNumberOfOperands() {
      return 0 + 1;
  }

  @Override
  public final int getNumberOfDefs() {
      return 0;
  }

  @Override
  public final int getNumberOfUses() {
      return 1; 
  }

  @Override
  public final Operand getOperand(int i) {
    switch (i) {
      case 0:
        return ref;
      default:
        throw new ArrayIndexOutOfBoundsException ();
    }
  }

  @Override
  public final void putOperand(int i, Operand op) {
    if (op != null)
      assert op.instruction == null;

    switch (i) {
      case 0:
        ref = (Operand)op;
      break;
      default:
        throw new ArrayIndexOutOfBoundsException ();
    }
  }
}
