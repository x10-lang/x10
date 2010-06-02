/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.editors;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.EValidationStatus;
import org.eclipse.imp.x10dt.ui.launch.core.utils.PTPConstants;
import org.eclipse.imp.x10dt.ui.launch.core.utils.SWTFormUtils;
import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IMessagePassingInterfaceConf;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IX10PlatformConfWorkCopy;
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
                           final IX10PlatformConfWorkCopy x10PlatformConf, final AbstractCommonSectionFormPart formPart) {
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
                                                          dependentToolCmdsControls, 3);
    
    this.fDebugCmdText = SWTFormUtils.createLabelAndText(toolsCommandsGroup, LaunchMessages.RMCP_DebugLabel, toolkit, 
                                                         dependentToolCmdsControls, 3);
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
    
    postCreationStep(toolkit, parent, managedForm, x10PlatformConf);
    
    initializeControls(managedForm, formPart, (IMessagePassingInterfaceConf) x10PlatformConf.getCommunicationInterfaceConf(),
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
  
//  public void updateTypeSelectionEvent() {
//    final boolean isOpenMPI = (this.fToolRMConf instanceof IOpenMPIResourceManagerConfiguration);
//    openMPIVersionLabel.setVisible(isOpenMPI);
//    openMPIVersionCombo.setVisible(isOpenMPI);
//    
//    if (this.fToolRMConf.getUseToolDefaults()) {
//      this.fDefaultToolsCmdsBt.setSelection(this.fToolRMConf.getUseToolDefaults());
//      this.fDefaultToolsCmdsBt.notifyListeners(SWT.Selection, new Event());
//      
//      this.fLaunchCmdText.setText(this.fToolRMConf.getLaunchCmd());
//      this.fDebugCmdText.setText(this.fToolRMConf.getDebugCmd());
//      discoverCmdText.setText(this.fToolRMConf.getDiscoverCmd());
//      monitorCmdText.setText(this.fToolRMConf.getPeriodicMonitorCmd());
//      periodicTimeSpinner.setSelection(this.fToolRMConf.getPeriodicMonitorTime());
//    } else {
//      launchCmdText.setText(this.fToolRMConf.getLaunchCmd());
//      debugCmdText.setText(this.fToolRMConf.getDebugCmd());
//      discoverCmdText.setText(this.fToolRMConf.getDiscoverCmd());
//      monitorCmdText.setText(this.fToolRMConf.getPeriodicMonitorCmd());
//      periodicTimeSpinner.setSelection(this.fToolRMConf.getPeriodicMonitorTime());
//      
//      defaultToolsCmdsBt.setSelection(this.fToolRMConf.getUseToolDefaults());
//      defaultToolsCmdsBt.notifyListeners(SWT.Selection, new Event());
//    }
//    if (rmConf.getUseInstallDefaults()) {
//      defaultInstallLocBt.setSelection(this.fToolRMConf.getUseInstallDefaults());
//      defaultInstallLocBt.notifyListeners(SWT.Selection, new Event());
//      
//      installLocText.setText(this.fToolRMConf.getRemoteInstallPath());
//    } else {
//      installLocText.setText(this.fToolRMConf.getRemoteInstallPath());
//      
//      defaultInstallLocBt.setSelection(this.fToolRMConf.getUseInstallDefaults());
//      defaultInstallLocBt.notifyListeners(SWT.Selection, new Event());
//    }
//  }
  
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
        formPart.handleTextValidation(new EmptyTextInputChecker(launchCmdText, LaunchMessages.RMCP_LaunchLabel), managedForm, 
                                      launchCmdText);
        formPart.updateDirtyState(managedForm);
        formPart.setPartCompleteFlag(hasCompleteInfo());
      }
      
    });
    debugCmdText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        x10PlatformConf.setDebugCommand(ciType, debugCmdText.getText().trim());
        formPart.handleTextValidation(new EmptyTextInputChecker(debugCmdText, LaunchMessages.RMCP_DebugLabel), managedForm, 
                                      debugCmdText);
        formPart.updateDirtyState(managedForm);
        formPart.setPartCompleteFlag(hasCompleteInfo());
      }
      
    });
    discoverCmdText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        x10PlatformConf.setDiscoverCommand(ciType, discoverCmdText.getText().trim());
        formPart.handleTextValidation(new EmptyTextInputChecker(discoverCmdText, LaunchMessages.RMCP_DiscoverLabel), 
                                      managedForm, discoverCmdText);
        formPart.updateDirtyState(managedForm);
        formPart.setPartCompleteFlag(hasCompleteInfo());
      }
      
    });
    monitorCmdText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        x10PlatformConf.setMonitorCommand(ciType, monitorCmdText.getText().trim());
        formPart.handleTextValidation(new EmptyTextInputChecker(monitorCmdText, LaunchMessages.RMCP_MonitorLabel), managedForm, 
                                      monitorCmdText);
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
        formPart.handleTextValidation(new EmptyTextInputChecker(installLocText, LaunchMessages.RMCP_LocationLabel), 
                                      managedForm, installLocText);
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
  
  private void initializeControls(final IManagedForm managedForm, final AbstractCommonSectionFormPart formPart,
                                  final IMessagePassingInterfaceConf ciConf, final Collection<Control> discoverCmdControls, 
                                  final Collection<Control> dependentToolCmdsControls, 
                                  final Collection<Control> installControls) {
    this.fDefaultToolsCmdsBt.setSelection(ciConf.shouldTakeDefaultToolCommands());
    if (ciConf.shouldTakeDefaultToolCommands()) {
      this.fLaunchCmdText.setText(this.fToolRMConf.getLaunchCmd());
      this.fDebugCmdText.setText(this.fToolRMConf.getDebugCmd());
      this.fDiscoverCmdText.setText(this.fToolRMConf.getDiscoverCmd());
      this.fMonitorCmdText.setText(this.fToolRMConf.getPeriodicMonitorCmd());
      this.fPeriodicTimeSpinner.setSelection(this.fToolRMConf.getPeriodicMonitorTime());
    } else {
      if (! isOpenMPIVersionAutotDetectOn()) {
        this.fLaunchCmdText.setText(ciConf.getLaunchCommand().trim());
        formPart.handleTextValidation(new EmptyTextInputChecker(this.fLaunchCmdText, LaunchMessages.RMCP_LaunchLabel), 
                                      managedForm, this.fLaunchCmdText);
        this.fDebugCmdText.setText(ciConf.getDebugCommand().trim());
        formPart.handleTextValidation(new EmptyTextInputChecker(this.fDebugCmdText, LaunchMessages.RMCP_DebugLabel), 
                                      managedForm, this.fDebugCmdText);
        this.fMonitorCmdText.setText(ciConf.getMonitorCommand().trim());
        formPart.handleTextValidation(new EmptyTextInputChecker(this.fMonitorCmdText, LaunchMessages.RMCP_MonitorLabel),
                                      managedForm, this.fMonitorCmdText);
      }
      this.fDiscoverCmdText.setText(ciConf.getDiscoverCommand().trim());
      formPart.handleTextValidation(new EmptyTextInputChecker(this.fDiscoverCmdText, LaunchMessages.RMCP_DiscoverLabel),
                                    managedForm, this.fDiscoverCmdText);
      this.fPeriodicTimeSpinner.setSelection(ciConf.getMonitorPeriod());
    }
    
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
      formPart.handleTextValidation(new EmptyTextInputChecker(this.fInstallLocText, LaunchMessages.RMCP_LocationLabel), 
                                    managedForm, this.fInstallLocText);
    }
    
    for (final Control control : installControls) {
      control.setEnabled(! this.fDefaultInstallLocBt.getSelection());
    }
  }
  
  // --- Fields
  
  private final IToolRMConfiguration fToolRMConf;
    
  protected Button fDefaultToolsCmdsBt;
  
  protected Text fLaunchCmdText;
  
  protected Text fDebugCmdText;
  
  protected Text fDiscoverCmdText;
  
  protected Text fMonitorCmdText;
  
  protected Spinner fPeriodicTimeSpinner;
  
  protected Button fDefaultInstallLocBt;
  
  protected Text fInstallLocText;

}
