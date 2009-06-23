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
import x10me.types.KnownTypes;
import x10me.types.Type;

/**
 * The CondMove instruction class.
 *
 * The header comment for {@link Instruction} contains
 * an explanation of the role of these interior class in 
 * opt compiler's IR.
 */
public abstract class CondMove extends Instruction
  implements HasResult {

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
    * cond use ConditionOperand.
    */
  private ConditionOperand cond;

  /**
   * Get the operand called cond from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called cond
   */
  public final ConditionOperand getCond() {
    return cond;
  }

  /**
   * Get the operand called cond from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called cond
   */
  public final ConditionOperand getClearCond() {
    ConditionOperand tmp = cond;
    cond = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called cond in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setCond(ConditionOperand o) {
    if (o == null) {
      cond = null;
    } else {      
      assert o.instruction == null;
      cond = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named cond?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named cond or <code>false</code>
   *         if it does not.
   */
  public final boolean hasCond() {
    return cond != null;
  }


  /**        
    * trueValue use Operand.
    */
  private Operand trueValue;

  /**
   * Get the operand called trueValue from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called trueValue
   */
  public final Operand getTrueValue() {
    return trueValue;
  }

  /**
   * Get the operand called trueValue from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called trueValue
   */
  public final Operand getClearTrueValue() {
    Operand tmp = trueValue;
    trueValue = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called trueValue in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setTrueValue(Operand o) {
    if (o == null) {
      trueValue = null;
    } else {      
      assert o.instruction == null;
      trueValue = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named trueValue?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named trueValue or <code>false</code>
   *         if it does not.
   */
  public final boolean hasTrueValue() {
    return trueValue != null;
  }


  /**        
    * falseValue use Operand.
    */
  private Operand falseValue;

  /**
   * Get the operand called falseValue from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called falseValue
   */
  public final Operand getFalseValue() {
    return falseValue;
  }

  /**
   * Get the operand called falseValue from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called falseValue
   */
  public final Operand getClearFalseValue() {
    Operand tmp = falseValue;
    falseValue = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called falseValue in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setFalseValue(Operand o) {
    if (o == null) {
      falseValue = null;
    } else {      
      assert o.instruction == null;
      falseValue = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named falseValue?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named falseValue or <code>false</code>
   *         if it does not.
   */
  public final boolean hasFalseValue() {
    return falseValue != null;
  }


  /**
    * Constructor for CondMove.
    *
    * @param result
    * @param val1
    * @param val2
    * @param cond
    * @param trueValue
    * @param falseValue
    */
  CondMove (Operand result, Operand val1, Operand val2, ConditionOperand cond, Operand trueValue, Operand falseValue) {
    this.result = result;
    this.val1 = val1;
    this.val2 = val2;
    this.cond = cond;
    this.trueValue = trueValue;
    this.falseValue = falseValue;
  }

  @Override
  public final int getNumberOfOperands() {
      return 1 + 5;
  }

  @Override
  public final int getNumberOfDefs() {
      return 1;
  }

  @Override
  public final int getNumberOfUses() {
      return 5; 
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
        return cond;
      case 4:
        return trueValue;
      case 5:
        return falseValue;
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
        cond = (ConditionOperand)op;
      break;
      case 4:
        trueValue = (Operand)op;
      break;
      case 5:
        falseValue = (Operand)op;
      break;
      default:
        throw new ArrayIndexOutOfBoundsException ();
    }
  }

  /**
   * Returns an unconditional move instruction of the given data type.
   *
   * @param type desired type to move
   * @param result the result of the conditional move
   * @param r1 destination of the move
   * @param r2 the source of the move
   * @param cond the condition to test
   * @param trueValue
   * @param falseValue
   * @return an unconditional move instruction of the given type
   */
  public static CondMove create (Type type, Operand result, Operand r1, Operand r2, ConditionOperand cond, Operand trueValue, Operand falseValue) {
    if (type.isLongType()) return new LongCondMove (result, r1, r2, cond, trueValue, falseValue);
    if (type.isFloatType()) return new FloatCondMove (result, r1, r2, cond, trueValue, falseValue);
    if (type.isDoubleType()) return new DoubleCondMove (result, r1, r2, cond, trueValue, falseValue);
    if (type == KnownTypes.VALIDATION_TYPE) return new GuardCondMove (result, r1, r2, cond, trueValue, falseValue);
    if (type.isReferenceType()) return new RefCondMove (result, r1, r2, cond, trueValue, falseValue);
    return new IntCondMove (result, r1, r2, cond, trueValue, falseValue);
  }
}
