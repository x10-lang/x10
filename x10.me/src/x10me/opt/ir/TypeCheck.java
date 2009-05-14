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
 * The TypeCheck instruction class.
 *
 * The header comment for {@link Instruction} contains
 * an explanation of the role of these interior class in 
 * opt compiler's IR.
 */
public abstract class TypeCheck extends Instruction
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
    * ref use Operand.
    */
  private Operand ref;

  /**
   * Get the operand called ref from this instruction. Note that the
   * returned operand will still point to its containing instruction.
   *
   * @return the operand called ref
   */
  public final Operand getRef() {
    return ref;
  }

  /**
   * Get the operand called ref from this instruction clearing its 
   * instruction pointer. The returned operand will not point to 
   * any containing instruction.
   * @return the operand called ref
   */
  public final Operand getClearRef() {
    Operand tmp = ref;
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
  public final void setRef(Operand o) {
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
    * Constructor for TypeCheck.
    *
    * @param result
    * @param ref
    * @param type
    * @param guard
    */
  TypeCheck (Operand result, Operand ref, TypeOperand type, Operand guard) {
    this.result = result;
    this.ref = ref;
    this.type = type;
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
        return ref;
      case 2:
        return type;
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
        ref = (Operand)op;
      break;
      case 2:
        type = (TypeOperand)op;
      break;
      case 3:
        guard = (Operand)op;
      break;
      default:
        throw new ArrayIndexOutOfBoundsException ();
    }
  }
}
