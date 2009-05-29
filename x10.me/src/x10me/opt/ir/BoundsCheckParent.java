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
 * The BoundsCheckParent instruction class.
 *
 * The header comment for {@link Instruction} contains
 * an explanation of the role of these interior class in 
 * opt compiler's IR.
 */
public abstract class BoundsCheckParent extends Instruction
  implements HasGuardResult, HasGuard {

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
    * ref use Operand.
    */
  private ArrayOperand ref;

  /**
   * Get the operand called ref from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called ref
   */
  public final ArrayOperand getRef() {
    return ref;
  }

  /**
   * Get the operand called ref from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called ref
   */
  public final ArrayOperand getClearRef() {
    ArrayOperand tmp = ref;
    ref = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called ref in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setRef(ArrayOperand o) {
    if (o == null) {
      ref = null;
    } else {      
      assert o.instruction == null;
      ref = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named ref?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named ref or <code>false</code>
   *         if it does not.
   */
  public final boolean hasRef() {
    return ref != null;
  }


  /**        
    * index use Operand.
    */
  private Operand index;

  /**
   * Get the operand called index from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called index
   */
  public final Operand getIndex() {
    return index;
  }

  /**
   * Get the operand called index from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called index
   */
  public final Operand getClearIndex() {
    Operand tmp = index;
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
  public final void setIndex(Operand o) {
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
    * Constructor for BoundsCheckParent.
    *
    * @param guardResult
    * @param ref
    * @param index
    * @param guard
    */
  BoundsCheckParent (RegisterOperand guardResult, ArrayOperand ref, Operand index, Operand guard) {
    this.guardResult = guardResult;
    this.ref = ref;
    this.index = index;
    this.guard = guard;
  }

  @Override
  public final int getNumberOfOperands() {
      return 1 + 3;
  }

  @Override
  public final int getNumberOfDefs() {
      return 1;
  }

  @Override
  public final int getNumberOfUses() {
      return 3; 
  }

  @Override
  public final Operand getOperand(int i) {
    switch (i) {
      case 0:
        return guardResult;
      case 1:
        return ref;
      case 2:
        return index;
      case 3:
        return guard;
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
        ref = (ArrayOperand)op;
      break;
      case 2:
        index = (Operand)op;
      break;
      case 3:
        guard = (Operand)op;
      break;
      default:
        throw new ArrayIndexOutOfBoundsException ();
    }
  }
}
