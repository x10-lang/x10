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
 * The IfCmp2 instruction class.
 *
 * The header comment for {@link Instruction} contains
 * an explanation of the role of these interior class in 
 * opt compiler's IR.
 */
public abstract class IfCmp2 extends Instruction
  implements HasGuardResult, HasTargets {

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
    * cond1 use ConditionOperand.
    */
  private ConditionOperand cond1;

  /**
   * Get the operand called cond1 from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called cond1
   */
  public final ConditionOperand getCond1() {
    return cond1;
  }

  /**
   * Get the operand called cond1 from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called cond1
   */
  public final ConditionOperand getClearCond1() {
    ConditionOperand tmp = cond1;
    cond1 = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called cond1 in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setCond1(ConditionOperand o) {
    if (o == null) {
      cond1 = null;
    } else {      
      assert o.instruction == null;
      cond1 = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named cond1?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named cond1 or <code>false</code>
   *         if it does not.
   */
  public final boolean hasCond1() {
    return cond1 != null;
  }


  /**        
    * target1 use BranchOperand.
    */
  private BranchOperand target1;

  /**
   * Get the operand called target1 from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called target1
   */
  public final BranchOperand getTarget1() {
    return target1;
  }

  /**
   * Get the operand called target1 from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called target1
   */
  public final BranchOperand getClearTarget1() {
    BranchOperand tmp = target1;
    target1 = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called target1 in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setTarget1(BranchOperand o) {
    if (o == null) {
      target1 = null;
    } else {      
      assert o.instruction == null;
      target1 = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named target1?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named target1 or <code>false</code>
   *         if it does not.
   */
  public final boolean hasTarget1() {
    return target1 != null;
  }


  /**        
    * branchProfile1 use BranchProfileOperand.
    */
  private BranchProfileOperand branchProfile1;

  /**
   * Get the operand called branchProfile1 from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called branchProfile1
   */
  public final BranchProfileOperand getBranchProfile1() {
    return branchProfile1;
  }

  /**
   * Get the operand called branchProfile1 from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called branchProfile1
   */
  public final BranchProfileOperand getClearBranchProfile1() {
    BranchProfileOperand tmp = branchProfile1;
    branchProfile1 = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called branchProfile1 in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setBranchProfile1(BranchProfileOperand o) {
    if (o == null) {
      branchProfile1 = null;
    } else {      
      assert o.instruction == null;
      branchProfile1 = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named branchProfile1?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named branchProfile1 or <code>false</code>
   *         if it does not.
   */
  public final boolean hasBranchProfile1() {
    return branchProfile1 != null;
  }


  /**        
    * cond2 use ConditionOperand.
    */
  private ConditionOperand cond2;

  /**
   * Get the operand called cond2 from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called cond2
   */
  public final ConditionOperand getCond2() {
    return cond2;
  }

  /**
   * Get the operand called cond2 from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called cond2
   */
  public final ConditionOperand getClearCond2() {
    ConditionOperand tmp = cond2;
    cond2 = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called cond2 in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setCond2(ConditionOperand o) {
    if (o == null) {
      cond2 = null;
    } else {      
      assert o.instruction == null;
      cond2 = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named cond2?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named cond2 or <code>false</code>
   *         if it does not.
   */
  public final boolean hasCond2() {
    return cond2 != null;
  }


  /**        
    * target2 use BranchOperand.
    */
  private BranchOperand target2;

  /**
   * Get the operand called target2 from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called target2
   */
  public final BranchOperand getTarget2() {
    return target2;
  }

  /**
   * Get the operand called target2 from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called target2
   */
  public final BranchOperand getClearTarget2() {
    BranchOperand tmp = target2;
    target2 = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called target2 in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setTarget2(BranchOperand o) {
    if (o == null) {
      target2 = null;
    } else {      
      assert o.instruction == null;
      target2 = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named target2?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named target2 or <code>false</code>
   *         if it does not.
   */
  public final boolean hasTarget2() {
    return target2 != null;
  }


  /**        
    * branchProfile2 use BranchProfileOperand.
    */
  private BranchProfileOperand branchProfile2;

  /**
   * Get the operand called branchProfile2 from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called branchProfile2
   */
  public final BranchProfileOperand getBranchProfile2() {
    return branchProfile2;
  }

  /**
   * Get the operand called branchProfile2 from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called branchProfile2
   */
  public final BranchProfileOperand getClearBranchProfile2() {
    BranchProfileOperand tmp = branchProfile2;
    branchProfile2 = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called branchProfile2 in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setBranchProfile2(BranchProfileOperand o) {
    if (o == null) {
      branchProfile2 = null;
    } else {      
      assert o.instruction == null;
      branchProfile2 = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named branchProfile2?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named branchProfile2 or <code>false</code>
   *         if it does not.
   */
  public final boolean hasBranchProfile2() {
    return branchProfile2 != null;
  }


  /**
    * Constructor for IfCmp2.
    *
    * @param guardResult
    * @param val1
    * @param val2
    * @param cond1
    * @param target1
    * @param branchProfile1
    * @param cond2
    * @param target2
    * @param branchProfile2
    */
  IfCmp2 (RegisterOperand guardResult, Operand val1, Operand val2, ConditionOperand cond1, BranchOperand target1, BranchProfileOperand branchProfile1, ConditionOperand cond2, BranchOperand target2, BranchProfileOperand branchProfile2) {
    this.guardResult = guardResult;
    this.val1 = val1;
    this.val2 = val2;
    this.cond1 = cond1;
    this.target1 = target1;
    this.branchProfile1 = branchProfile1;
    this.cond2 = cond2;
    this.target2 = target2;
    this.branchProfile2 = branchProfile2;
  }

  @Override
  public final int getNumberOfOperands() {
      return 1 + 8;
  }

  @Override
  public final int getNumberOfDefs() {
      return 1;
  }

  @Override
  public final int getNumberOfUses() {
      return 8; 
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
        return cond1;
      case 4:
        return target1;
      case 5:
        return branchProfile1;
      case 6:
        return cond2;
      case 7:
        return target2;
      case 8:
        return branchProfile2;
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
        cond1 = (ConditionOperand)op;
      break;
      case 4:
        target1 = (BranchOperand)op;
      break;
      case 5:
        branchProfile1 = (BranchProfileOperand)op;
      break;
      case 6:
        cond2 = (ConditionOperand)op;
      break;
      case 7:
        target2 = (BranchOperand)op;
      break;
      case 8:
        branchProfile2 = (BranchProfileOperand)op;
      break;
      default:
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
    BasicBlock.ComputedBBEnum e = new BasicBlock.ComputedBBEnum(2);
    e.addElement(target1.target.getBasicBlock());
    e.addPossiblyDuplicateElement(target2.target.getBasicBlock());
    return e;
  }
}
