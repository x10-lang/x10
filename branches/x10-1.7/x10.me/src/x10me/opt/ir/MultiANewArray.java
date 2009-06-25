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
 * The MultiANewArray instruction class.
 *
 * The header comment for {@link Instruction} contains
 * an explanation of the role of these interior class in 
 * opt compiler's IR.
 */
public abstract class MultiANewArray extends Instruction
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
    * type use TypeOperand.
    */
  private TypeOperand type;

  /**
   * Get the operand called type from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called type
   */
  public final TypeOperand getType() {
    return type;
  }

  /**
   * Get the operand called type from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called type
   */
  public final TypeOperand getClearType() {
    TypeOperand tmp = type;
    type = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called type in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setType(TypeOperand o) {
    if (o == null) {
      type = null;
    } else {      
      assert o.instruction == null;
      type = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named type?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named type or <code>false</code>
   *         if it does not.
   */
  public final boolean hasType() {
    return type != null;
  }

  /**
   * Count of the number of variable parameters.
   */
  private int count;

  /**        
    * dimension use Operand.
    */
  private Operand dimension[];

  /**
   * Get the k'th operand called dimension from this
   * instruction. Note that the returned operand
   * will still point to its containing instruction.
   * @param k the index of the operand
   * @return the k'th operand called dimension
   */
  public final Operand getDimension(int k) {
    return dimension[k];
  }

  /**
   * Get the k'th operand called dimension from this
   * instruction clearing its instruction pointer. The returned
   * operand will not point to any containing instruction.
   * @param k the index of the operand
   * @return the k'th operand called dimension
   */
  public final Operand getClearDimension(int k) {
    Operand tmp = dimension[k];
    dimension[k] = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the k'th operand called dimension in this
   * instruction to the argument operand. The operand will
   * now point to this instruction as its containing
   * instruction.
   * @param k the index of the operand
   * @param o the operand to store
   */
  public final void setDimension(int k, Operand o) {
    if (o == null) {
      dimension = null;
    } else {      
      assert o.instruction == null;
      dimension[k] = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * k'th operand named dimension?
   * @param k the index of the operand.
   * @return <code>true</code> if the instruction has an non-null
   *         k'th operand named dimension or <code>false</code>
   *         if it does not.
   */
  public final boolean hasDimension(int k) {
    return dimension[k] != null;
  }

  /**
   * Does this instruction have any dimension operands?
   * @return <code>true</code> if this instruction has 
   *         dimension operands or <code>false</code> 
   *         if it does not.
   */
  public final boolean hasDimensions()
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
	Operand newDimension[] = new Operand[numVarOps];
	System.arraycopy (dimension, 0, newDimension, 0, l);
	dimension = newDimension; 
        count = numVarOps;
      }
  }

  /**
    * Constructor for MultiANewArray.
    *
    * @param result
    * @param type
    * @param numVarOps of the number of variable arguments.
    */
  MultiANewArray (Operand result, TypeOperand type, int numVarOps) {
    this.result = result;
    this.type = type;
    this.dimension = new Operand[numVarOps]; 
    this.count = numVarOps;
  }

  @Override
  public final int getNumberOfOperands() {
      return 1 + 1 + (count * 1); 
  }

  @Override
  public final int getNumberOfDefs() {
      return 1;
  }

  @Override
  public final int getNumberOfUses() {
      return 1 + (count * 1); 
  }

  @Override
  public final Operand getOperand(int i) {
    switch (i) {
      case 0:
        return result;
      case 1:
        return type;
      default:
        if (i < (2 + ((0 + 1) * this.count)))
	  return this.dimension[(2 + (0 * this.count)) + i];
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
      case 1:
        type = (TypeOperand)op;
      break;
      default:
        if (i < (2 + ((0 + 1) * this.count)))
	  this.dimension[(2 + (0 * this.count)) + i] = (Operand)op;
        else
        throw new ArrayIndexOutOfBoundsException ();
    }
  }
}
