/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors;

import static x10dt.ui.launch.core.utils.PTPConstants.PARALLEL_ENVIRONMENT_SERVICE_PROVIDER_ID;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.imp.utils.Pair;
import org.eclipse.ptp.rm.ibm.pe.core.rmsystem.IPEResourceManagerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
import x10dt.ui.launch.cpp.platform_conf.ECIDebugLevel;
import x10dt.ui.launch.cpp.platform_conf.EClusterMode;
import x10dt.ui.launch.cpp.platform_conf.IIBMCommunicationInterfaceConf;
import x10dt.ui.launch.cpp.platform_conf.IParallelEnvironmentConf;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConfWorkCopy;


final class ParallelEnvironmentTypeConfigPart extends AbstractCITypeConfigurationPart implements ICITypeConfigurationPart {
  
  ParallelEnvironmentTypeConfigPart(final IPEResourceManagerConfiguration rmConf) {
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
    
    final Button useLoadLevelerBt = toolkit.createButton(ecCompo, LaunchMessages.PETCP_UseLoadLeveler, SWT.CHECK);
    addControl(useLoadLevelerBt);
    
    final Group llGroup = new Group(ecCompo, SWT.NONE);
    llGroup.setFont(ecCompo.getFont());
    llGroup.setLayout(new TableWrapLayout());
    llGroup.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    addControl(llGroup);
    
    final Collection<Control> llGroupControls = new ArrayList<Control>();
    
    final Combo multiClusterCombo = SWTFormUtils.createLabelAndCombo(llGroup, LaunchMessages.PETCP_MultiClusterMode, toolkit,
                                                                     llGroupControls);
    multiClusterCombo.add(LaunchMessages.PETCP_DefaultCluster);
    multiClusterCombo.setData(LaunchMessages.PETCP_DefaultCluster, EClusterMode.DEFAULT);
    multiClusterCombo.add(LaunchMessages.PETCP_LocalCluster);
    multiClusterCombo.setData(LaunchMessages.PETCP_LocalCluster, EClusterMode.LOCAL);
    multiClusterCombo.add(LaunchMessages.PETCP_MultiCluster);
    multiClusterCombo.setData(LaunchMessages.PETCP_MultiCluster, EClusterMode.MULTI_CLUSTER);
    final Spinner nodePollingMinSp = SWTFormUtils.createLabelAndSpinner(llGroup, LaunchMessages.PETCP_NodePollingMin, toolkit, 
                                                                        llGroupControls);
    final Spinner nodePollingMaxSp = SWTFormUtils.createLabelAndSpinner(llGroup, LaunchMessages.PETCP_NodePollingMax, toolkit, 
                                                                        llGroupControls);
    final Spinner jobPollingSp = SWTFormUtils.createLabelAndSpinner(llGroup, LaunchMessages.PETCP_JobPolling, toolkit, 
                                                                    llGroupControls);
    final Pair<Text, Button> pair2 = SWTFormUtils.createLabelTextButton(llGroup, LaunchMessages.PETCP_AltLibraryPath, 
                                                                        LaunchMessages.XPCP_BrowseBt, toolkit, 
                                                                        llGroupControls, 3);
    final Text alternateLibPathText = pair2.first;
    this.fBrowseBts.add(pair2.second);
    pair2.second.addSelectionListener(formPart.new FileDialogSelectionListener(alternateLibPathText));
    
    final Button runAfterProxyBt = toolkit.createButton(ecCompo, LaunchMessages.PETCP_RunAfterProxy, SWT.CHECK);
    addControl(runAfterProxyBt);
    final Combo traceOptCombo = SWTFormUtils.createLabelAndCombo(ecCompo, LaunchMessages.PETCP_TraceOpts, toolkit,
                                                                 getCtrlsContainer());
    traceOptCombo.add(LaunchMessages.PETCP_NoTraceOpt);
    traceOptCombo.setData(LaunchMessages.PETCP_NoTraceOpt, ECIDebugLevel.NONE);
    traceOptCombo.add(LaunchMessages.PETCP_FunctionTraceOpt);
    traceOptCombo.setData(LaunchMessages.PETCP_FunctionTraceOpt, ECIDebugLevel.FUNCTION);
    traceOptCombo.add(LaunchMessages.PETCP_DetailedTraceOpt);
    traceOptCombo.setData(LaunchMessages.PETCP_DetailedTraceOpt, ECIDebugLevel.DETAILED);
    final Button suspendProxyBt = toolkit.createButton(ecCompo, LaunchMessages.LLTCP_DebugProxy, SWT.CHECK);
    addControl(suspendProxyBt);
    
    ec.setClient(ecCompo);
    
    addControls(llGroupControls);
    
    final IX10PlatformConfWorkCopy x10PlatformConf = formPart.getPlatformConf();
    initConfiguration(x10PlatformConf, (IParallelEnvironmentConf) x10PlatformConf.getCommunicationInterfaceConf());
    initializeControls(formPart, (IParallelEnvironmentConf) x10PlatformConf.getCommunicationInterfaceConf(), 
                       proxyExecPathText, launchServerManuallyBt, useLoadLevelerBt, multiClusterCombo, 
                       nodePollingMinSp, nodePollingMaxSp, jobPollingSp, alternateLibPathText, runAfterProxyBt, traceOptCombo,
                       suspendProxyBt, pair.second, pair2.second, llGroupControls);
    
    addListeners(x10PlatformConf, managedForm, formPart, proxyExecPathText, launchServerManuallyBt, 
                 useLoadLevelerBt, multiClusterCombo, nodePollingMinSp, nodePollingMaxSp, jobPollingSp, alternateLibPathText,
                 runAfterProxyBt, traceOptCombo, suspendProxyBt, llGroupControls);
  }

