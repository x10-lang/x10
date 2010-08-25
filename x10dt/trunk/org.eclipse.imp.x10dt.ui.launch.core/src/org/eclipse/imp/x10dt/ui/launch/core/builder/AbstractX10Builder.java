/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.builder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.x10dt.core.X10DTCorePlugin;
import org.eclipse.imp.x10dt.core.builder.StreamSource;
import org.eclipse.imp.x10dt.core.preferences.generated.X10Constants;
import org.eclipse.imp.x10dt.ui.launch.core.Constants;
import org.eclipse.imp.x10dt.ui.launch.core.LaunchCore;
import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.imp.x10dt.ui.launch.core.builder.target_op.IX10BuilderFileOp;
import org.eclipse.imp.x10dt.ui.launch.core.utils.AlwaysTrueFilter;
import org.eclipse.imp.x10dt.ui.launch.core.utils.CollectionUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.CoreResourceUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.CountableIterableFactory;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IFilter;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IFunctor;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IdentityFunctor;
import org.eclipse.imp.x10dt.ui.launch.core.utils.ProjectUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.UIUtils;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.osgi.util.NLS;

import polyglot.frontend.Compiler;
import polyglot.frontend.Globals;
import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.util.InternalCompilerError;
import x10.ExtensionInfo;

/**
 * X10 builder base class for all the different back-ends.
 * 
 * @author egeay
 */
public abstract class AbstractX10Builder extends IncrementalProjectBuilder {
  
  // --- Abstract methods definition
  
  /**
   * Creates the Polyglot extension information that controls the compiler options for the particular back-end.
   * 
   * @param classPath The class path to consider for compilation.
   * @param sourcePath The source path to use.
   * @param localOutputDir The directory where the generated files will be created.
   * @param withMainMethod True if the main method should be generated, false otherwise.
   * @param monitor The monitor to use for reporting progress and/or cancel the operation.
   * @return A non-null object.
   */
  public abstract ExtensionInfo createExtensionInfo(final String classPath, final List<File> sourcePath,
                                                    final String localOutputDir, final boolean withMainMethod, 
                                                    final IProgressMonitor monitor);
  
  /**
   * Creates a filter for all the native files that may be present in the projects for the current back-end.
   * 
   * @return A non-null filter instance.
   */
  public abstract IFilter<IFile> createNativeFilesFilter();
  
  /**
   * Creates the instance of {@link IX10BuilderFileOp} depending of the connection type, local or remote.
   * 
   * @return A non-null object.
   * @throws CoreException Occurs if we could not load the X10 platform configuration associated with the project.
   */
  public abstract IX10BuilderFileOp createX10BuilderFileOp() throws CoreException;
  
 
  /**
   * 
   * @return The file extension corresponding to each backend (e.g., ".java" for Java backend).
   */
  public abstract String getFileExtension();
  
  
  
  // --- Abstract methods implementation
  
 
  
  
  @SuppressWarnings("rawtypes")
  protected final IProject[] build(final int kind, final Map args, final IProgressMonitor monitor) throws CoreException {
    if (! getProject().isAccessible()) {
      return null;
    }
    try {
      if (this.fProjectWrapper == null) {
        return new IProject[0];
      }
      final SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
      final Collection<IFile> sourcesToCompile = new HashSet<IFile>();
      final Collection<IFile> deletedSources = new HashSet<IFile>();
      final Collection<IFile> nativeFiles = new HashSet<IFile>();
      
      final IX10BuilderFileOp x10BuilderOp = createX10BuilderFileOp();
      if (! x10BuilderOp.hasAllPrerequisites()) {
        CoreResourceUtils.addBuildMarkerTo(getProject(), NLS.bind(Messages.AXB_IncompleteConfMsg, getProject().getName()), 
                                           IMarker.SEVERITY_ERROR, IMarker.PRIORITY_HIGH);
        UIUtils.showProblemsView();
        return null;
      }
      final Set<IProject> dependentProjects = cleanFiles(kind, subMonitor.newChild(10), sourcesToCompile, deletedSources, 
                                                         nativeFiles, x10BuilderOp);
      if (dependentProjects == null) {
        return null;
      }

      final String localOutputDir = ProjectUtils.getProjectOutputDirPath(getProject());
      x10BuilderOp.copyToOutputDir(nativeFiles, subMonitor.newChild(5));
      compileX10Files(localOutputDir, sourcesToCompile, subMonitor.newChild(20));
      
      compileGeneratedFiles(x10BuilderOp, sourcesToCompile, subMonitor.newChild(65));
      
      return dependentProjects.toArray(new IProject[dependentProjects.size()]);
    } finally {
      monitor.done();
    }
  }

