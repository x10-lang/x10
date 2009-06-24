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
 * The Move instruction class.
 *
 * The header comment for {@link Instruction} contains
 * an explanation of the role of these interior class in 
 * opt compiler's IR.
 */
public abstract class Move extends Instruction
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
    * val use Operand.
    */
  private Operand val;

  /**
   * Get the operand called val from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called val
   */
  public final Operand getVal() {
    return val;
  }

  /**
   * Get the operand called val from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called val
   */
  public final Operand getClearVal() {
    Operand tmp = val;
    val = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called val in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setVal(Operand o) {
    if (o == null) {
      val = null;
    } else {      
      assert o.instruction == null;
      val = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named val?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named val or <code>false</code>
   *         if it does not.
   */
  public final boolean hasVal() {
    return val != null;
  }


  /**
    * Constructor for Move.
    *
    * @param result
    * @param val
    */
  Move (Operand result, Operand val) {
    this.result = result;
    this.val = val;
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
        return result;
      case 1:
        return val;
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
        val = (Operand)op;
      break;
      default:
        throw new ArrayIndexOutOfBoundsException ();
    }
  }
  
  /**
   * Returns an unconditional move instruction of the given data type.
   *
   * @param type desired type to move
   * @param r1 destination of the move
   * @param r2 the source of the move
   * @return an unconditional move instruction of the given type
   */
  public static Move create (Type type, Operand r1, Operand r2) {
    if (type.isLongType()) return new LongMove (r1, r2);
    if (type.isFloatType()) return new FloatMove (r1, r2);
    if (type.isDoubleType()) return new DoubleMove (r1, r2);
    if (type == KnownTypes.VALIDATION_TYPE) return new GuardMove (r1, r2);
    if (type.isReferenceType()) return new RefMove (r1, r2);
    return new IntMove (r1, r2);
  }
}
