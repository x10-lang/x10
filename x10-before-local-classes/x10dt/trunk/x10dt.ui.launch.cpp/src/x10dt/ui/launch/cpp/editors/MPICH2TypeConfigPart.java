/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors;

import org.eclipse.ptp.rm.core.rmsystem.IToolRMConfiguration;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import x10dt.ui.launch.core.utils.PTPConstants;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConfWorkCopy;


final class MPICH2TypeConfigPart extends AbstractMPIBasedTypeConfigPart implements ICITypeConfigurationPart {
  
  MPICH2TypeConfigPart(final IToolRMConfiguration toolRMConf) {
    super(toolRMConf);
  }
  
  // --- Interface methods implementation

  public String getServiceProviderId() {
    return PTPConstants.MPICH2_SERVICE_PROVIDER_ID;
  }
  
  // --- Abstract methods implementation
  
  protected boolean isOpenMPIVersionAutotDetectOn() {
    return false;
  }
  
  protected void postCreationStep(final FormToolkit toolkit, final Composite parent, final IManagedForm managedForm,
                                  final IX10PlatformConfWorkCopy x10PlatformConf) {
    // Nothing to do
  }

  protected void preCreationStep(final FormToolkit toolkit, final Composite parent) {
    // Nothing to do
  }

}
