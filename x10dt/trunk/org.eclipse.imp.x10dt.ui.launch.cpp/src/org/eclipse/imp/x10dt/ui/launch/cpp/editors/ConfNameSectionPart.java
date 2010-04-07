/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.editors;

import java.util.Collection;

import org.eclipse.imp.x10dt.ui.launch.core.utils.PTPConstants;
import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IX10PlatformConfWorkCopy;
import org.eclipse.imp.x10dt.ui.launch.cpp.utils.PTPConfUtils;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.PTPCorePlugin;
import org.eclipse.ptp.core.events.IChangedResourceManagerEvent;
import org.eclipse.ptp.core.events.INewResourceManagerEvent;
import org.eclipse.ptp.core.events.IRemoveResourceManagerEvent;
import org.eclipse.ptp.core.listeners.IModelManagerChildListener;
import org.eclipse.ptp.services.core.IService;
import org.eclipse.ptp.services.core.IServiceConfiguration;
import org.eclipse.ptp.services.core.IServiceModelEvent;
import org.eclipse.ptp.services.core.IServiceModelEventListener;
import org.eclipse.ptp.services.core.IServiceProvider;
import org.eclipse.ptp.services.core.ServiceModelManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;


final class ConfNameSectionPart extends AbstractCommonSectionFormPart implements IFormPart, IServiceModelEventListener,
                                                                                 IModelManagerChildListener {

  ConfNameSectionPart(final Composite parent, final ConnectionAndCommunicationConfPage formPage,
                      final IX10PlatformConfWorkCopy x10PlatformConf,
                      final Collection<IServiceConfigurationListener> rmConfPageListeners) {
    super(formPage, x10PlatformConf);

    createClient(parent, formPage.getManagedForm(), formPage.getManagedForm().getToolkit(), x10PlatformConf, 
                 rmConfPageListeners);
    addCompletePartListener(formPage);
    ServiceModelManager.getInstance().addEventListener(this, IServiceModelEvent.SERVICE_CONFIGURATION_ADDED |
                                                       IServiceModelEvent.SERVICE_CONFIGURATION_REMOVED);
    PTPCorePlugin.getDefault().getModelManager().addListener(this);
  }
  
  // --- IFormPart's interface methods implementation
  
  public void dispose() {
    ServiceModelManager.getInstance().removeEventListener(this);
    PTPCorePlugin.getDefault().getModelManager().removeListener(this);
    removeCompletePartListener(getFormPage());
  }
  
  // --- IServiceModelEventListener's interface methods implementation
  
  public void handleEvent(final IServiceModelEvent event) {
    final IServiceConfiguration serviceConf = (IServiceConfiguration) event.getSource();
    switch (event.getType()) {
      case IServiceModelEvent.SERVICE_CONFIGURATION_ADDED:
        getFormPage().getSite().getShell().getDisplay().syncExec(new Runnable() {
          
          public void run() {
            if (ConfNameSectionPart.this.fRMServiceConfNameCombo.indexOf(serviceConf.getName()) == -1) {
              ConfNameSectionPart.this.fRMServiceConfNameCombo.add(serviceConf.getName());
              ConfNameSectionPart.this.fRMServiceConfNameCombo.setData(serviceConf.getName(), serviceConf);
            }
          }
          
        });
        break;
        
      case IServiceModelEvent.SERVICE_CONFIGURATION_REMOVED:
        getFormPage().getSite().getShell().getDisplay().syncExec(new Runnable() {
          
          public void run() {
            if (ConfNameSectionPart.this.fRMServiceConfNameCombo.indexOf(serviceConf.getName()) != -1) {
              ConfNameSectionPart.this.fRMServiceConfNameCombo.remove(serviceConf.getName());
            }
          }
          
        });
        break;
    }
  }
  
  // --- IModelManagerChildListener's interface methods implementation
  
  public void handleEvent(final IChangedResourceManagerEvent e) {
    
  }

  public void handleEvent(final INewResourceManagerEvent event) {
    
  }

  public void handleEvent(final IRemoveResourceManagerEvent event) {
    if (this.fRMServiceConfNameCombo.indexOf(event.getResourceManager().getName()) != -1) {
      this.fRMServiceConfNameCombo.remove(event.getResourceManager().getName());
    }
  }
  
  // --- Overridden methods
  
  public boolean setFormInput(final Object input) {
    setPartCompleteFlag(hasCompleteInfo());
    return false;
  }
 
  // --- Private code
  
  private void addListeners(final IManagedForm managedForm, final IX10PlatformConfWorkCopy x10PlatformConf,
                            final Combo rmServiceConfNameCombo, /* final Combo modeCombo, */ final Text descriptionText,
                            final Collection<IServiceConfigurationListener> rmConfPageListeners) {
    rmServiceConfNameCombo.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        x10PlatformConf.setName(rmServiceConfNameCombo.getText().trim());

        for (final IServiceConfigurationListener listener : rmConfPageListeners) {
          listener.serviceConfigurationModified(rmServiceConfNameCombo.getText().trim());
        }
        
        handleTextValidation(new EmptyComboInputChecker(rmServiceConfNameCombo, LaunchMessages.RMCP_ConfNameLabel), 
                             managedForm, rmServiceConfNameCombo);
        setPartCompleteFlag(hasCompleteInfo());
        updateDirtyState(managedForm);
      }
      
    });
    rmServiceConfNameCombo.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        x10PlatformConf.setName(rmServiceConfNameCombo.getText().trim());

        final String confName = rmServiceConfNameCombo.getItem(rmServiceConfNameCombo.getSelectionIndex());
        final IServiceConfiguration serviceConf = (IServiceConfiguration) rmServiceConfNameCombo.getData(confName);
        final String serviceModeId = getPlatformConf().getCommunicationInterfaceConf().getServiceModeId();
        for (final IService service : serviceConf.getServices()) {
          if (PTPConstants.RUNTIME_SERVICE_CATEGORY_ID.equals(service.getCategory().getId()) &&
              service.getId().equals(serviceModeId)) {
            final IServiceProvider serviceProvider = serviceConf.getServiceProvider(service);
            for (final IServiceConfigurationListener listener : rmConfPageListeners) {
              listener.serviceConfigurationSelected(serviceProvider);
            }

            setPartCompleteFlag(hasCompleteInfo());
            updateDirtyState(managedForm);
            break;
          }
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    descriptionText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        x10PlatformConf.setDescription(descriptionText.getText().trim());
        updateDirtyState(managedForm);
      }
      
    });
  }
  
  private void createClient(final Composite parent, final IManagedForm managedForm, final FormToolkit toolkit, 
                            final IX10PlatformConfWorkCopy x10PlatformConf,
                            final Collection<IServiceConfigurationListener> rmConfPageListeners) {    
    final Composite nameCompo = toolkit.createComposite(parent, SWT.NONE);
    nameCompo.setFont(parent.getFont());
    nameCompo.setLayout(new GridLayout(2, false));
    nameCompo.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 2, 1));
    
    toolkit.createLabel(nameCompo, LaunchMessages.RMCP_ConfNameLabel);
    this.fRMServiceConfNameCombo = new Combo(nameCompo, SWT.NONE);
    this.fRMServiceConfNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    final ServiceModelManager modelManager = ServiceModelManager.getInstance();
    for (final IServiceConfiguration serviceConf : modelManager.getConfigurations()) {
      this.fRMServiceConfNameCombo.add(serviceConf.getName());
      this.fRMServiceConfNameCombo.setData(serviceConf.getName(), serviceConf);
    }
    
    toolkit.createLabel(nameCompo, LaunchMessages.RMCP_DescriptionLabel);
    this.fDescriptionText = toolkit.createText(nameCompo, null);
    this.fDescriptionText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    
    initializeControls(managedForm);
    
    addListeners(managedForm, x10PlatformConf, this.fRMServiceConfNameCombo, this.fDescriptionText, rmConfPageListeners);
  }
  
  private boolean hasCompleteInfo() {
    return this.fRMServiceConfNameCombo.getText().length() > 0;
  }
  
  private void initializeControls(final IManagedForm managedForm) {
    final IX10PlatformConf platformConf = getPlatformConf();
    
    if ((platformConf.getName() == null) || (platformConf.getName().trim().length() == 0)) {
      this.fRMServiceConfNameCombo.setText(buildDefaultName(platformConf));
    } else {
      int index = -1;
      final ServiceModelManager modelManager = ServiceModelManager.getInstance();
      for (final IServiceConfiguration serviceConf : modelManager.getConfigurations()) {
        ++index;
        if (serviceConf.getName().equals(platformConf.getName())) {
          this.fRMServiceConfNameCombo.select(index);
          break;
        }
      }
      if (this.fRMServiceConfNameCombo.getSelectionIndex() == -1) {
        this.fRMServiceConfNameCombo.setText(platformConf.getName());
      }
    }
    handleTextValidation(new EmptyComboInputChecker(this.fRMServiceConfNameCombo, LaunchMessages.RMCP_ConfNameLabel), 
                         managedForm, this.fRMServiceConfNameCombo);
    
    if ((platformConf.getDescription() != null) && (platformConf.getDescription().trim().length() > 0)) {
      this.fDescriptionText.setText(platformConf.getDescription());
    }
  }
  
  // --- Private code
  
  private String buildDefaultName(final IX10PlatformConf platformConf) {
    final boolean isLocal = platformConf.getConnectionConf().isLocal();
    String connectionName = isLocal ? LaunchMessages.RMCP_DefaultLocalConnName : 
                                      platformConf.getConnectionConf().getConnectionName();
    if (connectionName.trim().length() == 0) {
      connectionName = LaunchMessages.RMCP_UnknownTargetName;
    }
    return NLS.bind(LaunchMessages.RMCP_DefaultConnName, 
                    PTPConfUtils.getCommunicationInterfaceTypeName(platformConf.getCommunicationInterfaceConf()),
                    connectionName);
  }
  
  // --- Fields
  
  private Combo fRMServiceConfNameCombo;
  
  private Text fDescriptionText;

}
