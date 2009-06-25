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

package x10me.opt.ir.operand;

import x10me.types.Type;

public final class ArrayOperand extends LocationOperand {
  /**
   * Array element type that corresponds to the type of the array that contains
   * this location; null if this is not an array access.
   */
  Type arrayElementType;

  /**
   * Constructs a new location operand with the given field.
   * @param loc location
   */
  public ArrayOperand (Type aet) {
    arrayElementType = aet;
  }

  /** 
   * Return the field inside of this operand.
   * @return the field inside of this operand.
   */
  public Type getArrayElementType() { return arrayElementType; }

  @Override
  public boolean mayBeVolatile() {
    return false;
  }
  
  @Override
  public boolean isArray() { return true;}
  
  @Override
  public Operand copy() {
    return new ArrayOperand (arrayElementType);
  }

  /**
   *  
   * @return the length of the array.
   */
  public int getArrayLength() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public boolean mayBeTypeBasedAliased(LocationOperand other) {
    if (this == other)
      return true;

    if (other instanceof ArrayOperand) {
      ArrayOperand o = (ArrayOperand) other;
      return arrayElementType.isArrayAssignableTo(o.arrayElementType);
    }
    return false;
  }

  @Override
  public boolean mustBeTypeBasedAliased(LocationOperand other) {
    if (this == other)
      return true;
    else
      return false;
  }

   @Override
  public boolean similar(Operand op) {
    return (op instanceof ArrayOperand) && this.mayBeAliased((ArrayOperand) op);
  }

  @Override
  public String toString() {
    return "<mem loc: array " + arrayElementType + "[]>";
  }
}