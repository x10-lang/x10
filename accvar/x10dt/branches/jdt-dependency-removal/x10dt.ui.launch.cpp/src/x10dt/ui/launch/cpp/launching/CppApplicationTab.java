/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.launching;

import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_ARGUMENTS;
import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_CONSOLE;
import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_PROJECT_NAME;
import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_WORK_DIRECTORY;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.imp.model.ISourceEntity;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.utils.Pair;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.launch.ui.LaunchConfigurationTab;
import org.eclipse.ptp.launch.ui.LaunchImages;
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
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import polyglot.types.ClassType;
import x10dt.core.X10DTCorePlugin;
import x10dt.ui.Messages;
import x10dt.ui.X10DTUIPlugin;
import x10dt.ui.editor.X10LabelProvider;
import x10dt.ui.launch.core.Constants;
import x10dt.ui.launch.core.platform_conf.ETargetOS;
import x10dt.ui.launch.core.utils.CoreResourceUtils;
import x10dt.ui.launch.cpp.CppLaunchCore;
import x10dt.ui.launch.cpp.LaunchMessages;
import x10dt.ui.launch.cpp.builder.target_op.ITargetOpHelper;
import x10dt.ui.launch.cpp.builder.target_op.TargetOpHelperFactory;
import x10dt.ui.launch.cpp.platform_conf.IConnectionConf;
import x10dt.ui.launch.cpp.platform_conf.ICppCompilationConf;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;
import x10dt.ui.launch.cpp.platform_conf.X10PlatformConfFactory;
import x10dt.ui.launch.cpp.utils.PlatformConfUtils;
import x10dt.ui.launching.ResourceToJavaElementAdapter;
import x10dt.ui.utils.LaunchUtils;

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

      final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
      if (project.exists()) {
        if (this.fX10PlatformConf != null) {
          try {
            configuration.setAttribute(ATTR_WORK_DIRECTORY, PlatformConfUtils.getWorkspaceDir(this.fX10PlatformConf, project));
          } catch (CoreException except) {
            // Let's forget it will be handled by the validation step.
          }
        }
      }

      configuration.setAttribute(Constants.ATTR_X10_MAIN_CLASS, this.fMainTypeText.getText().trim());

      final String content = this.fPgrmArgsText.getText().trim();
      configuration.setAttribute(ATTR_ARGUMENTS, (content.length() > 0) ? content : null);

      configuration.setAttribute(ATTR_CONSOLE, this.fToConsoleBt.getSelection());
    }
  }

  public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
    final IResource context = getCurrentSelectionContext();
    if (context == null) {
      configuration.setAttribute(ATTR_PROJECT_NAME, (String) null);
      configuration.setAttribute(Constants.ATTR_X10_MAIN_CLASS, (String) null);
    } else {
      configuration.setAttribute(ATTR_PROJECT_NAME, context.getProject().getName());
      final ISourceEntity[] scope = new ISourceEntity[] { new ResourceToJavaElementAdapter(context) };
      try {
        final Pair<ClassType, ISourceEntity> mainType = LaunchUtils.findMainType(scope, X10DTCorePlugin.X10_CPP_PRJ_NATURE_ID,
                                                                                getShell());
        if (mainType != null) {
          configuration.setAttribute(Constants.ATTR_X10_MAIN_CLASS, mainType.first.fullName().toString());
        }
      } catch (Exception except) {
        // Simply forgets.
      }
    }
    configuration.setAttribute(ATTR_WORK_DIRECTORY, (String) null);
    configuration.setAttribute(ATTR_ARGUMENTS, (String) null);
    configuration.setAttribute(ATTR_CONSOLE, true);
  }

  // --- Overridden methods

  public Image getImage() {
    return LaunchImages.getImage(LaunchImages.IMG_MAIN_TAB);
  }

  public void initializeFrom(final ILaunchConfiguration configuration) {
    super.initializeFrom(configuration);
    try {
      final String projectName = configuration.getAttribute(ATTR_PROJECT_NAME, Constants.EMPTY_STR);
      final IX10PlatformConf incomingPlatformConf;
      if (projectName.length() == 0) {
        incomingPlatformConf = null;
      } else {
        final IProject incomingProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        incomingPlatformConf = CppLaunchCore.getInstance().getPlatformConfiguration(incomingProject);
      }

      if (this.fX10PlatformConf == null || this.fX10PlatformConf != incomingPlatformConf) {
        setTextWithoutNotification(this.fProjectText, configuration, ATTR_PROJECT_NAME);
        setTextWithoutNotification(this.fMainTypeText, configuration, Constants.ATTR_X10_MAIN_CLASS);
        setTextWithoutNotification(this.fPgrmArgsText, configuration, ATTR_ARGUMENTS);
        this.fToConsoleBt.setSelection(configuration.getAttribute(ATTR_CONSOLE, true));

        if (this.fProjectText.getText().length() > 0) {
          final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(this.fProjectText.getText());
          if (project.exists()) {
            this.fX10PlatformConf = CppLaunchCore.getInstance().getPlatformConfiguration(project);
            if (this.fX10PlatformConf != null) {
              final int errors = CoreResourceUtils.getNumberOfPlatformConfErrorMarkers(X10PlatformConfFactory.getFile(project));
              if (errors == 0) {
                for (final ILaunchTabPlatformConfServices services : this.fPConfServices) {
                  services.setLaunchConfiguration(configuration);
                  services.platformConfSelected(this.fX10PlatformConf);
                }
              }
            }
          }
        }
      }
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
        if (!project.exists()) {
          setErrorMessage(NLS.bind(LaunchMessages.CAT_NoExistingProject, projectName));
          return false;
        }
        try {
          if (!project.hasNature(X10DTCorePlugin.X10_CPP_PRJ_NATURE_ID)) {
            setErrorMessage(NLS.bind(LaunchMessages.CAT_NoCPPProjectNature, projectName));
            return false;
          }
        } catch (CoreException except) {
          setErrorMessage(NLS.bind(LaunchMessages.CAT_ProjectNatureAccessError, projectName));
          return false;
        }
        if (!project.isOpen()) {
          setErrorMessage(NLS.bind(LaunchMessages.CAT_ClosedProject, projectName));
          return false;
        }
        // Project exists and is not closed. Checks now that it does not have platform configuration error.
        final int errorCount = CoreResourceUtils.getNumberOfPlatformConfErrorMarkers(X10PlatformConfFactory.getFile(project));
        if (errorCount > 0) {
          setErrorMessage(NLS.bind(LaunchMessages.CAT_FixPlatformConfErrors, project.getName()));
          return false;
        }
        // Checks now if we have a correct main type file path.
        if (this.fMainTypeText.getText().trim().length() == 0) {
          setErrorMessage(LaunchMessages.CAT_RequiredMainTypeName);
          return false;
        }
        if (this.fX10PlatformConf == null) {
          setMessage(NLS.bind(LaunchMessages.CAT_CouldNotLoadPlatformWarning, projectName));
        } else {
          final ITargetOpHelper targetOpHelper = getTargetOpHelper();
          if (targetOpHelper == null) {
            setErrorMessage(x10dt.ui.launch.core.Messages.CPPB_NoPTPConnectionForName);
            return false;
          }
          try {
            PlatformConfUtils.getWorkspaceDir(this.fX10PlatformConf, project);
          } catch (CoreException except) {
            setErrorMessage(LaunchMessages.CAT_CantAccessOutputDir);
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
  
  // --- Internal services
  
  void setPlatformConfServices(final Collection<ILaunchTabPlatformConfServices> pConfServices) {
    this.fPConfServices = pConfServices;
  }

  // --- Private code

  private void createMainTypeEditor(final Composite parent) {
    final Group group = new Group(parent, SWT.NONE);
    group.setFont(parent.getFont());
    group.setLayout(new GridLayout(2, false));
    group.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    group.setText(LaunchMessages.CAT_MainTypeGroupName);

    this.fMainTypeText = new Text(group, SWT.SINGLE | SWT.BORDER);
    this.fMainTypeText.setFont(group.getFont());
    this.fMainTypeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    this.fMainTypeText.addModifyListener(new TextModificationListener());

    this.fSearchMainTypeBt = createPushButton(group, LaunchMessages.CAT_SearchButton, null /* image */);
    this.fSearchMainTypeBt.addSelectionListener(new SearchMainTypeBtSelectionListener());
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
          } else if (!CppApplicationTab.this.fPgrmArgsText.isEnabled() || (event.stateMask & SWT.MODIFIER_MASK) != 0) {
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
    this.fProjectText.addModifyListener(new ProjectTextModificationListener());

    this.fProjectBt = createPushButton(group, LaunchMessages.CAT_BrowseButton, null /* image */);
    this.fProjectBt.addSelectionListener(new ProjectBtSelectionListener());
  }
  
  private IResource getCurrentSelectionContext() {
    final IWorkbenchWindow workbenchWindow = CppLaunchCore.getInstance().getWorkbench().getActiveWorkbenchWindow();
    if (workbenchWindow != null) {
      final IWorkbenchPage activePage = workbenchWindow.getActivePage();
      if (activePage != null) {
        final ISelection selection = activePage.getSelection();
        if (selection instanceof IStructuredSelection) {
          return getCurrentSelectionContext((IStructuredSelection) selection);
        }
      }
    }
    return null;
  }
  
  private IResource getCurrentSelectionContext(final IStructuredSelection selection) {
    final Object selectedObject;
    if (selection.size() > 1) {
      // Unless they have a common project, it doesn't really makes sense here.
      IProject commonProject = null;
      for (final Iterator<?> it = selection.iterator(); it.hasNext();) {
        final Object element = it.next();
        final IProject curPrj;
        if (element instanceof ISourceEntity) {
          curPrj = ((ISourceEntity) element).getProject().getRawProject();
        } else if (element instanceof IResource) {
          curPrj = ((IResource) element).getProject();
        } else {
          curPrj = null;
        }
        if (commonProject == null) {
          commonProject = curPrj;
        } else if ((curPrj != null) && ! commonProject.equals(curPrj)) {
          commonProject = null;
          break;
        }
      }
      selectedObject = commonProject;
    } else {
      selectedObject = selection.getFirstElement();
    }
    final IResource resource;
    if (selectedObject instanceof ISourceEntity) {
      resource = ((ISourceEntity) selectedObject).getResource();
    } else if (selectedObject instanceof IResource) {
      resource = (IResource) selectedObject;
    } else {
      resource = null;
    }
    if ((resource != null) && resource.exists()) {
      final IProject project = resource.getProject();
      try {
        if (project.hasNature(X10DTCorePlugin.X10_CPP_PRJ_NATURE_ID)) {
          return resource;
        }
      } catch (CoreException except) {
        // Simply forgets.
      }
    }
    return null;
  }

  private ITargetOpHelper getTargetOpHelper() {
    final IConnectionConf connConf = this.fX10PlatformConf.getConnectionConf();
    final ICppCompilationConf cppCompConf = this.fX10PlatformConf.getCppCompilationConf();
    final boolean isCygwin = cppCompConf.getTargetOS() == ETargetOS.WINDOWS;
    return TargetOpHelperFactory.create(connConf.isLocal(), isCygwin, connConf.getConnectionName());
  }

  private void setTextWithoutNotification(final Text text, final ILaunchConfiguration configuration, 
                                          final String name) throws CoreException {
    final Listener[] listeners = text.getListeners(SWT.Modify);
    for (final Listener listener : listeners) {
      text.removeListener(SWT.Modify, listener);
    }
    text.setText(configuration.getAttribute(name, Constants.EMPTY_STR));
    for (final Listener listener : listeners) {
      text.addListener(SWT.Modify, listener);
    }
  }

  // --- Private classes

  private final class ProjectBtSelectionListener implements SelectionListener {

    // --- Interface methods implementation

    public void widgetDefaultSelected(final SelectionEvent event) {
    }

    public void widgetSelected(final SelectionEvent event) {
      final ILabelProvider labelProvider = new X10LabelProvider();
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
        if (project.isOpen() && project.hasNature(X10DTCorePlugin.X10_CPP_PRJ_NATURE_ID)) {
          projects.add(project);
        }
      }
      return projects.toArray(new IProject[projects.size()]);
    }

  }

  private final class ProjectTextModificationListener implements ModifyListener {

    // --- Interface methods implementation

    public void modifyText(final ModifyEvent event) {
      final String name = CppApplicationTab.this.fProjectText.getText().trim();
      try {
        if (name.length() > 0) {
          final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
          if (project.exists() && project.hasNature(X10DTCorePlugin.X10_CPP_PRJ_NATURE_ID)) {
            final int errors = CoreResourceUtils.getNumberOfPlatformConfErrorMarkers(X10PlatformConfFactory.getFile(project));
            if (errors == 0) {
              CppApplicationTab.this.fX10PlatformConf = CppLaunchCore.getInstance().getPlatformConfiguration(project);
              for (final ILaunchTabPlatformConfServices services : CppApplicationTab.this.fPConfServices) {
                services.platformConfSelected(CppApplicationTab.this.fX10PlatformConf); 
              }
            }
          }
        }
      } catch (CoreException except) {
        // Forget at this point, it will be handled by the validation.
      }

      setDirty(true);
      updateLaunchConfigurationDialog();
    }

  }

  private final class SearchMainTypeBtSelectionListener implements SelectionListener {

    // --- Interface methods implementation

    public void widgetDefaultSelected(final SelectionEvent event) {
      widgetSelected(event);
    }

    public void widgetSelected(final SelectionEvent event) {
      final String projectName = CppApplicationTab.this.fProjectText.getText().trim();
      final IProject project;
      if (projectName.length() > 0) {
        project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
      } else {
        project = null;
      }
      final ISourceEntity[] scope;
      if ((project == null) || !project.exists()) {
        scope = null;
      } else {
        boolean hasValidNature = false;
        try {
          hasValidNature = project.hasNature(X10DTCorePlugin.X10_CPP_PRJ_NATURE_ID);
        } catch (CoreException except) {
          // Do nothing.
        }
        scope = hasValidNature ? new ISourceEntity[] { ModelFactory.getProject(project) } : null;
      }
      try {
        final Pair<ClassType, ISourceEntity> mainType = LaunchUtils.findMainType(scope, X10DTCorePlugin.X10_CPP_PRJ_NATURE_ID,
                                                                                getShell());
        if (mainType != null) {
          CppApplicationTab.this.fMainTypeText.setText(mainType.first.fullName().toString());
          if (scope == null) {
            CppApplicationTab.this.fProjectText.setText(mainType.second.getProject().getName());
          }
        }
      } catch (InvocationTargetException except) {
        if (except.getTargetException() instanceof CoreException) {
          ErrorDialog.openError(getShell(), Messages.AXLS_MainTypeSearchError, Messages.AXLS_MainTypeSearchErrorMsg,
                                ((CoreException) except.getTargetException()).getStatus());
          CppLaunchCore.log(((CoreException) except.getTargetException()).getStatus());
        } else {
          final IStatus status = new Status(IStatus.ERROR, X10DTUIPlugin.PLUGIN_ID, Messages.AXLS_MainTypeSearchInternalError,
                                            except.getTargetException());
          ErrorDialog.openError(getShell(), Messages.AXLS_MainTypeSearchError, Messages.AXLS_MainTypeSearchErrorMsg, status);
          CppLaunchCore.log(status);
        }
      } catch (InterruptedException except) {
        // Do nothing.
      }
      updateLaunchConfigurationDialog();
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

  // --- Fields

  private Collection<ILaunchTabPlatformConfServices> fPConfServices;

  private Text fProjectText;

  private Text fMainTypeText;

  private Text fPgrmArgsText;

  private Button fProjectBt;

  private Button fSearchMainTypeBt;

  private Button fToConsoleBt;

  private IX10PlatformConf fX10PlatformConf;

}
