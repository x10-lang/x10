/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package x10dt.ui.launch.core.wizards;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.imp.builder.ProjectNatureBase;
import org.eclipse.imp.editor.EditorUtility;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.IPathEntry;
import org.eclipse.imp.model.IPathEntry.PathEntryType;
import org.eclipse.imp.model.ISourceFolder;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ISourceRoot;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.ui.wizards.CapabilityConfigurationPage;
import org.eclipse.imp.ui.wizards.NewProjectWizardPageOne;
import org.eclipse.imp.utils.BuildPathUtils;
import org.eclipse.imp.utils.LoggingUtils;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PartInitException;

import x10dt.core.utils.X10DTCoreConstants;
import x10dt.ui.launch.core.LaunchCore;
import x10dt.ui.launch.core.Messages;
import x10dt.ui.launch.core.utils.WizardUtils;


public class X10ProjectPropertiesWizardPage extends CapabilityConfigurationPage {

  public X10ProjectPropertiesWizardPage(final NewProjectWizardPageOne firstPage) {
	super(LanguageRegistry.findLanguage("X10"));
	setTitle(Messages.PWSP_PageTitle);
	setDescription(Messages.PWSP_PageDescription);
	this.fFirstPage = (X10ProjectNameDefWizardPage)firstPage;
  }

  protected String[] getNatureIds()
  {
	  return new String[0];
  }
  
  protected ProjectNatureBase getProjectNature()
  {
	  return null;
  }
  
  protected List<IPathEntry> getBuildPath()
  {
	  List<IPathEntry> x10Entries = new ArrayList<IPathEntry>();
      x10Entries.add(ModelFactory.createSourceEntry(fCurrProject.getFullPath().append("src"), null));
	  x10Entries.add(ModelFactory.createContainerEntry(new Path(X10DTCoreConstants.X10_CONTAINER_ENTRY_ID)));
      return x10Entries;
  }
  
  // --- Overridden methods 
  
  /**
   * The purpose of this override is to add an extra check on the classpath,
   * to make sure that it has an explicit source folder entry. Unfortunately,
   * there does not appear to be enough API exposed on the base class to implement
   * this check in the normal manner.
   */
  protected void updateStatus(IStatus status) {
    if (status.isOK()) {
      if (!checkClasspath()) {
        super.updateStatus(new Status(IStatus.ERROR, LaunchCore.PLUGIN_ID, Messages.PWSP_noSourceEntry));
        return;
      }
    }
    super.updateStatus(status);
  }

  private boolean checkClasspath() {
    IPathEntry[] cpEntries= getRawBuildPath();
    for(IPathEntry entry: cpEntries) {
        if (entry.getEntryType() == PathEntryType.SOURCE_FOLDER) {
            return true;
        }
    }
    return false;
  }

  // --- Internal services

