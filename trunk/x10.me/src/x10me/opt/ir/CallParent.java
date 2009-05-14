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
 * The CallParent instruction class.
 *
 * The header comment for {@link Instruction} contains
 * an explanation of the role of these interior class in 
 * opt compiler's IR.
 */
public abstract class CallParent extends Instruction
  implements HasResult, HasGuard, HasVariableOperands {

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
    * address use Operand.
    */
  private Operand address;

  /**
   * Get the operand called address from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called address
   */
  public final Operand getAddress() {
    return address;
  }

  /**
   * Get the operand called address from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called address
   */
  public final Operand getClearAddress() {
    Operand tmp = address;
    address = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called address in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setAddress(Operand o) {
    if (o == null) {
      address = null;
    } else {      
      assert o.instruction == null;
      address = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named address?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named address or <code>false</code>
   *         if it does not.
   */
  public final boolean hasAddress() {
    return address != null;
  }


  /**        
    * method use MethodOperand.
    */
  private MethodOperand method;

  /**
   * Get the operand called method from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called method
   */
  public final MethodOperand getMethod() {
    return method;
  }

  /**
   * Get the operand called method from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called method
   */
  public final MethodOperand getClearMethod() {
    MethodOperand tmp = method;
    method = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called method in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setMethod(MethodOperand o) {
    if (o == null) {
      method = null;
    } else {      
      assert o.instruction == null;
      method = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named method?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named method or <code>false</code>
   *         if it does not.
   */
  public final boolean hasMethod() {
    return method != null;
  }


  /**        
    * guard use Operand.
    */
  private Operand guard;

  /**
   * Get the operand called guard from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called guard
   */
  public final Operand getGuard() {
    return guard;
  }

  /**
   * Get the operand called guard from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called guard
   */
  public final Operand getClearGuard() {
    Operand tmp = guard;
    guard = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called guard in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setGuard(Operand o) {
    if (o == null) {
      guard = null;
    } else {      
      assert o.instruction == null;
      guard = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named guard?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named guard or <code>false</code>
   *         if it does not.
   */
  public final boolean hasGuard() {
    return guard != null;
  }

  /**
   * Count of the number of variable parameters.
   */
  private int count;

  /**        
    * param use Operand.
    */
  private Operand param[];

  /**
   * Get the k'th operand called param from this
   * instruction. Note that the returned operand
   * will still point to its containing instruction.
   * @param k the index of the operand
   * @return the k'th operand called param
   */
  public final Operand getParam(int k) {
    return param[k];
  }

  /**
   * Get the k'th operand called param from this
   * instruction clearing its instruction pointer. The returned
   * operand will not point to any containing instruction.
   * @param k the index of the operand
   * @return the k'th operand called param
   */
  public final Operand getClearParam(int k) {
    Operand tmp = param[k];
    param[k] = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the k'th operand called param in this
   * instruction to the argument operand. The operand will
   * now point to this instruction as its containing
   * instruction.
   * @param k the index of the operand
   * @param o the operand to store
   */
  public final void setParam(int k, Operand o) {
    if (o == null) {
      param = null;
    } else {      
      assert o.instruction == null;
      param[k] = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * k'th operand named param?
   * @param k the index of the operand.
   * @return <code>true</code> if the instruction has an non-null
   *         k'th operand named param or <code>false</code>
   *         if it does not.
   */
  public final boolean hasParam(int k) {
    return param[k] != null;
  }

  /**
   * Does this instruction have any param operands?
   * @return <code>true</code> if this instruction has 
   *         param operands or <code>false</code> 
   *         if it does not.
   */
  public final boolean hasParams()
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
	Operand newParam[] = new Operand[numVarOps];
	System.arraycopy (param, 0, newParam, 0, l);
	param = newParam; 
        count = numVarOps;
      }
  }

  /**
    * Constructor for CallParent.
    *
    * @param result
    * @param address
    * @param method
    * @param guard
    * @param numVarOps of the number of variable arguments.
    */
  CallParent (Operand result, Operand address, MethodOperand method, Operand guard, int numVarOps) {
    this.result = result;
    this.address = address;
    this.method = method;
    this.guard = guard;
    this.param = new Operand[numVarOps]; 
    this.count = numVarOps;
  }

  @Override
  public final int getNumberOfOperands() {
      return 1 + 3 + (count * 1); 
  }

  @Override
  public final int getNumberOfDefs() {
      return 1;
  }

  @Override
  public final int getNumberOfUses() {
      return 3 + (count * 1); 
  }

  @Override
  public final Operand getOperand(int i) {
    switch (i) {
      case 0:
        return result;
      case 1:
        return address;
      case 2:
        return method;
      case 3:
        return guard;
      default:
        if (i < (4 + ((0 + 1) * this.count)))
	  return this.param[(4 + (0 * this.count)) + i];
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
        address = (Operand)op;
      break;
      case 2:
        method = (MethodOperand)op;
      break;
      case 3:
        guard = (Operand)op;
      break;
      default:
        if (i < (4 + ((0 + 1) * this.count)))
	  this.param[(4 + (0 * this.count)) + i] = (Operand)op;
        else
        throw new ArrayIndexOutOfBoundsException ();
    }
  }
}
