/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.ptp.rm.core.rmsystem.IToolRMConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import x10dt.ui.launch.core.platform_conf.EValidationStatus;
import x10dt.ui.launch.core.utils.KeyboardUtils;
import x10dt.ui.launch.core.utils.PTPConstants;
import x10dt.ui.launch.core.utils.SWTFormUtils;
import x10dt.ui.launch.cpp.LaunchMessages;
import x10dt.ui.launch.cpp.platform_conf.IMessagePassingInterfaceConf;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConfWorkCopy;


abstract class AbstractMPIBasedTypeConfigPart extends AbstractCITypeConfigurationPart implements ICITypeConfigurationPart {
  
  AbstractMPIBasedTypeConfigPart(final IToolRMConfiguration toolRMConf) {
    this.fToolRMConf = toolRMConf;
  }
  
  // --- Abstract methods definition
  
  protected abstract boolean isOpenMPIVersionAutotDetectOn();
  
  protected abstract void postCreationStep(final FormToolkit toolkit, final Composite parent, final IManagedForm managedForm,
                                           final IX10PlatformConfWorkCopy x10PlatformConf);

  protected abstract void preCreationStep(final FormToolkit toolkit, final Composite parent);
    
  // --- Interface methods implementation
  
  public final void connectionChanged(final boolean isLocal, final String remoteConnectionName, 
                                      final EValidationStatus validationStatus) {
    // Nothing to do.
  }

  public final void create(final IManagedForm managedForm, final FormToolkit toolkit, final Composite parent,
                           final AbstractCommonSectionFormPart formPart) {
    preCreationStep(toolkit, parent);
    
    final Group toolsCommandsGroup = new Group(parent, SWT.NONE);
    toolsCommandsGroup.setFont(parent.getFont());
    toolsCommandsGroup.setLayout(new TableWrapLayout());
    toolsCommandsGroup.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    toolsCommandsGroup.setText(LaunchMessages.RMCP_ToolsCommandsGroup);
    addControl(toolsCommandsGroup);
    
    this.fDefaultToolsCmdsBt = toolkit.createButton(toolsCommandsGroup, LaunchMessages.RMCP_UseDefaultToolsCmdsBt, SWT.CHECK);
    addControl(this.fDefaultToolsCmdsBt);
    
    final Collection<Control> dependentToolCmdsControls = new ArrayList<Control>();
    this.fLaunchCmdText = SWTFormUtils.createLabelAndText(toolsCommandsGroup, LaunchMessages.RMCP_LaunchLabel, toolkit, 
                                                          dependentToolCmdsControls, 3, SWT.NONE);
    
    this.fDebugCmdText = SWTFormUtils.createLabelAndText(toolsCommandsGroup, LaunchMessages.RMCP_DebugLabel, toolkit, 
                                                         dependentToolCmdsControls, 3, SWT.NONE);
    final Collection<Control> discoverCmdControls = new ArrayList<Control>();
    this.fDiscoverCmdText = SWTFormUtils.createLabelAndText(toolsCommandsGroup, LaunchMessages.RMCP_DiscoverLabel, 
                                                            toolkit, discoverCmdControls);
    
    final Composite monitorPeriodCompo = toolkit.createComposite(toolsCommandsGroup, SWT.NONE);
    monitorPeriodCompo.setFont(toolsCommandsGroup.getFont());
    final TableWrapLayout monitorLayout = new TableWrapLayout();
    monitorLayout.numColumns = 4;
    monitorPeriodCompo.setLayout(monitorLayout);
    monitorPeriodCompo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    addControl(monitorPeriodCompo);
    
    dependentToolCmdsControls.add(toolkit.createLabel(monitorPeriodCompo, LaunchMessages.RMCP_MonitorLabel));
    this.fMonitorCmdText = toolkit.createText(monitorPeriodCompo, null /* value */, SWT.WRAP);
    this.fMonitorCmdText.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    dependentToolCmdsControls.add(this.fMonitorCmdText);
    
    dependentToolCmdsControls.add(toolkit.createLabel(monitorPeriodCompo, LaunchMessages.RMCP_PeriodLabel));
    this.fPeriodicTimeSpinner = new Spinner(monitorPeriodCompo, SWT.SINGLE | SWT.BORDER);
    this.fPeriodicTimeSpinner.setMinimum(0);
    dependentToolCmdsControls.add(this.fPeriodicTimeSpinner);
    
    addControls(dependentToolCmdsControls);
    addControls(discoverCmdControls);
    
    final Group installLocationGroup = new Group(parent, SWT.NONE);
    installLocationGroup.setFont(parent.getFont());
    installLocationGroup.setLayout(new TableWrapLayout());
    installLocationGroup.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    installLocationGroup.setText(LaunchMessages.RMCP_InstallLocGroup);
    addControl(installLocationGroup);
    
    this.fDefaultInstallLocBt = toolkit.createButton(installLocationGroup, LaunchMessages.RMCP_UseDefaultLocBt, SWT.CHECK);
    addControl(this.fDefaultInstallLocBt);
    final Collection<Control> installControls = new ArrayList<Control>();
    this.fInstallLocText = SWTFormUtils.createLabelAndText(installLocationGroup, LaunchMessages.RMCP_LocationLabel, 
                                                           toolkit, installControls);
    
    addControls(installControls);
    
    final IX10PlatformConfWorkCopy x10PlatformConf = formPart.getPlatformConf();
    postCreationStep(toolkit, parent, managedForm, x10PlatformConf);
    
    initializeControls(x10PlatformConf, formPart, 
                       (IMessagePassingInterfaceConf) x10PlatformConf.getCommunicationInterfaceConf(),
                       discoverCmdControls, dependentToolCmdsControls, installControls);
    
    final String ciType = (this instanceof OpenMPITypeConfigPart) ? PTPConstants.OPEN_MPI_SERVICE_PROVIDER_ID :
                                                                    PTPConstants.MPICH2_SERVICE_PROVIDER_ID;
    addListeners(managedForm, x10PlatformConf, formPart, this.fDefaultToolsCmdsBt, this.fLaunchCmdText, this.fDebugCmdText, 
                 this.fDiscoverCmdText, this.fMonitorCmdText, this.fPeriodicTimeSpinner, this.fDefaultInstallLocBt,
                 this.fInstallLocText, discoverCmdControls, dependentToolCmdsControls, installControls, ciType);
  }

