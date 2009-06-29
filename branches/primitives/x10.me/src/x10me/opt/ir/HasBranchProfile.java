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

public interface HasBranchProfile {
  /**
   * Get the operand called branchProfile from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called branchProfile
   */
  public BranchProfileOperand getBranchProfile();

  /**
   * Get the operand called branchProfile from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called branchProfile
   */
  public BranchProfileOperand getClearBranchProfile();

  /**
   * Set the operand called branchProfile in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public void setBranchProfile(BranchProfileOperand o);

  /**
   * Does this instruction have a non-null
   * operand named branchProfile?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named branchProfile or <code>false</code>
   *         if it does not.
   */
  public boolean hasBranchProfile();
  
  /**
   * Return the probability (in the range 0.0 - 1.0) that this two-way
   * branch instruction is taken (as opposed to falling through).
   *
   * @return The probability that the branch is taken.
   */
  public float getBranchProbability();

  /**
   * Record the probability (in the range 0.0 - 1.0) that this two-way
   * branch instruction is taken (as opposed to falling through).
   *
   * @param takenProbability    The probability that the branch is taken.
   */
  public void setBranchProbability(float takenProbability);

  /**
   * Invert the probabilty of this branch being taken.  This method
   * should be called on a branch instruction when its condition is
   * reversed using flipCode().
   */
  public void flipBranchProbability();
}
