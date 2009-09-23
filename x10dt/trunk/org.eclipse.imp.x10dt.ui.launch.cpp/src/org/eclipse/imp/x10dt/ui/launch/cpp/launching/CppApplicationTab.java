/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.launching;

import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_ARGUMENTS;
import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_EXECUTABLE_PATH;
import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_PROJECT_NAME;
import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_WORK_DIRECTORY;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.imp.x10dt.ui.launch.cpp.Constants;
import org.eclipse.imp.x10dt.ui.launch.cpp.CppLaunchCore;
import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.elementcontrols.IResourceManagerControl;
import org.eclipse.ptp.core.elements.attributes.ResourceManagerAttributes;
import org.eclipse.ptp.launch.ui.LaunchConfigurationTab;
import org.eclipse.ptp.launch.ui.LaunchImages;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.PTPRemoteCorePlugin;
import org.eclipse.ptp.remote.ui.IRemoteUIFileManager;
import org.eclipse.ptp.remote.ui.IRemoteUIServices;
import org.eclipse.ptp.remote.ui.PTPRemoteUIPlugin;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;


final class CppApplicationTab extends LaunchConfigurationTab implements ILaunchConfigurationTab {

  // --- Interface methods implementation
  
  public void createControl(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NULL);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(1, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
    
    createProjectEditor(composite);
    createVerticalSpacer(composite, 2);
    createApplicationProgramEditor(composite);
    createVerticalSpacer(composite, 2);
    createProgramArgs(composite);
    
    setControl(composite);
  }

  public String getName() {
    return LaunchMessages.CAT_TabName;
  }

  public void performApply(final ILaunchConfigurationWorkingCopy configuration) {
    final String projectName = this.fProjectText.getText().trim();    
    configuration.setAttribute(ATTR_PROJECT_NAME, projectName);
    
    final String appProgName = this.fAppProgText.getText().trim();
    if ((appProgName.length() > 0) && (projectName.length() > 0)) {
      final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
      if (project.exists()) {
        configuration.setAttribute(ATTR_EXECUTABLE_PATH, appProgName);
        configuration.setAttribute(ATTR_WORK_DIRECTORY, appProgName.substring(0, appProgName.lastIndexOf('/')));
      }
    }
    
    final String content = this.fPgrmArgsText.getText().trim();    
    configuration.setAttribute(ATTR_ARGUMENTS, (content.length() > 0) ? content : null);
    
    configuration.setAttribute(Constants.ATTR_SHOULD_LINK_APP, this.fShouldLink.getSelection());
  }

