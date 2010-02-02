/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.properties;

import java.io.IOException;
import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.imp.utils.Pair;
import org.eclipse.imp.x10dt.ui.launch.core.Constants;
import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.IX10PlatformConfiguration;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.X10PlatformsManager;
import org.eclipse.imp.x10dt.ui.launch.core.utils.JavaProjectUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.PTPUtils;
import org.eclipse.imp.x10dt.ui.launch.cpp.CppLaunchCore;
import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.PTPCorePlugin;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.core.elements.attributes.ResourceManagerAttributes.State;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteFileManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * Defines the project properties page for updating the X10 target environment.
 * 
 * @author egeay
 */
public final class X10TargetEnvironmentPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {

  // --- PreferencePage's abstract methods implementation

  protected Control createContents(final Composite parent) {
    noDefaultAndApplyButton();
    
    final IProject project = (IProject) getElement().getAdapter(IProject.class);
    
    final Composite composite = new Composite(parent, SWT.NULL);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(1, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
    
    final Group x10PlatformGroup = new Group(composite, SWT.NONE);
    x10PlatformGroup.setFont(composite.getFont());
    x10PlatformGroup.setLayout(new GridLayout(1, false));
    x10PlatformGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    x10PlatformGroup.setText(LaunchMessages.CPWSP_X10PlatformGroup);
    
    createX10Platform(x10PlatformGroup);
    
    final Group resManagerGroup = new Group(composite, SWT.NONE);
    resManagerGroup.setFont(composite.getFont());
    resManagerGroup.setLayout(new GridLayout(1, false));
    resManagerGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    resManagerGroup.setText(LaunchMessages.CPWSP_ResManagerGroupName);
    
    createResourceManager(resManagerGroup);
    
    final Group workspaceGroup = new Group(composite, SWT.NONE);
    workspaceGroup.setFont(composite.getFont());
    workspaceGroup.setLayout(new GridLayout(1, false));
    workspaceGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    workspaceGroup.setText(LaunchMessages.CPWSP_TargetWorkspaceGroupName);
    
    createTargetWorkspace(workspaceGroup);
    
    initValues(project);
    
    return composite;
  }
  
  // --- Overridden methods
  
  public boolean okToLeave() {
    if (this.fResManagerCombo == null) {
      return true;
    }
    return ((this.fResManagerCombo.getSelectionIndex() != -1) && (this.fX10PlatformCombo.getSelectionIndex() != -1) &&
            (this.fWorkspaceLocText.getText().trim().length() > 0));
  }
  
  public boolean performOk() {
    final IProject project = (IProject) getElement().getAdapter(IProject.class);
    
    try {
      if (this.fX10PlatformCombo.getSelectionIndex() == -1) {
        setErrorMessage(LaunchMessages.XTEPP_MissingX10PlatformError);
        return false;
      } else {
        final String platformConf = this.fX10PlatformCombo.getItem(this.fX10PlatformCombo.getSelectionIndex());
        project.setPersistentProperty(Constants.X10_PLATFORM_CONF, platformConf);
      }
      
      if (this.fResManagerCombo.getSelectionIndex() == -1) {
        setErrorMessage(LaunchMessages.XTEPP_MissingRMError);
        return false;
      } else {
        final String resManagerName = this.fResManagerCombo.getItem(this.fResManagerCombo.getSelectionIndex());
        final String resManagerId = (String) this.fResManagerCombo.getData(resManagerName);
        project.setPersistentProperty(Constants.RES_MANAGER_ID, resManagerId);
      }
      
      if (this.fWorkspaceLocText.getText().trim().length() == 0) {
        setErrorMessage(LaunchMessages.XTEPP_MissingTargetWorkspaceError);
        return false;
      } else {
        final String newWDir = this.fWorkspaceLocText.getText().trim();
        final String curWDir = JavaProjectUtils.getTargetWorkspaceDir(project);
        project.setPersistentProperty(Constants.WORKSPACE_DIR, newWDir);
        
        if (! newWDir.equals(curWDir)) {
          final Job job = new Job(LaunchMessages.XTEPP_RebuildTargetWorkspace) {
            
            public IStatus run(final IProgressMonitor monitor) {
              try {
                monitor.beginTask(null, 3);
                monitor.subTask(LaunchMessages.XTEPP_CleanPreviousWDir);
                final String resManagerId = project.getPersistentProperty(Constants.RES_MANAGER_ID);
                final Pair<IRemoteConnection, IRemoteFileManager> pair = PTPUtils.getConnectionAndFileManager(resManagerId);
                final IFileStore curWDirFileStore = pair.second.getResource(curWDir);
                if (curWDirFileStore.fetchInfo().exists()) {
                  curWDirFileStore.delete(EFS.NONE, new SubProgressMonitor(monitor, 1));
                }
                
                monitor.subTask(LaunchMessages.XTEPP_RebuildProject);
                project.build(IncrementalProjectBuilder.FULL_BUILD, new SubProgressMonitor(monitor, 2));
                
                return Status.OK_STATUS;
              } catch (CoreException except) {
                return except.getStatus();
              }
            }
            
          };
          job.setPriority(Job.BUILD);
          job.setRule(project.getWorkspace().getRuleFactory().buildRule());
          job.schedule();
        }
      }
    } catch (CoreException except) {
      setErrorMessage(NLS.bind(LaunchMessages.XTEPP_DataStoringError, except.getMessage()));
    }
    return true;
  }
  
  // --- Private code
  
  private void createResourceManager(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    new Label(composite, SWT.NONE).setText(LaunchMessages.CPWSP_ResManagerLabel);
    
    this.fResManagerCombo = new Combo(composite, SWT.READ_ONLY);
    this.fResManagerCombo.setFont(composite.getFont());
    this.fResManagerCombo.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    this.fResManagerCombo.addSelectionListener(new ResourceManagerSelectionListener());
  }
  
  private Text createLabelAndText(final Composite parent, final String labelText, 
                                  final ModifyListener modifyListener) {
    final Label label = new Label(parent, SWT.NONE);
    label.setText(labelText);
    label.setFont(parent.getFont());
    label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
    
    final Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);
    text.setFont(parent.getFont());
    text.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    if (modifyListener != null) {
      text.addModifyListener(modifyListener);
    }
    return text;
  }
  
