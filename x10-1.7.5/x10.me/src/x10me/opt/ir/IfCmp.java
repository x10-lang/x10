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
 * The IfCmp instruction class.
 *
 * The header comment for {@link Instruction} contains
 * an explanation of the role of these interior class in 
 * opt compiler's IR.
 */
public abstract class IfCmp extends Instruction
  implements HasGuardResult, HasTarget, HasTargets, HasBranchProfile {

  /**        
    * guardResult def RegisterOperand.
    */
  private RegisterOperand guardResult;

  /**
   * Get the operand called guardResult from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called guardResult
   */
  public final RegisterOperand getGuardResult() {
    return guardResult;
  }

  /**
   * Get the operand called guardResult from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called guardResult
   */
  public final RegisterOperand getClearGuardResult() {
    RegisterOperand tmp = guardResult;
    guardResult = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called guardResult in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setGuardResult(RegisterOperand o) {
    if (o == null) {
      guardResult = null;
    } else {      
      assert o.instruction == null;
      guardResult = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named guardResult?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named guardResult or <code>false</code>
   *         if it does not.
   */
  public final boolean hasGuardResult() {
    return guardResult != null;
  }


  /**        
    * val1 use Operand.
    */
  private Operand val1;

  /**
   * Get the operand called val1 from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called val1
   */
  public final Operand getVal1() {
    return val1;
  }

  /**
   * Get the operand called val1 from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called val1
   */
  public final Operand getClearVal1() {
    Operand tmp = val1;
    val1 = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called val1 in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setVal1(Operand o) {
    if (o == null) {
      val1 = null;
    } else {      
      assert o.instruction == null;
      val1 = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named val1?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named val1 or <code>false</code>
   *         if it does not.
   */
  public final boolean hasVal1() {
    return val1 != null;
  }


  /**        
    * val2 use Operand.
    */
  private Operand val2;

  /**
   * Get the operand called val2 from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called val2
   */
  public final Operand getVal2() {
    return val2;
  }

  /**
   * Get the operand called val2 from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called val2
   */
  public final Operand getClearVal2() {
    Operand tmp = val2;
    val2 = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called val2 in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setVal2(Operand o) {
    if (o == null) {
      val2 = null;
    } else {      
      assert o.instruction == null;
      val2 = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named val2?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named val2 or <code>false</code>
   *         if it does not.
   */
  public final boolean hasVal2() {
    return val2 != null;
  }


  /**        
    * cond use ConditionOperand.
    */
  private ConditionOperand cond;

  /**
   * Get the operand called cond from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called cond
   */
  public final ConditionOperand getCond() {
    return cond;
  }

  /**
   * Get the operand called cond from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called cond
   */
  public final ConditionOperand getClearCond() {
    ConditionOperand tmp = cond;
    cond = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called cond in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setCond(ConditionOperand o) {
    if (o == null) {
      cond = null;
    } else {      
      assert o.instruction == null;
      cond = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named cond?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named cond or <code>false</code>
   *         if it does not.
   */
  public final boolean hasCond() {
    return cond != null;
  }


  /**        
    * target use BranchOperand.
    */
  private BranchOperand target;

  /**
   * Get the operand called target from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called target
   */
  public final BranchOperand getTarget() {
    return target;
  }

  /**
   * Get the operand called target from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called target
   */
  public final BranchOperand getClearTarget() {
    BranchOperand tmp = target;
    target = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called target in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setTarget(BranchOperand o) {
    if (o == null) {
      target = null;
    } else {      
      assert o.instruction == null;
      target = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named target?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named target or <code>false</code>
   *         if it does not.
   */
  public final boolean hasTarget() {
    return target != null;
  }

  /**        
    * branchProfile use BranchProfileOperand.
    */
  private BranchProfileOperand branchProfile;

  /**
   * Get the operand called branchProfile from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called branchProfile
   */
  public final BranchProfileOperand getBranchProfile() {
    return branchProfile;
  }

  /**
   * Get the operand called branchProfile from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called branchProfile
   */
  public final BranchProfileOperand getClearBranchProfile() {
    BranchProfileOperand tmp = branchProfile;
    branchProfile = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called branchProfile in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setBranchProfile(BranchProfileOperand o) {
    if (o == null) {
      branchProfile = null;
    } else {      
      assert o.instruction == null;
      branchProfile = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named branchProfile?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named branchProfile or <code>false</code>
   *         if it does not.
   */
  public final boolean hasBranchProfile() {
    return branchProfile != null;
  }


  /**
    * Constructor for IfCmp.
    *
    * @param guardResult
    * @param val1
    * @param val2
    * @param cond
    * @param target
    * @param branchProfile
    */
  IfCmp (RegisterOperand guardResult, Operand val1, Operand val2, ConditionOperand cond, BranchOperand target, BranchProfileOperand branchProfile) {
    this.guardResult = guardResult;
    this.val1 = val1;
    this.val2 = val2;
    this.cond = cond;
    this.target = target;
    this.branchProfile = branchProfile;
  }

  @Override
  public final int getNumberOfOperands() {
      return 1 + 5;
  }

  @Override
  public final int getNumberOfDefs() {
      return 1;
  }

  @Override
  public final int getNumberOfUses() {
      return 5; 
  }

  @Override
  public final Operand getOperand(int i) {
    switch (i) {
      case 0:
        return guardResult;
      case 1:
        return val1;
      case 2:
        return val2;
      case 3:
        return cond;
      case 4:
        return target;
      case 5:
        return branchProfile;
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
        guardResult = (RegisterOperand)op;
      break;
      case 1:
        val1 = (Operand)op;
      break;
      case 2:
        val2 = (Operand)op;
      break;
      case 3:
        cond = (ConditionOperand)op;
      break;
      case 4:
        target = (BranchOperand)op;
      break;
      case 5:
        branchProfile = (BranchProfileOperand)op;
      break;
      default:
        throw new ArrayIndexOutOfBoundsException ();
    }
  }  
  
  /**
   * Return the probability (in the range 0.0 - 1.0) that this two-way
   * branch instruction is taken (as opposed to falling through).
   *
   * @return The probability that the branch is taken.
   */
  public final float getBranchProbability() {
    return getBranchProfile().takenProbability;
  }

  /**
   * Record the probability (in the range 0.0 - 1.0) that this two-way
   * branch instruction is taken (as opposed to falling through).
   *
   * @param takenProbability    The probability that the branch is taken.
   */
  public final void setBranchProbability(float takenProbability) {
    getBranchProfile().takenProbability = takenProbability;
  }

  /**
   * Invert the probabilty of this branch being taken.  This method
   * should be called on a branch instruction when its condition is
   * reversed using flipCode().
   */
  public final void flipBranchProbability() {
    setBranchProbability(1.0f - getBranchProbability());
  }

  /**
   * Returns the basic block jumped to by this BRANCH instruction.
   *
   * @return the target of this branch instruction
   */	
  public final BasicBlock getBranchTarget () {
    return target.target.getBasicBlock();
  }

  /**
   * Return an enumeration of the basic blocks that are targets of this
   * branch instruction.
   *
   * @return the targets of this branch instruction
   */
  public final BasicBlockEnumeration getBranchTargets() {
    BasicBlock.ComputedBBEnum e = new BasicBlock.ComputedBBEnum(1);
    e.addElement(target.target.getBasicBlock());
    return e;
  }
}
