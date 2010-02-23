/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.launching;

import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_ARGUMENTS;
import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_CONSOLE;
import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_EXECUTABLE_PATH;
import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_PROJECT_NAME;
import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_WORK_DIRECTORY;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lpg.runtime.IMessageHandler;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.imp.utils.Pair;
import org.eclipse.imp.x10dt.ui.launch.core.Constants;
import org.eclipse.imp.x10dt.ui.launch.core.builder.CpEntryAsStringFunc;
import org.eclipse.imp.x10dt.ui.launch.core.builder.IPathToFileFunc;
import org.eclipse.imp.x10dt.ui.launch.core.builder.JavaModelFileResource;
import org.eclipse.imp.x10dt.ui.launch.core.builder.RuntimeFilter;
import org.eclipse.imp.x10dt.ui.launch.core.utils.AlwaysTrueFilter;
import org.eclipse.imp.x10dt.ui.launch.core.utils.CollectionUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.ErrorUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IdentityFunctor;
import org.eclipse.imp.x10dt.ui.launch.core.utils.JavaProjectUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.PTPUtils;
import org.eclipse.imp.x10dt.ui.launch.cpp.CppLaunchCore;
import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.imp.x10dt.ui.parser.ExtensionInfo;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.elementcontrols.IResourceManagerControl;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.launch.ui.LaunchConfigurationTab;
import org.eclipse.ptp.launch.ui.LaunchImages;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteFileManager;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.PTPRemoteCorePlugin;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.SelectionDialog;

import polyglot.ast.Node;
import polyglot.frontend.Compiler;
import polyglot.frontend.FileSource;
import polyglot.frontend.Globals;
import polyglot.frontend.Source;
import polyglot.types.ClassType;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;
import x10.X10CompilerOptions;
import x10.ast.X10MethodDecl;
import x10.types.X10ClassType;
import x10.types.X10TypeSystem;


final class CppApplicationTab extends LaunchConfigurationTab implements ILaunchConfigurationTab {

  // --- Interface methods implementation
  
  public void createControl(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NULL);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(1, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
    
    createProjectEditor(composite);
    createVerticalSpacer(composite, 2);
    createMainTypeEditor(composite);
    createVerticalSpacer(composite, 2);
    createProgramArgs(composite);
    createVerticalSpacer(composite, 2);
    createOutputToConsoleButton(composite);
    
    setControl(composite);
  }

  public String getName() {
    return LaunchMessages.CAT_TabName;
  }

  public void performApply(final ILaunchConfigurationWorkingCopy configuration) {
    final String projectName = this.fProjectText.getText().trim();
    if (projectName.length() > 0) {
      configuration.setAttribute(ATTR_PROJECT_NAME, projectName);
    
      final String mainTypeFilePath = this.fMainTypeText.getText().trim().replace('\\', '/');
      final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
      if (project.exists()) {
        configuration.setAttribute(Constants.ATTR_MAIN_TYPE_PATH, mainTypeFilePath);
        String workspaceDir = null;
        try {
          workspaceDir = JavaProjectUtils.getTargetWorkspaceDir(project);
        } catch (CoreException except) {
          final int slashIndex = mainTypeFilePath.lastIndexOf('/');
          if (slashIndex == -1) {
            workspaceDir = mainTypeFilePath.substring(0, slashIndex);
          }
        }
        if (workspaceDir != null) {
          configuration.setAttribute(ATTR_WORK_DIRECTORY, workspaceDir);
          configuration.setAttribute(ATTR_EXECUTABLE_PATH, getExecutablePath(mainTypeFilePath, workspaceDir, configuration));
        }
      }
    
      final String content = this.fPgrmArgsText.getText().trim();    
      configuration.setAttribute(ATTR_ARGUMENTS, (content.length() > 0) ? content : null);
    
      configuration.setAttribute(Constants.ATTR_SHOULD_LINK_APP, this.fShouldLink.getSelection());
      configuration.setAttribute(ATTR_CONSOLE, this.fToConsoleBt.getSelection());
    }
  }

