/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.rms.launch_configuration;

import static x10dt.ui.launch.rms.core.launch_configuration.LaunchConfigConstants.ATTR_NUM_PLACES;
import static x10dt.ui.launch.rms.core.launch_configuration.LaunchConfigConstants.DEFAULT_NUM_PLACES;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.attributes.IAttribute;
import org.eclipse.ptp.core.attributes.IllegalValueException;
import org.eclipse.ptp.core.elements.IPQueue;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.core.elements.attributes.JobAttributes;
import org.eclipse.ptp.launch.ui.extensions.IRMLaunchConfigurationContentsChangedListener;
import org.eclipse.ptp.launch.ui.extensions.IRMLaunchConfigurationDynamicTab;
import org.eclipse.ptp.launch.ui.extensions.RMLaunchValidation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import x10dt.ui.launch.cpp.rms.CppRMSActivator;
import x10dt.ui.launch.rms.core.Messages;


final class StandaloneRMLaunchConfigurationDynamicTab implements IRMLaunchConfigurationDynamicTab {
  
  StandaloneRMLaunchConfigurationDynamicTab() {
    this.fListeners = new ArrayList<IRMLaunchConfigurationContentsChangedListener>();
  }

  // --- Interface methods implementation
  
  public void addContentsChangedListener(final IRMLaunchConfigurationContentsChangedListener listener) {
    this.fListeners.add(listener);
  }

  public RMLaunchValidation canSave(final Control control, final IResourceManager resourceManager, final IPQueue queue) {
    return new RMLaunchValidation(true, null);
  }

  public void createControl(final Composite parent, final IResourceManager resourceManager, 
                            final IPQueue queue) throws CoreException {
    this.fControl = new Composite(parent, SWT.NONE);
    this.fControl.setFont(parent.getFont());
    this.fControl.setLayout(new GridLayout(1, false));
    this.fControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    
    final Composite placesCompo = new Composite(this.fControl, SWT.NONE);
    placesCompo.setFont(this.fControl.getFont());
    placesCompo.setLayout(new GridLayout(2, false));
    placesCompo.setLayoutData(new GridData(SWT.LEFT, SWT.NONE, true, false));
    
    new Label(placesCompo, SWT.NONE).setText(Messages.SRMLCDT_PlacesNumber);
    this.fNumPlacesSpinner = new Spinner(placesCompo, SWT.BORDER);
    this.fNumPlacesSpinner.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(final SelectionEvent event) {
        fireContentChange();
      }
      
      public void widgetDefaultSelected(final SelectionEvent event) {
        widgetSelected(event);
      }
      
    });
  }

  public IAttribute<?, ?, ?>[] getAttributes(final IResourceManager resourceManager, final IPQueue queue, 
                                             final ILaunchConfiguration configuration, 
                                             final String mode) throws CoreException {
    final List<IAttribute<?,?,?>> attrs = new ArrayList<IAttribute<?,?,?>>();

    try {
      final int numPlaces = configuration.getAttribute(ATTR_NUM_PLACES, DEFAULT_NUM_PLACES);
      attrs.add(JobAttributes.getNumberOfProcessesAttributeDefinition().create(numPlaces));
    } catch (IllegalValueException except) {
      throw new CoreException(new Status(IStatus.ERROR, CppRMSActivator.PLUGIN_ID, Messages.SRMLCDT_InvalidPlacesNb, except));
    }

    return attrs.toArray(new IAttribute<?,?,?>[attrs.size()]);
  }

  public Control getControl() {
    return this.fControl;
  }

  public RMLaunchValidation initializeFrom(final Control control, final IResourceManager resourceManager, final IPQueue queue,
                                           final ILaunchConfiguration configuration) {
    try {
      this.fNumPlacesSpinner.setSelection(configuration.getAttribute(ATTR_NUM_PLACES, DEFAULT_NUM_PLACES));
    } catch (CoreException except) {
      return new RMLaunchValidation(false, NLS.bind(Messages.SRMLCDT_PageInitializationError, except.getMessage()));
    }
    return new RMLaunchValidation(true, null);
  }

  public RMLaunchValidation isValid(final ILaunchConfiguration launchConfig, final IResourceManager resourceManager, 
                                    final IPQueue queue) {
    if (this.fNumPlacesSpinner.getSelection() < 1) {
      return new RMLaunchValidation(false, Messages.SRMLCDT_AtLeastOnePlaceMsg);
    }
    return new RMLaunchValidation(true, null);
  }

  public RMLaunchValidation performApply(final ILaunchConfigurationWorkingCopy configuration, 
                                         final IResourceManager resourceManager, final IPQueue queue) {
    configuration.setAttribute(ATTR_NUM_PLACES, this.fNumPlacesSpinner.getSelection());
    return new RMLaunchValidation(true, null);
  }

  public void removeContentsChangedListener(final IRMLaunchConfigurationContentsChangedListener listener) {
    this.fListeners.remove(listener);
  }

  public RMLaunchValidation setDefaults(final ILaunchConfigurationWorkingCopy configuration, 
                                        final IResourceManager resourceManager, final IPQueue queue) {
    return new RMLaunchValidation(true, null);
  }
  
  // --- Private code
  
  private void fireContentChange() {
    for (final IRMLaunchConfigurationContentsChangedListener listener : this.fListeners) {
      listener.handleContentsChanged(this);
    }
  }
    
  // --- Fields
  
  private Composite fControl;
  
  private Spinner fNumPlacesSpinner;
    
  private final List<IRMLaunchConfigurationContentsChangedListener> fListeners;

}
