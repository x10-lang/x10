/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors;

import static org.eclipse.ptp.rm.core.AbstractToolsPreferenceManager.PREFS_DEBUG_CMD;
import static org.eclipse.ptp.rm.core.AbstractToolsPreferenceManager.PREFS_DISCOVER_CMD;
import static org.eclipse.ptp.rm.core.AbstractToolsPreferenceManager.PREFS_LAUNCH_CMD;
import static org.eclipse.ptp.rm.core.AbstractToolsPreferenceManager.PREFS_PERIODIC_MONITOR_CMD;
import static org.eclipse.ptp.rm.core.AbstractToolsPreferenceManager.PREFS_PERIODIC_MONITOR_TIME;

import org.eclipse.ptp.core.Preferences;
import org.eclipse.ptp.rm.mpi.openmpi.core.OpenMPIPlugin;
import org.eclipse.ptp.rm.mpi.openmpi.core.OpenMPIPreferenceManager;
import org.eclipse.ptp.rm.mpi.openmpi.core.rmsystem.IOpenMPIResourceManagerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import x10dt.ui.launch.core.utils.PTPConstants;
import x10dt.ui.launch.cpp.LaunchMessages;
import x10dt.ui.launch.cpp.platform_conf.IOpenMPIInterfaceConf;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConfWorkCopy;


final class OpenMPITypeConfigPart extends AbstractMPIBasedTypeConfigPart implements ICITypeConfigurationPart {
  
  OpenMPITypeConfigPart(final IOpenMPIResourceManagerConfiguration toolRMConf) {
    super(toolRMConf);
  }
  
  // --- Interface methods implementation
  
  public String getServiceProviderId() {
    return PTPConstants.OPEN_MPI_SERVICE_PROVIDER_ID;
  }
  
  // --- Abstract methods implementation
  
  protected boolean isOpenMPIVersionAutotDetectOn() {
    if (this.fOpenMPIVersionCombo.getSelectionIndex() == -1) {
      return false;
    }
    final String versionName = this.fOpenMPIVersionCombo.getItem(this.fOpenMPIVersionCombo.getSelectionIndex());
    final EOpenMPIVersion openMPIVersion = (EOpenMPIVersion) this.fOpenMPIVersionCombo.getData(versionName);
    return openMPIVersion == EOpenMPIVersion.EAutoDetect;
  }
  
  protected void postCreationStep(final FormToolkit toolkit, final Composite parent, final IManagedForm managedForm,
                                  final IX10PlatformConfWorkCopy x10PlatformConf) {
    initializeControls(x10PlatformConf, (IOpenMPIInterfaceConf) x10PlatformConf.getCommunicationInterfaceConf());
    addListeners(x10PlatformConf, this.fOpenMPIVersionCombo);
  }

  protected void preCreationStep(final FormToolkit toolkit, final Composite parent) {
    final Composite versionsCompo = toolkit.createComposite(parent, SWT.NONE);
    versionsCompo.setFont(parent.getFont());
    final TableWrapLayout versionsLayout = new TableWrapLayout();
    versionsLayout.numColumns = 2;
    versionsCompo.setLayout(versionsLayout);
    versionsCompo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    addControl(versionsCompo);
    
    final Label openMPIVersionLabel = toolkit.createLabel(versionsCompo, LaunchMessages.CISP_OpenMPIVersion);
    openMPIVersionLabel.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.MIDDLE));
    addControl(openMPIVersionLabel);
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
    addControl(this.fOpenMPIVersionCombo);
  }
  
  // --- Private code
  
  private void addListeners(final IX10PlatformConfWorkCopy x10PlatformConf, final Combo openMPIVersionCombo) {
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
        OpenMPITypeConfigPart.this.fDefaultToolsCmdsBt.notifyListeners(SWT.Selection, new Event());
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
  }
  
  private void initConfiguration(final IX10PlatformConfWorkCopy platformConf, final IOpenMPIInterfaceConf mpiConf) {
    if (mpiConf.getOpenMPIVersion() == null) {
      final IOpenMPIResourceManagerConfiguration toolRMConf = (IOpenMPIResourceManagerConfiguration) super.fToolRMConf;
      final EOpenMPIVersion mpiVersion;
      if (IOpenMPIResourceManagerConfiguration.VERSION_12.equals(toolRMConf.getVersionId())) {
        mpiVersion = EOpenMPIVersion.EVersion_1_2;
      } else if (IOpenMPIResourceManagerConfiguration.VERSION_13.equals(toolRMConf.getVersionId())) {
        mpiVersion = EOpenMPIVersion.EVersion_1_3;
      } else if (IOpenMPIResourceManagerConfiguration.VERSION_14.equals(toolRMConf.getVersionId())) {
        mpiVersion = EOpenMPIVersion.EVersion_1_4;
      } else {
        mpiVersion = EOpenMPIVersion.EAutoDetect;
      }
      platformConf.setOpenMPIVersion(mpiVersion);
    }
  }
  
  private void initializeControls(final IX10PlatformConfWorkCopy platformConf, final IOpenMPIInterfaceConf ciConf) {
    initConfiguration(platformConf, ciConf);
    int index = -1;
    for (final String name : this.fOpenMPIVersionCombo.getItems()) {
      ++index;
      final EOpenMPIVersion openMPIVersion = (EOpenMPIVersion) this.fOpenMPIVersionCombo.getData(name);
      if (openMPIVersion == ciConf.getOpenMPIVersion()) {
        this.fOpenMPIVersionCombo.select(index);
        break;
      }
    }
  }
  
  private void updateOpenMPIVersionSelection(final String prefix) {
    super.fLaunchCmdText.setText(Preferences.getString(OpenMPIPlugin.getUniqueIdentifier(), prefix + PREFS_LAUNCH_CMD));
    super.fDebugCmdText.setText(Preferences.getString(OpenMPIPlugin.getUniqueIdentifier(), prefix + PREFS_DEBUG_CMD));
    super.fDiscoverCmdText.setText(Preferences.getString(OpenMPIPlugin.getUniqueIdentifier(), prefix + PREFS_DISCOVER_CMD));
    super.fMonitorCmdText.setText(Preferences.getString(OpenMPIPlugin.getUniqueIdentifier(), 
                                                        prefix + PREFS_PERIODIC_MONITOR_CMD));
    super.fPeriodicTimeSpinner.setSelection(Preferences.getInt(OpenMPIPlugin.getUniqueIdentifier(),
                                                               prefix + PREFS_PERIODIC_MONITOR_TIME));
  }
  
  // --- Fields
  
  private Combo fOpenMPIVersionCombo;

}