  private Set<IProject> cleanFiles(final int kind, final SubMonitor subMonitor, final Collection<IFile> sourcesToCompile,
                                   final Collection<IFile> deletedSources, final Collection<IFile> nativeFiles,
                                   final IX10BuilderFileOp x10BuilderFileOp) throws CoreException {
    subMonitor.beginTask(null, 10);
    try {
      final boolean shouldBuildAll;
      if (kind == FULL_BUILD) {
        this.fDependencyInfo.clearAllDependencies();
        shouldBuildAll = true;
      } else {
        shouldBuildAll = this.fDependencyInfo.getDependencies().isEmpty();
      }
      
      final Set<IProject> dependentProjects = new HashSet<IProject>();
      collectSourceFilesToCompile(sourcesToCompile, nativeFiles, deletedSources, this.fProjectWrapper, dependentProjects,
                                  createNativeFilesFilter(), shouldBuildAll, subMonitor.newChild(5));
      if (subMonitor.isCanceled()) {
        return null;
      }
      
      clearMarkers(sourcesToCompile);
      
      x10BuilderFileOp.cleanFiles(CountableIterableFactory.create(sourcesToCompile, nativeFiles, deletedSources),
                                  subMonitor.newChild(5));
      return dependentProjects;
    } finally {
      subMonitor.done();
    }
  }
  
  // --- Overridden methods
  
  protected void clean(final IProgressMonitor monitor) throws CoreException {
    if (getProject().isAccessible()) {
      if (this.fProjectWrapper == null) {
        this.fProjectWrapper = JavaCore.create(getProject());
      }
      this.fDependencyInfo.clearAllDependencies();
      final IX10BuilderFileOp x10BuilderOp = createX10BuilderFileOp();
      if (! x10BuilderOp.hasAllPrerequisites()) {
        CoreResourceUtils.addBuildMarkerTo(getProject(), NLS.bind(Messages.AXB_IncompleteConfMsg, getProject().getName()), 
                                           IMarker.SEVERITY_ERROR, IMarker.PRIORITY_HIGH);
        UIUtils.showProblemsView();
      }
      cleanFiles(CLEAN_BUILD, SubMonitor.convert(monitor), new HashSet<IFile>(), new HashSet<IFile>(), 
                 new HashSet<IFile>(), x10BuilderOp);
    }
  }
  
  protected void startupOnInitialize() {
    if (getProject().isAccessible()) {
      if (this.fProjectWrapper == null) {
        this.fProjectWrapper = JavaCore.create(getProject());
      }
      if (this.fDependencyInfo == null) {
        this.fDependencyInfo = new PolyglotDependencyInfo(getProject());
      }
    }
  }
  
  
  
  /**
   * Returns the main generated file for the X10 file provided, i.e. for C++ back-end this should return the C++ file
   * corresponding to the X10 file in question.
   * 
   * @param project The project containing the X10 file in question.
   * @param x10File The x10 file to consider.
   * @return A non-null file if we found one, otherwise <b>null</b>.
   * @throws CoreException Occurs if we could not access some project information or get the local file for the location
   * identified.
   */
	public File getMainGeneratedFile(final IJavaProject project, final IFile x10File) throws CoreException {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (final IClasspathEntry cpEntry : project.getRawClasspath()) {
			if (cpEntry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				final IPath outputLocation;
				if (cpEntry.getOutputLocation() == null) {
					outputLocation = project.getOutputLocation();
				} else {
					outputLocation = cpEntry.getOutputLocation();
				}
				final StringBuilder sb = new StringBuilder();
				sb.append(File.separatorChar).append(x10File.getProjectRelativePath().removeFileExtension().toString()).append(getFileExtension());
				final IPath projectRelativeFilePath = new Path(sb.toString());
				final int srcPathCount = cpEntry.getPath().removeFirstSegments(1).segmentCount();
				final IPath generatedFilePath = outputLocation.append(projectRelativeFilePath.removeFirstSegments(srcPathCount));
				final IFileStore fileStore = EFS.getLocalFileSystem().getStore(root.getFile(generatedFilePath).getLocationURI());
				if (fileStore.fetchInfo().exists()) {
					return fileStore.toLocalFile(EFS.NONE, null);
				}
			}
		}
		return null;
	}
  
