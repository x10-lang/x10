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
 * The Prologue instruction class.
 *
 * The header comment for {@link Instruction} contains
 * an explanation of the role of these interior class in 
 * opt compiler's IR.
 */
public abstract class Prologue extends Instruction
  implements HasVariableOperands {
  /**
   * Count of the number of variable parameters.
   */
  private int count;

  /**        
    * formal use RegisterOperand.
    */
  private RegisterOperand formal[];

  /**
   * Get the k'th operand called formal from this
   * instruction. Note that the returned operand
   * will still point to its containing instruction.
   * @param k the index of the operand
   * @return the k'th operand called formal
   */
  public final RegisterOperand getFormal(int k) {
    return formal[k];
  }

  /**
   * Get the k'th operand called formal from this
   * instruction clearing its instruction pointer. The returned
   * operand will not point to any containing instruction.
   * @param k the index of the operand
   * @return the k'th operand called formal
   */
  public final RegisterOperand getClearFormal(int k) {
    RegisterOperand tmp = formal[k];
    formal[k] = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the k'th operand called formal in this
   * instruction to the argument operand. The operand will
   * now point to this instruction as its containing
   * instruction.
   * @param k the index of the operand
   * @param o the operand to store
   */
  public final void setFormal(int k, RegisterOperand o) {
    if (o == null) {
      formal = null;
    } else {      
      assert o.instruction == null;
      formal[k] = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * k'th operand named formal?
   * @param k the index of the operand.
   * @return <code>true</code> if the instruction has an non-null
   *         k'th operand named formal or <code>false</code>
   *         if it does not.
   */
  public final boolean hasFormal(int k) {
    return formal[k] != null;
  }

  /**
   * Does this instruction have any formal operands?
   * @return <code>true</code> if this instruction has 
   *         formal operands or <code>false</code> 
   *         if it does not.
   */
  public final boolean hasFormals()
  {
    return count != 0;
  }

  /**
   * How many variable-length operands 
   * does this instruction have?
   * @return the number of operands this instruction has.
   */
  public final int getNumberOf()
  {
    return count;
  }

  /**
   * Change the number of variable length operands that may be stored in
   * this instruction to numVarOps.
   * @param numVarOps the new number of variable length operands 
   *        that may be stored in the instruction
   */
  public final void resize(int numVarOps) {
    if (numVarOps != count)
      {
        int l = Math.min (count, numVarOps);
	RegisterOperand newFormal[] = new RegisterOperand[numVarOps];
	System.arraycopy (formal, 0, newFormal, 0, l);
	formal = newFormal; 
        count = numVarOps;
      }
  }

  /**
    * Constructor for Prologue.
    *
    * @param numVarOps of the number of variable arguments.
    */
  Prologue (int numVarOps) {
    this.formal = new RegisterOperand[numVarOps]; 
    this.count = numVarOps;
  }

  @Override
  public final int getNumberOfOperands() {
      return 0 + 0 + (count * 1); 
  }

  @Override
  public final int getNumberOfDefs() {
      return 0 + (count * 1); 
  }

  @Override
  public final int getNumberOfUses() {
      return 0; 
  }

  @Override
  public final Operand getOperand(int i) {
    switch (i) {
      default:
        if (i < (0 + ((0 + 1) * this.count)))
	  return this.formal[(0 + (0 * this.count)) + i];
        else
        throw new ArrayIndexOutOfBoundsException ();
    }
  }

  @Override
  public final void putOperand(int i, Operand op) {
    if (op != null)
      assert op.instruction == null;

    switch (i) {
      default:
        if (i < (0 + ((0 + 1) * this.count)))
	  this.formal[(0 + (0 * this.count)) + i] = (RegisterOperand)op;
        else
        throw new ArrayIndexOutOfBoundsException ();
    }
  }
}
