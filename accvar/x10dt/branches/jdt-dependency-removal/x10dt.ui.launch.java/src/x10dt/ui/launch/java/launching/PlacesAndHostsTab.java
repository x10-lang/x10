/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.java.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.launch.ui.extensions.IRMLaunchConfigurationContentsChangedListener;
import org.eclipse.ptp.launch.ui.extensions.IRMLaunchConfigurationDynamicTab;
import org.eclipse.ptp.launch.ui.extensions.RMLaunchValidation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import x10dt.ui.launch.java.LaunchJavaImages;
import x10dt.ui.launch.java.Messages;
import x10dt.ui.launch.rms.core.launch_configuration.X10EnvVarsConfDynamicTab;


final class PlacesAndHostsTab extends AbstractLaunchConfigurationTab implements ILaunchConfigurationTab {
  
  PlacesAndHostsTab() {
    LaunchJavaImages.createManaged(LaunchJavaImages.PLACES_HOSTS);
  }
  
  // --- Interface methods implementation

  public void createControl(final Composite parent) {
    final ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL);
    
    final Composite composite = new Composite(scrolledComposite, SWT.NONE);
    composite.setFont(parent.getFont());
    composite.setLayout(new GridLayout(1, false));
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    
    createX10EnvVarsInfo(composite);
    
    scrolledComposite.setContent(composite);
    scrolledComposite.setExpandVertical(true);
    scrolledComposite.setExpandHorizontal(true);
    
    setControl(scrolledComposite);
  }

  public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
  }

  public void initializeFrom(final ILaunchConfiguration configuration) {
    this.fX10EnvVarsConfTab.initializeFrom(null /* control */, this.fResourceManager, null /* queue */, configuration);
  }

  public void performApply(final ILaunchConfigurationWorkingCopy configuration) {
    this.fX10EnvVarsConfTab.performApply(configuration, this.fResourceManager, null /* queue */);
  }

  public String getName() {
    return Messages.PAHT_TabName;
  }
  
  // --- Overridden methods
  
  public void dispose() {
    LaunchJavaImages.removeImage(LaunchJavaImages.PLACES_HOSTS);
    super.dispose();
  }
  
  public Image getImage() {
    return LaunchJavaImages.getImage(LaunchJavaImages.PLACES_HOSTS);
  }
  
  public boolean isValid(final ILaunchConfiguration configuration) {
    final RMLaunchValidation validation = this.fX10EnvVarsConfTab.isValid(configuration, this.fResourceManager, null);
    setErrorMessage(null);
    if (validation.isSuccess()) {
      return true;
    } else {
      setErrorMessage(validation.getMessage());
      return false;
    }
  }
  
  // --- Private code
    
  private void createX10EnvVarsInfo(final Composite parent) {
    this.fX10EnvVarsConfTab = new X10EnvVarsConfDynamicTab();
    try {
      this.fX10EnvVarsConfTab.createControl(parent, null /* resourceManager */, null /* queue */);
      this.fX10EnvVarsConfTab.addContentsChangedListener(new IRMLaunchConfigurationContentsChangedListener() {

        public void handleContentsChanged(final IRMLaunchConfigurationDynamicTab factory) {
          updateLaunchConfigurationDialog();
        }
        
      });
    } catch (CoreException except) {
      
    }
  }
  
  // --- Internal services
  
  void setResourceManager(final IResourceManager resourceManager) {
    this.fResourceManager = resourceManager;
  }
    
  // --- Fields
    
  private X10EnvVarsConfDynamicTab fX10EnvVarsConfTab;
  
  private IResourceManager fResourceManager;
  
}