   // --- Private code
	
  private void clearMarkers(final Collection<IFile> sourcesToCompile) {
    for (final IFile file : sourcesToCompile) {
      CoreResourceUtils.deleteBuildMarkers(file);
    }
    CoreResourceUtils.deleteBuildMarkers(getProject());
  }
  
  private void collectSourceFilesToCompile(final Collection<IFile> sourcesToCompile, final Collection<IFile> nativeFiles,
                                           final Collection<IFile> deletedSources, final IJavaProject javaProject, 
                                           final Set<IProject> dependentProjects, final IFilter<IFile> nativeFilesFilter, 
                                           final boolean fullBuild, final SubMonitor monitor) throws CoreException {
    try {
      monitor.beginTask(Messages.CPPB_CollectingSourcesTaskName, 1);
      
      final IProject project = javaProject.getProject();
      
      final IResourceDelta resourceDelta = getDelta(project);
      if (resourceDelta != null) {
        resourceDelta.accept(new IResourceDeltaVisitor() {
          
          public boolean visit(final IResourceDelta delta) throws CoreException {
        	if (delta.getResource().getType() == IResource.FOLDER){
        		final IFolder folder = (IFolder) delta.getResource();
        		sourcesToCompile.addAll(getChangeDependents(folder));
        	}
            if (delta.getResource().getType() == IResource.FILE) {
              final IFile file = (IFile) delta.getResource();
              if (isX10File(file)) {
                if (delta.getKind() == IResourceDelta.REMOVED) {
                  deletedSources.add(file);
                  sourcesToCompile.addAll(getChangeDependents(file));
                } else if (delta.getKind() == IResourceDelta.ADDED) {
                  sourcesToCompile.add(file);
                } else if (delta.getKind() == IResourceDelta.CHANGED) {
                  sourcesToCompile.add(file);
                  sourcesToCompile.addAll(getChangeDependents(file));
                }
              }
            }
            return true;
          }
          
        });
      }
      
      final IPreferencesService prefService = X10DTCorePlugin.getInstance().getPreferencesService();
      final boolean conservativeBuild = prefService.getBooleanPreference(X10Constants.P_CONSERVATIVEBUILD);
      
      final IResourceVisitor visitor = new IResourceVisitor() {
        
        // --- Interface methods implementation
        
        public boolean visit(final IResource resource) throws CoreException {
          if ((resource.getType() == IResource.FILE) && ! resource.isDerived()) {
            final IFile file = (IFile) resource;
            if (isX10File(file)) {
              final File generatedFile = getMainGeneratedFile(AbstractX10Builder.this.fProjectWrapper, file);
              if (fullBuild || (generatedFile == null)) {
                sourcesToCompile.add(file);
                
                if (! resource.getProject().equals(project)) {
                  dependentProjects.add(resource.getProject());
                }
              }
            } else if (nativeFilesFilter.accepts(file)) {
              nativeFiles.add(file);
            }
          }
          return true;
        }
        
      };
      
      if (fullBuild || conservativeBuild) {
        final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        for (final IClasspathEntry cpEntry : javaProject.getRawClasspath()) {
          if (cpEntry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
            root.getFolder(cpEntry.getPath()).accept(visitor);
          }
        }
      }
    } finally {
      monitor.done();
    }
  }
  
