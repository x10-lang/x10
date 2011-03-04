/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.launching;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.IModelManager;
import org.eclipse.ptp.core.IPTPLaunchConfigurationConstants;
import org.eclipse.ptp.core.PTPCorePlugin;
import org.eclipse.ptp.core.elements.IPUniverse;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import x10dt.ui.launch.cpp.LaunchMessages;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;
import x10dt.ui.launch.cpp.utils.PTPConfUtils;


final class CommunicationInterfaceTab extends LaunchConfigurationTab 
                                      implements ILaunchConfigurationTab {
  
  CommunicationInterfaceTab() {
    this.fRMDynamicTabs = new HashMap<IResourceManager, IRMLaunchConfigurationDynamicTab>();
  }
  
  // --- ILaunchConfigurationTab's interface methods implementation
  
  public void createControl(final Composite parent) {
    this.fComposite = new Composite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
    this.fComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    
    setControl(this.fComposite);
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
    final IModelManager modelManager = PTPCorePlugin.getDefault().getModelManager();
    final IPUniverse universe = modelManager.getUniverse();
    IResourceManager resourceManager = null;
    if (universe != null) {
      final IResourceManager[] rms = universe.getResourceManagers();
      if (rms.length == 1) {
        resourceManager = rms[0];
      }
    }
    if (resourceManager == null) {
      setErrorMessage(LaunchMessages.CIT_NoResourceManager);
      return;
    }
    final String resourceManagerUniqName = resourceManager.getUniqueName();
    configuration.setAttribute(IPTPLaunchConfigurationConstants.ATTR_RESOURCE_MANAGER_UNIQUENAME, resourceManagerUniqName);

    final IRMLaunchConfigurationDynamicTab rmDynamicTab = getRMLaunchConfigurationDynamicTab(resourceManager);
    if (rmDynamicTab == null) {
      setErrorMessage(NLS.bind(LaunchMessages.CIT_NoLaunchConfigForRM, 
                               new Object[] { resourceManager.getName() }));
    } else {
      rmDynamicTab.setDefaults(configuration, resourceManager, null);
    }
  }
  
  // --- ICppApplicationTabListener's interface methods implementation
  
  public void platformConfSelected(final IX10PlatformConf platformConf) {
    try {
			this.fResourceManager = PTPConfUtils.getResourceManager(platformConf);
		} catch (RemoteConnectionException except) {
			// Let's forget. Handled by next statement.
		} catch (CoreException except) {
			// Let's forget. Handled by next statement.
		}
    if (this.fResourceManager == null){
    	setErrorMessage(LaunchMessages.CIT_CouldNotFindResManager);
    	return;
    }
    if (this.fResourceManager.getState() == State.ERROR) {
    	try {
				this.fResourceManager.shutdown();
			} catch (CoreException except) {
				setErrorMessage(LaunchMessages.CIT_CouldNotStopResMgr);
				this.fResourceManager = null;
				return;
			}
    }
    if (this.fResourceManager.getState() != State.STARTED) { 
      try {
        this.fResourceManager.startUp(new NullProgressMonitor());
      } catch (CoreException except) {
        setErrorMessage(LaunchMessages.CIT_CouldNotStartResManager);
        this.fResourceManager = null;
      }
    }
    if ((getLaunchConfiguration() != null) && (this.fResourceManager != null)) {
      initializeFrom(getLaunchConfiguration());
    }
  }
  
  // --- Overridden methods
  
  public Image getImage() {
    return LaunchImages.getImage(LaunchImages.IMG_PARALLEL_TAB);
  }
  
  public void initializeFrom(final ILaunchConfiguration configuration) {
    super.initializeFrom(configuration);
    
    final IRMLaunchConfigurationDynamicTab rmDynamicTab = getRMLaunchConfigurationDynamicTab(this.fResourceManager);
    if (rmDynamicTab != null) {
      try {
        for (final Control child : this.fComposite.getChildren()) {
          if (! child.isDisposed()) {
            child.dispose();
          }
        }
        this.fComposite.setLayout(new GridLayout(1, false));
        rmDynamicTab.createControl(this.fComposite, this.fResourceManager, null /* queue */);
        rmDynamicTab.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        rmDynamicTab.initializeFrom(this.fComposite, this.fResourceManager, null /* queue */, 
                                    getLaunchConfiguration());
      } catch (CoreException except) {
        setErrorMessage(except.getMessage());
      }
    }
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
        final IRMLaunchConfigurationDynamicTab rmDynamicTab = rmFactory.create(resourceManager, getLaunchConfigurationDialog());
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
  
  private Composite fComposite;
  
  private IResourceManager fResourceManager;
  
  private final Map<IResourceManager, IRMLaunchConfigurationDynamicTab> fRMDynamicTabs;

}
