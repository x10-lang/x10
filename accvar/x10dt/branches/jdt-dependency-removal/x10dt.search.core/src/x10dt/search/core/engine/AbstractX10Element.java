/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.engine;

import org.eclipse.imp.pdb.facts.ISourceLocation;

import x10dt.search.core.elements.IX10Element;


abstract class AbstractX10Element implements IX10Element {
  
  AbstractX10Element(final ISourceLocation location) {
    this.fLocation = location;
  }
  
  // --- Interface methods implementation

  public final ISourceLocation getLocation() {
    return this.fLocation;
  }
  
  // --- Overridden methods
  
  public boolean equals(final Object rhs) {
    if ((rhs == null) || ! (rhs instanceof IX10Element)) {
      return false;
    }
    final IX10Element rhsObj = (IX10Element) rhs;
    return this.fLocation.equals(rhsObj.getLocation()); 
  }
  
  public int hashCode() {
    return this.fLocation.hashCode();
  }
  
  // --- Fields
  
  private final ISourceLocation fLocation;

}
