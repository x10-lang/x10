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
 * The ZeroCheck instruction class.
 *
 * The header comment for {@link Instruction} contains
 * an explanation of the role of these interior class in 
 * opt compiler's IR.
 */
public abstract class ZeroCheck extends Instruction
  implements HasGuardResult {

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
    * Constructor for ZeroCheck.
    *
    * @param guardResult
    * @param value
    */
  ZeroCheck (RegisterOperand guardResult, Operand value) {
    this.guardResult = guardResult;
    this.value = value;
  }

  @Override
  public final int getNumberOfOperands() {
      return 1 + 1;
  }

  @Override
  public final int getNumberOfDefs() {
      return 1;
  }

  @Override
  public final int getNumberOfUses() {
      return 1; 
  }

  @Override
  public final Operand getOperand(int i) {
    switch (i) {
      case 0:
        return guardResult;
      case 1:
        return value;
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
        value = (Operand)op;
      break;
      default:
        throw new ArrayIndexOutOfBoundsException ();
    }
  }
}
