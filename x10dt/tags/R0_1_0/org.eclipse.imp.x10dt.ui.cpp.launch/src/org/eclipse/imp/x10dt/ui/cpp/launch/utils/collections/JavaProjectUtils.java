/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.launch.utils.collections;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.imp.x10dt.ui.cpp.launch.LaunchCore;
import org.eclipse.imp.x10dt.ui.cpp.launch.LaunchMessages;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;

/**
 * Utility methods for JDT {@link IJavaProject} interface.
 *
 * @author egeay
 */
public final class JavaProjectUtils {
  
  /**
   * Returns a filtered set of class path entries for a given Java project.
   * 
   * @param <T> The type of the class path entries once transformed via the functor provided.
   * @param jProject The Java project to consider.
   * @param cpEntryFunctor The functor to use to transform an {@link IPath} related to a class path
   * entry into another type of interest.
   * @param libFilter The filter to user in order to filter the library entries.
   * @return A non-null, possibly empty, set of class path entries.
   * @throws JavaModelException Occurs if we could not resolve the class path entries.
   * @throws IllegalArgumentException Occurs if a class path entry kind is not one of the expected
   * list. More precisely, CPE_VARIABLE and CPE_CONTAINER should not be encountered.
   */
  public static <T> Set<T> getFilteredCpEntries(final IJavaProject jProject, final IFunctor<IPath, T> cpEntryFunctor,
                                                final IFilter<IPath> libFilter) throws JavaModelException {
    final Set<T> container = new HashSet<T>();
    final IWorkspaceRoot root = jProject.getResource().getWorkspace().getRoot();
    for (final IClasspathEntry cpEntry : jProject.getResolvedClasspath(true)) {
      collectCpEntries(container, cpEntry, root, libFilter, cpEntryFunctor);
    }
    return container;
  }
  
  // --- Private code
  
  private JavaProjectUtils() {}
  
  private static <T> void collectCpEntries(final Set<T> container, final IClasspathEntry cpEntry, final IWorkspaceRoot root, 
                                           final IFilter<IPath> libFilter, 
                                           final IFunctor<IPath, T> functor) throws JavaModelException {
    switch (cpEntry.getEntryKind()) {
      case IClasspathEntry.CPE_SOURCE:
        container.add(functor.apply(getAbsolutePath(root, cpEntry.getPath())));
        break;
        
      case IClasspathEntry.CPE_LIBRARY:
        if (libFilter.accepts(cpEntry.getPath())) {
          container.add(functor.apply(cpEntry.getPath()));
        }
        break;
      
      case IClasspathEntry.CPE_PROJECT:
        final IResource resource = root.findMember(cpEntry.getPath());
        if (resource == null) {
          LaunchCore.log(IStatus.WARNING, NLS.bind(LaunchMessages.JPU_ResourceErrorMsg, cpEntry.getPath()));
        } else {
          final IJavaProject refProject = JavaCore.create((IProject) resource);
          for (final IClasspathEntry newCPEntry : refProject.getResolvedClasspath(true)) {
            collectCpEntries(container, newCPEntry, root, libFilter, functor);
          }
        }
        break;
        
      default:
        throw new IllegalArgumentException(NLS.bind(LaunchMessages.JPU_UnexpectedEntryKindMsg, cpEntry.getEntryKind()));
    }
  }
  
  private static IPath getAbsolutePath(final IWorkspaceRoot root, final IPath path) {
    if (path.isRoot()) {
      return path;
    } else {
      return root.getLocation().append(path);
    }
  }

}
