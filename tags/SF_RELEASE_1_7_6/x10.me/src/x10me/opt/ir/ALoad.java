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
 * The ALoad instruction class.
 *
 * The header comment for {@link Instruction} contains
 * an explanation of the role of these interior class in 
 * opt compiler's IR.
 */
public abstract class ALoad extends Instruction
  implements HasResult, HasField, HasGuard {

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
    * array use Operand.
    */
  private Operand array;

  /**
   * Get the operand called array from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called array
   */
  public final Operand getArray() {
    return array;
  }

  /**
   * Get the operand called array from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called array
   */
  public final Operand getClearArray() {
    Operand tmp = array;
    array = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called array in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setArray(Operand o) {
    if (o == null) {
      array = null;
    } else {      
      assert o.instruction == null;
      array = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named array?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named array or <code>false</code>
   *         if it does not.
   */
  public final boolean hasArray() {
    return array != null;
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
    * field use FieldOperand.
    */
  private FieldOperand field;

  /**
   * Get the operand called field from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called field
   */
  public final FieldOperand getField() {
    return field;
  }

  /**
   * Get the operand called field from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called field
   */
  public final FieldOperand getClearField() {
    FieldOperand tmp = field;
    field = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called field in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setField(FieldOperand o) {
    if (o == null) {
      field = null;
    } else {      
      assert o.instruction == null;
      field = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named field?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named field or <code>false</code>
   *         if it does not.
   */
  public final boolean hasField() {
    return field != null;
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
    * Constructor for ALoad.
    *
    * @param result
    * @param array
    * @param index
    * @param field
    * @param guard
    */
  ALoad (Operand result, Operand array, Operand index, FieldOperand field, Operand guard) {
    this.result = result;
    this.array = array;
    this.index = index;
    this.field = field;
    this.guard = guard;
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
        return array;
      case 2:
        return index;
      case 3:
        return field;
      case 4:
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
        array = (Operand)op;
      break;
      case 2:
        index = (Operand)op;
      break;
      case 3:
        field = (FieldOperand)op;
      break;
      case 4:
        guard = (Operand)op;
      break;
      default:
        throw new ArrayIndexOutOfBoundsException ();
    }
  }
}
