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
 * The GuardedBinary instruction class.
 *
 * The header comment for {@link Instruction} contains
 * an explanation of the role of these interior class in 
 * opt compiler's IR.
 */
public abstract class GuardedBinary extends Instruction
  implements HasResult, HasGuard {

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
    * Constructor for GuardedBinary.
    *
    * @param result
    * @param val1
    * @param val2
    * @param guard
    */
  GuardedBinary (Operand result, Operand val1, Operand val2, Operand guard) {
    this.result = result;
    this.val1 = val1;
    this.val2 = val2;
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
        return result;
      case 1:
        return val1;
      case 2:
        return val2;
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
        result = (Operand)op;
      break;
      case 1:
        val1 = (Operand)op;
      break;
      case 2:
        val2 = (Operand)op;
      break;
      case 3:
        guard = (Operand)op;
      break;
      default:
        throw new ArrayIndexOutOfBoundsException ();
    }
  }
  
  /**
   * Create a copy of this instruction.
   * The copy has the same operator, but is not linked into
   * an instruction list and the operands are set from the parms.
   *
   * @param result the result
   * @param val1 the first operand
   * @param val2 the second operand
   * @param guard the guard operand
   * @return the copy
   */
  public GuardedBinary create(Operand result, Operand val1, Operand val2, Operand guard) {
    GuardedBinary copy = (GuardedBinary)copyWithoutLinks();
    copy.result = result;
    copy.val1 = val1;
    copy.val2 = val2;
    copy.guard = guard;
    return copy;
  }
}
