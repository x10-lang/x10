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

public final class ALengthOperand extends LocationOperand {
  /**
   * Constructs a new array length operand.
   */
  public ALengthOperand(Type t) {
    arrayElementType = t;
  }
    
  /**
   * Array element type that corresponds to the type of the array that contains
   * this location; null if this is not an array access.
   */
  Type arrayElementType;

  
  @Override
  public boolean mayBeVolatile() {
    return false;
  }
  
  @Override
  public boolean isALength() { 
    return true;
  }
  
  @Override
  public Operand copy() {
    return new ALengthOperand (arrayElementType);
  }
  
  /*
   * Aliasing for array lengths really means is it possible for the underlying
   * arrays to be the same. If they are the same, then we can say that the
   * lengths are the same, without actually knowing what those lengths are.
   */
  @Override
  public boolean mayBeTypeBasedAliased(LocationOperand other) {
    if (this == other)
      return true;

    if (other instanceof ALengthOperand) {
      ALengthOperand o = (ALengthOperand) other;
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
    return false;
  }

  @Override
  public String toString() {
    return "<mem loc: array length>";
  }
}
