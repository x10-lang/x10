/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.EValidationStatus;
import org.eclipse.imp.x10dt.ui.launch.core.utils.PTPConstants;
import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.ICommunicationInterfaceConf;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IX10PlatformConfWorkCopy;
import org.eclipse.ptp.rm.core.rmsystem.IRemoteResourceManagerConfiguration;
import org.eclipse.ptp.rm.core.rmsystem.IToolRMConfiguration;
import org.eclipse.ptp.rm.ibm.ll.core.rmsystem.IIBMLLResourceManagerConfiguration;
import org.eclipse.ptp.rm.ibm.pe.core.rmsystem.IPEResourceManagerConfiguration;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;
import org.eclipse.ptp.services.core.IService;
import org.eclipse.ptp.services.core.IServiceProvider;
import org.eclipse.ptp.services.core.IServiceProviderDescriptor;
import org.eclipse.ptp.services.core.ServiceModelManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;


final class CommunicationInterfaceSectionPart extends AbstractCommonSectionFormPart 
                                              implements IServiceConfigurationListener, IConnectionTypeListener, IFormPart {

  CommunicationInterfaceSectionPart(final Composite parent, final ConnectionAndCommunicationConfPage formPage,
                                    final IX10PlatformConfWorkCopy x10PlatformConf) {
    super(parent, formPage, x10PlatformConf);
    
    getSection().setFont(parent.getFont());
    getSection().setText(LaunchMessages.RMCP_CommInterfaceSectionTitle);
    getSection().setDescription(LaunchMessages.RMCP_CommInterfaceSectionDescr);
    getSection().setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    
    this.fCITypeListeners = new ArrayList<ICommunicationInterfaceTypeListener>();
    
    createClient(formPage.getManagedForm(), formPage.getManagedForm().getToolkit(), x10PlatformConf);
    addCompletePartListener(formPage);
  }
  
  // --- IServiceConfigurationListener's interface methods implementation
  
  public void serviceConfigurationModified(final String textContent) {
  }

  public void serviceConfigurationSelected(final IServiceProvider serviceProvider) {
//    int index  = -1;
//    for (final String name : this.fCITypeCombo.getItems()) {
//      ++index;
//      final ICITypeConfigurationPart comboConfPart = (ICITypeConfigurationPart) this.fCITypeCombo.getData(name);
//      if (serviceProvider.getId().equals(comboConfPart.getServiceProviderId())) {
//        this.fCITypeCombo.setData(name, createCITypeConfigurationPart(serviceProvider));
//        this.fCITypeCombo.select(index);
//          
//        if (PTPConstants.OPEN_MPI_SERVICE_PROVIDER_ID.equals(serviceProvider.getId())) {
//          final OpenMPIServiceProvider mpiServiceProvider = (OpenMPIServiceProvider) serviceProvider;
//          final EOpenMPIVersion mpiVersion;
//          if (IOpenMPIResourceManagerConfiguration.VERSION_12.equals(mpiServiceProvider.getVersionId())) {
//            mpiVersion = EOpenMPIVersion.EVersion_1_2;
//          } else if (IOpenMPIResourceManagerConfiguration.VERSION_13.equals(mpiServiceProvider.getVersionId())) {
//            mpiVersion = EOpenMPIVersion.EVersion_1_3;
//          } else if (IOpenMPIResourceManagerConfiguration.VERSION_14.equals(mpiServiceProvider.getVersionId())) {
//            mpiVersion = EOpenMPIVersion.EVersion_1_4;
//          } else {
//            mpiVersion = EOpenMPIVersion.EAutoDetect;
//          }
//          int vIndex = -1;
//          for (final String versionName : this.fOpenMPIVersionCombo.getItems()) {
//            ++vIndex;
//            final EOpenMPIVersion comboVersion = (EOpenMPIVersion) this.fOpenMPIVersionCombo.getData(versionName);
//            if (comboVersion == mpiVersion) {
//              this.fOpenMPIVersionCombo.select(vIndex);
//            }
//          }
//          
//          this.fCITypeCombo.notifyListeners(SWT.Selection, new Event());
//        }
//      }
//    }
//    
//    if (this.fCITypeCombo.getSelectionIndex() == -1) {
//      this.fCITypeCombo.select(0);
//      this.fCITypeCombo.notifyListeners(SWT.Selection, new Event());
//    }
//    
//    this.fDefaultToolsCmdsBt.notifyListeners(SWT.Selection, new Event());
//    this.fDefaultInstallLocBt.notifyListeners(SWT.Selection, new Event());
  }
  
  // --- IConnectionTypeListener's interface methods implementation
  
  public void connectionChanged(final boolean isLocal, final String remoteConnectionName, 
                                final EValidationStatus validationStatus) {
    for (final String itemName : this.fCITypeCombo.getItems()) {
      final ICITypeConfigurationPart typeConfPart = (ICITypeConfigurationPart) this.fCITypeCombo.getData(itemName);
      typeConfPart.connectionChanged(isLocal, remoteConnectionName, validationStatus);
    }
  }
  
  // --- IFormPart's methods implementation
  
  public void dispose() {
    removeCompletePartListener(getFormPage());
  }
  
  // --- Overridden methods
  
  public boolean setFormInput(final Object input) {
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
  
  private void addListeners(final IManagedForm managedForm, final FormToolkit toolkit, final Composite parent, 
                            final IX10PlatformConfWorkCopy x10PlatformConf, final Combo ciTypeCombo, final Combo ciModeCombo) {
    ciTypeCombo.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        updateCommunicationTypeInfo(ciTypeCombo, managedForm, x10PlatformConf, toolkit, parent);
        updateDirtyState(managedForm);
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    ciModeCombo.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        final String serviceName = ciModeCombo.getItem(ciModeCombo.getSelectionIndex());
        final String serviceModeId = (String) ciModeCombo.getData(serviceName);
        final String serviceTypeName = ciTypeCombo.getItem(ciTypeCombo.getSelectionIndex());
        x10PlatformConf.setServiceModeId(serviceTypeName, serviceModeId);
        
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
    
    final Label typeLabel = toolkit.createLabel(comboComposite, LaunchMessages.RMCP_CITypeLabel);
    typeLabel.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.MIDDLE));
    this.fCITypeCombo = new Combo(comboComposite, SWT.READ_ONLY);
    this.fCITypeCombo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    final ServiceModelManager serviceModelManager = ServiceModelManager.getInstance();
    
    final Label modeLabel = toolkit.createLabel(comboComposite, LaunchMessages.RMCP_CIModeLabel);
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
      if (serviceProvider instanceof IRemoteResourceManagerConfiguration) {
        final ICITypeConfigurationPart typeConfPart = createCITypeConfigurationPart(serviceProvider);
        if (typeConfPart != null) {
          this.fCITypeCombo.add(providerDescriptor.getName());
          this.fCITypeCombo.setData(providerDescriptor.getName(), typeConfPart);
        }
      }
    }
    
    new Label(sectionClient, SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(new TableWrapData(TableWrapData.FILL));
    
    initializeControls();
    
    updateCommunicationTypeInfo(this.fCITypeCombo, managedForm, x10PlatformConf, toolkit, sectionClient);
    
    addListeners(managedForm, toolkit, sectionClient, x10PlatformConf, this.fCITypeCombo, this.fCIModeCombo);
    
    getSection().setClient(sectionClient);
  }
  
  private ICITypeConfigurationPart createCITypeConfigurationPart(final IServiceProvider serviceProvider) {
    final String remoteServiceId = ((IResourceManagerConfiguration) serviceProvider).getResourceManagerId();
    if (PTPConstants.OPEN_MPI_SERVICE_PROVIDER_ID.equals(remoteServiceId)) {
      return new OpenMPITypeConfigPart((IToolRMConfiguration) serviceProvider);
    } else if (PTPConstants.MPICH2_SERVICE_PROVIDER_ID.equals(remoteServiceId)) {
      return new MPICH2TypeConfigPart((IToolRMConfiguration) serviceProvider);
    } else if (PTPConstants.PARALLEL_ENVIRONMENT_SERVICE_PROVIDER_ID.equals(remoteServiceId)) {
      return new ParallelEnvironmentTypeConfigPart((IPEResourceManagerConfiguration) serviceProvider);
    } else if (PTPConstants.LOAD_LEVELER_SERVICE_PROVIDER_ID.equals(remoteServiceId)) {
      return new LoadLevelerTypeConfigPart((IIBMLLResourceManagerConfiguration) serviceProvider);
    }
    return null;
  }

  private void initializeControls() {
    final ICommunicationInterfaceConf ciConf = getPlatformConf().getCommunicationInterfaceConf();
    
    int index = -1;
    for (final String name : this.fCITypeCombo.getItems()) {
      ++index;
      final ICITypeConfigurationPart typeConfPart = (ICITypeConfigurationPart) this.fCITypeCombo.getData(name);
      if (typeConfPart.getServiceProviderId().equals(ciConf.getServiceTypeId())) {
        this.fCITypeCombo.select(index);
        break;
      }
    }
    index = -1;
    for (final String name : this.fCIModeCombo.getItems()) {
      ++index;
      final String id = (String) this.fCIModeCombo.getData(name);
      if (id.equals(ciConf.getServiceModeId())) {
        this.fCIModeCombo.select(index);
        break;
      }
    }
  }
  
  private void updateCommunicationTypeInfo(final Combo ciTypeCombo, final IManagedForm managedForm,
                                           final IX10PlatformConfWorkCopy x10PlatformConf, final FormToolkit toolkit,
                                           final Composite parent) {
    final String itemName = ciTypeCombo.getItem(ciTypeCombo.getSelectionIndex());
    final ICITypeConfigurationPart typeConfPart = (ICITypeConfigurationPart) ciTypeCombo.getData(itemName);
    
    x10PlatformConf.setServiceTypeId(typeConfPart.getServiceProviderId());
    
    for (final ICommunicationInterfaceTypeListener listener : this.fCITypeListeners) {
      listener.communicationTypeChanged(itemName);
    }
    
    if (CommunicationInterfaceSectionPart.this.fPreviousTypeConfPart != null) {
      final IManagedForm headerForm = ((SharedHeaderFormEditor) getFormPage().getEditor()).getHeaderForm();
      this.fPreviousTypeConfPart.dispose(headerForm.getMessageManager(), managedForm.getMessageManager());
    }

    typeConfPart.create(managedForm, toolkit, parent, x10PlatformConf, this);
    managedForm.reflow(true);
    
    this.fPreviousTypeConfPart = typeConfPart;
    
    setPartCompleteFlag(typeConfPart.hasCompleteInfo());
  }
  
  // --- Fields
  
  private Combo fCITypeCombo;
  
  private Combo fCIModeCombo;
  
  private ICITypeConfigurationPart fPreviousTypeConfPart;
  
  
  private final Collection<ICommunicationInterfaceTypeListener> fCITypeListeners;
  
}
