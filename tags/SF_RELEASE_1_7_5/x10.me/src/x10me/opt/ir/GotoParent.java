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
 * The GotoParent instruction class.
 *
 * The header comment for {@link Instruction} contains
 * an explanation of the role of these interior class in 
 * opt compiler's IR.
 */
public abstract class GotoParent extends Instruction
  implements HasTarget, HasTargets {

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
    * Constructor for GotoParent.
    *
    * @param target
    */
  GotoParent (BranchOperand target) {
    this.target = target;
  }

  @Override
  public final int getNumberOfOperands() {
      return 0 + 1;
  }

  @Override
  public final int getNumberOfDefs() {
      return 0;
  }

  @Override
  public final int getNumberOfUses() {
      return 1; 
  }

  @Override
  public final Operand getOperand(int i) {
    switch (i) {
      case 0:
        return target;
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
        target = (BranchOperand)op;
      break;
      default:
        throw new ArrayIndexOutOfBoundsException ();
    }
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
