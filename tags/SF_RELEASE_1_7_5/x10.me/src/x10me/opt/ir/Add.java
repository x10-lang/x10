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
import x10me.types.Type;

/**
 * The Add instruction class.
 *
 * The header comment for {@link Instruction} contains
 * an explanation of the role of these interior class in 
 * opt compiler's IR.
 */
public abstract class Add extends Binary {

  Add(Operand r1, Operand r2, Operand r3) {
    super(r1, r2, r3);
  }
  
  /**
   * Returns an add instruction of the given data type.
   *
   * @param type desired type to add
   * @param r1 destination of the add
   * @param r2 the first operand of the add
   * @param r3 the second operand of the add
   * @return an add instruction of the given type
   */
  public static Add create (Type type, Operand r1, Operand r2, Operand r3) {
    if (type.isLongType()) return new LongAdd (r1, r2, r3);
    if (type.isFloatType()) return new FloatAdd (r1, r2, r3);
    if (type.isDoubleType()) return new DoubleAdd (r1, r2, r3);
    if (type.isReferenceType()) return new RefAdd (r1, r2, r3);
    return new IntAdd (r1, r2, r3);
  }
}
