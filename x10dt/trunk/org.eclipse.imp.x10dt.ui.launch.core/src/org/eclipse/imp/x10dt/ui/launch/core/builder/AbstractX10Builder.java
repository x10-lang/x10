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
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.imp.builder.DependencyInfo;
import org.eclipse.imp.x10dt.core.builder.PolyglotDependencyInfo;
import org.eclipse.imp.x10dt.ui.launch.core.LaunchCore;
import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.imp.x10dt.ui.launch.core.builder.target_op.IX10BuilderFileOp;
import org.eclipse.imp.x10dt.ui.launch.core.utils.AlwaysTrueFilter;
import org.eclipse.imp.x10dt.ui.launch.core.utils.CollectionUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IResourceUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IdentityFunctor;
import org.eclipse.imp.x10dt.ui.launch.core.utils.JavaProjectUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.UIUtils;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.osgi.util.NLS;

import polyglot.frontend.Compiler;
import polyglot.frontend.FileSource;
import polyglot.frontend.Globals;
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
   * Clears with the help of source files provided the related generated and compiled files on the target machine.
   * 
   * @param builderFileOp The helper class for file operations.
   * @param x10SourceFiles The X10 source files to use.
   * @param monitor The monitor to use for reporting progress and/or cancel the operation.
   * @throws CoreException Occurs if we could not delete some particular resource.
   */
  public abstract void clearGeneratedAndCompiledFiles(final IX10BuilderFileOp builderFileOp,
                                                      final Collection<IFile> x10SourceFiles, 
                                                      final SubMonitor monitor) throws CoreException;
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
   * Creates the instance of {@link IX10BuilderFileOp} depending of the connection type, local or remote.
   * 
   * @return A non-null object.
   * @throws CoreException Occurs if we could not load the X10 platform configuration associated with the project.
   */
  public abstract IX10BuilderFileOp createX10BuilderFileOp() throws CoreException;
  
  // --- Abstract methods implementation
  
  @SuppressWarnings("unchecked")
  protected final IProject[] build(final int kind, final Map args, final IProgressMonitor monitor) throws CoreException {
    try {
      if (this.fProjectWrapper == null) {
        return new IProject[0];
      }
      this.fDependencyInfo.clearAllDependencies();
      this.fSourcesToCompile.clear();

      final SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
      
      final Set<IProject> dependentProjects = new HashSet<IProject>();
      collectSourceFilesToCompile(dependentProjects, subMonitor.newChild(5));
      
      clearMarkers(kind);
      
      if (this.fX10BuilderFileOp == null) {
      	this.fX10BuilderFileOp = createX10BuilderFileOp();
      	if (! this.fX10BuilderFileOp.hasAllPrerequisites()) {
          IResourceUtils.addBuildMarkerTo(getProject(), NLS.bind(Messages.AXB_IncompleteConfMsg, getProject().getName()), 
                                          IMarker.SEVERITY_ERROR, IMarker.PRIORITY_HIGH);
          UIUtils.showProblemsView();
          return dependentProjects.toArray(new IProject[dependentProjects.size()]);
        }
      }
      
      clearGeneratedAndCompiledFiles(this.fX10BuilderFileOp, this.fSourcesToCompile, subMonitor);
            
      final String localOutputDir = JavaProjectUtils.getProjectOutputDirPath(getProject());
      compileX10Files(localOutputDir, subMonitor.newChild(30));
      
      compileGeneratedFiles(this.fX10BuilderFileOp, localOutputDir, subMonitor.newChild(65));
      
      return dependentProjects.toArray(new IProject[dependentProjects.size()]);
    } finally {
    	this.fX10BuilderFileOp = null;
      monitor.done();
    }
  }
  
  // --- Overridden methods

  protected void clean(final IProgressMonitor monitor) throws CoreException {
    monitor.subTask(Messages.CPPB_CleanTaskName);
    final SubMonitor subMonitor = SubMonitor.convert(monitor, 2);
    try {
      if (getProject().isAccessible()) {
        if (this.fProjectWrapper == null) {
          this.fProjectWrapper = JavaCore.create(getProject());
        }
        if (this.fX10BuilderFileOp == null) {
        	this.fX10BuilderFileOp = createX10BuilderFileOp();
        	if (! this.fX10BuilderFileOp.hasAllPrerequisites()) {
            IResourceUtils.addBuildMarkerTo(getProject(), NLS.bind(Messages.AXB_IncompleteConfMsg, getProject().getName()), 
                                            IMarker.SEVERITY_ERROR, getProject().getLocation().toString(), 
                                            IMarker.PRIORITY_HIGH);
            UIUtils.showProblemsView();
            return;
          }
        }
        // No need to persist dependent projects here since it will be repeated later on.
        collectSourceFilesToCompile(new HashSet<IProject>(), subMonitor.newChild(1));
        clearGeneratedAndCompiledFiles(this.fX10BuilderFileOp, this.fSourcesToCompile, subMonitor.newChild(1));
      }
    } finally {
      monitor.done();
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
  
  // --- Private code
  
  private void clearMarkers(final int kind) {
    for (final IFile file : this.fSourcesToCompile) {
      IResourceUtils.deleteBuildMarkers(file);
    }
    IResourceUtils.deleteBuildMarkers(getProject());
  }
  
  private void collectSourceFilesToCompile(final Set<IProject> dependentProjects,
                                           final SubMonitor monitor) throws CoreException {
    try {
      monitor.beginTask(Messages.CPPB_CollectingSourcesTaskName, 1);
      getProject().accept(new SourceFilesCollector(this.fSourcesToCompile, this.fDependencyInfo,
                                                   getProject(), dependentProjects));
    } finally {
      monitor.done();
    }
  }
  
  private void compileGeneratedFiles(final IX10BuilderFileOp builderOp, final String localOutputDir,
                                           final SubMonitor monitor) throws CoreException {
    monitor.beginTask(null, 100);

    builderOp.transfer(localOutputDir, monitor.newChild(10));
    if (builderOp.compile(monitor.newChild(70))) {
      builderOp.archive(monitor.newChild(20));
    }
  }
  
  private void compileX10Files(final String localOutputDir, final IProgressMonitor monitor) throws CoreException {
    final Set<String> cps = JavaProjectUtils.getFilteredCpEntries(this.fProjectWrapper, new CpEntryAsStringFunc(), 
                                                                  new AlwaysTrueFilter<IPath>());
    final StringBuilder cpBuilder = new StringBuilder();
    int i = -1;
    for (final String cpEntry : cps) {
      if (++i > 0) {
        cpBuilder.append(File.pathSeparatorChar);
      }
      cpBuilder.append(cpEntry);
    }
    
    final Set<IPath> srcPaths = JavaProjectUtils.getFilteredCpEntries(this.fProjectWrapper, new IdentityFunctor<IPath>(),
                                                                      new RuntimeFilter());
    final List<File> sourcePath = CollectionUtils.transform(srcPaths, new IPathToFileFunc());
    
    final ExtensionInfo extInfo = createExtensionInfo(cpBuilder.toString(), sourcePath, localOutputDir, 
                                                      false /* withMainMethod */, monitor);
    
    final Compiler compiler = new Compiler(extInfo, new X10ErrorQueue(1000000, getProject(), extInfo.compilerName()));
    Globals.initialize(compiler);
    
    compiler.compile(toSources(this.fSourcesToCompile));
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
  
  // --- Fields
  
  private DependencyInfo fDependencyInfo;
  
  private IJavaProject fProjectWrapper;
  
  private IX10BuilderFileOp fX10BuilderFileOp;
  
  private Collection<IFile> fSourcesToCompile = new HashSet<IFile>();
  
}
