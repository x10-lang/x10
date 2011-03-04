/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.engine.scope;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.pdb.facts.db.IFactContext;
import org.eclipse.imp.pdb.facts.db.context.ProjectContext;
import org.eclipse.osgi.util.NLS;

import x10dt.search.core.Messages;
import x10dt.search.core.SearchCoreActivator;
import x10dt.ui.launch.core.utils.CountableIterableFactory;
import x10dt.ui.launch.core.utils.ICountableIterable;


final class X10WorkspaceScope extends AbstractX10SearchScope implements IX10SearchScope {
  
  X10WorkspaceScope(final int searchMask) {
    super(searchMask);
  }

  // --- Interface methods implementation
  
  public boolean contains(final String resourceURI) {
    return true;
  }
  
  public ICountableIterable<IFactContext> createSearchContexts() {
    final Collection<IFactContext> searchContexts = new ArrayList<IFactContext>();
    for (final IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
      if (project.exists()) {
        try {
          searchContexts.add(new ProjectContext(ModelFactory.open(project)));
        } catch (ModelException except) {
          // Since we test for existence, this exception should never occur. Log it, in case the assertion is violated.
          SearchCoreActivator.log(IStatus.ERROR, NLS.bind(Messages.XS_ProjectExistenceError, project.getName()), except);
        }
      }
    }
    return CountableIterableFactory.create(searchContexts);
  }

}
