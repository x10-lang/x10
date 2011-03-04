/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.pdb;

import java.util.HashSet;

import org.eclipse.core.resources.IResource;
import org.eclipse.imp.pdb.analysis.AnalysisException;
import org.eclipse.imp.pdb.facts.ITuple;
import org.eclipse.imp.pdb.facts.db.FactBase;
import org.eclipse.imp.pdb.facts.db.IFactContext;
import org.eclipse.imp.pdb.facts.type.Type;

import polyglot.visit.NodeVisitor;
import x10dt.search.core.facts.FactWriterFactory;



final class AllMembersManager extends AbstractTypeManager implements ITypeManager {
  
  AllMembersManager(final Type type, final ITypeManager ... dependentManagers) {
    super(type, dependentManagers);
  }
  
  // --- Interface methods implementation
  
  public NodeVisitor createNodeVisitor(final String scopeTypeName) {
    return new FactWriterVisitor(FactWriterFactory.createAllMembersFactWriter(scopeTypeName, true));
  }
  
  public void initWriter(final FactBase factBase, final IFactContext factContext, 
                         final IResource resource) throws AnalysisException {
    initWriter(factBase, factContext, new HashSet<ITuple>());
  }
  
}
