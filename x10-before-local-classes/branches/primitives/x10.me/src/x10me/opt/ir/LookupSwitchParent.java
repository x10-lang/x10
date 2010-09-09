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
 * The LookupSwitchParent instruction class.
 *
 * The header comment for {@link Instruction} contains
 * an explanation of the role of these interior class in 
 * opt compiler's IR.
 */
public abstract class LookupSwitchParent extends Instruction
  implements HasTargets, HasVariableOperands {

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
    * unknown1 use Operand.
    */
  private Operand unknown1;

  /**
   * Get the operand called unknown1 from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called unknown1
   */
  public final Operand getUnknown1() {
    return unknown1;
  }

  /**
   * Get the operand called unknown1 from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called unknown1
   */
  public final Operand getClearUnknown1() {
    Operand tmp = unknown1;
    unknown1 = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called unknown1 in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setUnknown1(Operand o) {
    if (o == null) {
      unknown1 = null;
    } else {      
      assert o.instruction == null;
      unknown1 = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named unknown1?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named unknown1 or <code>false</code>
   *         if it does not.
   */
  public final boolean hasUnknown1() {
    return unknown1 != null;
  }


  /**        
    * unknown2 use Operand.
    */
  private Operand unknown2;

  /**
   * Get the operand called unknown2 from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called unknown2
   */
  public final Operand getUnknown2() {
    return unknown2;
  }

  /**
   * Get the operand called unknown2 from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called unknown2
   */
  public final Operand getClearUnknown2() {
    Operand tmp = unknown2;
    unknown2 = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called unknown2 in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setUnknown2(Operand o) {
    if (o == null) {
      unknown2 = null;
    } else {      
      assert o.instruction == null;
      unknown2 = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named unknown2?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named unknown2 or <code>false</code>
   *         if it does not.
   */
  public final boolean hasUnknown2() {
    return unknown2 != null;
  }


  /**        
    * defaultTarget use BranchOperand.
    */
  private BranchOperand defaultTarget;

  /**
   * Get the operand called defaultTarget from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called defaultTarget
   */
  public final BranchOperand getDefaultTarget() {
    return defaultTarget;
  }

  /**
   * Get the operand called defaultTarget from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called defaultTarget
   */
  public final BranchOperand getClearDefaultTarget() {
    BranchOperand tmp = defaultTarget;
    defaultTarget = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called defaultTarget in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setDefaultTarget(BranchOperand o) {
    if (o == null) {
      defaultTarget = null;
    } else {      
      assert o.instruction == null;
      defaultTarget = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named defaultTarget?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named defaultTarget or <code>false</code>
   *         if it does not.
   */
  public final boolean hasDefaultTarget() {
    return defaultTarget != null;
  }


  /**        
    * defaultBranchProfile use BranchProfileOperand.
    */
  private BranchProfileOperand defaultBranchProfile;

  /**
   * Get the operand called defaultBranchProfile from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called defaultBranchProfile
   */
  public final BranchProfileOperand getDefaultBranchProfile() {
    return defaultBranchProfile;
  }

  /**
   * Get the operand called defaultBranchProfile from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called defaultBranchProfile
   */
  public final BranchProfileOperand getClearDefaultBranchProfile() {
    BranchProfileOperand tmp = defaultBranchProfile;
    defaultBranchProfile = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called defaultBranchProfile in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setDefaultBranchProfile(BranchProfileOperand o) {
    if (o == null) {
      defaultBranchProfile = null;
    } else {      
      assert o.instruction == null;
      defaultBranchProfile = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named defaultBranchProfile?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named defaultBranchProfile or <code>false</code>
   *         if it does not.
   */
  public final boolean hasDefaultBranchProfile() {
    return defaultBranchProfile != null;
  }

  /**
   * Count of the number of variable parameters.
   */
  private int count;

  /**        
    * match use IntConstantOperand.
    */
  private IntConstantOperand match[];

  /**
   * Get the k'th operand called match from this
   * instruction. Note that the returned operand
   * will still point to its containing instruction.
   * @param k the index of the operand
   * @return the k'th operand called match
   */
  public final IntConstantOperand getMatch(int k) {
    return match[k];
  }

  /**
   * Get the k'th operand called match from this
   * instruction clearing its instruction pointer. The returned
   * operand will not point to any containing instruction.
   * @param k the index of the operand
   * @return the k'th operand called match
   */
  public final IntConstantOperand getClearMatch(int k) {
    IntConstantOperand tmp = match[k];
    match[k] = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the k'th operand called match in this
   * instruction to the argument operand. The operand will
   * now point to this instruction as its containing
   * instruction.
   * @param k the index of the operand
   * @param o the operand to store
   */
  public final void setMatch(int k, IntConstantOperand o) {
    if (o == null) {
      match = null;
    } else {      
      assert o.instruction == null;
      match[k] = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * k'th operand named match?
   * @param k the index of the operand.
   * @return <code>true</code> if the instruction has an non-null
   *         k'th operand named match or <code>false</code>
   *         if it does not.
   */
  public final boolean hasMatch(int k) {
    return match[k] != null;
  }

  /**
   * Does this instruction have any matches operands?
   * @return <code>true</code> if this instruction has
   *         matches operands or <code>false</code>
   *         if it does not.
   */
  public final boolean hasMatches()
  {
    return count != 0;
  }

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
	IntConstantOperand newMatch[] = new IntConstantOperand[numVarOps];
	System.arraycopy (match, 0, newMatch, 0, l);
	match = newMatch; 
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
    * Constructor for LookupSwitchParent.
    *
    * @param value
    * @param unknown1
    * @param unknown2
    * @param defaultTarget
    * @param defaultBranchProfile
    * @param numVarOps of the number of variable arguments.
    */
  LookupSwitchParent (Operand value, Operand unknown1, Operand unknown2, BranchOperand defaultTarget, BranchProfileOperand defaultBranchProfile, int numVarOps) {
    this.value = value;
    this.unknown1 = unknown1;
    this.unknown2 = unknown2;
    this.defaultTarget = defaultTarget;
    this.defaultBranchProfile = defaultBranchProfile;
    this.match = new IntConstantOperand[numVarOps]; 
    this.target = new BranchOperand[numVarOps]; 
    this.branchProfile = new BranchProfileOperand[numVarOps]; 
    this.count = numVarOps;
  }

  @Override
  public final int getNumberOfOperands() {
      return 0 + 5 + (count * 3); 
  }

  @Override
  public final int getNumberOfDefs() {
      return 0;
  }

  @Override
  public final int getNumberOfUses() {
      return 5 + (count * 3); 
  }

  @Override
  public final Operand getOperand(int i) {
    switch (i) {
      case 0:
        return value;
      case 1:
        return unknown1;
      case 2:
        return unknown2;
      case 3:
        return defaultTarget;
      case 4:
        return defaultBranchProfile;
      default:
        if (i < (5 + ((0 + 1) * this.count)))
	  return this.match[(5 + (0 * this.count)) + i];
        else
        if (i < (5 + ((1 + 1) * this.count)))
	  return this.target[(5 + (1 * this.count)) + i];
        else
        if (i < (5 + ((2 + 1) * this.count)))
	  return this.branchProfile[(5 + (2 * this.count)) + i];
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
        value = (Operand)op;
      break;
      case 1:
        unknown1 = (Operand)op;
      break;
      case 2:
        unknown2 = (Operand)op;
      break;
      case 3:
        defaultTarget = (BranchOperand)op;
      break;
      case 4:
        defaultBranchProfile = (BranchProfileOperand)op;
      break;
      default:
        if (i < (5 + ((0 + 1) * this.count)))
	  this.match[(5 + (0 * this.count)) + i] = (IntConstantOperand)op;
        else
        if (i < (5 + ((1 + 1) * this.count)))
	  this.target[(5 + (1 * this.count)) + i] = (BranchOperand)op;
        else
        if (i < (5 + ((2 + 1) * this.count)))
	  this.branchProfile[(5 + (2 * this.count)) + i] = (BranchProfileOperand)op;
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
    e.addElement(defaultTarget.target.getBasicBlock());
    for (int i = 0; i < count; i++) {
      e.addPossiblyDuplicateElement(target[i].target.getBasicBlock());
    }
    return e;
  }

}
