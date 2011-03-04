/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors;

import static x10dt.ui.launch.core.utils.PTPConstants.LOAD_LEVELER_SERVICE_PROVIDER_ID;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.imp.utils.Pair;
import org.eclipse.ptp.rm.ibm.ll.core.rmsystem.IIBMLLResourceManagerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import x10dt.ui.launch.core.platform_conf.EValidationStatus;
import x10dt.ui.launch.core.utils.KeyboardUtils;
import x10dt.ui.launch.core.utils.PTPConstants;
import x10dt.ui.launch.core.utils.SWTFormUtils;
import x10dt.ui.launch.cpp.LaunchMessages;
import x10dt.ui.launch.cpp.platform_conf.CLoadLevelerProxyMsgs;
import x10dt.ui.launch.cpp.platform_conf.EClusterMode;
import x10dt.ui.launch.cpp.platform_conf.ELLTemplateOpt;
import x10dt.ui.launch.cpp.platform_conf.ILoadLevelerConf;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConfWorkCopy;


final class LoadLevelerTypeConfigPart extends AbstractCITypeConfigurationPart implements ICITypeConfigurationPart {
  
  LoadLevelerTypeConfigPart(final IIBMLLResourceManagerConfiguration rmConf) {
    this.fRMConf = rmConf;
  }
             
  // --- Interface methods implementation
  
  public void connectionChanged(final boolean isLocal, final String remoteConnectionName, 
                                final EValidationStatus validationStatus) {
    this.fBrowseBtEnabled = isLocal || (validationStatus == EValidationStatus.VALID);
    for (final Button button : this.fBrowseBts) {
      button.setEnabled(this.fBrowseBtEnabled);
    }
  }