  public final boolean hasCompleteInfo() {
    if (this.fDefaultToolsCmdsBt == null) {
      // The part hasn't been initialized yet.
      return false;
    }
    if (! this.fDefaultToolsCmdsBt.getSelection()) {
      boolean shouldBeChecked = true;
      if (this instanceof OpenMPITypeConfigPart) {
        if (isOpenMPIVersionAutotDetectOn()) {
          shouldBeChecked = false;
        }
      }
      if (shouldBeChecked && this.fLaunchCmdText.getText().trim().length() == 0) {
        return false;
      }
      if (shouldBeChecked && this.fDebugCmdText.getText().trim().length() == 0) {
        return false;
      }
      if (this.fDiscoverCmdText.getText().trim().length() == 0) {
        return false;
      }
      if (shouldBeChecked && this.fMonitorCmdText.getText().trim().length() == 0) {
        return false;
      }
      if (shouldBeChecked && this.fPeriodicTimeSpinner.getText().length() == 0) {
        return false;
      }
    }
    if (this.fDefaultInstallLocBt.getSelection()) {
      return true;
    } else {
      return this.fInstallLocText.getText().trim().length() > 0;
    }
  }
  
  // --- Private code
  
  private void addListeners(final IManagedForm managedForm, final IX10PlatformConfWorkCopy x10PlatformConf, 
                            final AbstractCommonSectionFormPart formPart, final Button defaultToolsCmdsBt,
                            final Text launchCmdText, final Text debugCmdText,  final Text discoverCmdText, 
                            final Text monitorCmdText, final Spinner periodicTimeSpinner, final Button defaultInstallLocBt,
                            final Text installLocText, final Collection<Control> discoverCmdControls, 
                            final Collection<Control> dependentToolCmdsControls, final Collection<Control> installControls,
                            final String ciType) {
    launchCmdText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        x10PlatformConf.setLaunchCommand(ciType, launchCmdText.getText().trim());
        formPart.handleEmptyTextValidation(launchCmdText, LaunchMessages.RMCP_LaunchLabel);
        formPart.updateDirtyState(managedForm);
        formPart.setPartCompleteFlag(hasCompleteInfo());
      }
      
    });
    debugCmdText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        x10PlatformConf.setDebugCommand(ciType, debugCmdText.getText().trim());
        formPart.handleEmptyTextValidation(debugCmdText, LaunchMessages.RMCP_DebugLabel);
        formPart.updateDirtyState(managedForm);
        formPart.setPartCompleteFlag(hasCompleteInfo());
      }
      
    });
    discoverCmdText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        x10PlatformConf.setDiscoverCommand(ciType, discoverCmdText.getText().trim());
        formPart.handleEmptyTextValidation(discoverCmdText, LaunchMessages.RMCP_DiscoverLabel);
        formPart.updateDirtyState(managedForm);
        formPart.setPartCompleteFlag(hasCompleteInfo());
      }
      
    });
    monitorCmdText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        x10PlatformConf.setMonitorCommand(ciType, monitorCmdText.getText().trim());
        formPart.updateDirtyState(managedForm);
        formPart.setPartCompleteFlag(hasCompleteInfo());
      }
      
    });
    periodicTimeSpinner.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        final int period = Integer.parseInt(periodicTimeSpinner.getText().trim());
        x10PlatformConf.setMonitorPeriod(ciType, period);
        formPart.updateDirtyState(managedForm);
        formPart.setPartCompleteFlag(hasCompleteInfo());
      }
      
    });
    
    defaultToolsCmdsBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        x10PlatformConf.setDefaultToolCommands(ciType, defaultToolsCmdsBt.getSelection());
        for (final Control control : discoverCmdControls) {
          control.setEnabled(! defaultToolsCmdsBt.getSelection());
          if (control instanceof Text) {
            control.notifyListeners(SWT.Modify, new Event());
          }
        }
        final boolean enableDepCmds = ! defaultToolsCmdsBt.getSelection() && ! isOpenMPIVersionAutotDetectOn();
        for (final Control control : dependentToolCmdsControls) {
          control.setEnabled(enableDepCmds);
          if (control instanceof Text) {
            control.notifyListeners(SWT.Modify, new Event());
          }
        }
        
        formPart.updateDirtyState(managedForm);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    
    installLocText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        x10PlatformConf.setInstallLocation(ciType, installLocText.getText().trim());
        formPart.handleEmptyTextValidation(installLocText, LaunchMessages.RMCP_InstallLocGroup);
        formPart.updateDirtyState(managedForm);
        formPart.setPartCompleteFlag(hasCompleteInfo());
      }
      
    });
    
    defaultInstallLocBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        x10PlatformConf.setDefaultInstallLocationFlag(ciType, defaultInstallLocBt.getSelection());
        for (final Control control : installControls) {
          control.setEnabled(! defaultInstallLocBt.getSelection());
        }
        installLocText.notifyListeners(SWT.Modify, new Event());
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
  }
  
  private void initConfiguration(final IX10PlatformConfWorkCopy platformConf, final IMessagePassingInterfaceConf mpiConf) {
    if ((mpiConf.getLaunchCommand().length() == 0) &&  (mpiConf.getDebugCommand().length() == 0) &&
        (mpiConf.getDiscoverCommand().length() == 0) && (mpiConf.getMonitorCommand().length() == 0) &&
        (mpiConf.getMonitorPeriod() == 0)) {
      platformConf.setDefaultToolCommands(getServiceProviderId(), this.fToolRMConf.getUseToolDefaults());
    }
    if (mpiConf.getLaunchCommand().length() == 0) {
      platformConf.setLaunchCommand(getServiceProviderId(), this.fToolRMConf.getLaunchCmd());
    }
    if (mpiConf.getDebugCommand().length() == 0) {
      platformConf.setDebugCommand(getServiceProviderId(), this.fToolRMConf.getDebugCmd());
    }
    if (mpiConf.getDiscoverCommand().length() == 0) {
      platformConf.setDiscoverCommand(getServiceProviderId(), this.fToolRMConf.getDiscoverCmd());
    }
    if (mpiConf.getMonitorCommand().length() == 0) {
      platformConf.setMonitorCommand(getServiceProviderId(), this.fToolRMConf.getPeriodicMonitorCmd());
    }
    if (mpiConf.getMonitorPeriod() == 0) {
      platformConf.setMonitorPeriod(getServiceProviderId(), this.fToolRMConf.getPeriodicMonitorTime());
    }
    if (mpiConf.getInstallLocation().length() == 0) {
      platformConf.setDefaultInstallLocationFlag(getServiceProviderId(), this.fToolRMConf.getUseInstallDefaults());
      platformConf.setInstallLocation(getServiceProviderId(), this.fToolRMConf.getRemoteInstallPath());
    }
  }
  
  private void initializeControls(final IX10PlatformConfWorkCopy x10PlatformConf, final AbstractCommonSectionFormPart formPart,
                                  final IMessagePassingInterfaceConf ciConf, final Collection<Control> discoverCmdControls, 
                                  final Collection<Control> dependentToolCmdsControls, 
                                  final Collection<Control> installControls) {
    initConfiguration(x10PlatformConf, ciConf);
    
    this.fDefaultToolsCmdsBt.setSelection(ciConf.shouldTakeDefaultToolCommands());
    if (! isOpenMPIVersionAutotDetectOn()) {
      this.fLaunchCmdText.setText(ciConf.getLaunchCommand().trim());
      formPart.handleEmptyTextValidation(this.fLaunchCmdText, LaunchMessages.RMCP_LaunchLabel);
      this.fDebugCmdText.setText(ciConf.getDebugCommand().trim());
      formPart.handleEmptyTextValidation(this.fDebugCmdText, LaunchMessages.RMCP_DebugLabel);
      this.fMonitorCmdText.setText(ciConf.getMonitorCommand().trim());
    }
    this.fDiscoverCmdText.setText(ciConf.getDiscoverCommand().trim());
    formPart.handleEmptyTextValidation(this.fDiscoverCmdText, LaunchMessages.RMCP_DiscoverLabel);
    this.fPeriodicTimeSpinner.setSelection(ciConf.getMonitorPeriod());
    
    // Take care of enabling/disabling the controls in this group
    
    for (final Control control : discoverCmdControls) {
      control.setEnabled(! this.fDefaultToolsCmdsBt.getSelection());
    }
    final boolean enableDepCmds = ! this.fDefaultToolsCmdsBt.getSelection() && ! isOpenMPIVersionAutotDetectOn();
    for (final Control control : dependentToolCmdsControls) {
      control.setEnabled(enableDepCmds);
    }
    
    this.fDefaultInstallLocBt.setSelection(ciConf.shouldTakeDefaultInstallLocation());
    if (ciConf.shouldTakeDefaultInstallLocation()) {
      this.fInstallLocText.setText(this.fToolRMConf.getRemoteInstallPath());
    } else {
      this.fInstallLocText.setText(ciConf.getInstallLocation().trim());
      formPart.handleEmptyTextValidation(this.fInstallLocText, LaunchMessages.RMCP_LocationLabel);
    }
    
    for (final Control control : installControls) {
      control.setEnabled(! this.fDefaultInstallLocBt.getSelection());
    }
    
    KeyboardUtils.addDelayedActionOnControl(this.fInstallLocText, new Runnable() {
      
      public void run() {
        formPart.getFormPage().getEditorSite().getShell().getDisplay().asyncExec(new Runnable() {
          
          public void run() {
            formPart.handlePathValidation(AbstractMPIBasedTypeConfigPart.this.fInstallLocText, 
                                          LaunchMessages.RMCP_InstallLocGroup);
          }
          
        });
      }
      
    });
  }
  
  // --- Fields
  
  protected final IToolRMConfiguration fToolRMConf;
    
  protected Button fDefaultToolsCmdsBt;
  
  protected Text fLaunchCmdText;
  
  protected Text fDebugCmdText;
  
  protected Text fDiscoverCmdText;
  
  protected Text fMonitorCmdText;
  
  protected Spinner fPeriodicTimeSpinner;
  
  protected Button fDefaultInstallLocBt;
  
  protected Text fInstallLocText;

}
