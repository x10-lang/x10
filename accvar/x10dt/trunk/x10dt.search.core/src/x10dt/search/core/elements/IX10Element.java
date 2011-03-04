/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.elements;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.model.ISourceEntity;
import org.eclipse.imp.pdb.facts.ISourceLocation;

/**
 * Common services for all X10 elements provided by the X10 indexer.
 * 
 * @author egeay
 */
public interface IX10Element {
  
  /**
   * Indicates if the underlying type still exists in the indexing database.
   * 
   * @param monitor Monitor to report progress and/or cancel the operation.
   * @return True if it exists, false otherwise.
   */
  public boolean exists(final IProgressMonitor monitor);
  
  /**
   * Returns the location of the type declaration.
   * 
   * @return The location or <b>null</b> if there is none.
   */
  public ISourceLocation getLocation();
  
  /**
   * Returns the source entity enclosing or representing the particular X10 element.
   * 
   * @return A <b>possibly null</b> object instance if we not could access the source entity.
   */
  public ISourceEntity getSourceEntity();

}
