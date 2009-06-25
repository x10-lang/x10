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
import x10me.opt.controlflow.BasicBlock;
import x10me.opt.controlflow.BasicBlockEnumeration;
import x10me.opt.ir.operand.*;

/**
 * The LowTableSwitchParent instruction class.
 *
 * The header comment for {@link Instruction} contains
 * an explanation of the role of these interior class in 
 * opt compiler's IR.
 */
public abstract class LowTableSwitchParent extends Instruction
  implements HasTargets, HasVariableOperands {

  /**        
    * index use RegisterOperand.
    */
  private RegisterOperand index;

  /**
   * Get the operand called index from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called index
   */
  public final RegisterOperand getIndex() {
    return index;
  }

  /**
   * Get the operand called index from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called index
   */
  public final RegisterOperand getClearIndex() {
    RegisterOperand tmp = index;
    index = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called index in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setIndex(RegisterOperand o) {
    if (o == null) {
      index = null;
    } else {      
      assert o.instruction == null;
      index = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named index?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named index or <code>false</code>
   *         if it does not.
   */
  public final boolean hasIndex() {
    return index != null;
  }

  /**
   * Count of the number of variable parameters.
   */
  private int count;

  /**        
    * target use BranchOperand.
    */
  private BranchOperand target[];

  /**
   * Get the k'th operand called target from this
   * instruction. Note that the returned operand
   * will still point to its containing instruction.
   * @param k the index of the operand
   * @return the k'th operand called target
   */
  public final BranchOperand getTarget(int k) {
    return target[k];
  }

  /**
   * Get the k'th operand called target from this
   * instruction clearing its instruction pointer. The returned
   * operand will not point to any containing instruction.
   * @param k the index of the operand
   * @return the k'th operand called target
   */
  public final BranchOperand getClearTarget(int k) {
    BranchOperand tmp = target[k];
    target[k] = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the k'th operand called target in this
   * instruction to the argument operand. The operand will
   * now point to this instruction as its containing
   * instruction.
   * @param k the index of the operand
   * @param o the operand to store
   */
  public final void setTarget(int k, BranchOperand o) {
    if (o == null) {
      target = null;
    } else {      
      assert o.instruction == null;
      target[k] = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * k'th operand named target?
   * @param k the index of the operand.
   * @return <code>true</code> if the instruction has an non-null
   *         k'th operand named target or <code>false</code>
   *         if it does not.
   */
  public final boolean hasTarget(int k) {
    return target[k] != null;
  }

  /**
   * Does this instruction have any target operands?
   * @return <code>true</code> if this instruction has 
   *         target operands or <code>false</code> 
   *         if it does not.
   */
  public final boolean hasTargets()
  {
    return count != 0;
  }

  /**        
    * branchProfile use BranchProfileOperand.
    */
  private BranchProfileOperand branchProfile[];

  /**
   * Get the k'th operand called branchProfile from this
   * instruction. Note that the returned operand
   * will still point to its containing instruction.
   * @param k the index of the operand
   * @return the k'th operand called branchProfile
   */
  public final BranchProfileOperand getBranchProfile(int k) {
    return branchProfile[k];
  }

  /**
   * Get the k'th operand called branchProfile from this
   * instruction clearing its instruction pointer. The returned
   * operand will not point to any containing instruction.
   * @param k the index of the operand
   * @return the k'th operand called branchProfile
   */
  public final BranchProfileOperand getClearBranchProfile(int k) {
    BranchProfileOperand tmp = branchProfile[k];
    branchProfile[k] = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the k'th operand called branchProfile in this
   * instruction to the argument operand. The operand will
   * now point to this instruction as its containing
   * instruction.
   * @param k the index of the operand
   * @param o the operand to store
   */
  public final void setBranchProfile(int k, BranchProfileOperand o) {
    if (o == null) {
      branchProfile = null;
    } else {      
      assert o.instruction == null;
      branchProfile[k] = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * k'th operand named branchProfile?
   * @param k the index of the operand.
   * @return <code>true</code> if the instruction has an non-null
   *         k'th operand named branchProfile or <code>false</code>
   *         if it does not.
   */
  public final boolean hasBranchProfile(int k) {
    return branchProfile[k] != null;
  }

  /**
   * Does this instruction have any branchProfile operands?
   * @return <code>true</code> if this instruction has 
   *         branchProfile operands or <code>false</code> 
   *         if it does not.
   */
  public final boolean hasBranchProfiles()
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
	BranchOperand newTarget[] = new BranchOperand[numVarOps];
	System.arraycopy (target, 0, newTarget, 0, l);
	target = newTarget; 
	BranchProfileOperand newBranchProfile[] = new BranchProfileOperand[numVarOps];
	System.arraycopy (branchProfile, 0, newBranchProfile, 0, l);
	branchProfile = newBranchProfile; 
        count = numVarOps;
      }
  }

  /**
    * Constructor for LowTableSwitchParent.
    *
    * @param index
    * @param numVarOps of the number of variable arguments.
    */
  LowTableSwitchParent (RegisterOperand index, int numVarOps) {
    this.index = index;
    this.target = new BranchOperand[numVarOps]; 
    this.branchProfile = new BranchProfileOperand[numVarOps]; 
    this.count = numVarOps;
  }

  @Override
  public final int getNumberOfOperands() {
      return 0 + 1 + (count * 2); 
  }

  @Override
  public final int getNumberOfDefs() {
      return 0;
  }

  @Override
  public final int getNumberOfUses() {
      return 1 + (count * 2); 
  }

  @Override
  public final Operand getOperand(int i) {
    switch (i) {
      case 0:
        return index;
      default:
        if (i < (1 + ((0 + 1) * this.count)))
	  return this.target[(1 + (0 * this.count)) + i];
        else
        if (i < (1 + ((1 + 1) * this.count)))
	  return this.branchProfile[(1 + (1 * this.count)) + i];
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
        index = (RegisterOperand)op;
      break;
      default:
        if (i < (1 + ((0 + 1) * this.count)))
	  this.target[(1 + (0 * this.count)) + i] = (BranchOperand)op;
        else
        if (i < (1 + ((1 + 1) * this.count)))
	  this.branchProfile[(1 + (1 * this.count)) + i] = (BranchProfileOperand)op;
        else
        throw new ArrayIndexOutOfBoundsException ();
    }
  }
  
  /**
   * Return an enumeration of the basic blocks that are targets of this
   * branch instruction.
   *
   * @return the targets of this branch instruction
   */
  public final BasicBlockEnumeration getBranchTargets() {
    BasicBlock.ComputedBBEnum e = new BasicBlock.ComputedBBEnum(count);
    for (int i = 0; i < count; i++) {
	  e.addPossiblyDuplicateElement(target[i].target.getBasicBlock());
    }
    return e;
  }
}
