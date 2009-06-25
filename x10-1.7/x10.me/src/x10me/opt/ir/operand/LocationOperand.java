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

import x10me.opt.alias.MemoryAtom;

/**
 * Parent of various types of memory references.
 */


public abstract class LocationOperand extends Operand {

  /**
   * The heap variable corresponding to this operand.
   */
  MemoryAtom memAtom;
  
  /**
   * Return <code>true</code> if the this MAY be type based aliased with other, otherwise
   * return <code>false</code>. The conservative result is <code>true</code>.
   * 
   * @param other the value to be compared with this.
   * @return <code>true</code> if the this MAY be aliased with other, otherwise
   *         return <code>false</code>.
   */
  abstract boolean mayBeTypeBasedAliased(LocationOperand other);
  
  /**
   * Return <code>true</code> if the this MUST be type based aliased with other, otherwise
   * return <code>false</code>. The conservative result is <code>false</code>.
   * 
   * @param other the value to be compared with this.
   * @return <code>true</code> if the this MAY be aliased with other, otherwise
   *         return <code>false</code>.
   */
  abstract boolean mustBeTypeBasedAliased(LocationOperand other);
  
  /**
   * Return <code>true</code> if the this MAY be aliased with other, otherwise
   * return <code>false</code>. The conservative result is <code>true</code>.
   * 
   * @param other the value to be compared with this.
   * @return <code>true</code> if the this MAY be aliased with other, otherwise
   *         return <code>false</code>.
   */
  public final boolean mayBeAliased(LocationOperand other) {
    if (this == other)
      return true;

    /*
     * If type based alias analysis analysis can prove that they cannot be aliased, 
     * return <code>false</code>.
     */
    if (!this.mayBeTypeBasedAliased(other))
      return false;
    
    /*
     * Determine if there is precise info yet.
     */
    if (this.memAtom != null)
      return this.memAtom.mayBeAliased(other.memAtom);
    else return true;
  }

  /**
   * Return <code>true</code> if the this MUST be aliased with other, otherwise
   * return <code>false</code>. The conservative result is <code>false</code>.
   * 
   * @param other the value to be compared with this.
   * @return <code>true</code> if the this MUST be aliased with other, otherwise
   *         return <code>false</code>.
   */
  public final boolean mustBeAliased(LocationOperand other) {
    if (this == other)
      return true;

    /*
     * If type based alias analysis analysis can prove that they cannot be aliased, 
     * return <code>false</code>.
     */
    if (!this.mustBeTypeBasedAliased(other))
      return false;
    
    /*
     * Determine if there is precise info yet.
     */
    if (this.memAtom != null)
      return this.memAtom.mustBeAliased(other.memAtom);
    else return true;
  }
}


