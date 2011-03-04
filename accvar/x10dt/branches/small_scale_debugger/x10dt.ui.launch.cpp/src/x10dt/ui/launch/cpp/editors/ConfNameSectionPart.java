/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors;

import java.util.Collection;

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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import x10dt.ui.launch.core.utils.PTPConstants;
import x10dt.ui.launch.cpp.LaunchMessages;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;


final class ConfNameSectionPart extends AbstractCommonSectionFormPart implements IFormPart, IServiceModelEventListener,
                                                                                 IModelManagerChildListener {

  ConfNameSectionPart(final Composite parent, final ConnectionAndCommunicationConfPage formPage,
                      final Collection<IServiceConfigurationListener> rmConfPageListeners) {
    super(formPage);

    createClient(parent, formPage.getManagedForm(), formPage.getManagedForm().getToolkit(), rmConfPageListeners);
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
  }
  
  // --- IModelManagerChildListener's interface methods implementation
  
  public void handleEvent(final IChangedResourceManagerEvent e) {
    
  }

  public void handleEvent(final INewResourceManagerEvent event) {
    
  }

  public void handleEvent(final IRemoveResourceManagerEvent event) {
  }
  
  // --- Overridden methods
  
  public boolean setFormInput(final Object input) {
    setPartCompleteFlag(hasCompleteInfo());
    return false;
  }
 
  // --- Private code
  
  private void addListeners(final IManagedForm managedForm, final Combo rmServiceConfNameCombo, final Text descriptionText,
                            final Collection<IServiceConfigurationListener> rmConfPageListeners) {
    rmServiceConfNameCombo.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        getPlatformConf().setName(rmServiceConfNameCombo.getText().trim());

        for (final IServiceConfigurationListener listener : rmConfPageListeners) {
          listener.serviceConfigurationModified(rmServiceConfNameCombo.getText().trim());
        }
        
        handleEmptyTextValidation(rmServiceConfNameCombo, LaunchMessages.RMCP_ConfNameLabel);
        setPartCompleteFlag(hasCompleteInfo());
        updateDirtyState(managedForm);
      }
      
    });
    rmServiceConfNameCombo.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        if (rmServiceConfNameCombo.getSelectionIndex() != -1) {
          getPlatformConf().applyChanges();
          final String name = rmServiceConfNameCombo.getText().trim();

          final String confName = rmServiceConfNameCombo.getItem(rmServiceConfNameCombo.getSelectionIndex());
          final IServiceConfiguration serviceConf = (IServiceConfiguration) rmServiceConfNameCombo.getData(confName);
          for (final IService service : serviceConf.getServices()) {
            if (PTPConstants.RUNTIME_SERVICE_CATEGORY_ID.equals(service.getCategory().getId())) {
              final IServiceProvider serviceProvider = serviceConf.getServiceProvider(service);
              if (name.equals(serviceProvider.getName())) {
                setNewPlatformConfState(name, serviceProvider);
                getPlatformConf().setName(name);

                for (final IServiceConfigurationListener listener : rmConfPageListeners) {
                  listener.serviceConfigurationSelected(serviceProvider);
                }

                setPartCompleteFlag(hasCompleteInfo());
                updateDirtyState(managedForm);
                break;
              }
            }
          }
        } else {
          setPartCompleteFlag(hasCompleteInfo());
        }
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
    descriptionText.addModifyListener(new ModifyListener() {
      
      public void modifyText(final ModifyEvent event) {
        getPlatformConf().setDescription(descriptionText.getText().trim());
        updateDirtyState(managedForm);
      }
      
    });
  }
  
  private void createClient(final Composite parent, final IManagedForm managedForm, final FormToolkit toolkit, 
                            final Collection<IServiceConfigurationListener> rmConfPageListeners) {    
    final Composite nameCompo = toolkit.createComposite(parent, SWT.NONE);
    nameCompo.setFont(parent.getFont());
    final TableWrapLayout layout = new TableWrapLayout();
    layout.numColumns = 2;
    nameCompo.setLayout(layout);
    nameCompo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP, 1, 2));
    
    toolkit.createLabel(nameCompo, LaunchMessages.RMCP_ConfNameLabel);
    this.fRMServiceConfNameCombo = new Combo(nameCompo, SWT.NONE);
    this.fRMServiceConfNameCombo.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    
    final ServiceModelManager modelManager = ServiceModelManager.getInstance();
    for (final IServiceConfiguration serviceConf : modelManager.getConfigurations()) {
      this.fRMServiceConfNameCombo.add(serviceConf.getName());
      this.fRMServiceConfNameCombo.setData(serviceConf.getName(), serviceConf);
    }
    
    toolkit.createLabel(nameCompo, LaunchMessages.RMCP_DescriptionLabel);
    this.fDescriptionText = toolkit.createText(nameCompo, null);
    this.fDescriptionText.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    
    initializeControls();
    
    addListeners(managedForm, this.fRMServiceConfNameCombo, this.fDescriptionText, rmConfPageListeners);
  }
  
  private boolean hasCompleteInfo() {
    return this.fRMServiceConfNameCombo.getText().length() > 0;
  }
  
  private void initializeControls() {
    final IX10PlatformConf platformConf = getPlatformConf();
    
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
    handleEmptyTextValidation(this.fRMServiceConfNameCombo, LaunchMessages.RMCP_ConfNameLabel);
    
    if ((platformConf.getDescription() != null) && (platformConf.getDescription().trim().length() > 0)) {
      this.fDescriptionText.setText(platformConf.getDescription());
    }
  }
  
  // --- Fields
  
  private Combo fRMServiceConfNameCombo;
  
  private Text fDescriptionText;

}
