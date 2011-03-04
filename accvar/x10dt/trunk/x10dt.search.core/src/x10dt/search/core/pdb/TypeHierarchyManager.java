/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.pdb;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.imp.pdb.analysis.AnalysisException;
import org.eclipse.imp.pdb.facts.IRelation;
import org.eclipse.imp.pdb.facts.ITuple;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.db.FactBase;
import org.eclipse.imp.pdb.facts.db.FactKey;
import org.eclipse.imp.pdb.facts.db.IFactContext;
import org.eclipse.imp.pdb.facts.db.IFactKey;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.osgi.util.NLS;

import polyglot.visit.NodeVisitor;
import x10dt.search.core.Messages;
import x10dt.search.core.facts.FactWriterFactory;



final class TypeHierarchyManager extends AbstractTypeManager implements ITypeManager {
  
  TypeHierarchyManager(final Type type, final ITypeManager allTypesManager) {
    super(type, allTypesManager);
    this.fAllTypesManager = (AllTypesManager) allTypesManager;
  }
  
  // --- Interface methods implementation
  
  public NodeVisitor createNodeVisitor(final String scopeTypeName) {
    return new FactWriterVisitor(FactWriterFactory.createTypeHierarchyFactWriter(scopeTypeName));
  }
  
  public void initWriter(final FactBase factBase, final IFactContext factContext, 
                         final IResource resource) throws AnalysisException {
    initWriter();
    
    final Set<ITuple> typesToRemove = new HashSet<ITuple>();
    this.fAllTypesManager.initWriter(factBase, factContext, resource, typesToRemove);
    
    final IFactKey key = new FactKey(getType(), factContext);
    if (factBase.getAllKeys().contains(key)) {
      final IRelation currentRelation = (IRelation) factBase.queryFact(key);
      if (currentRelation == null) {
        throw new AnalysisException(NLS.bind(Messages.ATM_NoDataBaseValue, key));
      }
      for (final IValue value : currentRelation) {
        final ITuple tuple = (ITuple) value;
        if (! typesToRemove.contains(tuple.get(0)) && ! typesToRemove.contains(tuple.get(1))) {
          getWriter().insert(value);
        }
      }
    }
  }
  
  // --- Private code
  
  private final AllTypesManager fAllTypesManager;

}