  private void createTargetWorkspace(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    final Composite locationComp = new Composite(composite, SWT.NONE);
    locationComp.setFont(parent.getFont());
    locationComp.setLayout(new GridLayout(2, false));
    locationComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    this.fWorkspaceLocText = createLabelAndText(locationComp, LaunchMessages.CPWSP_LocationLabel, null);
    
    final Composite browseComp = new Composite(composite, SWT.NONE);
    browseComp.setLayout(new GridLayout(1, false));
    browseComp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
    this.fBrowseBt = new Button(browseComp, SWT.PUSH);
    this.fBrowseBt.setText(LaunchMessages.CPWSP_BrowseBt);
    this.fBrowseBt.addSelectionListener(new BrowseBtSelectionListener());
  }
  
  private void createX10Platform(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    new Label(composite, SWT.NONE).setText(LaunchMessages.CPWSP_X10Platforms);
    
    this.fX10PlatformCombo = new Combo(composite, SWT.READ_ONLY);
    this.fX10PlatformCombo.setFont(composite.getFont());
    this.fX10PlatformCombo.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    try {
      this.fX10Platforms = X10PlatformsManager.loadPlatformsConfiguration();
      for (final String configurationName : this.fX10Platforms.keySet()) {
        this.fX10PlatformCombo.add(configurationName);
      }
    } catch (WorkbenchException except) {
      setErrorMessage(Messages.XPCPP_LoadingErrorMsg);
      CppLaunchCore.log(IStatus.ERROR, org.eclipse.imp.x10dt.ui.launch.core.Messages.XPCPP_LoadingErrorLogMsg, except);
    } catch (IOException except) {
      setErrorMessage(Messages.XPCPP_LoadingErrorMsg);
      CppLaunchCore.log(IStatus.ERROR, org.eclipse.imp.x10dt.ui.launch.core.Messages.XPCPP_LoadingErrorLogMsg, except);
    }
  }
  
  private void initValues(final IProject project) {
    try {
      final IResourceManager resourceManager = PTPUtils.getResourceManager(getShell(), project);
      if (resourceManager == null) {
        setErrorMessage(LaunchMessages.XPCPP_NoResManagerStartedError);
      } else {
        if (resourceManager.getState() != State.STARTED) {
          resourceManager.startUp(new NullProgressMonitor());
        }
        int index = -1;
        for (final IResourceManager rm : PTPCorePlugin.getDefault().getUniverse().getResourceManagers()) {
          if (rm.getState() == State.STARTED) {
            this.fResManagerCombo.add(rm.getName());
            this.fResManagerCombo.setData(rm.getName(), rm.getUniqueName());
            ++index;
            if (rm.getUniqueName().equals(resourceManager.getUniqueName())) {
              this.fResManagerCombo.select(index);
            }
          }
        }
      }
      
      final String x10Platform = project.getPersistentProperty(Constants.X10_PLATFORM_CONF);
      int index = -1;
      for (final String platform : this.fX10PlatformCombo.getItems()) {
        ++index;
        if (platform.equals(x10Platform)) {
          this.fX10PlatformCombo.select(index);
          break;
        }
      }
      
      final String targetWorkspace = JavaProjectUtils.getTargetWorkspaceDir(project);
      if (targetWorkspace != null) {
        this.fWorkspaceLocText.setText(targetWorkspace);
      }
    } catch (CoreException except) {
      setErrorMessage(NLS.bind(LaunchMessages.XTEPP_InitializationError, except.getMessage()));
    }
  }
  
  // --- Private classes
  
  private final class BrowseBtSelectionListener implements SelectionListener {

    // --- Interface methods implementation
    
    public void widgetDefaultSelected(final SelectionEvent event) {
      widgetSelected(event);
    }

    public void widgetSelected(final SelectionEvent event) {
      final String path = PTPUtils.browseDirectory(getShell(), X10TargetEnvironmentPropertyPage.this.fResManagerCombo, 
                                                   LaunchMessages.CPWSP_BrowseDescription, "/"); //$NON-NLS-1$
      if (path != null) {
        X10TargetEnvironmentPropertyPage.this.fWorkspaceLocText.setText(path);
        updateApplyButton();
      }
    }
    
  }
  
  private final class ResourceManagerSelectionListener implements SelectionListener {

    // --- Interface methods implementation
    
    public void widgetDefaultSelected(final SelectionEvent event) {
      widgetSelected(event);
    }

    public void widgetSelected(final SelectionEvent event) {
      final Combo combo = X10TargetEnvironmentPropertyPage.this.fResManagerCombo;
      final String resManagerId = (String) combo.getData(combo.getItem(combo.getSelectionIndex()));
      final IProject project = (IProject) getElement().getAdapter(IProject.class);
      final String workspaceDir = PTPUtils.getTargetWorkspaceDirectory(resManagerId, project.getName());
      X10TargetEnvironmentPropertyPage.this.fWorkspaceLocText.setText(workspaceDir);
    }
    
  }
  
  // --- Fields
  
  private Combo fResManagerCombo;
  
  private Combo fX10PlatformCombo;
    
  private Button fBrowseBt;
    
  private Text fWorkspaceLocText;
  
  private Map<String, IX10PlatformConfiguration> fX10Platforms;

}
