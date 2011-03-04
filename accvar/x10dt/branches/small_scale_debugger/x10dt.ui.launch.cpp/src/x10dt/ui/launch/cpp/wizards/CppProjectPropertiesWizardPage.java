/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package x10dt.ui.launch.cpp.wizards;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.smapifier.builder.SmapiProjectNature;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import x10dt.ui.launch.core.LaunchCore;
import x10dt.ui.launch.cpp.CppLaunchCore;
import x10dt.ui.launch.cpp.LaunchMessages;
import x10dt.ui.utils.WizardUtils;


final class CppProjectPropertiesWizardPage extends JavaCapabilityConfigurationPage {

  CppProjectPropertiesWizardPage(final CppProjectNameDefWizardPage firstPage) {
    this.fFirstPage = firstPage;

    setTitle(LaunchMessages.PWSP_PageTitle);
    setDescription(LaunchMessages.PWSP_PageDescription);
  }

  // --- Overridden methods

  public void setVisible(boolean visible) {
    boolean isShownFirstTime = visible && this.fCurrProject == null;
    try {
      if (visible) {
        if (isShownFirstTime) {
          createProvisionalProject();
        }
      } else {
        if (this.fFirstPage == getContainer().getCurrentPage()) {
          removeProvisionalProject();
        }
      }
    } catch (CoreException except) {
      // Do nothing, just update status.
    }
    super.setVisible(visible);
    if (isShownFirstTime) {
      setFocus();
    }
  }

  // --- Internal services

  IFile getCreatedFile() {
    return this.fCreatedFile;
  }

  void performCancel() throws CoreException {
    if (this.fCurrProject != null) {
      removeProvisionalProject();
    }
  }

