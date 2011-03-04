/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.engine.scope;


abstract class AbstractX10SearchScope implements IX10SearchScope {

  protected AbstractX10SearchScope(final int searchMask) {
    this.fSearchMask = searchMask;
  }
  
  // --- Interface methods implementation

  public int getSearchMask() {
    return this.fSearchMask;
  }
  
  // --- Fields
  
  private final int fSearchMask;

}
