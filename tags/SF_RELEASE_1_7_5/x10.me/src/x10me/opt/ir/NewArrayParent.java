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
 * The NewArray instruction class.
 *
 * The header comment for {@link Instruction} contains
 * an explanation of the role of these interior class in
 * opt compiler's IR.
 */
public abstract class NewArrayParent extends Instruction
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
    * type use TypeOperand.
    */
  private TypeOperand type;

  /**
   * Get the operand called type from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called type
   */
  public final TypeOperand getType() {
    return type;
  }

  /**
   * Get the operand called type from this instruction clearing its
   * instruction pointer. The returned operand will not point to
   * any containing instruction.
   * @return the operand called type
   */
  public final TypeOperand getClearType() {
    TypeOperand tmp = type;
    type = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called type in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setType(TypeOperand o) {
    if (o == null) {
      type = null;
    } else {
      assert o.instruction == null;
      type = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named type?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named type or <code>false</code>
   *         if it does not.
   */
  public final boolean hasType() {
    return type != null;
  }


  /**
    * size use Operand.
    */
  private Operand size;

  /**
   * Get the operand called size from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called size
   */
  public final Operand getSize() {
    return size;
  }

  /**
   * Get the operand called size from this instruction clearing its
   * instruction pointer. The returned operand will not point to
   * any containing instruction.
   * @return the operand called size
   */
  public final Operand getClearSize() {
    Operand tmp = size;
    size = null;
    clearOperand (tmp);
    return tmp;
  }

  /**
   * Set the operand called size in the argument
   * instruction to the argument operand. The operand will
   * now point to the argument instruction as its containing
   * instruction.
   * @param o the operand to store.
   */
  public final void setSize(Operand o) {
    if (o == null) {
      size = null;
    } else {
      assert o.instruction == null;
      size = o;
      o.instruction = this;
    }
  }

  /**
   * Does this instruction have a non-null
   * operand named size?
   * @return <code>true</code> if this instruction has a non-null
   *         operand named size or <code>false</code>
   *         if it does not.
   */
  public final boolean hasSize() {
    return size != null;
  }


  /**
    * Constructor for NewArray.
    *
    * @param result
    * @param type
    * @param size
    */
  NewArrayParent (Operand result, TypeOperand type, Operand size) {
    this.result = result;
    this.type = type;
    this.size = size;
  }

  @Override
  public final int getNumberOfOperands() {
      return 1 + 2;
  }

  @Override
  public final int getNumberOfDefs() {
      return 1;
  }

  @Override
  public final int getNumberOfUses() {
      return 2;
  }

  @Override
  public final Operand getOperand(int i) {
    switch (i) {
      case 0:
        return result;
      case 1:
        return type;
      case 2:
        return size;
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
        type = (TypeOperand)op;
      break;
      case 2:
        size = (Operand)op;
      break;
      default:
        throw new ArrayIndexOutOfBoundsException ();
    }
  }
}