  public void create(final IManagedForm managedForm, final FormToolkit toolkit, final Composite parent,
                     final AbstractCommonSectionFormPart formPart) {
    final Pair<Text, Button> pair = SWTFormUtils.createLabelTextButton(parent, LaunchMessages.PETCP_ProxyExecPath, 
                                                                       LaunchMessages.XPCP_BrowseBt, toolkit, 
                                                                       getCtrlsContainer(), 3);
    final Text proxyExecPathText = pair.first;
    this.fProxyExecPathText = proxyExecPathText;
    this.fBrowseBts.add(pair.second);
    pair.second.addSelectionListener(formPart.new FileDialogSelectionListener(proxyExecPathText));
    
    final Button launchServerManuallyBt = toolkit.createButton(parent, LaunchMessages.PETCP_LaunchServerManually, SWT.CHECK);
    addControl(launchServerManuallyBt);
    
    final ExpandableComposite ec = toolkit.createExpandableComposite(parent, ExpandableComposite.TREE_NODE|
                                                                     ExpandableComposite.CLIENT_INDENT);
    ec.setText(LaunchMessages.PETCP_AdvancedSettings);
    ec.setFont(parent.getFont());
    ec.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    ec.addExpansionListener(new ExpansionAdapter() {
      public void expansionStateChanged(final ExpansionEvent event) {
        managedForm.reflow(true);
      }
    });
    addControl(ec);
    
    final Composite ecCompo = toolkit.createComposite(ec);
    ecCompo.setFont(ec.getFont());
    ecCompo.setLayout(new TableWrapLayout());
    ecCompo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    addControl(ecCompo);
    
    final Combo multiClusterCombo = SWTFormUtils.createLabelAndCombo(ecCompo, LaunchMessages.PETCP_MultiClusterMode, toolkit,
                                                                     getCtrlsContainer());
    multiClusterCombo.add(LaunchMessages.PETCP_DefaultCluster);
    multiClusterCombo.setData(LaunchMessages.PETCP_DefaultCluster, EClusterMode.DEFAULT);
    multiClusterCombo.add(LaunchMessages.PETCP_LocalCluster);
    multiClusterCombo.setData(LaunchMessages.PETCP_LocalCluster, EClusterMode.LOCAL);
    multiClusterCombo.add(LaunchMessages.PETCP_MultiCluster);
    multiClusterCombo.setData(LaunchMessages.PETCP_MultiCluster, EClusterMode.MULTI_CLUSTER);
    final Spinner nodePollingMinSp = SWTFormUtils.createLabelAndSpinner(ecCompo, LaunchMessages.PETCP_NodePollingMin, toolkit, 
                                                                        getCtrlsContainer());
    final Spinner nodePollingMaxSp = SWTFormUtils.createLabelAndSpinner(ecCompo, LaunchMessages.PETCP_NodePollingMax, toolkit, 
                                                                        getCtrlsContainer());
    final Spinner jobPollingSp = SWTFormUtils.createLabelAndSpinner(ecCompo, LaunchMessages.PETCP_JobPolling, toolkit, 
                                                                    getCtrlsContainer());
    final Pair<Text, Button> pair2 = SWTFormUtils.createLabelTextButton(ecCompo, LaunchMessages.PETCP_AltLibraryPath, 
                                                                        LaunchMessages.XPCP_BrowseBt, toolkit, 
                                                                        getCtrlsContainer(), 3);
    final Text alternateLibPathText = pair2.first;
    this.fBrowseBts.add(pair2.second);
    pair2.second.addSelectionListener(formPart.new FileDialogSelectionListener(alternateLibPathText));

    final Group proxyMsgGrp = new Group(ecCompo, SWT.NONE);
    proxyMsgGrp.setFont(ecCompo.getFont());
    proxyMsgGrp.setLayout(new TableWrapLayout());
    proxyMsgGrp.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    proxyMsgGrp.setText(LaunchMessages.LLTCP_ProxyMsgOpts);
    addControl(proxyMsgGrp);
    
    final Composite proxyMsgCompo = toolkit.createComposite(proxyMsgGrp);
    proxyMsgCompo.setFont(proxyMsgGrp.getFont());
    final TableWrapLayout proxyMsgLayout = new TableWrapLayout();
    proxyMsgLayout.numColumns = 6;
    proxyMsgCompo.setLayout(proxyMsgLayout);
    proxyMsgCompo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    addControl(proxyMsgCompo);
    
    final Button traceBt = toolkit.createButton(proxyMsgCompo, LaunchMessages.LLTCP_TraceBt, SWT.CHECK);
    addControl(traceBt);
    final Button infoBt = toolkit.createButton(proxyMsgCompo, LaunchMessages.LLTCP_InfoBt, SWT.CHECK);
    addControl(infoBt);
    final Button warningBt = toolkit.createButton(proxyMsgCompo, LaunchMessages.LLTCP_WarningBt, SWT.CHECK);
    addControl(warningBt);
    final Button errorBt = toolkit.createButton(proxyMsgCompo, LaunchMessages.LLTCP_ErrorBt, SWT.CHECK);
    addControl(errorBt);
    final Button fatalBt = toolkit.createButton(proxyMsgCompo, LaunchMessages.LLTCP_FatalBt, SWT.CHECK);
    addControl(fatalBt);
    final Button argsBt = toolkit.createButton(proxyMsgCompo, LaunchMessages.LLTCP_ArgsBt, SWT.CHECK);
    addControl(argsBt);
    
    final Group templateGrp = new Group(ecCompo, SWT.NONE);
    templateGrp.setFont(ecCompo.getFont());
    templateGrp.setLayout(new TableWrapLayout());
    templateGrp.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    templateGrp.setText(LaunchMessages.LLTCP_TemplateFileGroup);
    templateGrp.setToolTipText(LaunchMessages.LLTCP_TemplateFileTooltipDescription);
    addControl(templateGrp);
    
    final Pair<Text, Button> pair3 = SWTFormUtils.createLabelTextButton(templateGrp, LaunchMessages.RMCP_LocationLabel, 
                                                                        LaunchMessages.XPCP_BrowseBt, toolkit, 
                                                                        getCtrlsContainer(), 3);
    this.fBrowseBts.add(pair3.second);
    
    final Combo templateOptCombo = SWTFormUtils.createLabelAndCombo(templateGrp, LaunchMessages.LLTCP_Option, toolkit);
    templateOptCombo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    templateOptCombo.add(LaunchMessages.LLTCP_NeverWriteOpt);
    templateOptCombo.setData(LaunchMessages.LLTCP_NeverWriteOpt, ELLTemplateOpt.ENeverWrite);
    templateOptCombo.add(LaunchMessages.LLTCP_AlwaysWriteOpt);
    templateOptCombo.setData(LaunchMessages.LLTCP_AlwaysWriteOpt, ELLTemplateOpt.EAlwaysWrite);
    addControl(templateOptCombo);
    
    final Button suspendProxyAtStartupBt = toolkit.createButton(ecCompo, LaunchMessages.LLTCP_DebugProxy, SWT.CHECK);
    addControl(suspendProxyAtStartupBt);
    
    ec.setClient(ecCompo);
    
    final IX10PlatformConfWorkCopy x10PlatformConf = formPart.getPlatformConf();
    initConfiguration(x10PlatformConf, (ILoadLevelerConf) x10PlatformConf.getCommunicationInterfaceConf());
    initializeControls(formPart, (ILoadLevelerConf) x10PlatformConf.getCommunicationInterfaceConf(), proxyExecPathText, 
                       launchServerManuallyBt, multiClusterCombo, nodePollingMinSp, nodePollingMaxSp, 
                       jobPollingSp, alternateLibPathText, pair.second, traceBt, infoBt, warningBt, errorBt, fatalBt, argsBt,
                       pair3.first, templateOptCombo, suspendProxyAtStartupBt);
    
    addListeners(x10PlatformConf, managedForm, formPart, (ILoadLevelerConf) x10PlatformConf.getCommunicationInterfaceConf(),
                 proxyExecPathText, launchServerManuallyBt, multiClusterCombo, nodePollingMinSp, 
                 nodePollingMaxSp, jobPollingSp, alternateLibPathText, traceBt, infoBt, warningBt, errorBt, fatalBt, argsBt, 
                 pair3.first, templateOptCombo, suspendProxyAtStartupBt);
  }

