/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.tests.services.swbot.conditions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.waits.WaitForObjectCondition;
import org.hamcrest.Matcher;


final class MatchResourceCondition extends WaitForObjectCondition<IResource> implements ICondition {
  
  MatchResourceCondition(final Matcher<IResource> resourceMatcher) {
    super(resourceMatcher);
  }
  
  // --- Interface methods implementation

  public String getFailureMessage() {
    return NLS.bind("Could not find resource matching {0}", super.matcher); //$NON-NLS-1$
  }

  // --- Abstract methods implementation
  
  protected List<IResource> findMatches() {
    final List<IResource> resources = new ArrayList<IResource>();
    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    try {
      root.accept(new IResourceVisitor() {
        
        public boolean visit(final IResource resource) {
          if (MatchResourceCondition.super.matcher.matches(resource)) {
            resources.add(resource);
            return false;
          }
          return true;
        }
        
      });
    } catch (CoreException except) {
      // Let's forget.
    }
    return resources;
  }

}
