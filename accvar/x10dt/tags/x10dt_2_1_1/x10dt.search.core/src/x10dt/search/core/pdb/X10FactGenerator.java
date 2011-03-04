/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.pdb;

import static x10dt.search.core.pdb.X10FactTypeNames.APPLICATION;
import static x10dt.search.core.pdb.X10FactTypeNames.RUNTIME;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.pdb.analysis.AnalysisException;
import org.eclipse.imp.pdb.analysis.IFactGenerator;
import org.eclipse.imp.pdb.analysis.IFactUpdater;
import org.eclipse.imp.pdb.facts.ISet;
import org.eclipse.imp.pdb.facts.db.FactBase;
import org.eclipse.imp.pdb.facts.db.FactKey;
import org.eclipse.imp.pdb.facts.db.IFactContext;
import org.eclipse.imp.pdb.facts.db.context.ISourceEntityContext;
import org.eclipse.imp.pdb.facts.db.context.WorkspaceContext;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.utils.UnimplementedError;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import polyglot.frontend.FileResource;
import polyglot.frontend.FileSource;
import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.frontend.ZipResource;
import x10dt.search.core.Messages;
import x10dt.search.core.SearchCoreActivator;
import x10dt.ui.launch.core.Constants;
import x10dt.ui.launch.core.dialogs.DialogsFactory;


final class X10FactGenerator implements IFactGenerator, IFactUpdater {
  
  X10FactGenerator(final SearchDBTypes searchDBTypes) {
    this.fIndexingCompiler = new IndexingCompiler();
    this.fSearchDBTypes = searchDBTypes;
  }

  // --- IFactGenerator's interface methods implementation
  
  public void generate(final FactBase factBase, final Type type, final IFactContext context) throws AnalysisException {
    // Should never be called by the indexer.
    throw new UnimplementedError();
  }
  
  // --- IFactUpdater's interface methods implementation