  private void compileGeneratedFiles(final IX10BuilderFileOp builderOp, final Collection<IFile> sourcesToCompile,
                                     final SubMonitor monitor) throws CoreException {
    monitor.beginTask(null, 100);

    final List<File> files = CollectionUtils.transform(sourcesToCompile, new IFileToFileFunctor());
    if (! files.isEmpty()) {
      builderOp.transfer(files, monitor.newChild(10));
      if (builderOp.compile(monitor.newChild(70))) {
        builderOp.archive(monitor.newChild(20));
      }
    }
  }
  
  private void compileX10Files(final String localOutputDir, final Collection<IFile> sourcesToCompile,
                               final IProgressMonitor monitor) throws CoreException {
    final Set<String> cps = ProjectUtils.getFilteredCpEntries(this.fProjectWrapper, new CpEntryAsStringFunc(), 
                                                              new AlwaysTrueFilter<IPath>());
    final StringBuilder cpBuilder = new StringBuilder();
    int i = -1;
    for (final String cpEntry : cps) {
      if (++i > 0) {
        cpBuilder.append(File.pathSeparatorChar);
      }
      cpBuilder.append(cpEntry);
    }
    
    final Set<IPath> srcPaths = ProjectUtils.getFilteredCpEntries(this.fProjectWrapper, new IdentityFunctor<IPath>(),
                                                                  new RuntimeFilter());
    final List<File> sourcePath = CollectionUtils.transform(srcPaths, new IPathToFileFunc());
    
    final ExtensionInfo extInfo = createExtensionInfo(cpBuilder.toString(), sourcePath, localOutputDir, 
                                                      false /* withMainMethod */, monitor);
    
    final Compiler compiler = new Compiler(extInfo, new X10ErrorQueue(1000000, extInfo.compilerName()));
    Globals.initialize(compiler);
    try {
      compiler.compile(toSources(sourcesToCompile));
      computeDependencies(extInfo.scheduler().commandLineJobs());
    } catch (InternalCompilerError except) {
      // The exception is also pushed on the error queue... A marker will be created accordingly for it.
      sourcesToCompile.clear(); // To prevent post-compilation step.
    } finally {
      Globals.initialize(null);
    }
  }
  
  private void computeDependencies(final Collection<Job> jobs){
    for (final Job job: jobs){
      final ComputeDependenciesVisitor visitor = new ComputeDependenciesVisitor(fProjectWrapper, job, job.extensionInfo().typeSystem(), 
                                                                                this.fDependencyInfo);
      if (job.ast() != null) {
        job.ast().visit(visitor.begin());
      }
    }
  }
  
  private Collection<IFile> getChangeDependents(final IResource srcFile) {
    final Collection<IFile> result = new ArrayList<IFile>();
    final Set<String> fileDependents = this.fDependencyInfo.getDependentsOf(srcFile.getFullPath().toString());
    final IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
    if (fileDependents != null) {
      for (final String dependent : fileDependents) {
        result.add(wsRoot.getFile(new Path(dependent)));
      }
    }
    return result;
  } 
  
  private boolean isX10File(final IFile file) {
    return Constants.X10_EXT.equals('.' + file.getFileExtension());
  }
  
  private Collection<Source> toSources(final Collection<IFile> sources) throws CoreException {
    final Collection<Source> pSources = new ArrayList<Source>(sources.size());
    for (final IFile file : sources) {
      try {
        pSources.add(new StreamSource(file.getContents(), file.getLocation().toOSString()));
      } catch (IOException except) {
        throw new CoreException(new Status(IStatus.ERROR, LaunchCore.getInstance().getBundle().getSymbolicName(), 
                                           NLS.bind(Messages.CPPB_FileReadingErrorMessage, file), except));
      }
    }
    return pSources;
  }
  
  // --- Private classes
  
  private final class IFileToFileFunctor implements IFunctor<IFile, File> {

    // --- Interface methods implementation
    
    public File apply(final IFile file) {
      try {
        return getMainGeneratedFile(AbstractX10Builder.this.fProjectWrapper, file);
      } catch (CoreException except) {
        LaunchCore.log(except.getStatus());
        return null;
      }
    }
    
  }
  
  // --- Fields
  
  private PolyglotDependencyInfo fDependencyInfo;
  
  private IJavaProject fProjectWrapper;
  
}
