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

import org.eclipse.core.resources.IFile;
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
import org.eclipse.imp.x10dt.core.builder.ComputeDependenciesVisitor;
import org.eclipse.imp.x10dt.core.builder.PolyglotDependencyInfo;
import org.eclipse.imp.x10dt.core.preferences.generated.X10Constants;
import org.eclipse.imp.x10dt.core.utils.JavaModelFileResource;
import org.eclipse.imp.x10dt.ui.launch.core.Constants;
import org.eclipse.imp.x10dt.ui.launch.core.LaunchCore;
import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.imp.x10dt.ui.launch.core.builder.target_op.IX10BuilderFileOp;
import org.eclipse.imp.x10dt.ui.launch.core.utils.AlwaysTrueFilter;
import org.eclipse.imp.x10dt.ui.launch.core.utils.CollectionUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.CountableIterableFactory;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IFilter;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IFunctor;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IResourceUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IdentityFunctor;
import org.eclipse.imp.x10dt.ui.launch.core.utils.ProjectUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.UIUtils;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.osgi.util.NLS;

import polyglot.frontend.Compiler;
import polyglot.frontend.FileSource;
import polyglot.frontend.Globals;
import polyglot.frontend.Job;
import polyglot.frontend.Source;
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
   * Returns the main generated file for the X10 file provided, i.e. for C++ back-end this should return the C++ file
   * corresponding to the X10 file in question.
   * 
   * @param project The project containing the X10 file in question.
   * @param x10File The x10 file to consider.
   * @return A non-null file if we found one, otherwise <b>null</b>.
   * @throws CoreException Occurs if we could not access some project information or get the local file for the location
   * identified.
   */
  public abstract File getMainGeneratedFile(final IJavaProject project, final IFile x10File) throws CoreException;
  
  // --- Abstract methods implementation
  
  @SuppressWarnings("rawtypes")
  protected final IProject[] build(final int kind, final Map args, final IProgressMonitor monitor) throws CoreException {
    try {
      if (this.fProjectWrapper == null) {
        return new IProject[0];
      }
      final boolean shouldBuildAll;
      if (kind == CLEAN_BUILD || kind == FULL_BUILD) {
        this.fDependencyInfo.clearAllDependencies();
        shouldBuildAll = true;
      } else {
        shouldBuildAll = this.fDependencyInfo.getDependencies().isEmpty();
      }
      final Collection<IFile> sourcesToCompile = new HashSet<IFile>();
      final Collection<IFile> deletedSources = new HashSet<IFile>();
      final Collection<IFile> nativeFiles = new HashSet<IFile>();

      final SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
      
      final Set<IProject> dependentProjects = new HashSet<IProject>();
      collectSourceFilesToCompile(sourcesToCompile, nativeFiles, deletedSources, this.fProjectWrapper, dependentProjects,
                                  createNativeFilesFilter(), shouldBuildAll, subMonitor.newChild(10));
      
      clearMarkers(sourcesToCompile);
      
      if (this.fX10BuilderFileOp == null) {
      	this.fX10BuilderFileOp = createX10BuilderFileOp();
      	if (! this.fX10BuilderFileOp.hasAllPrerequisites()) {
          IResourceUtils.addBuildMarkerTo(getProject(), NLS.bind(Messages.AXB_IncompleteConfMsg, getProject().getName()), 
                                          IMarker.SEVERITY_ERROR, IMarker.PRIORITY_HIGH);
          UIUtils.showProblemsView();
          return dependentProjects.toArray(new IProject[dependentProjects.size()]);
        }
      }
      
      this.fX10BuilderFileOp.cleanFiles(CountableIterableFactory.create(sourcesToCompile, nativeFiles, deletedSources),
                                        subMonitor);

      final String localOutputDir = ProjectUtils.getProjectOutputDirPath(getProject());
      this.fX10BuilderFileOp.copyToOutputDir(nativeFiles, subMonitor.newChild(5));
      compileX10Files(localOutputDir, sourcesToCompile, subMonitor.newChild(20));
      
      compileGeneratedFiles(this.fX10BuilderFileOp, sourcesToCompile, subMonitor.newChild(65));
      
      return dependentProjects.toArray(new IProject[dependentProjects.size()]);
    } finally {
    	this.fX10BuilderFileOp = null;
      monitor.done();
    }
  }
  
  // --- Overridden methods
  
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
  
  // --- Private code
  
  private void clearMarkers(final Collection<IFile> sourcesToCompile) {
    for (final IFile file : sourcesToCompile) {
      IResourceUtils.deleteBuildMarkers(file);
    }
    IResourceUtils.deleteBuildMarkers(getProject());
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

    builderOp.transfer(CollectionUtils.transform(sourcesToCompile, new IFileToFileFunctor()), monitor.newChild(10));
    if (builderOp.compile(monitor.newChild(70))) {
      builderOp.archive(monitor.newChild(20));
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
    
    final Compiler compiler = new Compiler(extInfo, new X10ErrorQueue(1000000, getProject(), extInfo.compilerName()));
    Globals.initialize(compiler);
    
    compiler.compile(toSources(sourcesToCompile));
    computeDependencies(extInfo.scheduler().commandLineJobs());
  }
  
  private void computeDependencies(final Collection<Job> jobs){
    for (final Job job: jobs){
      final ComputeDependenciesVisitor visitor = new ComputeDependenciesVisitor(job, job.extensionInfo().typeSystem(), 
                                                                                this.fDependencyInfo);
      if (job.ast() != null) {
        job.ast().visit(visitor.begin());
      }
    }
  }
  
  private Collection<IFile> getChangeDependents(final IFile srcFile) {
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
        pSources.add(new FileSource(new JavaModelFileResource(file)));
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
  
  private IX10BuilderFileOp fX10BuilderFileOp;
  
}
