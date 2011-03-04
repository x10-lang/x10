/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.pdb.facts.db.IFactContext;
import org.eclipse.imp.pdb.facts.db.context.CompilationUnitContext;
import org.eclipse.imp.pdb.facts.db.context.FolderContext;
import org.eclipse.imp.pdb.facts.db.context.ProjectContext;
import org.eclipse.imp.pdb.facts.db.context.WorkspaceContext;

/**
 * Utility methods to create {@link IFactContext} implementations.  
 * 
 * @author egeay
 */
public final class ContextUtils {
  
  public static IFactContext createProjectContext(final String projectName) throws ModelException {
    final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
    return new ProjectContext(ModelFactory.open(project));
  }
  
  public static IFactContext createFileContext(final String filePath) throws ModelException {
    final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filePath));
    return new CompilationUnitContext((ICompilationUnit) ModelFactory.open(file));
  }
  
  public static IFactContext createFolderContext(final String folderPath) throws ModelException {
    final IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(new Path(folderPath));
    return new FolderContext(ModelFactory.open(folder));
  }
  
  public static IFactContext createWorkspaceContext() {
    return WorkspaceContext.getInstance();
  }
  
  // --- Private code
  
  private ContextUtils() {}

}
