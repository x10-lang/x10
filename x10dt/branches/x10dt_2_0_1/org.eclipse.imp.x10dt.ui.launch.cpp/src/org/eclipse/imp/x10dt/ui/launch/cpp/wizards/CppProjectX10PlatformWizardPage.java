/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.wizards;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.x10dt.ui.launch.core.Constants;
import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.imp.x10dt.ui.launch.core.dialogs.DialogsFactory;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.EValidStatus;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.IX10PlatformConfiguration;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.X10PlatformsManager;
import org.eclipse.imp.x10dt.ui.launch.core.preferences.X10PlatformConfigurationPrefPage;
import org.eclipse.imp.x10dt.ui.launch.core.utils.PTPUtils;
import org.eclipse.imp.x10dt.ui.launch.cpp.CppLaunchCore;
import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.IModelManager;
import org.eclipse.ptp.core.PTPCorePlugin;
import org.eclipse.ptp.core.elements.IPUniverse;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.core.elements.attributes.ResourceManagerAttributes;
import org.eclipse.ptp.ui.wizards.RMServicesConfigurationWizard;
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
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.WorkbenchException;


final class CppProjectX10PlatformWizardPage extends WizardPage {
  
  CppProjectX10PlatformWizardPage(final CppProjectNameDefWizardPage firstPage) {
    super(LaunchMessages.CPWSP_WizardTitle, LaunchMessages.CPWSP_WizardName, null /* titleImage */);
    
    setPageComplete(false);
    setDescription(LaunchMessages.CPWSP_WizardDescription);
    
    this.fFirstPage = firstPage;
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
    
    final Group x10PlatformGroup = new Group(composite, SWT.NONE);
    x10PlatformGroup.setFont(composite.getFont());
    x10PlatformGroup.setLayout(new GridLayout(1, false));
    x10PlatformGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    x10PlatformGroup.setText(LaunchMessages.CPWSP_X10PlatformGroup);
    
    createX10Platform(x10PlatformGroup);
    
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
    if (this.fTargetWorkspaceDir != null) {
      project.setPersistentProperty(Constants.WORKSPACE_DIR, this.fTargetWorkspaceDir);
    }
    final String platformConfName = this.fX10PlatformCombo.getItem(this.fX10PlatformCombo.getSelectionIndex());
    project.setPersistentProperty(Constants.X10_PLATFORM_CONF, platformConfName);
  }
  
  // --- Private code
  