  public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
    configuration.setAttribute(ATTR_PROJECT_NAME, (String) null);
    configuration.setAttribute(ATTR_EXECUTABLE_PATH, (String) null);
    configuration.setAttribute(ATTR_WORK_DIRECTORY, (String) null);
    configuration.setAttribute(ATTR_ARGUMENTS, (String) null);
    configuration.setAttribute(Constants.ATTR_SHOULD_LINK_APP, true);
  }
  
  // --- Overridden methods
  
  public Image getImage() {
    return LaunchImages.getImage(LaunchImages.IMG_MAIN_TAB);
  }
  
  public void initializeFrom(ILaunchConfiguration configuration) {
    super.initializeFrom(configuration);
    
    try {
      this.fProjectText.setText(configuration.getAttribute(ATTR_PROJECT_NAME, EMPTY_STRING));
      this.fAppProgText.setText(configuration.getAttribute(ATTR_EXECUTABLE_PATH, EMPTY_STRING));
      this.fPgrmArgsText.setText(configuration.getAttribute(ATTR_ARGUMENTS, EMPTY_STRING));
      this.fShouldLink.setSelection(configuration.getAttribute(Constants.ATTR_SHOULD_LINK_APP, true));
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
        // Project exists and is not closed. Checks now if we have a main class.
        final String className = this.fAppProgText.getText().trim();
        if (className == null) {
          setErrorMessage(LaunchMessages.CAT_RequiredMainClassName);
          return false;
        } else if (! hasMain(project, className)) {
          setErrorMessage(LaunchMessages.CAT_NoMainMethod);
          return false;
        }
      } else {
        setErrorMessage(NLS.bind(LaunchMessages.CAT_IllegalPrjName, projectName));
        return false;
      }
    }
    return true;
  }
  
  // --- Private code
  
  private void createApplicationProgramEditor(final Composite parent) {
    final Group group = new Group(parent, SWT.NONE);
    group.setFont(parent.getFont());
    group.setLayout(new GridLayout(2, false));
    group.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    group.setText(LaunchMessages.CAT_AppProgGroupName);
    
    this.fAppProgText = new Text(group, SWT.SINGLE | SWT.BORDER);
    this.fAppProgText.setFont(group.getFont());
    this.fAppProgText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    this.fAppProgText.addModifyListener(new TextModificationListener());
    
    this.fSearchAppProgBt = createPushButton(group, LaunchMessages.CAT_SearchButton, null /* image */);
    this.fSearchAppProgBt.addSelectionListener(new SearchMainClassBtSelectionListener());
    
    this.fShouldLink = createCheckButton(group, LaunchMessages.CAT_LinkApp);
    this.fShouldLink.setData(new GridData(SWT.FILL, SWT.NONE, true, false));
    this.fShouldLink.setSelection(true);
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
    this.fPgrmArgsText.addModifyListener(new ModifyListener() {
      public void modifyText(final ModifyEvent event) {
        updateLaunchConfigurationDialog();
      }
    });

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
  
  private boolean hasMain(final IProject project, final String appProgName) {
    // Be optimistic by default.
    // TODO: This method should use X10DT in order to check if the file given has really an X10 main method in it.
    return true;
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
  
  private final class SearchMainClassBtSelectionListener implements SelectionListener {

    // --- Interface methods implementation
    
    public void widgetDefaultSelected(final SelectionEvent event) {
    }

    public void widgetSelected(final SelectionEvent event) {
      String initialPath = "/"; //$NON-NLS-1$
      final String projectName = CppApplicationTab.this.fProjectText.getText().trim();
      if (projectName.length() > 0) {
        final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        if (project.exists()) {
          try {
            final String workingDir = project.getPersistentProperty(Constants.WORKSPACE_DIR);
            if (workingDir != null) {
              initialPath = workingDir + '/' + projectName;
            }
          } catch (CoreException except) {
            // Simply forgets.
          }
        }
      }
      
      final IResourceManagerControl rm = (IResourceManagerControl) getResourceManager(getLaunchConfiguration());
      if ((rm == null) || (rm.getState() != ResourceManagerAttributes.State.STARTED)) {
        setErrorMessage(LaunchMessages.CAT_NoRunningResManager);
        return;
      }
      final IResourceManagerConfiguration rmc = rm.getConfiguration();
      final IRemoteServices remServices = PTPRemoteCorePlugin.getDefault().getRemoteServices(rmc.getRemoteServicesId());
      final IRemoteUIServices remUIServices = PTPRemoteUIPlugin.getDefault().getRemoteUIServices(remServices);
      if (remServices != null && remUIServices != null) {
        final IRemoteConnection rmConn = remServices.getConnectionManager().getConnection(rmc.getConnectionName());
        if (rmConn != null) {
          final IRemoteUIFileManager fileMgr = remUIServices.getUIFileManager();
          if (fileMgr != null) {
            fileMgr.setConnection(rmConn);
            fileMgr.showConnections(false);
            final IPath path = fileMgr.browseFile(getShell(), LaunchMessages.CAT_SelectMainDialogDescription, initialPath);
            if (path != null) {
              final String file = path.toString();
              CppApplicationTab.this.fAppProgText.setText(file.substring(0, file.length() - 3));
            }
          }
        }
      }
    }
    
  }
  
  private final class TextModificationListener implements ModifyListener {

    // --- Interface methods implementation
    
    public void modifyText(final ModifyEvent event) {
      updateLaunchConfigurationDialog();
    }
    
  }
  
  // --- Fields
  
  private Text fProjectText;
  
  private Text fAppProgText;
  
  private Text fPgrmArgsText;
  
  private Button fProjectBt;
  
  private Button fSearchAppProgBt;
  
  private Button fShouldLink;

}
