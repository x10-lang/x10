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

package x10me.opt.alias;

import x10me.types.Type;

/**
 * Memory element for aliasing oracle.
 * 
 *
 */

public class MemoryAtom {

  private Type type;
  
  /**
   * Return <code>true</code> if the this MAY be aliased with other, 
   * otherwise return <code>false</code>.
   * @param other the value to be compared with this.
   * @return <code>true</code> if the this MAY be aliased with other, 
   * otherwise return <code>false</code>.
   */
  public boolean mayBeAliased(MemoryAtom other) {
    if (this == other)
      return true;
    
    return this.type.isAssignableTo(other.type);
  }

  /**
   * Return <code>true</code> if the this MUST be aliased with other, 
   * otherwise return <code>false</code>.
   * 
   * @param other the value to be compared with this.
   * @return <code>true</code> if the this MUST be aliased with other, 
   * otherwise return <code>false</code>.
   */
  public boolean mustBeAliased(MemoryAtom other) {
    if (this == other)
      return true;
    
    return false;
  }
}