  public String getServiceProviderId() {
    return PTPConstants.PARALLEL_ENVIRONMENT_SERVICE_PROVIDER_ID;
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
                            final AbstractCommonSectionFormPart formPart, final Text proxyExecPathText, 
                            final Button launchServerManuallyBt, final Button useLoadLevelerBt, final Combo multiClusterCombo,
                            final Spinner nodePollingMinSp, final Spinner nodePollingMaxSp, final Spinner jobPollingSp, 
                            final Text alternateLibPathText, final Button runAfterProxyBt, final Combo traceOptCombo, 
                            final Button suspendProxyBt, final Collection<Control> llGroupControls) {
    proxyExecPathText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        x10PlatformConf.setProxyServerPath(PARALLEL_ENVIRONMENT_SERVICE_PROVIDER_ID, proxyExecPathText.getText().trim());
        formPart.handleEmptyTextValidation(proxyExecPathText, LaunchMessages.PETCP_ProxyExecPath);
        formPart.updateDirtyState(managedForm);
        formPart.setPartCompleteFlag(hasCompleteInfo());
      }
      
    });
    launchServerManuallyBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        x10PlatformConf.setLaunchProxyManuallyFlag(PARALLEL_ENVIRONMENT_SERVICE_PROVIDER_ID, 
                                                   launchServerManuallyBt.getSelection());
        formPart.updateDirtyState(managedForm);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    useLoadLevelerBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        x10PlatformConf.setUseLoadLeveler(useLoadLevelerBt.getSelection());
        
        for (final Control control : llGroupControls) {
          control.setEnabled(useLoadLevelerBt.getSelection());
        }
        
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
        x10PlatformConf.setClusterMode(PARALLEL_ENVIRONMENT_SERVICE_PROVIDER_ID, mode);
        formPart.updateDirtyState(managedForm);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    nodePollingMinSp.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        x10PlatformConf.setNodeMinPolling(PARALLEL_ENVIRONMENT_SERVICE_PROVIDER_ID, nodePollingMinSp.getSelection());
        formPart.updateDirtyState(managedForm);
      }
      
    });
    nodePollingMaxSp.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        x10PlatformConf.setNodeMaxPolling(PARALLEL_ENVIRONMENT_SERVICE_PROVIDER_ID, nodePollingMaxSp.getSelection());
        formPart.updateDirtyState(managedForm);
      }
      
    });
    jobPollingSp.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        x10PlatformConf.setJobPolling(PARALLEL_ENVIRONMENT_SERVICE_PROVIDER_ID, jobPollingSp.getSelection());
        formPart.updateDirtyState(managedForm);
      }
      
    });
    alternateLibPathText.addModifyListener(new ModifyListener() {
      public void modifyText(final ModifyEvent event) {
        formPart.handleEmptyTextValidation(alternateLibPathText, LaunchMessages.PETCP_AltLibraryPath);
        x10PlatformConf.setAlternateLibraryPath(PARALLEL_ENVIRONMENT_SERVICE_PROVIDER_ID, 
                                                alternateLibPathText.getText().trim());
        formPart.updateDirtyState(managedForm);
      }
    });
    runAfterProxyBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        x10PlatformConf.setRunMiniProxyFlag(runAfterProxyBt.getSelection());
        formPart.updateDirtyState(managedForm);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    traceOptCombo.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        final String name = traceOptCombo.getItem(traceOptCombo.getSelectionIndex());
        final ECIDebugLevel debugLevel = (ECIDebugLevel) traceOptCombo.getData(name);
        x10PlatformConf.setDebuggingLevel(debugLevel);
        formPart.updateDirtyState(managedForm);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    suspendProxyBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        x10PlatformConf.setSuspendProxyAtStartupFlag(PARALLEL_ENVIRONMENT_SERVICE_PROVIDER_ID, suspendProxyBt.getSelection());
        formPart.updateDirtyState(managedForm);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
  }
  
  private void initConfiguration(final IX10PlatformConfWorkCopy platformConf, final IIBMCommunicationInterfaceConf ciConf) {
    if (ciConf.getProxyServerPath().length() == 0) {
      platformConf.setProxyServerPath(PARALLEL_ENVIRONMENT_SERVICE_PROVIDER_ID, this.fRMConf.getProxyServerPath());
    }
    if (ciConf.getNodePollingMin() == 0) {
      platformConf.setNodeMinPolling(PARALLEL_ENVIRONMENT_SERVICE_PROVIDER_ID, 
                                     Integer.parseInt(this.fRMConf.getNodeMinPollInterval()));
    }
    if (ciConf.getNodePollingMax() == 0) {
      platformConf.setNodeMaxPolling(PARALLEL_ENVIRONMENT_SERVICE_PROVIDER_ID, 
                                     Integer.parseInt(this.fRMConf.getNodeMaxPollInterval()));
    }
    if (ciConf.getJobPolling() == 0) {
      platformConf.setJobPolling(PARALLEL_ENVIRONMENT_SERVICE_PROVIDER_ID, 
                                 Integer.parseInt(this.fRMConf.getJobPollInterval()));
    }
  }
  
  private void initializeControls(final AbstractCommonSectionFormPart formPart, final IParallelEnvironmentConf ciConf, 
                                  final Text proxyExecPathText, final Button launchServerManuallyBt, 
                                  final Button useLoadLevelerBt, final Combo multiClusterCombo, final Spinner nodePollingMinSp, 
                                  final Spinner nodePollingMaxSp, final Spinner jobPollingSp, final Text alternateLibPathText,
                                  final Button runAfterProxyBt, final Combo traceOptCombo, final Button suspendProxyBt,
                                  final Button proxyServerBrowseBt, final Button libPathBrowseBt,
                                  final Collection<Control> llGroupControls) {
    proxyExecPathText.setText(ciConf.getProxyServerPath());
    formPart.handleEmptyTextValidation(proxyExecPathText, LaunchMessages.PETCP_ProxyExecPath);
    proxyServerBrowseBt.setEnabled(this.fBrowseBtEnabled);
    launchServerManuallyBt.setSelection(ciConf.shouldLaunchProxyManually());
    
    useLoadLevelerBt.setSelection(ciConf.shouldUseLoadLeveler());
    for (final Control control : llGroupControls) {
      control.setEnabled(ciConf.shouldUseLoadLeveler());
    }
    
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
    libPathBrowseBt.setEnabled(ciConf.shouldUseLoadLeveler() && this.fBrowseBtEnabled);
    runAfterProxyBt.setSelection(ciConf.shouldRunMiniProxy());
    final int traceLevelIndex;
    switch (ciConf.getDebuggingLevel()) {
      case FUNCTION:
        traceLevelIndex = 1;
        break;
      case DETAILED:
        traceLevelIndex = 2;
        break;
      default:
        traceLevelIndex = 0;
    }
    traceOptCombo.select(traceLevelIndex);
    suspendProxyBt.setSelection(ciConf.shouldSuspendProxyAtStartup());
    
    KeyboardUtils.addDelayedActionOnControl(alternateLibPathText, new Runnable() {
      
      public void run() {
        formPart.getFormPage().getEditorSite().getShell().getDisplay().asyncExec(new Runnable() {
          
          public void run() {
            formPart.handlePathValidation(alternateLibPathText, LaunchMessages.PETCP_AltLibraryPath);
          }
          
        });
      }
      
    });
  }
  
  // --- Fields
  
  private final IPEResourceManagerConfiguration fRMConf;
  
  private final Collection<Button> fBrowseBts = new ArrayList<Button>(2);
  
  private Text fProxyExecPathText;
  
  private boolean fBrowseBtEnabled;

}