  public void performFinish(final IProgressMonitor monitor) throws CoreException {
    try {
      monitor.beginTask(Messages.PWSP_FinishTaskName, 4);
      if (this.fCurrProject == null) {
        updateProject(new SubProgressMonitor(monitor, 2));
      }

      if (this.fFirstPage.shouldGenerateHelloWorldProgram()) {
        ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
          public void run(final IProgressMonitor mtr) throws CoreException {
            try {
				createSampleCode(mtr);
			} catch (ModelException e) {
				throw ModelFactory.createCoreException(e);
			}
          }
        }, new SubProgressMonitor(monitor, 2));
      } else {
        monitor.worked(2);
      }
    } finally {
      this.fCurrProject = null;
      monitor.done();
    }
  }

  // --- Private code

  private void configureX10Project(final IProgressMonitor monitor) throws CoreException {
    try {
//      final IProjectDescription description = this.fCurrProject.getDescription();
//      final String[] natureIds = getNatureIds();
//      description.setNatureIds(natureIds);
//      this.fCurrProject.setDescription(description, monitor);
      
      getJavaProject().setBuildPath(LanguageRegistry.findLanguage("X10"), getBuildPath(), getOutputLocation(), monitor);
    } finally {
      monitor.done();
    }
  }

  private void createFolder(final IFolder folder, final int updateFlags, final IProgressMonitor monitor) throws CoreException {
    if (!folder.exists()) {
      final IContainer parent = folder.getParent();
      if (parent instanceof IFolder) {
        createFolder((IFolder) parent, updateFlags, monitor);
      }
      folder.create(updateFlags, true /* local */, monitor);
    }
  }

  private void createSampleCode(final IProgressMonitor monitor) throws CoreException, ModelException {
    this.fCreatedFile = this.fCurrProject.getFile("src/Hello.x10"); //$NON-NLS-1$
    final IFolder srcFolder = this.fCurrProject.getFolder("src"); //$NON-NLS-1$
    final ISourceProject javaProject = getJavaProject();
    final ISourceRoot pkgFragRoot = BuildPathUtils.getSourceRoot(javaProject, srcFolder);
    final ISourceFolder pkgFrag = pkgFragRoot.createSourceFolder("", true, monitor); //$NON-NLS-1$

    this.fCreatedFile.create(WizardUtils.createSampleContentStream(pkgFrag.getName(), "Hello"), true /* force */, //$NON-NLS-1$
                             monitor);

    openSourceFile(this.fCreatedFile);
  }

  private void deleteProjectFile(final URI projectLocation) throws CoreException {
    final IFileStore file = EFS.getStore(projectLocation);
    if (file.fetchInfo().exists()) {
      final IFileStore projectFile = file.getFileStore(new Path(FILENAME_PROJECT));
      if (projectFile.fetchInfo().exists()) {
        projectFile.delete(EFS.NONE, null /* monitor */);
      }
    }
  }

  private void initializeBuildPath(final ISourceProject sourceProject, final IProgressMonitor monitor) throws CoreException {
    monitor.beginTask(Messages.PWSP_BuildPathInitTaskName, 2);

    try {
      final IProject project = sourceProject.getRawProject();

      final List<IPathEntry> cpEntries = new ArrayList<IPathEntry>();

      final IWorkspaceRoot root = project.getWorkspace().getRoot();
      final IPathEntry[] sourceClasspathEntries = this.fFirstPage.getSourceBuildPathEntries();
      for (int i = 0; i < sourceClasspathEntries.length; i++) {
        final IPath path = sourceClasspathEntries[i].getRawPath();
        if (path.segmentCount() > 1) {
          createFolder(root.getFolder(path), IResource.FORCE, new SubProgressMonitor(monitor, 1));
        }
        cpEntries.add(sourceClasspathEntries[i]);
      }

      final IPathEntry[] defaultCPEntries = this.fFirstPage.getDefaultClasspathEntries();
      if (defaultCPEntries.length == 0) {
        final IStatus status = new Status(IStatus.ERROR, LaunchCore.PLUGIN_ID, Messages.PWSP_CPEntriesError);
        updateStatus(status);
        throw new CoreException(status);
      }
      cpEntries.addAll(Arrays.asList(defaultCPEntries));

      final IPathEntry[] entries = cpEntries.toArray(new IPathEntry[cpEntries.size()]);

      final IPath outputLocation = this.fFirstPage.getOutputLocation();
      if (outputLocation.segmentCount() > 1) {
        createFolder(root.getFolder(outputLocation), IResource.FORCE | IResource.DERIVED, new SubProgressMonitor(monitor, 1));
      }

      init(ModelFactory.getProject(project), outputLocation, entries, false /* overridingExistingClassPath */);
    } finally {
      monitor.done();
    }
  }
  
  private void openSourceFile(final IFile srcFile) {
    try {
      EditorUtility.openInEditor(srcFile);
    } catch (PartInitException except) {
      final IStatus status = except.getStatus();
      LoggingUtils.logErrorStatus(LaunchCore.PLUGIN_ID, NLS.bind(Messages.PWSP_EditorOpeningError, srcFile), 
                        status);
    }
  }

  private final void updateProject(final IProgressMonitor monitor) throws CoreException {
    try {
      monitor.beginTask(Messages.PWSP_UpdateProjectTaskName, 7);
      if (monitor.isCanceled()) {
        throw new OperationCanceledException();
      }

      final String projectName = this.fFirstPage.getProjectName();

      this.fCurrProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
      this.fCurrProjectLocation = this.fFirstPage.getProjectLocationURI();

      if (this.fCurrProjectLocation != null) {
        deleteProjectFile(this.fCurrProjectLocation);
      }

      createProject(this.fCurrProject, this.fCurrProjectLocation, new SubProgressMonitor(monitor, 2));

      if (monitor.isCanceled()) {
        throw new OperationCanceledException();
      }

      initializeBuildPath(ModelFactory.open(this.fCurrProject), new SubProgressMonitor(monitor, 2));
      configureX10Project(new SubProgressMonitor(monitor, 3));
      
      ProjectNatureBase base = getProjectNature();
      if(base != null)
      {
    	  base.addToProject(fCurrProject);
      }
      
    } catch (ModelException except) {
      throw new CoreException(new Status(IStatus.ERROR, LaunchCore.PLUGIN_ID, Messages.PWSP_SourceProjectError, except));
    } finally {
      monitor.done();
    }
  }
  

  // --- Fields

  private final X10ProjectNameDefWizardPage fFirstPage;

  private IFile fCreatedFile;

  private URI fCurrProjectLocation;

  private IProject fCurrProject;

  private static final String FILENAME_PROJECT = ".project"; //$NON-NLS-1$

}
