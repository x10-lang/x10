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
 * The Attempt instruction class.
 *
 * The header comment for {@link Instruction} contains
 * an explanation of the role of these interior class in 
 * opt compiler's IR.
 */
public abstract class Attempt extends Instruction
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
    * address use Operand.
    */
  private Operand address;

  /**
   * Get the operand called address from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called address
   */
  public final Operand getAddress() {
    return address;
  }

  /**
   * Get the operand called address from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called address
   */
  public final Operand getClearAddress() {
    Operand tmp = address;
    address = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called address in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setAddress(Operand o) {
    if (o == null) {
      address = null;
    } else {      
      assert o.instruction == null;
      address = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named address?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named address or <code>false</code>
   *         if it does not.
   */
  public final boolean hasAddress() {
    return address != null;
  }


  /**        
    * offset use Operand.
    */
  private Operand offset;

  /**
   * Get the operand called offset from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called offset
   */
  public final Operand getOffset() {
    return offset;
  }

  /**
   * Get the operand called offset from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called offset
   */
  public final Operand getClearOffset() {
    Operand tmp = offset;
    offset = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called offset in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setOffset(Operand o) {
    if (o == null) {
      offset = null;
    } else {      
      assert o.instruction == null;
      offset = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named offset?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named offset or <code>false</code>
   *         if it does not.
   */
  public final boolean hasOffset() {
    return offset != null;
  }


  /**        
    * oldValue use Operand.
    */
  private Operand oldValue;

  /**
   * Get the operand called oldValue from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called oldValue
   */
  public final Operand getOldValue() {
    return oldValue;
  }

  /**
   * Get the operand called oldValue from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called oldValue
   */
  public final Operand getClearOldValue() {
    Operand tmp = oldValue;
    oldValue = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called oldValue in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setOldValue(Operand o) {
    if (o == null) {
      oldValue = null;
    } else {      
      assert o.instruction == null;
      oldValue = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named oldValue?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named oldValue or <code>false</code>
   *         if it does not.
   */
  public final boolean hasOldValue() {
    return oldValue != null;
  }


  /**        
    * newValue use Operand.
    */
  private Operand newValue;

  /**
   * Get the operand called newValue from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called newValue
   */
  public final Operand getNewValue() {
    return newValue;
  }

  /**
   * Get the operand called newValue from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called newValue
   */
  public final Operand getClearNewValue() {
    Operand tmp = newValue;
    newValue = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called newValue in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setNewValue(Operand o) {
    if (o == null) {
      newValue = null;
    } else {      
      assert o.instruction == null;
      newValue = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named newValue?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named newValue or <code>false</code>
   *         if it does not.
   */
  public final boolean hasNewValue() {
    return newValue != null;
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
    * Constructor for Attempt.
    *
    * @param result
    * @param address
    * @param offset
    * @param oldValue
    * @param newValue
    * @param field
    * @param guard
    */
  Attempt (Operand result, Operand address, Operand offset, Operand oldValue, Operand newValue, FieldOperand field, Operand guard) {
    this.result = result;
    this.address = address;
    this.offset = offset;
    this.oldValue = oldValue;
    this.newValue = newValue;
    this.field = field;
    this.guard = guard;
  }

  @Override
  public final int getNumberOfOperands() {
      return 1 + 6;
  }

  @Override
  public final int getNumberOfDefs() {
      return 1;
  }

  @Override
  public final int getNumberOfUses() {
      return 6; 
  }

  @Override
  public final Operand getOperand(int i) {
    switch (i) {
      case 0:
        return result;
      case 1:
        return address;
      case 2:
        return offset;
      case 3:
        return oldValue;
      case 4:
        return newValue;
      case 5:
        return field;
      case 6:
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
        address = (Operand)op;
      break;
      case 2:
        offset = (Operand)op;
      break;
      case 3:
        oldValue = (Operand)op;
      break;
      case 4:
        newValue = (Operand)op;
      break;
      case 5:
        field = (FieldOperand)op;
      break;
      case 6:
        guard = (Operand)op;
      break;
      default:
        throw new ArrayIndexOutOfBoundsException ();
    }
  }
}
