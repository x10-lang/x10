/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.engine.scope;

import org.eclipse.core.resources.IResource;

/**
 * Factory methods to create implementations of {@link IX10SearchScope}.
 * 
 * @author egeay
 */
public final class SearchScopeFactory {
  
  /**
   * Creates a search scope around a list of particular resources.
   * 
   * @param searchMask Defines the scope level of the search. Most of the time, {@link X10SearchScope#ALL} will be wanted. 
   * @param resources The list of resources to consider for the search.
   * @return A non-null instance of {@link IX10SearchScope}.
   */
  public static IX10SearchScope createSelectiveScope(final int searchMask, final IResource ... resources) {
    return new X10SelectiveScope(searchMask, resources);
  }
  
  /**
   * Creates a workspace search scope.
   * 
   * @param searchMask Defines the scope level of the search. Most of the time, {@link X10SearchScope#ALL} will be wanted. 
   * @return A non-null instance of {@link IX10SearchScope}.
   */
  public static IX10SearchScope createWorkspaceScope(final int searchMask) {
    return new X10WorkspaceScope(searchMask);
  }
  
  // --- Private code
  
  private SearchScopeFactory() {}

}
