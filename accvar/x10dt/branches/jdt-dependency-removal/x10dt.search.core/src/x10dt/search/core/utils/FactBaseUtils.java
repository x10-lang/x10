/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.utils;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.pdb.facts.ISet;
import org.eclipse.imp.pdb.facts.db.FactBase;
import org.eclipse.imp.pdb.facts.db.FactKey;
import org.eclipse.imp.pdb.facts.db.IFactContext;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.indexing.IndexManager;

import x10dt.search.core.pdb.SearchDBTypes;

/**
 * Utility methods around {@link FactBase} and {@link SearchDBTypes} classes.
 * 
 * @author egeay
 */
public final class FactBaseUtils {
  
  /**
   * Extracts and returns an {@link ISet} from the database for types of {@link SearchDBTypes}.
   * 
   * <p>The call may be long since we will assure that we are in sync with the database first.
   * 
   * @param factBase The fact database to consider.
   * @param type The fact database type to use.
   * @param context The context to use.
   * @param monitor The progress monitor that can be used for cancellation.
   * @return May returns <b>null</b> if there are no correspondence in the database.
   * @throws InterruptedException Occurs if the search thread got interrupted.
   */
  public static ISet getFactBaseSetValue(final FactBase factBase, final Type type, final IFactContext context, 
                                         final IProgressMonitor monitor) throws InterruptedException {
    while (! IndexManager.isAvailable() && ! monitor.isCanceled()) {
      synchronized (Thread.currentThread()) {
        Thread.currentThread().wait(500);
      }
    }
    if (monitor.isCanceled()) {
      throw new InterruptedException();
    }
    return (ISet) factBase.queryFact(new FactKey(type, context));
  }
  
  /**
   * Extracts and returns an {@link ISet} from the database for types of {@link SearchDBTypes}.
   * 
   * <p>The call may be long since we will assure that we are in sync with the database first.
   * 
   * @param factBase The fact database to consider.
   * @param context The context to use.
   * @param parametricTypeName The parametric type name to use for identifying the database type.
   * @param scopeTypeName The scope type name bound.
   * @param monitor The progress monitor that can be used for cancellation.
   * @return May returns <b>null</b> if there are no correspondence in the database.
   * @throws InterruptedException Occurs if the search thread got interrupted.
   */
  public static ISet getFactBaseSetValue(final FactBase factBase, final IFactContext context, final String parametricTypeName,
                                         final String scopeTypeName, 
                                         final IProgressMonitor monitor) throws InterruptedException {
    return getFactBaseSetValue(factBase, SearchDBTypes.getInstance().getType(parametricTypeName, scopeTypeName), context, 
                               monitor);
  }
  
  // --- Private code
  
  private FactBaseUtils() {}

}
