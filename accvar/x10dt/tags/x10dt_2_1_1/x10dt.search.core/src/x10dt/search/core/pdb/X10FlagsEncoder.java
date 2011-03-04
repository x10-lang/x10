/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.pdb;

import polyglot.types.Flags;

/**
 * Responsible for encoding relevant X10 flags for search indexing with a simple integer.
 * 
 * @author egeay
 */
public final class X10FlagsEncoder {
  
  /**
   * The list of X10 flags.
   * 
   * @author egeay
   */
  public enum X10 {
    
    /**
     * <code>public</code>.
     */
    PUBLIC(1),
    
    /**
     * <code>private</code>.
     */
    PRIVATE(1 << 1),
    
    /**
     * <code>protected</code>.
     */
    PROTECTED(1 << 2),
    
    /**
     * <code>static</code>.
     */
    STATIC(1 << 3),
    
    /**
     * <code>final</code>.
     */
    FINAL(1 << 4),
        
    /**
     * <code>native</code>.
     */
    NATIVE(1 << 5),
    
    /**
     * <code>interface</code>.
     */
    INTERFACE(1 << 6),
    
    /**
     * <code>abstract</code>.
     */
    ABSTRACT(1 << 7),
        
    /**
     * <code>atomic</code>.
     */
    ATOMIC(1 << 8),
        
    /**
     * <code>property</code>.
     */
    PROPERTY(1 << 9),
    
    /**
     * <code>clocked</code>.
     */
    CLOCKED(1 << 10);
    
    // --- Public services
    
    /**
     * Returns the unique code that identifies the list of X10 flags modeled.
     * 
     * @return A natural number.
     */
    public int getCode() {
      return this.fCode;
    }
   
    // --- Private code
    
    private X10(final int code) {
      this.fCode = code;
    }
    
    private final int fCode;
    
  }
  
  /**
   * Initializes the unique code representing the X10 flags.
   * 
   * @param flags The X10 flags to consider.
   */
  public X10FlagsEncoder(final Flags flags) {
    int code = 0;
    if (flags.isPublic()) {
      code |= X10.PUBLIC.getCode();
    }
    if (flags.isPrivate()) {
      code |= X10.PRIVATE.getCode();
    }
    if (flags.isProtected()) {
      code |= X10.PROTECTED.getCode();
    }
    if (flags.isStatic()) {
      code |= X10.STATIC.getCode();
    }
    if (flags.isFinal()) {
      code |= X10.FINAL.getCode();
    }
    if (flags.isNative()) {
      code |= X10.NATIVE.getCode();
    }
    if (flags.isInterface()) {
      code |= X10.INTERFACE.getCode();
    }
    if (flags.isAbstract()) {
      code |= X10.ABSTRACT.getCode();
    }
    if (flags.isAtomic()) {
      code |= X10.ATOMIC.getCode();
    }
    if (flags.isProperty()) {
      code |= X10.PROPERTY.getCode();
    }
    this.fCodeRep = code;
  }
  
  // --- Public services
  
  /**
   * Returns the unique code representing the list of X10 flags encapsulated.
   * 
   * @return A natural number.
   */
  public int getCode() {
    return this.fCodeRep;
  }
  
  // --- Fields
  
  private final int fCodeRep;

}
