/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package x10dt.ui.launch.core.utils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.IPathEntry;
import org.eclipse.imp.model.IPathEntry.PathEntryType;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.utils.BuildPathUtils;
import org.eclipse.osgi.util.NLS;

import x10dt.ui.launch.core.LaunchCore;
import x10dt.ui.launch.core.Messages;

/**
 * Utility methods for JDT {@link ISourceProject} interface.
 *
 * @author egeay
 */
public final class ProjectUtils {
  
  /**
   * Returns a filtered set of class path entries for a given Java project.
   * 
   * @param <T> The type of the class path entries once transformed via the functor provided.
   * @param jProject The Java project to consider.
   * @param cpEntryFunctor The functor to use to transform an {@link IPath} related to a class path
   * entry into another type of interest.
   * @param libFilter The filter to user in order to filter the library entries.
   * @return A non-null, possibly empty, set of class path entries.
   * @throws ModelException Occurs if we could not resolve the class path entries.
   * @throws IllegalArgumentException Occurs if a class path entry kind is not one of the expected
   * list. More precisely, CPE_VARIABLE and CPE_CONTAINER should not be encountered.
   */
  public static <T> Set<T> getFilteredCpEntries(final ISourceProject jProject, final IFunctor<IPath, T> cpEntryFunctor,
                                                final IFilter<IPath> libFilter) throws ModelException {
    final Set<T> container = new HashSet<T>();
    final IWorkspaceRoot root = jProject.getResource().getWorkspace().getRoot();
    for (final IPathEntry cpEntry : jProject.getResolvedBuildPath(LanguageRegistry.findLanguage("X10"), true)) {
      collectCpEntries(container, cpEntry, root, libFilter, cpEntryFunctor);
    }
    return container;
  }
  
  /**
   * Returns the path to the project main output directory.
   * 
   * @param project The project of interest.
   * @return A non-null string identifying the project output directory.
   * @throws CoreException Occurs if we could not access the output directory for the project transmitted.
   */
  public static String getProjectOutputDirPath(final IProject project) throws CoreException {
    final ISourceProject javaProject = ModelFactory.getProject(project);
    final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    final URI outputFolderURI = root.getFolder(javaProject.getOutputLocation(LanguageRegistry.findLanguage("X10"))).getLocationURI();
    return EFS.getStore(outputFolderURI).toLocalFile(EFS.NONE, new NullProgressMonitor()).getAbsolutePath();
  }
  
  /**
   * Returns the collection of source folders for a given project.
   * 
   * @param project The project of interest.
   * @return A non-null collection of workspace-relative strings representing the src folders of the project.
   * @throws ModelException Occurs if we could not resolve the project class path.
   */
  public static Collection<String> collectSourceFolders(final ISourceProject project) throws ModelException {
    final Collection<String> result = new ArrayList<String>();
    for (final IPathEntry cpEntry : project.getBuildPath(LanguageRegistry.findLanguage("X10"))) {
      if (cpEntry.getEntryType() == PathEntryType.SOURCE_FOLDER) {
        final IPath entryPath = cpEntry.getRawPath();
        if (! entryPath.segment(0).equals(project.getName())) {
          continue;
        }
        result.add(entryPath.toOSString());
      }
    }
    return result;
  }
  
  // --- Private code
  
  private ProjectUtils() {}
  
  private static <T> void collectCpEntries(final Set<T> container, final IPathEntry cpEntry, final IWorkspaceRoot root, 
                                           final IFilter<IPath> libFilter, 
                                           final IFunctor<IPath, T> functor) throws ModelException {
    switch (cpEntry.getEntryType()) {
      case SOURCE_FOLDER:
        container.add(functor.apply(getAbsolutePath(root, cpEntry.getRawPath())));
        break;
        
      case ARCHIVE:
        if (libFilter.accepts(cpEntry.getRawPath())) {
          container.add(functor.apply(cpEntry.getRawPath()));
        }
        break;
      
      case PROJECT:
        final IResource resource = root.findMember(cpEntry.getRawPath());
        if (resource == null) {
          LaunchCore.log(IStatus.WARNING, NLS.bind(Messages.JPU_ResourceErrorMsg, cpEntry.getRawPath()));
        } else {
          final ISourceProject refProject = ModelFactory.getProject((IProject) resource);
          for (final IPathEntry newCPEntry : refProject.getBuildPath(LanguageRegistry.findLanguage("X10"))) {
            collectCpEntries(container, newCPEntry, root, libFilter, functor);
          }
        }
        break;
        
      default:
        throw new IllegalArgumentException(NLS.bind(Messages.JPU_UnexpectedEntryKindMsg, cpEntry.getEntryType()));
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
