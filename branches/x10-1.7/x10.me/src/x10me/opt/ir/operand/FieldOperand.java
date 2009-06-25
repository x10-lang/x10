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

import x10me.types.Field;
import x10me.types.KnownTypes;

public final class FieldOperand extends LocationOperand {
  /**
   * Field that corresponds to this location.
   */
  Field fieldRef;

  /**
   * Constructs a new location operand with the given field.
   * @param loc location
   */
  public FieldOperand (Field f) {
    fieldRef = f;
  }

  /** 
   * Return the field inside of this operand.
   * @return the field inside of this operand.
   */
  public Field getField() { 
    return fieldRef; 
  }

  @Override
  public boolean mayBeVolatile() {
    return fieldRef.isVolatile();
  }
  
  @Override
  public boolean isField() { return true;}
  
  @Override
  public Operand copy() {
	return new FieldOperand (fieldRef);
  }
  
  @Override
  public boolean mayBeTypeBasedAliased(LocationOperand other) {
    if (this == other)
      return true;

    if (other instanceof FieldOperand) {
      FieldOperand o = (FieldOperand) other;
      return this.fieldRef == o.fieldRef;
    }
    return false;
  }

  @Override
  public boolean mustBeTypeBasedAliased(LocationOperand other) {
    if (this == other)
      return true;

    /*
     * If the fields are static, then if the two fields are the same, 
     * then they definitely alias.
     */
    if (fieldRef.isStatic() && other instanceof FieldOperand) {
      FieldOperand o = (FieldOperand) other;
      return this.fieldRef == o.fieldRef;
    }
    return false;

  }
  
  @Override
  public boolean similar(Operand op) {
    return (op instanceof LocationOperand) && this.mayBeAliased((LocationOperand)op);
  }

  @Override
  public String toString() {
    return "<mem loc: " + fieldRef.getType().getName() + "." + fieldRef.getName() + ">";
  }
}