  public String getServiceProviderId() {
    return PTPConstants.LOAD_LEVELER_SERVICE_PROVIDER_ID;
  }

  public boolean hasCompleteInfo() {
    return this.fProxyExecPathText.getText().trim().length() > 0;
  }
  
  // --- Overridden methods
  
  public void dispose(final IMessageManager ... messageManagers) {
    super.dispose(messageManagers);
    this.fBrowseBts.clear();
  }
  
  // --- Private code
  
  private void addListeners(final IX10PlatformConfWorkCopy x10PlatformConf, final IManagedForm managedForm, 
                            final AbstractCommonSectionFormPart formPart, final ILoadLevelerConf ciConf,
                            final Text proxyExecPathText, final Button launchServerManuallyBt, final Combo multiClusterCombo, final Spinner nodePollingMinSp, final Spinner nodePollingMaxSp, 
                            final Spinner jobPollingSp, final Text alternateLibPathText, final Button traceBt, 
                            final Button infoBt, final Button warningBt, final Button errorBt, final Button fatalBt, 
                            final Button argsBt, final Text templateFilePathText, final Combo templateOptCombo, 
                            final Button suspendProxyAtStartupBt) {
    proxyExecPathText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        x10PlatformConf.setProxyServerPath(LOAD_LEVELER_SERVICE_PROVIDER_ID, proxyExecPathText.getText().trim());
        formPart.handleEmptyTextValidation(proxyExecPathText, LaunchMessages.PETCP_ProxyExecPath);
        formPart.updateDirtyState(managedForm);
        formPart.setPartCompleteFlag(hasCompleteInfo());
      }
      
    });
    launchServerManuallyBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        x10PlatformConf.setLaunchProxyManuallyFlag(LOAD_LEVELER_SERVICE_PROVIDER_ID, 
                                                   launchServerManuallyBt.getSelection());
        formPart.updateDirtyState(managedForm);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    multiClusterCombo.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        final String name = multiClusterCombo.getItem(multiClusterCombo.getSelectionIndex());
        final EClusterMode mode = (EClusterMode) multiClusterCombo.getData(name);
        x10PlatformConf.setClusterMode(LOAD_LEVELER_SERVICE_PROVIDER_ID, mode);
        formPart.updateDirtyState(managedForm);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    nodePollingMinSp.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        x10PlatformConf.setNodeMinPolling(LOAD_LEVELER_SERVICE_PROVIDER_ID, nodePollingMinSp.getSelection());
        formPart.updateDirtyState(managedForm);
      }
      
    });
    nodePollingMaxSp.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        x10PlatformConf.setNodeMaxPolling(LOAD_LEVELER_SERVICE_PROVIDER_ID, nodePollingMaxSp.getSelection());
        formPart.updateDirtyState(managedForm);
      }
      
    });
    jobPollingSp.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        x10PlatformConf.setJobPolling(LOAD_LEVELER_SERVICE_PROVIDER_ID, jobPollingSp.getSelection());
        formPart.updateDirtyState(managedForm);
      }
      
    });
    alternateLibPathText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        x10PlatformConf.setAlternateLibraryPath(LOAD_LEVELER_SERVICE_PROVIDER_ID, 
                                                alternateLibPathText.getText().trim());
        formPart.handleEmptyTextValidation(alternateLibPathText, LaunchMessages.PETCP_AltLibraryPath);
        formPart.updateDirtyState(managedForm);
        formPart.setPartCompleteFlag(hasCompleteInfo());
      }
      
    });
    traceBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        x10PlatformConf.setProxyMessageOptions(ciConf.getProxyMessageOptions() ^ CLoadLevelerProxyMsgs.TRACE);
        formPart.updateDirtyState(managedForm);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    infoBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        x10PlatformConf.setProxyMessageOptions(ciConf.getProxyMessageOptions() ^ CLoadLevelerProxyMsgs.INFO);
        formPart.updateDirtyState(managedForm);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    warningBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        x10PlatformConf.setProxyMessageOptions(ciConf.getProxyMessageOptions() ^ CLoadLevelerProxyMsgs.WARNING);
        formPart.updateDirtyState(managedForm);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    errorBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        x10PlatformConf.setProxyMessageOptions(ciConf.getProxyMessageOptions() ^ CLoadLevelerProxyMsgs.ERROR);
        formPart.updateDirtyState(managedForm);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    fatalBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        x10PlatformConf.setProxyMessageOptions(ciConf.getProxyMessageOptions() ^ CLoadLevelerProxyMsgs.FATAL);
        formPart.updateDirtyState(managedForm);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    argsBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        x10PlatformConf.setProxyMessageOptions(ciConf.getProxyMessageOptions() ^ CLoadLevelerProxyMsgs.ARGS);
        formPart.updateDirtyState(managedForm);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    templateFilePathText.addModifyListener(new ModifyListener() {
      public void modifyText(final ModifyEvent event) {
        formPart.handleEmptyTextValidation(templateFilePathText, LaunchMessages.LLTCP_TemplateFileGroup);
        x10PlatformConf.setTemplateFilePath(templateFilePathText.getText().trim());
        formPart.updateDirtyState(managedForm);
      }
    });
    templateOptCombo.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        final String name = templateOptCombo.getItem(templateOptCombo.getSelectionIndex());
        final ELLTemplateOpt templateOpt = (ELLTemplateOpt) templateOptCombo.getData(name);
        x10PlatformConf.setTemplateOption(templateOpt);
        formPart.updateDirtyState(managedForm);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    suspendProxyAtStartupBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        x10PlatformConf.setSuspendProxyAtStartupFlag(LOAD_LEVELER_SERVICE_PROVIDER_ID, suspendProxyAtStartupBt.getSelection());
        formPart.updateDirtyState(managedForm);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
  }
  
  private void initConfiguration(final IX10PlatformConfWorkCopy platformConf, final ILoadLevelerConf ciConf) {
    if (ciConf.getProxyServerPath().length() == 0) {
      platformConf.setProxyServerPath(LOAD_LEVELER_SERVICE_PROVIDER_ID, this.fRMConf.getProxyServerPath());
    }
    if (ciConf.getNodePollingMin() == 0) {
      platformConf.setNodeMinPolling(LOAD_LEVELER_SERVICE_PROVIDER_ID, this.fRMConf.getMinNodePolling());
    }
    if (ciConf.getNodePollingMax() == 0) {
      platformConf.setNodeMaxPolling(LOAD_LEVELER_SERVICE_PROVIDER_ID, this.fRMConf.getMaxNodePolling());
    }
    if (ciConf.getJobPolling() == 0) {
      platformConf.setJobPolling(LOAD_LEVELER_SERVICE_PROVIDER_ID, this.fRMConf.getJobPolling());
    }
  }
  
  private void initializeControls(final AbstractCommonSectionFormPart formPart, final ILoadLevelerConf ciConf, 
                                  final Text proxyExecPathText, final Button launchServerManuallyBt, 
                                  final Combo multiClusterCombo, final Spinner nodePollingMinSp, 
                                  final Spinner nodePollingMaxSp, final Spinner jobPollingSp, final Text alternateLibPathText,
                                  final Button proxyServerBrowseBt, final Button traceBt, final Button infoBt, 
                                  final Button warningBt, final Button errorBt, final Button fatalBt, final Button argsBt,
                                  final Text templateFilePathText, final Combo templateOptCombo, 
                                  final Button suspendProxyAtStartupBt) {
    proxyExecPathText.setText(ciConf.getProxyServerPath());
    formPart.handleEmptyTextValidation(proxyExecPathText, LaunchMessages.PETCP_ProxyExecPath);
    proxyServerBrowseBt.setEnabled(this.fBrowseBtEnabled);
    launchServerManuallyBt.setSelection(ciConf.shouldLaunchProxyManually());
 
    
    final int clusterIndex;
    switch (ciConf.getClusterMode()) {
      case LOCAL:
        clusterIndex = 1;
        break;
      case MULTI_CLUSTER:
        clusterIndex = 2;
        break;
      default:
        clusterIndex = 0;
    }
    multiClusterCombo.select(clusterIndex);
    nodePollingMinSp.setSelection(ciConf.getNodePollingMin());
    nodePollingMaxSp.setSelection(ciConf.getNodePollingMax());
    jobPollingSp.setSelection(ciConf.getJobPolling());
    alternateLibPathText.setText(ciConf.getAlternateLibraryPath());
    
    traceBt.setSelection((ciConf.getProxyMessageOptions() & CLoadLevelerProxyMsgs.TRACE) != 0);
    infoBt.setSelection((ciConf.getProxyMessageOptions() & CLoadLevelerProxyMsgs.INFO) != 0);
    warningBt.setSelection((ciConf.getProxyMessageOptions() & CLoadLevelerProxyMsgs.WARNING) != 0);
    errorBt.setSelection((ciConf.getProxyMessageOptions() & CLoadLevelerProxyMsgs.ERROR) != 0);
    fatalBt.setSelection((ciConf.getProxyMessageOptions() & CLoadLevelerProxyMsgs.FATAL) != 0);
    argsBt.setSelection((ciConf.getProxyMessageOptions() & CLoadLevelerProxyMsgs.ARGS) != 0);
    templateFilePathText.setText(ciConf.getTemplateFilePath());
    int templateOptIndex = -1;
    switch (ciConf.getTemplateOption()) {
      case ENeverWrite:
        templateOptIndex = 0;
        break;
      case EAlwaysWrite:
        templateOptIndex = 1;
        break;
    }
    if (templateOptIndex != -1) {
      templateOptCombo.select(templateOptIndex);
    }
    suspendProxyAtStartupBt.setSelection(ciConf.shouldSuspendProxyAtStartup());
    
    KeyboardUtils.addDelayedActionOnControl(proxyExecPathText, new Runnable() {
      
      public void run() {
        formPart.getFormPage().getEditorSite().getShell().getDisplay().asyncExec(new Runnable() {
          
          public void run() {
            formPart.handlePathValidation(proxyExecPathText, LaunchMessages.PETCP_ProxyExecPath);
          }
          
        });
      }
      
    });
    KeyboardUtils.addDelayedActionOnControl(alternateLibPathText, new Runnable() {
      
      public void run() {
        formPart.getFormPage().getEditorSite().getShell().getDisplay().asyncExec(new Runnable() {
          
          public void run() {
            formPart.handlePathValidation(alternateLibPathText, LaunchMessages.PETCP_AltLibraryPath);
          }
          
        });
      }
      
    });
    KeyboardUtils.addDelayedActionOnControl(templateFilePathText, new Runnable() {
      
      public void run() {
        formPart.getFormPage().getEditorSite().getShell().getDisplay().asyncExec(new Runnable() {
          
          public void run() {
            formPart.handlePathValidation(templateFilePathText, LaunchMessages.LLTCP_TemplateFileGroup);
          }
          
        });
      }
      
    });
  }
  
  // --- Fields
  
  private final IIBMLLResourceManagerConfiguration fRMConf;
  
  private final Collection<Button> fBrowseBts = new ArrayList<Button>(2);

  private Text fProxyExecPathText;
  
  private boolean fBrowseBtEnabled;

}
