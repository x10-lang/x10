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
 * The InlineGuard instruction class.
 *
 * The header comment for {@link Instruction} contains
 * an explanation of the role of these interior class in 
 * opt compiler's IR.
 */
public abstract class InlineGuard extends Instruction
  implements HasGuard, HasTarget, HasTargets, HasBranchProfile {

  /**        
    * value use Operand.
    */
  private Operand value;

  /**
   * Get the operand called value from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called value
   */
  public final Operand getValue() {
    return value;
  }

  /**
   * Get the operand called value from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called value
   */
  public final Operand getClearValue() {
    Operand tmp = value;
    value = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called value in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setValue(Operand o) {
    if (o == null) {
      value = null;
    } else {      
      assert o.instruction == null;
      value = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named value?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named value or <code>false</code>
   *         if it does not.
   */
  public final boolean hasValue() {
    return value != null;
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
    * goal use Operand.
    */
  private Operand goal;

  /**
   * Get the operand called goal from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called goal
   */
  public final Operand getGoal() {
    return goal;
  }

  /**
   * Get the operand called goal from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called goal
   */
  public final Operand getClearGoal() {
    Operand tmp = goal;
    goal = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called goal in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setGoal(Operand o) {
    if (o == null) {
      goal = null;
    } else {      
      assert o.instruction == null;
      goal = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named goal?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named goal or <code>false</code>
   *         if it does not.
   */
  public final boolean hasGoal() {
    return goal != null;
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
    * Constructor for InlineGuard.
    *
    * @param value
    * @param guard
    * @param goal
    * @param target
    * @param branchProfile
    */
  InlineGuard (Operand value, Operand guard, Operand goal, BranchOperand target, BranchProfileOperand branchProfile) {
    this.value = value;
    this.guard = guard;
    this.goal = goal;
    this.target = target;
    this.branchProfile = branchProfile;
  }

  @Override
  public final int getNumberOfOperands() {
      return 0 + 5;
  }

  @Override
  public final int getNumberOfDefs() {
      return 0;
  }

  @Override
  public final int getNumberOfUses() {
      return 5; 
  }

  @Override
  public final Operand getOperand(int i) {
    switch (i) {
      case 0:
        return value;
      case 1:
        return guard;
      case 2:
        return goal;
      case 3:
        return target;
      case 4:
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
        value = (Operand)op;
      break;
      case 1:
        guard = (Operand)op;
      break;
      case 2:
        goal = (Operand)op;
      break;
      case 3:
        target = (BranchOperand)op;
      break;
      case 4:
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
