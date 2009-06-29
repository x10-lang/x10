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
 * The PhiParent instruction class.
 *
 * The header comment for {@link Instruction} contains
 * an explanation of the role of these interior class in 
 * opt compiler's IR.
 */
public abstract class PhiParent extends Instruction
  implements HasResult, HasVariableOperands {

  /**        
    * result def Operand.
    */
  private Operand result;

  /**
   * Get the operand called result from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called result
   */
  public final Operand getResult() {
    return result;
  }

  /**
   * Get the operand called result from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called result
   */
  public final Operand getClearResult() {
    Operand tmp = result;
    result = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called result in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setResult(Operand o) {
    if (o == null) {
      result = null;
    } else {      
      assert o.instruction == null;
      result = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named result?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named result or <code>false</code>
   *         if it does not.
   */
  public final boolean hasResult() {
    return result != null;
  }

  /**
   * Count of the number of variable parameters.
   */
  private int count;

  /**        
    * value use Operand.
    */
  private Operand value[];

  /**
   * Get the k'th operand called value from this
   * instruction. Note that the returned operand
   * will still point to its containing instruction.
   * @param k the index of the operand
   * @return the k'th operand called value
   */
  public final Operand getValue(int k) {
    return value[k];
  }

  /**
   * Get the k'th operand called value from this
   * instruction clearing its instruction pointer. The returned
   * operand will not point to any containing instruction.
   * @param k the index of the operand
   * @return the k'th operand called value
   */
  public final Operand getClearValue(int k) {
    Operand tmp = value[k];
    value[k] = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the k'th operand called value in this
   * instruction to the argument operand. The operand will
   * now point to this instruction as its containing
   * instruction.
   * @param k the index of the operand
   * @param o the operand to store
   */
  public final void setValue(int k, Operand o) {
    if (o == null) {
      value = null;
    } else {      
      assert o.instruction == null;
      value[k] = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * k'th operand named value?
   * @param k the index of the operand.
   * @return <code>true</code> if the instruction has an non-null
   *         k'th operand named value or <code>false</code>
   *         if it does not.
   */
  public final boolean hasValue(int k) {
    return value[k] != null;
  }

  /**
   * Does this instruction have any value operands?
   * @return <code>true</code> if this instruction has 
   *         value operands or <code>false</code> 
   *         if it does not.
   */
  public final boolean hasValues()
  {
    return count != 0;
  }

  /**        
    * pred use BasicBlockOperand.
    */
  private BasicBlockOperand pred[];

  /**
   * Get the k'th operand called pred from this
   * instruction. Note that the returned operand
   * will still point to its containing instruction.
   * @param k the index of the operand
   * @return the k'th operand called pred
   */
  public final BasicBlockOperand getPred(int k) {
    return pred[k];
  }

  /**
   * Get the k'th operand called pred from this
   * instruction clearing its instruction pointer. The returned
   * operand will not point to any containing instruction.
   * @param k the index of the operand
   * @return the k'th operand called pred
   */
  public final BasicBlockOperand getClearPred(int k) {
    BasicBlockOperand tmp = pred[k];
    pred[k] = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the k'th operand called pred in this
   * instruction to the argument operand. The operand will
   * now point to this instruction as its containing
   * instruction.
   * @param k the index of the operand
   * @param o the operand to store
   */
  public final void setPred(int k, BasicBlockOperand o) {
    if (o == null) {
      pred = null;
    } else {      
      assert o.instruction == null;
      pred[k] = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * k'th operand named pred?
   * @param k the index of the operand.
   * @return <code>true</code> if the instruction has an non-null
   *         k'th operand named pred or <code>false</code>
   *         if it does not.
   */
  public final boolean hasPred(int k) {
    return pred[k] != null;
  }

  /**
   * Does this instruction have any pred operands?
   * @return <code>true</code> if this instruction has 
   *         pred operands or <code>false</code> 
   *         if it does not.
   */
  public final boolean hasPreds()
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
	Operand newValue[] = new Operand[numVarOps];
	System.arraycopy (value, 0, newValue, 0, l);
	value = newValue; 
	BasicBlockOperand newPred[] = new BasicBlockOperand[numVarOps];
	System.arraycopy (pred, 0, newPred, 0, l);
	pred = newPred; 
        count = numVarOps;
      }
  }

  /**
    * Constructor for PhiParent.
    *
    * @param result
    * @param numVarOps of the number of variable arguments.
    */
  PhiParent (Operand result, int numVarOps) {
    this.result = result;
    this.value = new Operand[numVarOps]; 
    this.pred = new BasicBlockOperand[numVarOps]; 
    this.count = numVarOps;
  }

  @Override
  public final int getNumberOfOperands() {
      return 1 + 0 + (count * 2); 
  }

  @Override
  public final int getNumberOfDefs() {
      return 1;
  }

  @Override
  public final int getNumberOfUses() {
      return 0 + (count * 2); 
  }

  @Override
  public final Operand getOperand(int i) {
    switch (i) {
      case 0:
        return result;
      default:
        if (i < (1 + ((0 + 1) * this.count)))
	  return this.value[(1 + (0 * this.count)) + i];
        else
        if (i < (1 + ((1 + 1) * this.count)))
	  return this.pred[(1 + (1 * this.count)) + i];
        else
        throw new ArrayIndexOutOfBoundsException ();
    }
  }

  @Override
  public final void putOperand(int i, Operand op) {
    if (op != null)
      assert op.instruction == null;

    switch (i) {
      case 0:
        result = (Operand)op;
      break;
      default:
        if (i < (1 + ((0 + 1) * this.count)))
	  this.value[(1 + (0 * this.count)) + i] = (Operand)op;
        else
        if (i < (1 + ((1 + 1) * this.count)))
	  this.pred[(1 + (1 * this.count)) + i] = (BasicBlockOperand)op;
        else
        throw new ArrayIndexOutOfBoundsException ();
    }
  }
}
