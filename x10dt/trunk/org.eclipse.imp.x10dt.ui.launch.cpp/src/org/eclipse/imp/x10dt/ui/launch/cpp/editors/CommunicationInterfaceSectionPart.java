/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.editors;

import static org.eclipse.imp.x10dt.ui.launch.core.Constants.EMPTY_STR;
import static org.eclipse.ptp.rm.core.AbstractToolsPreferenceManager.PREFS_DEBUG_CMD;
import static org.eclipse.ptp.rm.core.AbstractToolsPreferenceManager.PREFS_DISCOVER_CMD;
import static org.eclipse.ptp.rm.core.AbstractToolsPreferenceManager.PREFS_LAUNCH_CMD;
import static org.eclipse.ptp.rm.core.AbstractToolsPreferenceManager.PREFS_PERIODIC_MONITOR_CMD;
import static org.eclipse.ptp.rm.core.AbstractToolsPreferenceManager.PREFS_PERIODIC_MONITOR_TIME;
import static org.eclipse.ptp.rm.core.AbstractToolsPreferenceManager.PREFS_REMOTE_INSTALL_PATH;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.imp.x10dt.ui.launch.core.utils.PTPConstants;
import org.eclipse.imp.x10dt.ui.launch.core.utils.SWTFormUtils;
import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.ICommunicationInterfaceConf;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IX10PlatformConfWorkCopy;
import org.eclipse.ptp.rm.core.rmsystem.IToolRMConfiguration;
import org.eclipse.ptp.rm.mpi.openmpi.core.OpenMPIPreferenceManager;
import org.eclipse.ptp.rm.mpi.openmpi.core.rmsystem.IOpenMPIResourceManagerConfiguration;
import org.eclipse.ptp.rm.mpi.openmpi.core.rmsystem.OpenMPIServiceProvider;
import org.eclipse.ptp.services.core.IService;
import org.eclipse.ptp.services.core.IServiceProvider;
import org.eclipse.ptp.services.core.IServiceProviderDescriptor;
import org.eclipse.ptp.services.core.ServiceModelManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

