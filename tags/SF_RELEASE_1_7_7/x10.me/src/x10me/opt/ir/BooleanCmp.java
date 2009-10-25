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
import x10me.types.*;

/**
 * The BooleanCmp instruction class.
 *
 * The header comment for {@link Instruction} contains
 * an explanation of the role of these interior class in 
 * opt compiler's IR.
 */
public abstract class BooleanCmp extends Instruction
  implements HasResult, HasBranchProfile {

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
    * Constructor for BooleanCmp.
    *
    * @param result
    * @param val1
    * @param val2
    * @param cond
    * @param branchProfile
    */
  BooleanCmp (Operand result, Operand val1, Operand val2, ConditionOperand cond, BranchProfileOperand branchProfile) {
    this.result = result;
    this.val1 = val1;
    this.val2 = val2;
    this.cond = cond;
    this.branchProfile = branchProfile;
  }

  @Override
  public final int getNumberOfOperands() {
      return 1 + 4;
  }

  @Override
  public final int getNumberOfDefs() {
      return 1;
  }

  @Override
  public final int getNumberOfUses() {
      return 4; 
  }

  @Override
  public final Operand getOperand(int i) {
    switch (i) {
      case 0:
        return result;
      case 1:
        return val1;
      case 2:
        return val2;
      case 3:
        return cond;
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
        result = (Operand)op;
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
   * Returns an booleanCmp instruction of the given data type.
   *
   * @param type of BooleanCmp to create 
   * @param result
   * @param val1
   * @param val2
   * @param cond
   * @param branchProfile
   */
  public static BooleanCmp create (Type type, Operand result, Operand val1, Operand val2, ConditionOperand cond, BranchProfileOperand branchProfile) {
    if (type.isLongType()) return new BooleanCmpLong (result, val1, val2, cond, branchProfile);
    if (type.isFloatType()) return new BooleanCmpFloat (result, val1, val2, cond, branchProfile);
    if (type.isDoubleType()) return new BooleanCmpDouble (result, val1, val2, cond, branchProfile);
    if (type.isAddressType()) return new BooleanCmpAddr (result, val1, val2, cond, branchProfile);
    return new BooleanCmpInt (result, val1, val2, cond, branchProfile);
  }
}