  public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
    configuration.setAttribute(ATTR_PROJECT_NAME, (String) null);
    configuration.setAttribute(ATTR_EXECUTABLE_PATH, (String) null);
    configuration.setAttribute(ATTR_WORK_DIRECTORY, (String) null);
    configuration.setAttribute(ATTR_ARGUMENTS, (String) null);
    configuration.setAttribute(Constants.ATTR_MAIN_TYPE_PATH, (String) null);
    configuration.setAttribute(Constants.ATTR_SHOULD_LINK_APP, true);
    configuration.setAttribute(ATTR_CONSOLE, true);
  }
  
  // --- Overridden methods
  
  public Image getImage() {
    return LaunchImages.getImage(LaunchImages.IMG_MAIN_TAB);
  }
  
  public void initializeFrom(ILaunchConfiguration configuration) {
    super.initializeFrom(configuration);
    
    try {
      this.fProjectText.setText(configuration.getAttribute(ATTR_PROJECT_NAME, EMPTY_STRING));
      this.fMainTypeText.setText(configuration.getAttribute(Constants.ATTR_MAIN_TYPE_PATH, EMPTY_STRING));
      this.fPgrmArgsText.setText(configuration.getAttribute(ATTR_ARGUMENTS, EMPTY_STRING));
      this.fShouldLink.setSelection(configuration.getAttribute(Constants.ATTR_SHOULD_LINK_APP, true));
      this.fToConsoleBt.setSelection(configuration.getAttribute(ATTR_CONSOLE, true));
    } catch (CoreException except) {
      setErrorMessage(LaunchMessages.CAT_ReadConfigError);
      CppLaunchCore.log(except.getStatus());
    }
  }
  
  public boolean isValid(final ILaunchConfiguration configuration) {
    setErrorMessage(null);
    setMessage(null);
    
    final String projectName = this.fProjectText.getText().trim();
    if (projectName.length() == 0) {
      setErrorMessage(LaunchMessages.CAT_RequiredPrjName);
      return false;
    } else {
      final IWorkspace workspace = ResourcesPlugin.getWorkspace();
      final IStatus status = workspace.validateName(projectName, IResource.PROJECT);
      if (status.isOK()) {
        final IProject project = workspace.getRoot().getProject(projectName);
        if (! project.exists()) {
          setErrorMessage(NLS.bind(LaunchMessages.CAT_NoExistingProject, projectName));
          return false;
        }
        if (! project.isOpen()) {
          setErrorMessage(NLS.bind(LaunchMessages.CAT_ClosedProject, projectName));
          return false;
        }
        // Project exists and is not closed. Checks now if we have a correct main type file path.
        final String mainTypeFilePath = this.fMainTypeText.getText().trim().replace('\\', '/');
        if (mainTypeFilePath.length() == 0) {
          setErrorMessage(LaunchMessages.CAT_RequiredMainTypeName);
          return false;
        }
        final IResourceManager resourceManager = getResourceManager(configuration);
        if (resourceManager != null) {
          final Pair<IRemoteConnection, IRemoteFileManager> pair = PTPUtils.getConnectionAndFileManager(resourceManager);
          try {
            pair.second.setWorkingDirectory(JavaProjectUtils.getTargetWorkspaceDir(project));
          } catch (CoreException except) {
            // Simply forgets.
          }
          if (! pair.second.getResource(mainTypeFilePath).fetchInfo().exists()) {
            setErrorMessage(LaunchMessages.CAT_NotExistingPathError);
            return false;
          }
        }
      } else {
        setErrorMessage(NLS.bind(LaunchMessages.CAT_IllegalPrjName, projectName));
        return false;
      }
    }
    return true;
  }
  
  // --- Private code
  
  private void collectX10Types(final IProject project, final Collection<ClassType> x10Types,
                               final IProgressMonitor monitor) throws CoreException {
    monitor.beginTask(null, 10);
    
    final Collection<Source> x10Files = new LinkedList<Source>();
    project.accept(new X10FileResourceVisitor(x10Files));
    
    final IJavaProject javaProject = JavaCore.create(project);
    
    final Set<String> cps = JavaProjectUtils.getFilteredCpEntries(javaProject, new CpEntryAsStringFunc(), 
                                                                  new AlwaysTrueFilter<IPath>());
    final StringBuilder cpBuilder = new StringBuilder();
    int i = -1;
    for (final String cpEntry : cps) {
      if (++i > 0) {
        cpBuilder.append(File.pathSeparatorChar);
      }
      cpBuilder.append(cpEntry);
    }
    
    final Set<IPath> srcPaths = JavaProjectUtils.getFilteredCpEntries(javaProject, new IdentityFunctor<IPath>(),
                                                                      new RuntimeFilter());
    final List<File> sourcePath = CollectionUtils.transform(srcPaths, new IPathToFileFunc());
    
    final ExtensionInfo extInfo = new ExtensionInfo(null /* monitor */, new ShallowMessageHander());
    final X10CompilerOptions compilerOptions = (X10CompilerOptions) extInfo.getOptions();
    compilerOptions.assertions = true;
    compilerOptions.compile_command_line_only = true;
    compilerOptions.serialize_type_info = false;
    compilerOptions.post_compiler = null;
    compilerOptions.classpath = cpBuilder.toString();
    compilerOptions.output_classpath = compilerOptions.classpath;
    compilerOptions.source_path = sourcePath;
    
    extInfo.setInterestingSources(x10Files);
    
    final Compiler compiler = new Compiler(extInfo, new ShallowErrorQueue());
    Globals.initialize(compiler);
    
    monitor.subTask(LaunchMessages.CAT_ParsingX10FilesMsg);
    // Unfortunately we can't pass the monitor to the compiler :-/ So we have no way of canceling the operation.
    compiler.compile(x10Files);
    monitor.worked(7);
    if (! monitor.isCanceled()) {
      monitor.subTask(LaunchMessages.CAT_SearchingMainMethodsMsg);
      final NodeVisitor nodeVisitor = new X10TypeNodeVisitor(x10Types);
      for (final Source source : x10Files) {
        final Node astRootNode = extInfo.getASTFor(source);
        if (astRootNode != null) {
          astRootNode.visit(nodeVisitor);
        }
      }
    }
    monitor.worked(3);
  }
  
  private void createMainTypeEditor(final Composite parent) {
    final Group group = new Group(parent, SWT.NONE);
    group.setFont(parent.getFont());
    group.setLayout(new GridLayout(2, false));
    group.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    group.setText(LaunchMessages.CAT_MainTypeGroupName);
    
    final Label label = new Label(group, SWT.NONE);
    label.setLayoutData(new GridData(SWT.LEFT, SWT.NONE, false, false, 2, 1));
    label.setFont(group.getFont());
    label.setText(LaunchMessages.CAT_AbsoluteAndRelativePathMsg);
    
    this.fMainTypeText = new Text(group, SWT.SINGLE | SWT.BORDER);
    this.fMainTypeText.setFont(group.getFont());
    this.fMainTypeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    this.fMainTypeText.addModifyListener(new TextModificationListener());
    
    this.fSearchMainTypeBt = createPushButton(group, LaunchMessages.CAT_SearchButton, null /* image */);
    this.fSearchMainTypeBt.addSelectionListener(new SearchMainTypeBtSelectionListener());
    
    this.fShouldLink = createCheckButton(group, LaunchMessages.CAT_LinkApp);
    this.fShouldLink.setData(new GridData(SWT.FILL, SWT.NONE, true, false));
    this.fShouldLink.setSelection(false);
    this.fShouldLink.setToolTipText(LaunchMessages.CAT_LinkAppToolTip);
    this.fShouldLink.addSelectionListener(new ButtonSelectionListener());
  }
  
  private void createProgramArgs(final Composite parent) {
    final Group group = new Group(parent, SWT.NONE);
    group.setFont(parent.getFont());
    group.setLayout(new GridLayout(1, false));
    group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    group.setText(LaunchMessages.AT_ProgArgsGroupName);
    
    this.fPgrmArgsText = new Text(group, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
    this.fPgrmArgsText.addTraverseListener(new TraverseListener() {
      public void keyTraversed(final TraverseEvent event) {
        switch (event.detail) {
        case SWT.TRAVERSE_ESCAPE:
        case SWT.TRAVERSE_PAGE_NEXT:
        case SWT.TRAVERSE_PAGE_PREVIOUS:
          event.doit = true;
          break;
        case SWT.TRAVERSE_RETURN:
        case SWT.TRAVERSE_TAB_NEXT:
        case SWT.TRAVERSE_TAB_PREVIOUS:
          if ((CppApplicationTab.this.fPgrmArgsText.getStyle() & SWT.SINGLE) != 0) {
            event.doit = true;
          } else if (! CppApplicationTab.this.fPgrmArgsText.isEnabled() || (event.stateMask & SWT.MODIFIER_MASK) != 0) {
            event.doit = true;
          }
          break;
        }
      }
    });
    final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
    gd.heightHint = 40;
    gd.widthHint = 100;
    this.fPgrmArgsText.setLayoutData(gd);
    this.fPgrmArgsText.setFont(parent.getFont());
    this.fPgrmArgsText.addModifyListener(new TextModificationListener());

    final Button pgrmArgVarBt = createPushButton(group, LaunchMessages.AT_VariablesBtName, null /* image */);
    pgrmArgVarBt.setLayoutData(new GridData(SWT.END, SWT.NONE, false, false));
    pgrmArgVarBt.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(final SelectionEvent event) {
        final StringVariableSelectionDialog dialog = new StringVariableSelectionDialog(getShell());
        dialog.open();
        final String variable = dialog.getVariableExpression();
        if (variable != null) {
          CppApplicationTab.this.fPgrmArgsText.insert(variable);
        }
      }
    });
  }
  
  private void createOutputToConsoleButton(final Composite parent) {
    this.fToConsoleBt = new Button(parent, SWT.CHECK);
    this.fToConsoleBt.setText(LaunchMessages.CAT_OutputToConsoleMsg);
    this.fToConsoleBt.addSelectionListener(new ButtonSelectionListener());
  }
  
  private void createProjectEditor(final Composite parent) {
    final Group group = new Group(parent, SWT.NONE);
    group.setFont(parent.getFont());
    group.setLayout(new GridLayout(2, false));
    group.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    group.setText(LaunchMessages.CAT_ProjectGroupName);
    
    this.fProjectText = new Text(group, SWT.SINGLE | SWT.BORDER);
    this.fProjectText.setFont(group.getFont());
    this.fProjectText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    this.fProjectText.addModifyListener(new TextModificationListener());
    
    this.fProjectBt = createPushButton(group, LaunchMessages.CAT_BrowseButton, null /* image */);
    this.fProjectBt.addSelectionListener(new ProjectBtSelectionListener());
  }
  
  private String getExecutablePath(final String mainClassPath, final String workspaceDir, 
                                   final ILaunchConfiguration configuration) {
    final IPath path = new Path(mainClassPath);
    final StringBuilder execPathBuilder = new StringBuilder();
    if (! path.isAbsolute()) {
      execPathBuilder.append(workspaceDir).append('/');
    }
    
    final int dotIndex = mainClassPath.lastIndexOf('.');
    final int lastIndex = (dotIndex == -1) ? mainClassPath.length() : dotIndex;
    final String className = mainClassPath.substring(0, lastIndex);
    
    execPathBuilder.append(className);
    
    final IResourceManagerControl rmControl = (IResourceManagerControl) getResourceManager(configuration);
    final IResourceManagerConfiguration rmc = rmControl.getConfiguration();
    final IRemoteServices remoteServices = PTPRemoteCorePlugin.getDefault().getRemoteServices(rmc.getRemoteServicesId());
    final IRemoteConnection rmConnection = remoteServices.getConnectionManager().getConnection(rmc.getConnectionName());
    if (isWindowsSystem(rmConnection)) {
      execPathBuilder.append(EXE_EXT);
    }
    return execPathBuilder.toString();
  }
  
  private boolean isWindowsSystem(final IRemoteConnection connection) {
    return connection.getEnv(COMSPEC_ENV_VAR) != null;
  }
  
  private void searchForMatchingGeneratedFile(final Collection<IFileStore> matches, final IFileStore dirStore,
                                              final String curDir, final String pkgName, 
                                              final String typeName, final IProgressMonitor monitor) throws CoreException {
    for (final IFileStore fileStore : dirStore.childStores(EFS.NONE, monitor)) {
      final IFileInfo fileInfo = fileStore.fetchInfo();
      if (fileInfo.isDirectory()) {
        final StringBuilder newDir = new StringBuilder();
        if (! curDir.equals(EMPTY_STR)) {
          newDir.append(curDir).append('.');
        }
        newDir.append(fileInfo.getName());
        searchForMatchingGeneratedFile(matches, fileStore, newDir.toString(), pkgName, typeName, monitor);
      } else {
        final String name = fileInfo.getName();
        final int dotIndex = name.lastIndexOf('.');
        final String ext = name.substring(dotIndex + 1);
        if (CC_EXT.equals(ext)) {
          final String nameWithoutExt = name.substring(0, (dotIndex == -1) ? name.length() : dotIndex);
          final StringBuilder typeNameBuilder = new StringBuilder();
          if (! curDir.equals(EMPTY_STR)) {
            typeNameBuilder.append(curDir).append('.');
          }
          typeNameBuilder.append(nameWithoutExt);
          if (typeName.equals(typeNameBuilder.toString())) {
            matches.add(fileStore);
          }
        }
      }
    }
  }
  
  private void setMainType(final IProject project, final IResourceManager resourceManager,
                           final ClassType mainType) throws CoreException {
    final String workspaceDir = JavaProjectUtils.getTargetWorkspaceDir(project);
    final IRemoteFileManager fileManager = PTPUtils.getConnectionAndFileManager(resourceManager).second;
    final IFileStore wDirStore = fileManager.getResource(workspaceDir);
    
    final Collection<IFileStore> matches = new ArrayList<IFileStore>();
    final String pkgName = (mainType.package_() == null) ? EMPTY_STR : mainType.package_().fullName().toString();
    searchForMatchingGeneratedFile(matches, wDirStore, EMPTY_STR, pkgName, mainType.fullName().toString(),
                                   new NullProgressMonitor());
    
    if (matches.isEmpty()) {
      this.fMainTypeText.setText(NLS.bind(LaunchMessages.CAT_NoCPPFileForMainType, mainType.name().toString()));
    } else {
      this.fMainTypeText.setText(fileManager.toPath(matches.iterator().next().toURI()));
    }
  }
  
  // --- Private classes
  
  private final class ProjectBtSelectionListener implements SelectionListener {
    
    // --- Interface methods implementation
    
    public void widgetDefaultSelected(final SelectionEvent event) {
    }

    public void widgetSelected(final SelectionEvent event) {
      final ILabelProvider labelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
      final ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);
      dialog.setTitle(LaunchMessages.CAT_PrjSelectionDialogTitle);
      dialog.setMessage(LaunchMessages.CAT_PrjSelectionDialogMsg);
      try {
        dialog.setElements(getProjectList());
      } catch (CoreException except) {
        setErrorMessage(except.getMessage());
        CppLaunchCore.log(except.getStatus());
      }
      if (dialog.open() == Window.OK) {
        final IProject project = (IProject) dialog.getFirstResult();
        CppApplicationTab.this.fProjectText.setText(project.getName());
      }
    }
    
    // --- Private code
    
    private IProject[] getProjectList() throws CoreException {
      final IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
      final Collection<IProject> projects = new ArrayList<IProject>(allProjects.length);
      for (final IProject project : allProjects) {
        if (project.isOpen() && project.hasNature(CppLaunchCore.X10_CPP_PRJ_NATURE_ID)) {
          projects.add(project);
        }
      }
      return projects.toArray(new IProject[projects.size()]);
    }
    
  }
  
  private final class SearchMainTypeBtSelectionListener implements SelectionListener {

    // --- Interface methods implementation
    
    public void widgetDefaultSelected(final SelectionEvent event) {
    }

    public void widgetSelected(final SelectionEvent event) {
      final String projectName = CppApplicationTab.this.fProjectText.getText().trim();
      if (projectName.length() > 0) {
        final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        if (project.exists()) {
          final Collection<ClassType> x10Types = new ArrayList<ClassType>();
          final IRunnableWithProgress runnable = new IRunnableWithProgress() {
              
            public void run(IProgressMonitor monitor) {
              try {
                collectX10Types(project, x10Types, monitor);
              } catch (CoreException except) {
                ErrorUtils.dialogWithLog(getShell(), LaunchMessages.CAT_MainTypeSearchError, IStatus.ERROR, 
                                         NLS.bind(LaunchMessages.CAT_X10FileSearchOrCPFailure, project.getName()), except);
              }
            }
              
          };
          try {
            new ProgressMonitorDialog(getShell()).run(true, false, runnable);
          } catch (Exception except) {
            ErrorUtils.dialogWithLog(getShell(), LaunchMessages.CAT_MainTypeSearchInternalError, IStatus.ERROR, 
                                     LaunchMessages.CAT_X10ParsingInternalError, except);
          }
            
          if (! x10Types.isEmpty()) {
            final SelectionDialog dialog = new X10ClassTypeSelectionDialog(getShell(), x10Types);
            if (dialog.open() == Window.OK) {
              final ClassType resultType = (ClassType) dialog.getResult()[0];
              try {
                setMainType(project, getResourceManager(getLaunchConfiguration()), resultType);
              } catch (CoreException except) {
                ErrorUtils.dialogWithLog(getShell(), LaunchMessages.CAT_SetMainTypeError, IStatus.ERROR, 
                                         LaunchMessages.CAT_ResourcesAccessError, except);
              }
            }
          }
        } else {
          setErrorMessage(LaunchMessages.CAT_NotExistingProject);
        }
      } else {
        setErrorMessage(LaunchMessages.CAT_NoProjectDefinition);
      }
    }
    
  }
  
  private final class ButtonSelectionListener implements SelectionListener {
    
    // --- Interface methods implementation

    public void widgetDefaultSelected(final SelectionEvent event) {
      widgetSelected(event);
    }

    public void widgetSelected(final SelectionEvent event) {
      setDirty(true);
      updateLaunchConfigurationDialog();
    }
    
  }
  
  private final class TextModificationListener implements ModifyListener {

    // --- Interface methods implementation
    
    public void modifyText(final ModifyEvent event) {
      setDirty(true);
      updateLaunchConfigurationDialog();
    }
    
  }
  
  private static final class ShallowMessageHander implements IMessageHandler {
    
    // --- Interface methods implementation
    
    public void handleMessage(final int errorCode, final int[] msgLocation, final int[] errorLocation, 
                              final String filename, final String[] errorInfo) {
      // Do nothing
    }
    
  }
  
  private static final class X10FileResourceVisitor implements IResourceVisitor {
    
    X10FileResourceVisitor(final Collection<Source> x10Files) {
      this.fX10Files = x10Files;
    }
    
    // --- Interface methods implementation
    
    public boolean visit(final IResource resource) throws CoreException {
      if (resource.getType() == IResource.FILE) {
        final IFile file = (IFile) resource;
        if (X10_EXT.equalsIgnoreCase(file.getFileExtension())) {
          try {
            this.fX10Files.add(new FileSource(new JavaModelFileResource(file)));
          } catch (IOException except) {
            throw new CoreException(new Status(IStatus.ERROR, CppLaunchCore.PLUGIN_ID, LaunchMessages.CAT_IOError, except));
          }
        }
      }
      return true;
    }
    
    // --- Fields
    
    private final Collection<Source> fX10Files;
    
  }
  
  private static final class ShallowErrorQueue implements ErrorQueue {

    // --- Interface methods implementation
    
    public void enqueue(final int type, final String message) {
    }

    public void enqueue(final int type, final String message, final Position position) {
    }

    public void enqueue(final ErrorInfo errorInfo) {
    }

    public int errorCount() {
      return 0;
    }

    public void flush() {
    }

    public boolean hasErrors() {
      return false;
    }
    
  }
  
  private static final class X10TypeNodeVisitor extends NodeVisitor {
    
    X10TypeNodeVisitor(final Collection<ClassType> x10Types) {
      this.fX10Types = x10Types;
    }
    
    // --- Overridden methods
    
    public Node override(final Node node) {
      if (node instanceof X10MethodDecl) {
        final X10MethodDecl methodDecl = (X10MethodDecl) node;
        final X10ClassType classType = (X10ClassType) methodDecl.methodDef().asInstance().container();
        final X10TypeSystem typeSystem = (X10TypeSystem) classType.typeSystem();
        if (methodDecl.name().toString().equals(MAIN_METHOD_NAME) && methodDecl.flags().flags().isPublic() &&
            methodDecl.flags().flags().isStatic() && methodDecl.returnType().type().isVoid() &&
            (methodDecl.formals().size() == 1) && 
            typeSystem.isSubtype(methodDecl.formals().get(0).type().type(), 
                                 typeSystem.Rail(typeSystem.String()), typeSystem.emptyContext())) {
          this.fX10Types.add(classType);
        }
        // We don't search for a "main method" within methods.
        return node;
      } else {
        return null;
      }
    }
    
    // --- Fields
    
    private final Collection<ClassType> fX10Types;
    
  }
  
  // --- Fields
  
  private Text fProjectText;
  
  private Text fMainTypeText;
  
  private Text fPgrmArgsText;
  
  private Button fProjectBt;
  
  private Button fSearchMainTypeBt;
  
  private Button fShouldLink;
  
  private Button fToConsoleBt;
  
  
  private static final String EXE_EXT = ".exe"; //$NON-NLS-1$
  
  private static final String COMSPEC_ENV_VAR = "COMSPEC"; //$NON-NLS-1$
  
  private static final String X10_EXT = "x10"; //$NON-NLS-1$
  
  private static final String CC_EXT = "cc"; //$NON-NLS-1$
  
  private static final String MAIN_METHOD_NAME = "main"; //$NON-NLS-1$
  
  private static final String EMPTY_STR = ""; //$NON-NLS-1$
    
}
