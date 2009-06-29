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
 * The PutStaticParent instruction class.
 *
 * The header comment for {@link Instruction} contains
 * an explanation of the role of these interior class in 
 * opt compiler's IR.
 */
public abstract class PutStaticParent extends Instruction
  implements HasField {

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
    * Constructor for PutStaticParent.
    *
    * @param value
    * @param offset
    * @param field
    */
  PutStaticParent (Operand value, Operand offset, FieldOperand field) {
    this.value = value;
    this.offset = offset;
    this.field = field;
  }

  @Override
  public final int getNumberOfOperands() {
      return 0 + 3;
  }

  @Override
  public final int getNumberOfDefs() {
      return 0;
  }

  @Override
  public final int getNumberOfUses() {
      return 3; 
  }

  @Override
  public final Operand getOperand(int i) {
    switch (i) {
      case 0:
        return value;
      case 1:
        return offset;
      case 2:
        return field;
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
        value = (Operand)op;
      break;
      case 1:
        offset = (Operand)op;
      break;
      case 2:
        field = (FieldOperand)op;
      break;
      default:
        throw new ArrayIndexOutOfBoundsException ();
    }
  }

  @Override
  public boolean isPut() {
    return true;
  }
}
