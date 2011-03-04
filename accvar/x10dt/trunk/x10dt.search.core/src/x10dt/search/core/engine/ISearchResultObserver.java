/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.engine;

import org.eclipse.imp.pdb.facts.ISourceLocation;

import x10dt.search.core.elements.IMemberInfo;

/**
 * Responsible for notifying the search results that have been found when looking for references.
 * 
 * See {@link X10SearchEngine#collectFieldUses}, {@link X10SearchEngine#collectMethodUses} and
 * {@link X10SearchEngine#collectTypeUses}.
 * 
 * @author egeay
 */
public interface ISearchResultObserver {
  
  /**
   * Notifies a search result with the container and source location of the finding.
   * 
   * @param container The containing entity.
   * @param sourceLocation The source location of the finding.
   */
  public void accept(final IMemberInfo container, final ISourceLocation sourceLocation);

}
