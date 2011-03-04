/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.engine.scope;

import org.eclipse.imp.pdb.facts.db.IFactContext;

import x10dt.search.core.engine.X10SearchEngine;
import x10dt.ui.launch.core.utils.ICountableIterable;

/**
 * Defines where the search should occur when using {@link X10SearchEngine}.
 * 
 * <p>Use {@link SearchScopeFactory} to access some implementations of this interface.
 * 
 * @author egeay
 */
public interface IX10SearchScope {
  
  /**
   * Indicates if the search scope contains the particular resource URI transmitted.
   * 
   * @param resourceURI The resource URI to consider.
   * @return True if the search scope contains such resource, false otherwise.
   */
  public boolean contains(final String resourceURI);
  
  /**
   * Creates the list of fact contexts to use for the search according to the scope defined.
   * 
   * @return A non-null iterable instance.
   */
  public ICountableIterable<IFactContext> createSearchContexts();
  
  /**
   * Returns the search mask that defines at which layer of the application the search should occur. 
   * 
   * See {@link X10SearchScope}.
   * 
   * @return A positive number.
   */
  public int getSearchMask();

}
