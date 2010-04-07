/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.launching;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;
import org.eclipse.imp.x10dt.ui.launch.cpp.utils.PTPConfUtils;
import org.eclipse.ptp.core.IPTPLaunchConfigurationConstants;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.core.elements.attributes.ResourceManagerAttributes.State;
import org.eclipse.ptp.launch.PTPLaunchPlugin;
import org.eclipse.ptp.launch.ui.LaunchConfigurationTab;
import org.eclipse.ptp.launch.ui.LaunchImages;
import org.eclipse.ptp.launch.ui.extensions.AbstractRMLaunchConfigurationFactory;
import org.eclipse.ptp.launch.ui.extensions.IRMLaunchConfigurationContentsChangedListener;
import org.eclipse.ptp.launch.ui.extensions.IRMLaunchConfigurationDynamicTab;
import org.eclipse.ptp.launch.ui.extensions.RMLaunchValidation;
import org.eclipse.ptp.remote.core.exception.RemoteConnectionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


final class CommunicationInterfaceTab extends LaunchConfigurationTab 
                                      implements ILaunchConfigurationTab, ICppApplicationTabListener {
  
  CommunicationInterfaceTab() {
    this.fRMDynamicTabs = new HashMap<IResourceManager, IRMLaunchConfigurationDynamicTab>();
  }
  
  // --- ILaunchConfigurationTab's interface methods implementation
  
  public void createControl(final Composite parent) {
    this.fScrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
    this.fScrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    this.fScrolledComposite.setExpandHorizontal(true);
    this.fScrolledComposite.setExpandVertical(true);
    
    setControl(this.fScrolledComposite);
  }
  
  public String getName() {
    return LaunchMessages.CIT_CommunicationInterface;
  }
  
  public void performApply(final ILaunchConfigurationWorkingCopy configuration) {
    if (this.fResourceManager != null) {
      configuration.setAttribute(IPTPLaunchConfigurationConstants.ATTR_RESOURCE_MANAGER_UNIQUENAME, 
                                 this.fResourceManager.getUniqueName());
      final IRMLaunchConfigurationDynamicTab rmDynamicTab = getRMLaunchConfigurationDynamicTab(this.fResourceManager);
      if (rmDynamicTab != null) {
        final RMLaunchValidation validation = rmDynamicTab.performApply(configuration, this.fResourceManager, null);
        if (! validation.isSuccess()) {
          setErrorMessage(validation.getMessage());
        }
      }
    }
  }
  
  public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
    // Do nothing here.
  }
  
  // --- ICppApplicationTabListener's interface methods implementation
  
  public void platformConfSelected(final IX10PlatformConf platformConf) {
    this.fResourceManager = PTPConfUtils.findResourceManager(platformConf.getName());
    if (this.fResourceManager == null){
      try {
        this.fResourceManager = PTPConfUtils.createResourceManager(platformConf);
      } catch (RemoteConnectionException except1) {
        setErrorMessage(LaunchMessages.CIT_CouldNotCreateResManager);
        return;
      }
    }
    if (this.fResourceManager.getState() != State.STARTED) { 
      try {
        this.fResourceManager.startUp(new NullProgressMonitor());
        initializeFrom(getLaunchConfiguration());
      } catch (CoreException except) {
        setErrorMessage(LaunchMessages.CIT_CouldNotStartResManager);
        this.fResourceManager = null;
      }
    }
  }
  
  // --- Overridden methods
  
  public Image getImage() {
    return LaunchImages.getImage(LaunchImages.IMG_PARALLEL_TAB);
  }
  
  public void initializeFrom(final ILaunchConfiguration configuration) {
    super.initializeFrom(configuration);
    
    final IRMLaunchConfigurationDynamicTab rmDynamicTab = getRMLaunchConfigurationDynamicTab(this.fResourceManager);
    this.fScrolledComposite.setContent(null);
    for (final Control child : this.fScrolledComposite.getChildren()) {
      child.dispose();
    }
    if (rmDynamicTab != null) {
      try {
        rmDynamicTab.createControl(this.fScrolledComposite, this.fResourceManager, null /* queue */);
        final Control dynControl = rmDynamicTab.getControl();
        this.fScrolledComposite.setContent(dynControl);
        final Point size = dynControl.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        this.fScrolledComposite.setMinSize(size);
        rmDynamicTab.initializeFrom(this.fScrolledComposite, this.fResourceManager, null /* queue */, getLaunchConfiguration());
      } catch (CoreException except) {
        setErrorMessage(except.getMessage());
      }
    }
    this.fScrolledComposite.layout(true);
  }
  
  public boolean isValid(final ILaunchConfiguration configuration) {
    setErrorMessage(null);
    setMessage(null);
    if (this.fResourceManager == null) {
      setErrorMessage(LaunchMessages.CIT_NoX10ProjectIdentified);
      return false;
    }
    final IRMLaunchConfigurationDynamicTab rmDynamicTab = getRMLaunchConfigurationDynamicTab(this.fResourceManager);
    if (rmDynamicTab == null) {
      setErrorMessage(LaunchMessages.CIT_NoLaunchConfigAvailable);
      return false;
    }
    final RMLaunchValidation validation = rmDynamicTab.isValid(configuration, this.fResourceManager, null);
    if (! validation.isSuccess()) {
      setErrorMessage(validation.getMessage());
      return false;
    }
    
    return true;
  }
  
  public void setLaunchConfigurationDialog(final ILaunchConfigurationDialog dialog) {
    super.setLaunchConfigurationDialog(dialog);
  }
  
  // --- Private code
  
  private IRMLaunchConfigurationDynamicTab getRMLaunchConfigurationDynamicTab(final IResourceManager resourceManager) {
    if (! this.fRMDynamicTabs.containsKey(resourceManager)) {
      try {
        final PTPLaunchPlugin launchPlugin = PTPLaunchPlugin.getDefault();
        final AbstractRMLaunchConfigurationFactory rmFactory = launchPlugin.getRMLaunchConfigurationFactory(resourceManager);
        if (rmFactory == null) {
          return null;
        }
        final IRMLaunchConfigurationDynamicTab rmDynamicTab = rmFactory.create(resourceManager);
        if (rmDynamicTab == null) {
          return null;
        }
        rmDynamicTab.addContentsChangedListener(new IRMLaunchConfigurationContentsChangedListener() {

          public void handleContentsChanged(final IRMLaunchConfigurationDynamicTab factory) {
            updateLaunchConfigurationDialog();
          }
          
        });
        this.fRMDynamicTabs.put(resourceManager, rmDynamicTab);
        return rmDynamicTab;
      } catch (CoreException except) {
        setErrorMessage(except.getMessage());
        return null;
      }
    }
    return this.fRMDynamicTabs.get(resourceManager);
  }
  
  // --- Fields
  
  private ScrolledComposite fScrolledComposite;
  
  private IResourceManager fResourceManager;
  
  private final Map<IResourceManager, IRMLaunchConfigurationDynamicTab> fRMDynamicTabs;

}