  @SuppressWarnings("unchecked")
  void performFinish(final IProgressMonitor monitor) throws CoreException {
    try {
      monitor.beginTask(LaunchMessages.PWSP_FinishTaskName, 4);
      if (this.fCurrProject == null) {
        updateProject(new SubProgressMonitor(monitor, 2));
      }

      final String compliance = this.fFirstPage.getCompilerCompliance();
      if (compliance != null) {
        final Map<String, String> options = getJavaProject().getOptions(false);
        JavaCore.setComplianceOptions(compliance, options);
        options.put(JavaCore.COMPILER_CODEGEN_INLINE_JSR_BYTECODE, JavaCore.ENABLED);
        options.put(JavaCore.COMPILER_LOCAL_VARIABLE_ATTR, JavaCore.GENERATE);
        options.put(JavaCore.COMPILER_LINE_NUMBER_ATTR, JavaCore.GENERATE);
        options.put(JavaCore.COMPILER_SOURCE_FILE_ATTR, JavaCore.GENERATE);
        options.put(JavaCore.COMPILER_CODEGEN_UNUSED_LOCAL, JavaCore.PRESERVE);
        getJavaProject().setOptions(options);
      }

      if (this.fFirstPage.shouldGenerateHelloWorldProgram()) {
        ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
          public void run(final IProgressMonitor mtr) throws CoreException {
            createSampleCode(mtr);
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
      final IProjectDescription description = this.fCurrProject.getDescription();
      final String[] natureIds = new String[] { 
        LaunchCore.X10_CPP_PRJ_NATURE_ID, SmapiProjectNature.k_natureID, JavaCore.NATURE_ID
      };
      description.setNatureIds(natureIds);
      this.fCurrProject.setDescription(description, monitor);

      // We need to remove Java builder in the case of X10 CPP Back-end.
      final IProjectDescription newDescr = this.fCurrProject.getDescription();
      final ICommand[] commands = newDescr.getBuildSpec();
      final ICommand[] newCommands = new ICommand[commands.length - 1];
      for (int i = 0; i < commands.length; ++i) {
        if (JavaCore.BUILDER_ID.equals(commands[i].getBuilderName())) {
          System.arraycopy(commands, 0, newCommands, 0, i);
          System.arraycopy(commands, i + 1, newCommands, i, commands.length - i - 1);
          break;
        }
      }
      newDescr.setBuildSpec(newCommands);
      this.fCurrProject.setDescription(newDescr, monitor);

      getJavaProject().setRawClasspath(getRawClassPath(), getOutputLocation(), monitor);
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

  private IProject createProvisionalProject() throws CoreException {
    updateProject(new NullProgressMonitor());
    return this.fCurrProject;
  }

  private void createSampleCode(final IProgressMonitor monitor) throws CoreException {
    this.fCreatedFile = this.fCurrProject.getFile("src/Hello.x10"); //$NON-NLS-1$
    final IFolder srcFolder = this.fCurrProject.getFolder("src"); //$NON-NLS-1$
    final IJavaProject javaProject = getJavaProject();
    final IPackageFragmentRoot pkgFragRoot = javaProject.getPackageFragmentRoot(srcFolder);
    final IPackageFragment pkgFrag = pkgFragRoot.getPackageFragment(""); //$NON-NLS-1$

    this.fCreatedFile.create(WizardUtils.createSampleContentStream(pkgFrag.getElementName(), "Hello"), true /* force */, //$NON-NLS-1$
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
    monitor.beginTask(LaunchMessages.PWSP_BuildPathInitTaskName, 2);

    try {
      final IProject project = sourceProject.getRawProject();

      final List<IClasspathEntry> cpEntries = new ArrayList<IClasspathEntry>();

      final IWorkspaceRoot root = project.getWorkspace().getRoot();
      final IClasspathEntry[] sourceClasspathEntries = this.fFirstPage.getSourceClasspathEntries();
      for (int i = 0; i < sourceClasspathEntries.length; i++) {
        final IPath path = sourceClasspathEntries[i].getPath();
        if (path.segmentCount() > 1) {
          createFolder(root.getFolder(path), IResource.FORCE, new SubProgressMonitor(monitor, 1));
        }
        cpEntries.add(sourceClasspathEntries[i]);
      }

      final IClasspathEntry[] defaultCPEntries = this.fFirstPage.getDefaultClasspathEntries();
      if (defaultCPEntries.length == 0) {
        final IStatus status = new Status(IStatus.ERROR, CppLaunchCore.PLUGIN_ID, LaunchMessages.PWSP_CPEntriesError);
        updateStatus(status);
        throw new CoreException(status);
      }
      cpEntries.addAll(Arrays.asList(defaultCPEntries));

      final IClasspathEntry[] entries = cpEntries.toArray(new IClasspathEntry[cpEntries.size()]);

      final IPath outputLocation = this.fFirstPage.getOutputLocation();
      if (outputLocation.segmentCount() > 1) {
        createFolder(root.getFolder(outputLocation), IResource.FORCE | IResource.DERIVED, new SubProgressMonitor(monitor, 1));
      }

      init(JavaCore.create(project), outputLocation, entries, false /* overridingExistingClassPath */);
    } finally {
      monitor.done();
    }
  }
  
  private void openSourceFile(final IFile srcFile) {
    Display.getDefault().asyncExec(new Runnable() {
      public void run() {
        final IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        try {
          IDE.openEditor(activePage, srcFile, true);
        } catch (PartInitException except) {
          final IStatus status = except.getStatus();
          CppLaunchCore.log(status.getSeverity(), NLS.bind(LaunchMessages.PWSP_EditorOpeningError, srcFile), 
                            status.getException());
        }
      }
    });
  }

  private void removeProvisionalProject() throws CoreException {
    if (this.fCurrProject.exists()) {
      this.fCurrProject.delete(true /* removeContent */, false /* force */, new NullProgressMonitor());
    }

    this.fCurrProject = null;
  }

  private final void updateProject(final IProgressMonitor monitor) throws CoreException {
    try {
      monitor.beginTask(LaunchMessages.PWSP_UpdateProjectTaskName, 7);
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
    } catch (ModelException except) {
      throw new CoreException(new Status(IStatus.ERROR, CppLaunchCore.PLUGIN_ID, LaunchMessages.PWSP_SourceProjectError, except));
    } finally {
      monitor.done();
    }
  }

  // --- Fields

  private final CppProjectNameDefWizardPage fFirstPage;

  private IFile fCreatedFile;

  private URI fCurrProjectLocation;

  private IProject fCurrProject;

  private static final String FILENAME_PROJECT = ".project"; //$NON-NLS-1$

}
