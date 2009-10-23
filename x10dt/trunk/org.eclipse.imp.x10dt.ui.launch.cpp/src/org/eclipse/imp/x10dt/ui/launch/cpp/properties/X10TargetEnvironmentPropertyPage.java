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

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.imp.x10dt.ui.launch.core.Constants;
import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.IX10PlatformConfiguration;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.X10PlatformsManager;
import org.eclipse.imp.x10dt.ui.launch.cpp.CppLaunchCore;
import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.IModelManager;
import org.eclipse.ptp.core.PTPCorePlugin;
import org.eclipse.ptp.core.elementcontrols.IResourceManagerControl;
import org.eclipse.ptp.core.elements.IPUniverse;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.core.elements.attributes.ResourceManagerAttributes;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteFileManager;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.PTPRemoteCorePlugin;
import org.eclipse.ptp.remote.ui.IRemoteUIConstants;
import org.eclipse.ptp.remote.ui.IRemoteUIFileManager;
import org.eclipse.ptp.remote.ui.IRemoteUIServices;
import org.eclipse.ptp.remote.ui.PTPRemoteUIPlugin;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;
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
    
    boolean noResourceManager = true;
    final IModelManager modelManager = PTPCorePlugin.getDefault().getModelManager();
    final IPUniverse universe = modelManager.getUniverse();
    for (final IResourceManager resourceManager : universe.getResourceManagers()) {
      if (resourceManager.getState() == ResourceManagerAttributes.State.STARTED) {
        noResourceManager = false;
      }
    }
    if (noResourceManager) {
      setErrorMessage(LaunchMessages.OXPCWTA_ErrorDialogMsg);
      return null;
    }
    
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

    int index = -1;
    for (final IResourceManager resourceManager : universe.getResourceManagers()) {
      if (resourceManager.getState() == ResourceManagerAttributes.State.STARTED) {
        noResourceManager = false;
        ++index;
        this.fResManagerCombo.add(resourceManager.getName());
        this.fResManagerCombo.setData(resourceManager.getName(), resourceManager.getID());
      }
    }
    
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
        final String targetWorkspace = this.fWorkspaceLocText.getText().trim();
//        final String resManagerName = this.fResManagerCombo.getItem(this.fResManagerCombo.getSelectionIndex());
//        final String resManagerId = (String) this.fResManagerCombo.getData(resManagerName);
//        
//        if (! checkDirectory(targetWorkspace, resManagerId)) {
//          return false;
//        }
        project.setPersistentProperty(Constants.WORKSPACE_DIR, targetWorkspace);
      }
    } catch (CoreException except) {
      setErrorMessage(NLS.bind(LaunchMessages.XTEPP_DataStoringError, except.getMessage()));
    }
    return true;
  }
  
  // --- Private code
  
  private boolean checkDirectory(final String targetWorkspace, final String resManagerId) {
    final IModelManager modelManager = PTPCorePlugin.getDefault().getModelManager();
    final IResourceManager resourceManager = modelManager.getUniverse().getResourceManager(resManagerId);
    final IResourceManagerControl rmControl = (IResourceManagerControl) resourceManager;
    final IResourceManagerConfiguration rmc = rmControl.getConfiguration();
    final IRemoteServices remoteServices = PTPRemoteCorePlugin.getDefault().getRemoteServices(rmc.getRemoteServicesId());
    final IRemoteConnection connection = remoteServices.getConnectionManager().getConnection(rmc.getConnectionName());
    final IRemoteFileManager fileManager = remoteServices.getFileManager(connection);
    try {
      final IFileInfo fileInfo = fileManager.getResource(new Path(targetWorkspace), new NullProgressMonitor()).fetchInfo();
      if (fileInfo.exists()) {
        if (fileInfo.isDirectory()) {
          return true;
        } else {
          setErrorMessage(LaunchMessages.XTEPP_NoDirectoryError);
          return false;
        }
      } else {
        setErrorMessage(LaunchMessages.XTEPP_DirNotFound);
        return false;
      }
    } catch (IOException except) {
      setErrorMessage(except.getMessage());
      return false;
    }
  }
  
  private void createResourceManager(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    new Label(composite, SWT.NONE).setText(LaunchMessages.CPWSP_ResManagerLabel);
    
    this.fResManagerCombo = new Combo(composite, SWT.READ_ONLY);
    this.fResManagerCombo.setFont(composite.getFont());
    this.fResManagerCombo.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
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
      final String resManagerId = project.getPersistentProperty(Constants.RES_MANAGER_ID);
      
      int index = -1;
      final IModelManager modelManager = PTPCorePlugin.getDefault().getModelManager();
      final IPUniverse universe = modelManager.getUniverse();
      for (final IResourceManager resourceManager : universe.getResourceManagers()) {
        if (resourceManager.getState() == ResourceManagerAttributes.State.STARTED) {
          ++index;
          if (resourceManager.getID().equals(resManagerId)) {
            this.fResManagerCombo.select(index);
            break;
          }
        }
      }
      
      final String x10Platform = project.getPersistentProperty(Constants.X10_PLATFORM_CONF);
      index = -1;
      for (final String platform : this.fX10PlatformCombo.getItems()) {
        ++index;
        if (platform.equals(x10Platform)) {
          this.fX10PlatformCombo.select(index);
          break;
        }
      }
      
      final String targetWorkspace = project.getPersistentProperty(Constants.WORKSPACE_DIR);
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
    }

    public void widgetSelected(final SelectionEvent event) {
      final IModelManager modelManager = PTPCorePlugin.getDefault().getModelManager();
      final IResourceManager[] resourceManagers = modelManager.getUniverse().getResourceManagers();
      final IResourceManager resMgr = resourceManagers[X10TargetEnvironmentPropertyPage.this.fResManagerCombo.getSelectionIndex()];
      final IResourceManagerControl rm = (IResourceManagerControl) resMgr;
      
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
            final String path = fileMgr.browseDirectory(getShell(), LaunchMessages.CPWSP_BrowseDescription, "/", //$NON-NLS-1$
                                                        IRemoteUIConstants.NONE);
            if (path != null) {
              X10TargetEnvironmentPropertyPage.this.fWorkspaceLocText.setText(path);
            }
          }
        }
      }
    }
    
  }
  
  // --- Fields
  
  private Combo fResManagerCombo;
  
  private Combo fX10PlatformCombo;
    
  private Button fBrowseBt;
    
  private Text fWorkspaceLocText;
  
  private Map<String, IX10PlatformConfiguration> fX10Platforms;

}
