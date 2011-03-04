/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.pdb;

import org.eclipse.imp.pdb.analysis.IFactGenerator;
import org.eclipse.imp.pdb.analysis.IFactGeneratorFactory;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeStore;

/**
 * Factory class to create the {@link TypeStore} and {@link IFactGenerator} implementations for X10 searching activities.
 * 
 * @author egeay
 */
public final class X10FactGeneratorFactory implements IFactGeneratorFactory {

  // --- Interface methods implementation
  
  public String getName() {
    return "X10 Fact Generator Factory"; //$NON-NLS-1$
  }

  public IFactGenerator create(final Type type) {
    return new X10FactGenerator(SearchDBTypes.getInstance());
  }

  public TypeStore declareTypes() {
    return SearchDBTypes.getInstance().getTypeStore();
  }

}
