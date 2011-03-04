/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.builder;

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.imp.builder.DependencyInfo;


final class SourceFilesCollector implements IResourceVisitor {
  
  SourceFilesCollector(final Collection<IFile> sourcesToCompile, final DependencyInfo dependencyInfo, final IProject project,
                       final Set<IProject> dependentProjects) {
    this.fSourcesToCompile = sourcesToCompile;
    this.fDependencyInfo = dependencyInfo;
    this.fProject = project;
    this.fDependentProjects = dependentProjects;
  }
  
  // --- Interface methods implementation

  public boolean visit(final IResource resource) throws CoreException {
    if ((resource.getType() == IResource.FILE) && ! resource.isDerived() && hasX10Ext(resource)) {
      this.fSourcesToCompile.add((IFile) resource);
      updateDependentProjects(resource);
      
      // Adds possible dependents now.
      final String path = resource.getLocation().toString();
      final Set<String> dependents = this.fDependencyInfo.getDependentsOf(path);
      if (dependents != null) {
        for (final String dependent : dependents) {
          final IFile file = this.fProject.getFile(dependent);
          if (file != null) {
            this.fSourcesToCompile.add(file);
            updateDependentProjects(file);
          }
        }
      }
    }
    return true;
  }
  
  // --- Private code
  
  private boolean hasX10Ext(final IResource resource) {
    return X10_EXT.equals(((IFile) resource).getFileExtension());
  }
  
  private void updateDependentProjects(final IResource resource) {
    if (! resource.getProject().equals(this.fProject)) {
      this.fDependentProjects.add(resource.getProject());
    }
  }
  
  // --- Fields
  
  private final Collection<IFile> fSourcesToCompile;
  
  private final DependencyInfo fDependencyInfo;
  
  private final IProject fProject;
  
  private final Set<IProject> fDependentProjects;
  
  
  private static final String X10_EXT = "x10"; //$NON-NLS-1$

}