@SuppressWarnings("deprecation")
final class CommunicationInterfaceSectionPart extends AbstractCommonSectionFormPart 
                                              implements IServiceConfigurationListener, IFormPart {

  CommunicationInterfaceSectionPart(final Composite parent, final ConnectionAndCommunicationConfPage formPage,
                                    final IX10PlatformConfWorkCopy x10PlatformConf) {
    super(parent, formPage, x10PlatformConf);
    
    getSection().setFont(parent.getFont());
    getSection().setText(LaunchMessages.RMCP_CommInterfaceSectionTitle);
    getSection().setDescription(LaunchMessages.RMCP_CommInterfaceSectionDescr);
    getSection().setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    this.fCITypeListeners = new ArrayList<ICommunicationInterfaceTypeListener>();
    
    createClient(formPage.getManagedForm(), formPage.getManagedForm().getToolkit(), x10PlatformConf);
    addCompletePartListener(formPage);
  }
  
  // --- IServiceConfigurationListener's interface methods implementation
  
  public void serviceConfigurationModified(final String textContent) {
  }

  public void serviceConfigurationSelected(final IServiceProvider serviceProvider) {
    int index  = -1;
    for (final String name : this.fCITypeCombo.getItems()) {
      ++index;
      final IServiceProvider comboProvider = (IServiceProvider) this.fCITypeCombo.getData(name);
      if (serviceProvider.getId().equals(comboProvider.getId())) {
        this.fCITypeCombo.setData(name, serviceProvider);
        this.fCITypeCombo.select(index);
          
        if (PTPConstants.OPEN_MPI_SERVICE_PROVIDER_ID.equals(serviceProvider.getId())) {
          final OpenMPIServiceProvider mpiServiceProvider = (OpenMPIServiceProvider) serviceProvider;
          final EOpenMPIVersion mpiVersion;
          if (IOpenMPIResourceManagerConfiguration.VERSION_12.equals(mpiServiceProvider.getVersionId())) {
            mpiVersion = EOpenMPIVersion.EVersion_1_2;
          } else if (IOpenMPIResourceManagerConfiguration.VERSION_13.equals(mpiServiceProvider.getVersionId())) {
            mpiVersion = EOpenMPIVersion.EVersion_1_3;
          } else if (IOpenMPIResourceManagerConfiguration.VERSION_14.equals(mpiServiceProvider.getVersionId())) {
            mpiVersion = EOpenMPIVersion.EVersion_1_4;
          } else {
            mpiVersion = EOpenMPIVersion.EAutoDetect;
          }
          int vIndex = -1;
          for (final String versionName : this.fOpenMPIVersionCombo.getItems()) {
            ++vIndex;
            final EOpenMPIVersion comboVersion = (EOpenMPIVersion) this.fOpenMPIVersionCombo.getData(versionName);
            if (comboVersion == mpiVersion) {
              this.fOpenMPIVersionCombo.select(vIndex);
            }
          }
          
          this.fCITypeCombo.notifyListeners(SWT.Selection, new Event());
        }
      }
    }
    
    if (this.fCITypeCombo.getSelectionIndex() == -1) {
      this.fCITypeCombo.select(0);
      this.fCITypeCombo.notifyListeners(SWT.Selection, new Event());
    }
    
    this.fDefaultToolsCmdsBt.notifyListeners(SWT.Selection, new Event());
    this.fDefaultInstallLocBt.notifyListeners(SWT.Selection, new Event());
  }
  
  // --- IFormPart's methods implementation
  
  public void dispose() {
    removeCompletePartListener(getFormPage());
  }
  
  // --- Overridden methods
  
  public boolean setFormInput(final Object input) {
    setPartCompleteFlag(hasCompleteInfo());
    
    for (final IFormPart formPart : getFormPage().getManagedForm().getParts()) {
      if (formPart instanceof ICommunicationInterfaceTypeListener) {
        this.fCITypeListeners.add((ICommunicationInterfaceTypeListener) formPart);
      }
    }
    
    return false;
  }
  
  // --- Internal services
  
  Combo getCommunicationModeCombo() {
    return this.fCIModeCombo;
  }
  
  Combo getCommunicationTypeCombo() {
    return this.fCITypeCombo;
  }
 
  // --- Private code
  
  private void addListeners(final IManagedForm managedForm, final IX10PlatformConfWorkCopy x10PlatformConf, 
                            final Combo ciTypeCombo, final Combo ciModeCombo, final Button defaultToolsCmdsBt, 
                            final Label openMPIVersionLabel, final Combo openMPIVersionCombo, final Text launchCmdText, 
                            final Text debugCmdText,  final Text discoverCmdText, final Text monitorCmdText, 
                            final Spinner periodicTimeSpinner, final Button defaultInstallLocBt, final Text installLocText, 
                            final Collection<Control> discoverCmdControls, final Collection<Control> dependentToolCmdsControls,
                            final Collection<Control> installControls) {
    openMPIVersionCombo.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        final String versionName = openMPIVersionCombo.getItem(openMPIVersionCombo.getSelectionIndex());
        final EOpenMPIVersion openMPIVersion = (EOpenMPIVersion) openMPIVersionCombo.getData(versionName);
        x10PlatformConf.setOpenMPIVersion(openMPIVersion);
        
        final String ptpPrefix;
        switch (openMPIVersion) {
          case EVersion_1_2:
            ptpPrefix = OpenMPIPreferenceManager.PREFIX_12;
            break;
          case EVersion_1_3:
            ptpPrefix = OpenMPIPreferenceManager.PREFIX_13;
            break;
          case EVersion_1_4:
            ptpPrefix = OpenMPIPreferenceManager.PREFIX_14;
            break;
          default:
            ptpPrefix = OpenMPIPreferenceManager.PREFIX_AUTO;
            break;
        }
        
        updateOpenMPIVersionSelection(ptpPrefix);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });

    launchCmdText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        x10PlatformConf.setLaunchCommand(launchCmdText.getText().trim());
        handleTextValidation(new EmptyTextInputChecker(launchCmdText, LaunchMessages.RMCP_LaunchLabel), managedForm, 
                             launchCmdText);
        updateDirtyState(managedForm);
        setPartCompleteFlag(hasCompleteInfo());
      }
      
    });
    debugCmdText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        x10PlatformConf.setDebugCommand(debugCmdText.getText().trim());
        handleTextValidation(new EmptyTextInputChecker(debugCmdText, LaunchMessages.RMCP_DebugLabel), managedForm, 
                             debugCmdText);
        updateDirtyState(managedForm);
        setPartCompleteFlag(hasCompleteInfo());
      }
      
    });
    discoverCmdText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        x10PlatformConf.setDiscoverCommand(discoverCmdText.getText().trim());
        handleTextValidation(new EmptyTextInputChecker(discoverCmdText, LaunchMessages.RMCP_DiscoverLabel), managedForm, 
                             discoverCmdText);
        updateDirtyState(managedForm);
        setPartCompleteFlag(hasCompleteInfo());
      }
      
    });
    monitorCmdText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        x10PlatformConf.setMonitorCommand(monitorCmdText.getText().trim());
        handleTextValidation(new EmptyTextInputChecker(monitorCmdText, LaunchMessages.RMCP_MonitorLabel), managedForm, 
                             monitorCmdText);
        updateDirtyState(managedForm);
        setPartCompleteFlag(hasCompleteInfo());
      }
      
    });
    periodicTimeSpinner.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        final int period = Integer.parseInt(periodicTimeSpinner.getText().trim());
        x10PlatformConf.setMonitorPeriod(period);
        updateDirtyState(managedForm);
        setPartCompleteFlag(hasCompleteInfo());
      }
      
    });
    
    defaultToolsCmdsBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        x10PlatformConf.setDefaultToolCommands(defaultToolsCmdsBt.getSelection());
        for (final Control control : discoverCmdControls) {
          control.setEnabled(! defaultToolsCmdsBt.getSelection());
          if (control instanceof Text) {
            control.notifyListeners(SWT.Modify, new Event());
          }
        }
        if (defaultToolsCmdsBt.getSelection()) {
          for (final Control control : dependentToolCmdsControls) {
            control.setEnabled(false);
            if (control instanceof Text) {
              control.notifyListeners(SWT.Modify, new Event());
            }
          }
        } else {
          final String versionName = openMPIVersionCombo.getItem(openMPIVersionCombo.getSelectionIndex());
          final EOpenMPIVersion openMPIVersion = (EOpenMPIVersion) openMPIVersionCombo.getData(versionName);

          if (! openMPIVersionCombo.isVisible() || (openMPIVersion != EOpenMPIVersion.EAutoDetect)) {
            for (final Control control : dependentToolCmdsControls) {
              control.setEnabled(true);
              if (control instanceof Text) {
                control.notifyListeners(SWT.Modify, new Event());
              }
            }
          }          
        }
        
        updateDirtyState(managedForm);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    
    installLocText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        x10PlatformConf.setInstallLocation(installLocText.getText().trim());
        handleTextValidation(new EmptyTextInputChecker(installLocText, LaunchMessages.RMCP_LocationLabel), managedForm, 
                             installLocText);
        updateDirtyState(managedForm);
        setPartCompleteFlag(hasCompleteInfo());
      }
      
    });
    
    defaultInstallLocBt.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        x10PlatformConf.setDefaultInstallLocationFlag(defaultInstallLocBt.getSelection());
        for (final Control control : installControls) {
          control.setEnabled(! defaultInstallLocBt.getSelection());
        }
        installLocText.notifyListeners(SWT.Modify, new Event());
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    
    ciTypeCombo.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        final String itemName = ciTypeCombo.getItem(ciTypeCombo.getSelectionIndex());
        final IToolRMConfiguration rmConf = (IToolRMConfiguration) ciTypeCombo.getData(itemName);
        
        x10PlatformConf.setServiceTypeId(rmConf.getResourceManagerId());
        
        final boolean isOpenMPI = (rmConf instanceof IOpenMPIResourceManagerConfiguration);
        openMPIVersionLabel.setVisible(isOpenMPI);
        openMPIVersionCombo.setVisible(isOpenMPI);
        
        for (final ICommunicationInterfaceTypeListener listener : CommunicationInterfaceSectionPart.this.fCITypeListeners) {
          listener.communicationTypeChanged(itemName);
        }
        
        if (rmConf.getUseToolDefaults()) {
          defaultToolsCmdsBt.setSelection(rmConf.getUseToolDefaults());
          defaultToolsCmdsBt.notifyListeners(SWT.Selection, new Event());
          
          launchCmdText.setText(rmConf.getLaunchCmd());
          debugCmdText.setText(rmConf.getDebugCmd());
          discoverCmdText.setText(rmConf.getDiscoverCmd());
          monitorCmdText.setText(rmConf.getPeriodicMonitorCmd());
          periodicTimeSpinner.setSelection(rmConf.getPeriodicMonitorTime());
        } else {
          launchCmdText.setText(rmConf.getLaunchCmd());
          debugCmdText.setText(rmConf.getDebugCmd());
          discoverCmdText.setText(rmConf.getDiscoverCmd());
          monitorCmdText.setText(rmConf.getPeriodicMonitorCmd());
          periodicTimeSpinner.setSelection(rmConf.getPeriodicMonitorTime());
          
          defaultToolsCmdsBt.setSelection(rmConf.getUseToolDefaults());
          defaultToolsCmdsBt.notifyListeners(SWT.Selection, new Event());
        }
        if (rmConf.getUseToolDefaults()) {
          defaultInstallLocBt.setSelection(rmConf.getUseInstallDefaults());
          defaultInstallLocBt.notifyListeners(SWT.Selection, new Event());
          
          installLocText.setText(rmConf.getRemoteInstallPath());
        } else {
          installLocText.setText(rmConf.getRemoteInstallPath());
          
          defaultInstallLocBt.setSelection(rmConf.getUseInstallDefaults());
          defaultInstallLocBt.notifyListeners(SWT.Selection, new Event());
        }
                
        updateDirtyState(managedForm);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    ciModeCombo.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        final String serviceName = ciModeCombo.getItem(ciModeCombo.getSelectionIndex());
        final String serviceId = (String) ciModeCombo.getData(serviceName);
        x10PlatformConf.setServiceModeId(serviceId);
        
        updateDirtyState(managedForm);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
  }
  
  private void createClient(final IManagedForm managedForm, final FormToolkit toolkit, 
                            final IX10PlatformConfWorkCopy x10PlatformConf) {
    final Composite sectionClient = toolkit.createComposite(getSection());
    sectionClient.setLayout(new TableWrapLayout());
    sectionClient.setFont(getSection().getFont());
    sectionClient.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    
    final Composite comboComposite = toolkit.createComposite(sectionClient);
    comboComposite.setFont(getSection().getFont());
    final TableWrapLayout comboLayout = new TableWrapLayout();
    comboLayout.numColumns = 4;
    comboComposite.setLayout(comboLayout);
    comboComposite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    
    final Label typeLabel = toolkit.createLabel(comboComposite, LaunchMessages.RMCP_CITypeLabel, SWT.WRAP);
    typeLabel.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.MIDDLE));
    this.fCITypeCombo = new Combo(comboComposite, SWT.READ_ONLY);
    this.fCITypeCombo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    final ServiceModelManager serviceModelManager = ServiceModelManager.getInstance();
    
    final Label modeLabel = toolkit.createLabel(comboComposite, LaunchMessages.RMCP_CIModeLabel, SWT.WRAP);
    modeLabel.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.MIDDLE));
    this.fCIModeCombo = new Combo(comboComposite, SWT.READ_ONLY);
    
    final Set<IServiceProviderDescriptor> serviceProviders = new HashSet<IServiceProviderDescriptor>();
    
    for (final IService service : serviceModelManager.getServices()) {
      if (PTPConstants.RUNTIME_SERVICE_CATEGORY_ID.equals(service.getCategory().getId())) {
        this.fCIModeCombo.add(service.getName());
        this.fCIModeCombo.setData(service.getName(), service.getId());
        
        serviceProviders.addAll(service.getProviders());
      }
    }
    for (final IServiceProviderDescriptor providerDescriptor : serviceProviders) {
      final IServiceProvider serviceProvider = serviceModelManager.getServiceProvider(providerDescriptor);
      if (serviceProvider instanceof IToolRMConfiguration) {
        this.fCITypeCombo.add(providerDescriptor.getName());
        this.fCITypeCombo.setData(providerDescriptor.getName(), serviceProvider);
      }
    }
    
    new Label(sectionClient, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(new TableWrapData(TableWrapData.FILL));
    
    final Composite expandableClient = sectionClient;
    
    final Composite versionsCompo = toolkit.createComposite(expandableClient, SWT.NONE);
    versionsCompo.setFont(expandableClient.getFont());
    final TableWrapLayout versionsLayout = new TableWrapLayout();
    versionsLayout.numColumns = 2;
    versionsCompo.setLayout(versionsLayout);
    versionsCompo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    
    final Label openMPIVersionLabel = toolkit.createLabel(versionsCompo, LaunchMessages.CISP_OpenMPIVersion);
    openMPIVersionLabel.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.MIDDLE));
    this.fOpenMPIVersionCombo = new Combo(versionsCompo, SWT.READ_ONLY);
    this.fOpenMPIVersionCombo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    this.fOpenMPIVersionCombo.add(LaunchMessages.CISP_AutoDetect);
    this.fOpenMPIVersionCombo.setData(LaunchMessages.CISP_AutoDetect, EOpenMPIVersion.EAutoDetect);
    this.fOpenMPIVersionCombo.add(LaunchMessages.CISP_Version14);
    this.fOpenMPIVersionCombo.setData(LaunchMessages.CISP_Version14, EOpenMPIVersion.EVersion_1_4);
    this.fOpenMPIVersionCombo.add(LaunchMessages.CISP_Version13);
    this.fOpenMPIVersionCombo.setData(LaunchMessages.CISP_Version13, EOpenMPIVersion.EVersion_1_3);
    this.fOpenMPIVersionCombo.add(LaunchMessages.CISP_Version12);
    this.fOpenMPIVersionCombo.setData(LaunchMessages.CISP_Version12, EOpenMPIVersion.EVersion_1_2);
        
    final Group toolsCommandsGroup = new Group(expandableClient, SWT.NONE);
    toolsCommandsGroup.setFont(expandableClient.getFont());
    toolsCommandsGroup.setLayout(new TableWrapLayout());
    toolsCommandsGroup.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    toolsCommandsGroup.setText(LaunchMessages.RMCP_ToolsCommandsGroup);
    
    this.fDefaultToolsCmdsBt = toolkit.createButton(toolsCommandsGroup, LaunchMessages.RMCP_UseDefaultToolsCmdsBt, SWT.CHECK);
    
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
    
    dependentToolCmdsControls.add(toolkit.createLabel(monitorPeriodCompo, LaunchMessages.RMCP_MonitorLabel));
    this.fMonitorCmdText = toolkit.createText(monitorPeriodCompo, null /* value */, SWT.WRAP);
    this.fMonitorCmdText.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    dependentToolCmdsControls.add(this.fMonitorCmdText);
    
    dependentToolCmdsControls.add(toolkit.createLabel(monitorPeriodCompo, LaunchMessages.RMCP_PeriodLabel));
    this.fPeriodicTimeSpinner = new Spinner(monitorPeriodCompo, SWT.SINGLE | SWT.BORDER);
    this.fPeriodicTimeSpinner.setMinimum(0);
    dependentToolCmdsControls.add(this.fPeriodicTimeSpinner);
    
    final Group installLocationGroup = new Group(expandableClient, SWT.NONE);
    installLocationGroup.setFont(expandableClient.getFont());
    installLocationGroup.setLayout(new TableWrapLayout());
    installLocationGroup.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    installLocationGroup.setText(LaunchMessages.RMCP_InstallLocGroup);
    
    this.fDefaultInstallLocBt = toolkit.createButton(installLocationGroup, LaunchMessages.RMCP_UseDefaultLocBt, SWT.CHECK);
    final Collection<Control> installControls = new ArrayList<Control>();
    this.fInstallLocText = SWTFormUtils.createLabelAndText(installLocationGroup, LaunchMessages.RMCP_LocationLabel, 
                                                           toolkit, installControls);
    
    initializeControls(managedForm, openMPIVersionLabel, this.fOpenMPIVersionCombo, discoverCmdControls, 
                       dependentToolCmdsControls, installControls);
    
    addListeners(managedForm, x10PlatformConf, this.fCITypeCombo, this.fCIModeCombo, this.fDefaultToolsCmdsBt, 
                 openMPIVersionLabel, this.fOpenMPIVersionCombo, this.fLaunchCmdText, this.fDebugCmdText, 
                 this.fDiscoverCmdText, this.fMonitorCmdText, this.fPeriodicTimeSpinner, this.fDefaultInstallLocBt,
                 this.fInstallLocText, discoverCmdControls, dependentToolCmdsControls, installControls);
    
    getSection().setClient(sectionClient);
  }
  
  private boolean hasCompleteInfo() {
    if (! this.fDefaultToolsCmdsBt.getSelection()) {
      boolean shouldBeChecked = true;
      if (this.fOpenMPIVersionCombo.isVisible()) {
        final String versionName = this.fOpenMPIVersionCombo.getItem(this.fOpenMPIVersionCombo.getSelectionIndex());
        final EOpenMPIVersion openMPIVersion = (EOpenMPIVersion) this.fOpenMPIVersionCombo.getData(versionName);
        if (openMPIVersion == EOpenMPIVersion.EAutoDetect) {
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
  
  private void initializeControls(final IManagedForm managedForm, final Label openMPIVersionLabel, 
                                  final Combo openMPIVersionCombo, final Collection<Control> discoverCmdControls,
                                  final Collection<Control> dependentToolCmdsControls, 
                                  final Collection<Control> installControls) {
    final ICommunicationInterfaceConf ciConf = getPlatformConf().getCommunicationInterfaceConf();
    
    if (ciConf.getServiceTypeId() != null) {
      int index = -1;
      for (final String name : this.fCITypeCombo.getItems()) {
        ++index;
        final IServiceProvider serviceProvider = (IServiceProvider) this.fCITypeCombo.getData(name);
        if (serviceProvider.getId().equals(ciConf.getServiceTypeId())) {
          this.fCITypeCombo.select(index);
          break;
        }
      }
    }
    if (this.fCITypeCombo.getSelectionIndex() == -1) {
      this.fCITypeCombo.select(0);
      this.fCITypeCombo.notifyListeners(SWT.Selection, new Event());
    }
    if (ciConf.getServiceModeId() != null) {
      int index = -1;
      for (final String name : this.fCIModeCombo.getItems()) {
        ++index;
        final String id = (String) this.fCIModeCombo.getData(name);
        if (id.equals(ciConf.getServiceModeId())) {
          this.fCIModeCombo.select(index);
          break;
        }
      }
    }
    final boolean isOpenMPI = PTPConstants.OPEN_MPI_SERVICE_PROVIDER_ID.equals(ciConf.getServiceTypeId());
    openMPIVersionLabel.setVisible(isOpenMPI);
    openMPIVersionCombo.setVisible(isOpenMPI);
    int index = -1;
    for (final String name : openMPIVersionCombo.getItems()) {
      ++index;
      final EOpenMPIVersion openMPIVersion = (EOpenMPIVersion) openMPIVersionCombo.getData(name);
      if (openMPIVersion == ciConf.getOpenMPIVersion()) {
        openMPIVersionCombo.select(index);
        break;
      }
    }
    this.fDefaultToolsCmdsBt.setSelection(ciConf.shouldTakeDefaultToolCommands());
    if (ciConf.shouldTakeDefaultToolCommands()) {
    	final String itemName = this.fCITypeCombo.getItem(this.fCITypeCombo.getSelectionIndex());
      final IToolRMConfiguration rmConf = (IToolRMConfiguration) this.fCITypeCombo.getData(itemName);
      
      this.fLaunchCmdText.setText(rmConf.getLaunchCmd());
      this.fDebugCmdText.setText(rmConf.getDebugCmd());
      this.fDiscoverCmdText.setText(rmConf.getDiscoverCmd());
      this.fMonitorCmdText.setText(rmConf.getPeriodicMonitorCmd());
      this.fPeriodicTimeSpinner.setSelection(rmConf.getPeriodicMonitorTime());
    } else {
      this.fLaunchCmdText.setText((ciConf.getLaunchCommand() == null) ? EMPTY_STR : ciConf.getLaunchCommand().trim());
      handleTextValidation(new EmptyTextInputChecker(this.fLaunchCmdText, LaunchMessages.RMCP_LaunchLabel), managedForm, 
                           this.fLaunchCmdText);
      this.fDebugCmdText.setText((ciConf.getDebugCommand() == null) ? EMPTY_STR : ciConf.getDebugCommand().trim());
      handleTextValidation(new EmptyTextInputChecker(this.fDebugCmdText, LaunchMessages.RMCP_DebugLabel), managedForm, 
                           this.fDebugCmdText);
      this.fDiscoverCmdText.setText((ciConf.getDiscoverCommand() == null) ? EMPTY_STR : ciConf.getDiscoverCommand().trim());
      handleTextValidation(new EmptyTextInputChecker(this.fDiscoverCmdText, LaunchMessages.RMCP_DiscoverLabel), managedForm, 
                           this.fDiscoverCmdText);
      this.fMonitorCmdText.setText((ciConf.getMonitorCommand() == null) ? EMPTY_STR : ciConf.getMonitorCommand().trim());
      handleTextValidation(new EmptyTextInputChecker(this.fMonitorCmdText, LaunchMessages.RMCP_MonitorLabel), managedForm, 
                           this.fMonitorCmdText);
      this.fPeriodicTimeSpinner.setSelection(ciConf.getMonitorPeriod());
    }
    
    // Take care of enabling/disabling the controls in this group
    
    for (final Control control : discoverCmdControls) {
      control.setEnabled(! this.fDefaultToolsCmdsBt.getSelection());
    }
    if (this.fDefaultToolsCmdsBt.getSelection()) {
      for (final Control control : dependentToolCmdsControls) {
        control.setEnabled(false);
      }
    } else {
      final String versionName = this.fOpenMPIVersionCombo.getItem(this.fOpenMPIVersionCombo.getSelectionIndex());
      final EOpenMPIVersion openMPIVersion = (EOpenMPIVersion) this.fOpenMPIVersionCombo.getData(versionName);

      if (! this.fOpenMPIVersionCombo.isVisible() || (openMPIVersion != EOpenMPIVersion.EAutoDetect)) {
        for (final Control control : dependentToolCmdsControls) {
          control.setEnabled(true);
        }
      }          
    }
    
    this.fDefaultInstallLocBt.setSelection(ciConf.shouldTakeDefaultInstallLocation());
    if (ciConf.shouldTakeDefaultInstallLocation()) {
    	final String itemName = this.fCITypeCombo.getItem(this.fCITypeCombo.getSelectionIndex());
      final IToolRMConfiguration rmConf = (IToolRMConfiguration) this.fCITypeCombo.getData(itemName);
      
      this.fInstallLocText.setText(rmConf.getRemoteInstallPath());
    } else {
      this.fInstallLocText.setText((ciConf.getInstallLocation() == null) ? EMPTY_STR : ciConf.getInstallLocation().trim());
      handleTextValidation(new EmptyTextInputChecker(this.fInstallLocText, LaunchMessages.RMCP_LocationLabel), managedForm, 
                           this.fInstallLocText);
    }
    
    for (final Control control : installControls) {
      control.setEnabled(! this.fDefaultInstallLocBt.getSelection());
    }
  }
  
  private void updateOpenMPIVersionSelection(final String prefix) {
    final Preferences preferences = OpenMPIPreferenceManager.getPreferences();
    this.fLaunchCmdText.setText(preferences.getString(prefix + PREFS_LAUNCH_CMD));
    this.fDebugCmdText.setText(preferences.getString(prefix + PREFS_DEBUG_CMD));
    this.fDiscoverCmdText.setText(preferences.getString(prefix + PREFS_DISCOVER_CMD));
    this.fMonitorCmdText.setText(preferences.getString(prefix + PREFS_PERIODIC_MONITOR_CMD));
    this.fPeriodicTimeSpinner.setSelection(preferences.getInt(prefix + PREFS_PERIODIC_MONITOR_TIME));
    this.fInstallLocText.setText(preferences.getString(prefix + PREFS_REMOTE_INSTALL_PATH));
  }
  
  // --- Fields
  
  private Combo fCITypeCombo;
  
  private Combo fCIModeCombo;
  
  private Combo fOpenMPIVersionCombo;
  
  private Button fDefaultToolsCmdsBt;
  
  private Text fLaunchCmdText;
  
  private Text fDebugCmdText;
  
  private Text fDiscoverCmdText;
  
  private Text fMonitorCmdText;
  
  private Spinner fPeriodicTimeSpinner;
  
  private Button fDefaultInstallLocBt;
  
  private Text fInstallLocText;
  
  private final Collection<ICommunicationInterfaceTypeListener> fCITypeListeners;
  
}