  public void update(final FactBase factBase, final Type type, final IFactContext context, 
                     final IResource resource) throws AnalysisException {
    if (context instanceof ISourceEntityContext) {
      final ContextWrapper sourceContext = processResource(resource);
      if (sourceContext != null) {
        final Set<Map.Entry<String, Collection<Source>>> entries = sourceContext.getSourceEntrySet();
        if (entries.isEmpty()) {
          final ITypeManager typeManager = this.fSearchDBTypes.getTypeManager(type.getName(), APPLICATION);
          try {
            typeManager.initWriter(factBase, context, resource);
            typeManager.writeDataInFactBase(factBase, context);
          } finally {
            typeManager.clearWriter();
          }
        } else {
          for (final Map.Entry<String, Collection<Source>> entry : entries) {
            final IFactContext factContext = RUNTIME.equals(entry.getKey()) ? WorkspaceContext.getInstance() : context;

            final ITypeManager typeManager = this.fSearchDBTypes.getTypeManager(type.getName(), entry.getKey());

            if (RUNTIME.equals(entry.getKey())) {
              typeManager.loadIndexingFile(factBase, factContext);

              final ISet value = (ISet) factBase.queryFact(new FactKey(typeManager.getType(), factContext));
              if ((value != null) && !value.isEmpty()) {
                continue; // We build the type info for the runtime context only one time.
              }
            }
            typeManager.initWriter(factBase, factContext, resource);

            final FactWriterVisitor visitor = typeManager.createNodeVisitor();
            visitor.setScopeType(entry.getKey());
            try {
              for (final Job job : this.fIndexingCompiler.compile(sourceContext.getClassPath(), sourceContext.getSourcePath(),
                                                                  entry.getValue())) {
                if (job.ast() != null) {
                  job.ast().visit(visitor);
                }
              }

              typeManager.writeDataInFactBase(factBase, factContext);

              if (RUNTIME.equals(entry.getKey()) && !hasIndexingFile(typeManager.getType().getName())) {
                typeManager.createIndexingFile(factBase, factContext);
              }
              if (fFailedResources.contains(resource)) {
                fFailedResources.remove(resource);
                if (fFailedResources.isEmpty()) {
                  PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
                    public void run() {
                      final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                      MessageDialog.openInformation(shell, Messages.XFG_IndexStatusDialogTitle,
                                                    Messages.XFG_IndexerStatusDialogMsg);
                    }
                  });
                }
              }
            } catch (Throwable except) {
              SearchCoreActivator.log(IStatus.ERROR, Messages.XFG_IndexerCompilationLogError, except);
              final Throwable exception = except;
              if (fFailedResources.isEmpty()) {
                fFailedResources.add(resource);
                PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
                  public void run() {
                    final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                    DialogsFactory.createErrorBuilder()
                                  .setDetailedMessage(exception)
                                  .createAndOpen(shell, Messages.XFG_IndexingProblemDialogTitle,
                                                 NLS.bind(Messages.XFG_IndexingProblemDialogMsg, resource.getLocation()));
                  }
                });
              }
            } finally {
              typeManager.clearWriter();
            }
          }
        }
      }
    }
  }
  
  // --- Private code
  
  private boolean hasIndexingFile(final String indexingFileName) {
    final File pluginStateLocation = Platform.getStateLocation(SearchCoreActivator.getInstance().getBundle()).toFile();
    final File indexingFile = new File(pluginStateLocation, indexingFileName);
    return indexingFile.exists();
  }
  
  private void processEntries(final ContextWrapper context, final IWorkspaceRoot wsRoot, final IClasspathEntry[] entries, 
                              final IJavaProject javaProject, final IResource contextResource,
                              final boolean isInRuntime) throws JavaModelException, AnalysisException {
    for (final IClasspathEntry pathEntry : entries) {
      switch (pathEntry.getEntryKind()) {
      case IClasspathEntry.CPE_SOURCE:
        if (pathEntry.getPath().isRoot()) {
          context.addToSourcePath(pathEntry.getPath().toFile());
        } else {
          context.addToSourcePath(wsRoot.getLocation().append(pathEntry.getPath()).toFile());
        }
        if (pathEntry.getPath().segmentCount() > 1) {
          processSourceFolder(context, wsRoot.getFolder(pathEntry.getPath()), wsRoot, contextResource);
        }
        break;

      case IClasspathEntry.CPE_LIBRARY:
        try {
          final IPackageFragmentRoot pkgRoot = javaProject.findPackageFragmentRoot(pathEntry.getPath());
          if ((pkgRoot != null) && pkgRoot.exists()) {
            final File localFile;
            if (pkgRoot.isExternal()) {
              localFile = pathEntry.getPath().toFile();
            } else {
              localFile = pkgRoot.getResource().getLocation().toFile();
            }
            context.addToClassPath(localFile.getAbsolutePath());
            if (isInRuntime) {
              context.addToSourcePath(localFile);
            }
            final ZipFile zipFile;
            if (JAR_EXT.equals(pathEntry.getPath().getFileExtension())) {
              zipFile = new JarFile(localFile);
            } else {
              zipFile = new ZipFile(localFile);
            }
            processLibrary(context, zipFile, localFile, contextResource, isInRuntime);
          }
        } catch (IOException except) {
          throw new AnalysisException(NLS.bind(Messages.XFG_JarReadingError, pathEntry.getPath()), except);
        }
        break;

      case IClasspathEntry.CPE_CONTAINER:
        final IClasspathContainer cpContainer = JavaCore.getClasspathContainer(pathEntry.getPath(), javaProject);
        processEntries(context, wsRoot, cpContainer.getClasspathEntries(), javaProject, contextResource, true);
        break;

      case IClasspathEntry.CPE_PROJECT:
        final IResource projectResource = ResourcesPlugin.getWorkspace().getRoot().findMember(pathEntry.getPath());
        if ((projectResource != null) && projectResource.isAccessible()) {
          final IJavaProject newJavaProject = JavaCore.create((IProject) projectResource);
          processEntries(context, wsRoot, newJavaProject.getRawClasspath(), newJavaProject, contextResource, false);
        }
        break;

      case IClasspathEntry.CPE_VARIABLE:
        processEntries(context, wsRoot, new IClasspathEntry[] { JavaCore.getResolvedClasspathEntry(pathEntry) }, javaProject, 
                       contextResource, false);
        break;
      }
    }
  }
  
  private ContextWrapper processResource(final IResource resource) throws AnalysisException {
    final IJavaProject javaProject = JavaCore.create(resource.getProject());  
    if (javaProject.exists() && resource.exists()) {
      final ContextWrapper context = new ContextWrapper();
      final IWorkspaceRoot wsRoot = javaProject.getProject().getWorkspace().getRoot();
      try {
        processEntries(context, wsRoot, javaProject.getRawClasspath(), javaProject, resource, false);
      } catch (CoreException except) {
        throw new AnalysisException(NLS.bind(Messages.XFG_ResourceAccessError, resource.getFullPath()), except);
      }
      return context;
    } else {
      return null;
    }
  }
  
  private void processSourceFolder(final ContextWrapper context, final IFolder folder, final IWorkspaceRoot wsRoot,
                                   final IResource contextResource) throws AnalysisException {
    final IResource curResource;
    if (contextResource.getType() == IResource.PROJECT) {
      curResource = folder;
    } else {
      curResource = wsRoot.findMember(contextResource.getFullPath());
    }
    if (curResource != null) {
      try {
        curResource.accept(new IResourceVisitor() {
          
          public boolean visit(final IResource resource) throws CoreException {
            if (resource.getType() == IResource.FILE) {
              final IFile file = (IFile) resource;
              if (! file.isSynchronized(IResource.DEPTH_ZERO)) {
                file.refreshLocal(IResource.DEPTH_ZERO, null);
              }
              final IPath location  = file.getLocation();
              if (location != null) {
                final File localFile = location.toFile();
                if (localFile.exists() && X10_EXT.equals(file.getFileExtension())) {
                  try {
                    context.addSource(X10FactTypeNames.APPLICATION, new FileSource(new FileResource(localFile)));
                  } catch (IOException except) {
                    // It can't occur since we already have tested for existence.
                    SearchCoreActivator.log(IStatus.ERROR, null, except);
                  }
                }
              }
            }
            return true;
          }
          
        });
      } catch (CoreException except) {
        throw new AnalysisException(NLS.bind(Messages.XFG_ResourceAccessError, curResource.getFullPath()), except);
      }
    }
  }
  
  private void processLibrary(final ContextWrapper context, final ZipFile zipFile, final File file, 
                              final IResource contextResource, final boolean isInRuntime) throws IOException {
    if (contextResource.getType() == IResource.PROJECT) {
      final Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
      while (zipEntries.hasMoreElements()) {
        final ZipEntry entry = zipEntries.nextElement();
        if (entry.getName().endsWith(Constants.X10_EXT)) {
          context.addSource(isInRuntime ? X10FactTypeNames.RUNTIME : X10FactTypeNames.LIBRARY,
                            new FileSource(new ZipResource(file, zipFile, entry.getName())));
        }
      }
    }
  }
  
  // --- Private classes
  
  private static final class ContextWrapper {
    
    // --- Internal services
    
    void addSource(final String scope, final Source source) {
      final Collection<Source> container = this.fSources.get(scope);
      if (container == null) {
        final Collection<Source> newContainer = new LinkedList<Source>();
        newContainer.add(source);
        this.fSources.put(scope, newContainer);
      } else {
        container.add(source);
      }
    }
    
    void addToClassPath(final String classPathEntry) {
      if (this.fClassPathBuilder.length() > 0) {
        this.fClassPathBuilder.append(File.pathSeparatorChar);
      }
      this.fClassPathBuilder.append(classPathEntry);
    }
    
    void addToSourcePath(final File srcEntry) {
      this.fSourcePath.add(srcEntry);
    }
    
    String getClassPath() {
      return this.fClassPathBuilder.toString();
    }
    
    Set<Map.Entry<String, Collection<Source>>> getSourceEntrySet() {
      return this.fSources.entrySet();
    }
    
    List<File> getSourcePath() {
      return this.fSourcePath;
    }
    
    // --- Overridden methods
    
    public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append("Class path: ").append(this.fClassPathBuilder) //$NON-NLS-1$
        .append("\nSource path: ").append(this.fSourcePath); //$NON-NLS-1$
      for (final Map.Entry<String, Collection<Source>> entry : this.fSources.entrySet()) {
        sb.append("\n-> ").append(entry.getKey()).append('\n').append(entry.getValue()); //$NON-NLS-1$
      }
      return sb.toString();
    }
    
    // --- Fields
    
    private final StringBuilder fClassPathBuilder = new StringBuilder();
    
    private final List<File> fSourcePath = new ArrayList<File>();
    
    private final Map<String, Collection<Source>> fSources = new HashMap<String, Collection<Source>>();
    
  }
  
  // --- Fields
    
  private final IndexingCompiler fIndexingCompiler;
  
  private final SearchDBTypes fSearchDBTypes;
  
  
  private static Set<IResource> fFailedResources = new HashSet<IResource>();
  
  
  private static final String JAR_EXT = "jar"; //$NON-NLS-1$
  
  private static final String X10_EXT = "x10"; //$NON-NLS-1$

}
