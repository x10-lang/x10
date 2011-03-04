/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.launch.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.imp.x10dt.ui.cpp.launch.Constants;
import org.eclipse.imp.x10dt.ui.cpp.launch.LaunchMessages;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ptp.core.IModelManager;
import org.eclipse.ptp.core.PTPCorePlugin;
import org.eclipse.ptp.core.elementcontrols.IResourceManagerControl;
import org.eclipse.ptp.core.elements.IPUniverse;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.core.elements.attributes.ResourceManagerAttributes;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.PTPRemoteCorePlugin;
import org.eclipse.ptp.remote.ui.IRemoteUIConstants;
import org.eclipse.ptp.remote.ui.IRemoteUIFileManager;
import org.eclipse.ptp.remote.ui.IRemoteUIServices;
import org.eclipse.ptp.remote.ui.PTPRemoteUIPlugin;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


final class CppProjectWizardSecondPage extends WizardPage {
  
  CppProjectWizardSecondPage() {
    super(LaunchMessages.CPWSP_WizardTitle, LaunchMessages.CPWSP_WizardName, null /* titleImage */);
    
    setPageComplete(false);
    setDescription(LaunchMessages.CPWSP_WizardDescription);
  }
  
  // --- Interface methods implementation

  public void createControl(final Composite parent) {
    initializeDialogUnits(parent);
    
    final Composite composite = new Composite(parent, SWT.NULL);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(1, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
    
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
    
    updateDisablingPart();
    
    setControl(composite);
  }
  
  // --- Internal services
  
  void attachConnectionParameters(final IProject project) throws CoreException {
    final String resName = this.fResManagerCombo.getItem(this.fResManagerCombo.getSelectionIndex());
    final String resID = (String) this.fResManagerCombo.getData(resName);
    project.setPersistentProperty(Constants.RES_MANAGER_ID, resID);
    project.setPersistentProperty(Constants.WORKSPACE_DIR, this.fWorkspaceLocText.getText().trim());
  }
  
  // --- Private code
  
  private void createResourceManager(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    final IModelManager modelManager = PTPCorePlugin.getDefault().getModelManager();
    final IPUniverse universe = modelManager.getUniverse();
    
    new Label(composite, SWT.NONE).setText(LaunchMessages.CPWSP_ResManagerLabel);
    
    this.fResManagerCombo = new Combo(composite, SWT.READ_ONLY);
    this.fResManagerCombo.setFont(composite.getFont());
    this.fResManagerCombo.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    for (final IResourceManager resourceManager : universe.getResourceManagers()) {
      if (resourceManager.getState() == ResourceManagerAttributes.State.STARTED) {
        this.fResManagerCombo.add(resourceManager.getName());
        this.fResManagerCombo.setData(resourceManager.getName(), resourceManager.getID());
      }
    }
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
    this.fWorkspaceLocText.setEnabled(false);
    this.fWorkspaceLocText.addModifyListener(new WorkspaceLocModifyListener());
    
    final Composite browseComp = new Composite(composite, SWT.NONE);
    browseComp.setLayout(new GridLayout(1, false));
    browseComp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
    this.fRemoteBrowseBt = new Button(browseComp, SWT.PUSH);
    this.fRemoteBrowseBt.setText(LaunchMessages.CPWSP_RemoteBrowseBt);
    this.fRemoteBrowseBt.addSelectionListener(new RemoteButtonSelectionListener());
    this.fRemoteBrowseBt.setEnabled(false);
  }
  
  private void updateDisablingPart() {
    final int selectionIndex = this.fResManagerCombo.getSelectionIndex();
    this.fWorkspaceLocText.setEnabled(selectionIndex != -1);
    this.fRemoteBrowseBt.setEnabled(selectionIndex != -1);
  }
  
  private void updateMessage() {
    if (isPageComplete()) {
      setPageComplete(false);
    }
    final int selectionIndex = this.fResManagerCombo.getSelectionIndex();
    if (selectionIndex == -1) {
      setMessage(LaunchMessages.CPWSP_SelectResMsg);
    } else {
      setMessage(LaunchMessages.CPWSP_SelectWorkspaceMsg);
    }
    setPageComplete(this.fWorkspaceLocText.getText().trim().length() > 0);
  }
  
  // --- Private classes
   
  private final class RemoteButtonSelectionListener implements SelectionListener {

    // --- Interface methods implementation
    
    public void widgetDefaultSelected(final SelectionEvent event) {
    }

    public void widgetSelected(final SelectionEvent event) {
      final IModelManager modelManager = PTPCorePlugin.getDefault().getModelManager();
      final IResourceManager[] resourceManagers = modelManager.getUniverse().getResourceManagers();
      final IResourceManager resMgr = resourceManagers[CppProjectWizardSecondPage.this.fResManagerCombo.getSelectionIndex()];
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
            final String path = fileMgr.browseDirectory(getShell(), LaunchMessages.CPWSP_RemoteBrowseDescription, "/", IRemoteUIConstants.NONE); //$NON-NLS-1$
            if (path != null) {
              CppProjectWizardSecondPage.this.fWorkspaceLocText.setText(path);
            }
          }
        }
      }
      
      updateMessage();
    }
    
  }
  
  private final class ResourceManagerSelectionListener implements SelectionListener {

    // --- Interface methods implementation
    
    public void widgetDefaultSelected(final SelectionEvent event) {
    }

    public void widgetSelected(final SelectionEvent event) {
      updateDisablingPart();
      updateMessage();
    }
    
  }
  
  private final class WorkspaceLocModifyListener implements ModifyListener {

    // --- Interface methods implementation
    
    public void modifyText(final ModifyEvent event) {
      updateMessage();
    }
    
  }
  
  // --- Fields
  
  private Combo fResManagerCombo;
    
  private Button fRemoteBrowseBt;
    
  private Text fWorkspaceLocText;

}