  private void createResourceManager(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    final IModelManager modelManager = PTPCorePlugin.getDefault().getModelManager();
    final IPUniverse universe = modelManager.getUniverse();
    final Collection<IResourceManager> startedResManagerList = new ArrayList<IResourceManager>();
    final Collection<IResourceManager> stoppedResManagerList = new ArrayList<IResourceManager>();
    for (final IResourceManager resourceManager : universe.getResourceManagers()) {
      if (resourceManager.getState() == ResourceManagerAttributes.State.STARTED) {
        startedResManagerList.add(resourceManager);
      } else {
        stoppedResManagerList.add(resourceManager);
      }
    }
    final Link link = new Link(parent, SWT.NONE);
    link.setFont(composite.getFont());
    link.setData(new GridData(SWT.FILL, SWT.NONE, true, false));
    if (startedResManagerList.isEmpty()) {
      if (stoppedResManagerList.isEmpty()) {
        link.setText(NLS.bind(LaunchMessages.CPWSP_MustDefineRMMsg, LaunchMessages.CPWSP_ResourceManager));
      } else {
        link.setText(NLS.bind(LaunchMessages.CPWSP_MustDefineOrStartMsg, LaunchMessages.CPWSP_ResourceManager, 
                              LaunchMessages.CPWSP_StartExistingRMMsg));
      }
    } else {
      link.setText(NLS.bind(LaunchMessages.CPWSP_AddOrStartMsg, LaunchMessages.CPWSP_ResourceManager, 
                            LaunchMessages.CPWSP_StartExistingRMMsg));
    }
    link.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        if (event.text.equals(LaunchMessages.CPWSP_ResourceManager)) {
          final RMServicesConfigurationWizard wizard = new RMServicesConfigurationWizard();
          final WizardDialog dialog = new WizardDialog(getShell(), wizard);
          dialog.open();
          
          final Collection<String> failedRMs = new ArrayList<String>();
          for (final IResourceManager rmManager : universe.getResourceManagers()) {
            if (rmManager.getState() != ResourceManagerAttributes.State.STARTED) {
              try {
                rmManager.startUp(new NullProgressMonitor());
              } catch (CoreException except) {
                failedRMs.add(rmManager.getName());
              }
            }
          }
          if (! failedRMs.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            sb.append('[');
            int i = 0;
            for (final String failedRM : failedRMs) {
              if (i == 0) {
                i = 1;
              } else {
                sb.append(',');
              }
              sb.append(failedRM);
            }
            sb.append(']');
            setMessage(NLS.bind(LaunchMessages.CPWSP_RMStartFailure, sb.toString()), IMessageProvider.WARNING);
          }
        } else {
          DialogsFactory.openResourceManagerStartDialog(getShell(), stoppedResManagerList);
        }
        
        CppProjectX10PlatformWizardPage.this.fResManagerCombo.removeAll();
        for (final IResourceManager resManager : universe.getResourceManagers()) {
          if (resManager.getState() == ResourceManagerAttributes.State.STARTED) {
            CppProjectX10PlatformWizardPage.this.fResManagerCombo.add(resManager.getName());
            CppProjectX10PlatformWizardPage.this.fResManagerCombo.setData(resManager.getName(), resManager.getUniqueName());
          }
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    
    new Label(composite, SWT.NONE).setText(LaunchMessages.CPWSP_ResManagerLabel);
    
    this.fResManagerCombo = new Combo(composite, SWT.READ_ONLY);
    this.fResManagerCombo.setFont(composite.getFont());
    this.fResManagerCombo.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    for (final IResourceManager resourceManager : startedResManagerList) {
      this.fResManagerCombo.add(resourceManager.getName());
      this.fResManagerCombo.setData(resourceManager.getName(), resourceManager.getUniqueName());
    }
    if (startedResManagerList.size() == 1) {
      this.fResManagerCombo.select(0);
    }
    this.fResManagerCombo.addSelectionListener(new PageUpdateSelectionListener());
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
    this.fWorkspaceLocText.addModifyListener(new WorkspaceLocModifyListener());
    this.fWorkspaceLocText.setEnabled(false);
    
    final Composite browseComp = new Composite(composite, SWT.NONE);
    browseComp.setLayout(new GridLayout(1, false));
    browseComp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
    this.fBrowseBt = new Button(browseComp, SWT.PUSH);
    this.fBrowseBt.setText(LaunchMessages.CPWSP_BrowseBt);
    this.fBrowseBt.addSelectionListener(new BrowseBtSelectionListener());
    this.fBrowseBt.setEnabled(false);
  }
  
  private void createX10Platform(final Composite parent) {
    final Composite composite = new Composite(parent, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

    try {
      this.fX10Platforms = X10PlatformsManager.loadPlatformsConfiguration();
      final Link link = new Link(parent, SWT.NONE);
      link.setFont(composite.getFont());
      link.setData(new GridData(SWT.FILL, SWT.NONE, true, false));
      if (this.fX10Platforms.isEmpty()) {
        link.setText(NLS.bind(LaunchMessages.CPWSP_DefinePlatformFirstMsg, LaunchMessages.CPWSP_X10PlatformConf));
      } else {
        link.setText(NLS.bind(LaunchMessages.CPWSP_AddPlatformMsg, LaunchMessages.CPWSP_X10PlatformConf));
      }
      link.addSelectionListener(new SelectionListener() {
        
        public void widgetSelected(final SelectionEvent event) {
          final IPreferencePage page = new X10PlatformConfigurationPrefPage();
          final PreferenceManager manager = new PreferenceManager();
          final IPreferenceNode node = new PreferenceNode("1001", page); //$NON-NLS-1$
          manager.addToRoot(node);
          final PreferenceDialog dialog = new PreferenceDialog(getShell(), manager);
          dialog.create();
          dialog.setMessage(page.getTitle());
          if (dialog.open() == Window.OK) {
            try {
              CppProjectX10PlatformWizardPage.this.fX10Platforms = X10PlatformsManager.loadPlatformsConfiguration();
              
              CppProjectX10PlatformWizardPage.this.fX10PlatformCombo.removeAll();
              for (final IX10PlatformConfiguration x10Conf : CppProjectX10PlatformWizardPage.this.fX10Platforms.values()) {
                if (x10Conf.getValidationStatus() == EValidStatus.VALID) {
                  CppProjectX10PlatformWizardPage.this.fX10PlatformCombo.add(x10Conf.getName());
                }
              }
            } catch (Exception except) {
              setErrorMessage(Messages.XPCPP_LoadingErrorMsg);
              CppLaunchCore.log(IStatus.ERROR, org.eclipse.imp.x10dt.ui.launch.core.Messages.XPCPP_LoadingErrorLogMsg, except);
            }
          }
        }
        
        public void widgetDefaultSelected(final SelectionEvent event) {
          widgetSelected(event);
        }
        
      });
      new Label(composite, SWT.NONE).setText(LaunchMessages.CPWSP_X10Platforms);
    
      this.fX10PlatformCombo = new Combo(composite, SWT.READ_ONLY);
      this.fX10PlatformCombo.setFont(composite.getFont());
      this.fX10PlatformCombo.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      this.fX10PlatformCombo.addSelectionListener(new PageUpdateSelectionListener());
    
      int nbValidConfigs = 0;
      for (final IX10PlatformConfiguration x10Conf : this.fX10Platforms.values()) {
        if (x10Conf.getValidationStatus() == EValidStatus.VALID) {
          this.fX10PlatformCombo.add(x10Conf.getName());
          ++nbValidConfigs;
        }
      }
      if (nbValidConfigs == 1) {
        this.fX10PlatformCombo.select(0);
      }
    }  catch (WorkbenchException except) {
      setErrorMessage(Messages.XPCPP_LoadingErrorMsg);
      CppLaunchCore.log(IStatus.ERROR, org.eclipse.imp.x10dt.ui.launch.core.Messages.XPCPP_LoadingErrorLogMsg, except);
    } catch (IOException except) {
      setErrorMessage(Messages.XPCPP_LoadingErrorMsg);
      CppLaunchCore.log(IStatus.ERROR, org.eclipse.imp.x10dt.ui.launch.core.Messages.XPCPP_LoadingErrorLogMsg, except);
    }
  }
  
  private void updateDisablingPart() {
    final int selectionIndex = this.fResManagerCombo.getSelectionIndex();
    final boolean isLocal;
    final String resManagerId;
    if (selectionIndex == -1) {
      resManagerId = null;
      isLocal = true;
    } else {
      resManagerId = (String) this.fResManagerCombo.getData(this.fResManagerCombo.getItem(selectionIndex));
      final IModelManager modelManager = PTPCorePlugin.getDefault().getModelManager();
      isLocal = PTPUtils.isLocal(modelManager.getResourceManagerFromUniqueName(resManagerId));
    }
    
    this.fWorkspaceLocText.setEnabled(selectionIndex != -1 && ! isLocal);
    this.fBrowseBt.setEnabled(selectionIndex != -1 && ! isLocal);
    
    if (this.fWorkspaceLocText.isEnabled()) {
      this.fWorkspaceLocText.setText(PTPUtils.getTargetWorkspaceDirectory(resManagerId, this.fFirstPage.getProjectName()));
      this.fTargetWorkspaceDir = this.fWorkspaceLocText.getText().trim();
    }
  }
  
  private void updateMessage() {
    if (isPageComplete()) {
      setPageComplete(false);
    }
    if (this.fX10PlatformCombo.getSelectionIndex() == -1) {
      setMessage(LaunchMessages.CPWSP_X10PlatformSelectionMsg);
    } else {
      if (this.fResManagerCombo.getSelectionIndex() == -1) {
        setMessage(LaunchMessages.CPWSP_SelectResMsg);
      } else if (this.fWorkspaceLocText.isEnabled()) {
        setMessage(LaunchMessages.CPWSP_SelectWorkspaceMsg);
      }
    }
    boolean isComplete = (! this.fWorkspaceLocText.isEnabled() || this.fWorkspaceLocText.getText().trim().length() > 0) &&
                         this.fX10PlatformCombo.getSelectionIndex() > -1;
    setPageComplete(isComplete);
  }
  
  // --- Private classes
   
  private final class BrowseBtSelectionListener implements SelectionListener {

    // --- Interface methods implementation
    
    public void widgetDefaultSelected(final SelectionEvent event) {
    }

    public void widgetSelected(final SelectionEvent event) {
      final String path = PTPUtils.browseDirectory(getShell(), CppProjectX10PlatformWizardPage.this.fResManagerCombo, 
                                                   LaunchMessages.CPWSP_BrowseDescription, "/"); //$NON-NLS-1$
      if (path != null) {
        CppProjectX10PlatformWizardPage.this.fWorkspaceLocText.setText(path);
        updateMessage();
      }
    }
    
  }
  
  private final class PageUpdateSelectionListener implements SelectionListener {

    // --- Interface methods implementation
    
    public void widgetDefaultSelected(final SelectionEvent event) {
      widgetSelected(event);
    }

    public void widgetSelected(final SelectionEvent event) {
      updateMessage();
      updateDisablingPart();
    }
    
  }
  
  private final class WorkspaceLocModifyListener implements ModifyListener {

    // --- Interface methods implementation
    
    public void modifyText(final ModifyEvent event) {
      updateMessage();
    }
    
  }
  
  // --- Fields
  
  private final CppProjectNameDefWizardPage fFirstPage;
  
  private Combo fResManagerCombo;
  
  private Combo fX10PlatformCombo;
    
  private Button fBrowseBt;
    
  private Text fWorkspaceLocText;
  
  private String fTargetWorkspaceDir;
  
  private Map<String, IX10PlatformConfiguration> fX10Platforms;
  
}
