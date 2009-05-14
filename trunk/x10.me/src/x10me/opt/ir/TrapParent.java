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

public abstract class TrapParent extends Instruction{
  
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
   * Constructor for TrapParent.
   *
   * @param type
   */
 TrapParent (TypeOperand type) {
   this.type = type;
 }

 @Override
 public final int getNumberOfOperands() {
     return 1;
 }

 @Override
 public final int getNumberOfDefs() {
     return 0;
 }

 @Override
 public final int getNumberOfUses() {
     return 1; 
 }

 @Override
 public final Operand getOperand(int i) {
   switch (i) {
     case 0:
       return type;
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
       type = (TypeOperand)op;
     break;
     default:
       throw new ArrayIndexOutOfBoundsException ();
   }
 }
}
