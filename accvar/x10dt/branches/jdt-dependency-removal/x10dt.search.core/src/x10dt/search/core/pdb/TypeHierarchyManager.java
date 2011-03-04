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
import org.eclipse.imp.pdb.facts.IString;
import org.eclipse.imp.pdb.facts.ITuple;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.db.FactBase;
import org.eclipse.imp.pdb.facts.db.FactKey;
import org.eclipse.imp.pdb.facts.db.IFactContext;
import org.eclipse.imp.pdb.facts.db.IFactKey;
import org.eclipse.imp.pdb.facts.impl.fast.ValueFactory;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.osgi.util.NLS;

import polyglot.visit.NodeVisitor;
import x10dt.search.core.Messages;



final class TypeHierarchyManager extends AbstractTypeManager implements ITypeManager {
  
  TypeHierarchyManager(final Type type, final ITypeManager allTypesManager) {
    super(type);
    this.fAllTypesManager = allTypesManager;
  }
  
  // --- Interface methods implementation
  
  public void clearWriter() {
    this.fAllTypesManager.clearWriter();
    super.fWriter = null;
  }
  
  public void createIndexingFile(final FactBase factBase, final IFactContext factContext) {
    this.fAllTypesManager.createIndexingFile(factBase, factContext);
    createIndexingFile(factBase.queryFact(new FactKey(getType(), factContext)));
  }
  
  public NodeVisitor createNodeVisitor(final String scopeTypeName) {
    return new TypeHierarchyFactWriterVisitor(scopeTypeName);
  }
  
  public void writeDataInFactBase(final FactBase factBase, final IFactContext factContext) {
    this.fAllTypesManager.writeDataInFactBase(factBase, factContext);
    factBase.defineFact(new FactKey(getType(), factContext), getWriter().done());
  }
  
  public final void initWriter() {
    this.fAllTypesManager.initWriter();
    super.fWriter = getType().writer(ValueFactory.getInstance());
  }
  
  public void initWriter(final FactBase factBase, final IFactContext factContext, 
                         final IResource resource) throws AnalysisException {
    final Set<IString> typesToRemove = new HashSet<IString>();
    ((AllTypesManager) this.fAllTypesManager).initWriter(factBase, factContext, resource, typesToRemove);
    
    super.fWriter = getType().writer(ValueFactory.getInstance());
    
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
  
  public void loadIndexingFile(final FactBase factBase, final IFactContext factContext) {
    this.fAllTypesManager.loadIndexingFile(factBase, factContext);
    loadIndexingFileForManagedType(factBase, factContext);
  }
  
  // --- Fields
  
  private final ITypeManager fAllTypesManager;

}
