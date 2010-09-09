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
 * The InstrumentedCounter instruction class.
 *
 * The header comment for {@link Instruction} contains
 * an explanation of the role of these interior class in 
 * opt compiler's IR.
 */
public abstract class InstrumentedCounter extends Instruction {

  /**        
    * data use IntConstantOperand.
    */
  private IntConstantOperand data;

  /**
   * Get the operand called data from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called data
   */
  public final IntConstantOperand getData() {
    return data;
  }

  /**
   * Get the operand called data from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called data
   */
  public final IntConstantOperand getClearData() {
    IntConstantOperand tmp = data;
    data = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called data in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setData(IntConstantOperand o) {
    if (o == null) {
      data = null;
    } else {      
      assert o.instruction == null;
      data = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named data?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named data or <code>false</code>
   *         if it does not.
   */
  public final boolean hasData() {
    return data != null;
  }


  /**        
    * index use IntConstantOperand.
    */
  private IntConstantOperand index;

  /**
   * Get the operand called index from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called index
   */
  public final IntConstantOperand getIndex() {
    return index;
  }

  /**
   * Get the operand called index from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called index
   */
  public final IntConstantOperand getClearIndex() {
    IntConstantOperand tmp = index;
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
  public final void setIndex(IntConstantOperand o) {
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
    * increment use Operand.
    */
  private Operand increment;

  /**
   * Get the operand called increment from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called increment
   */
  public final Operand getIncrement() {
    return increment;
  }

  /**
   * Get the operand called increment from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called increment
   */
  public final Operand getClearIncrement() {
    Operand tmp = increment;
    increment = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called increment in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setIncrement(Operand o) {
    if (o == null) {
      increment = null;
    } else {      
      assert o.instruction == null;
      increment = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named increment?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named increment or <code>false</code>
   *         if it does not.
   */
  public final boolean hasIncrement() {
    return increment != null;
  }


  /**
    * Constructor for InstrumentedCounter.
    *
    * @param data
    * @param index
    * @param increment
    */
  InstrumentedCounter (IntConstantOperand data, IntConstantOperand index, Operand increment) {
    this.data = data;
    this.index = index;
    this.increment = increment;
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
        return data;
      case 1:
        return index;
      case 2:
        return increment;
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
        data = (IntConstantOperand)op;
      break;
      case 1:
        index = (IntConstantOperand)op;
      break;
      case 2:
        increment = (Operand)op;
      break;
      default:
        throw new ArrayIndexOutOfBoundsException ();
    }
  }
}
