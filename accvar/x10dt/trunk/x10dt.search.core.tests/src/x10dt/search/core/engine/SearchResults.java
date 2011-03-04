/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.engine;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.imp.pdb.facts.ISourceLocation;
import org.eclipse.imp.utils.Pair;

import x10dt.search.core.elements.IMemberInfo;


final class SearchResults implements ISearchResultObserver {

  // --- Interface methods implementation
  
  public void accept(final IMemberInfo container, final ISourceLocation sourceLocation) {
    this.fFindings.add(new Pair<IMemberInfo, ISourceLocation>(container, sourceLocation));
  }
  
  // --- Internal services
  
  Iterable<Pair<IMemberInfo, ISourceLocation>> getFindings() {
    return this.fFindings;
  }
  
  int getNumberOfFindings() {
    return this.fFindings.size();
  }
  
  // --- Fields
  
  private final Collection<Pair<IMemberInfo,ISourceLocation>> fFindings = new ArrayList<Pair<IMemberInfo,ISourceLocation>>();

}
