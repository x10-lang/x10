/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import x10dt.ui.launch.core.platform_conf.EValidationStatus;


abstract class AbstractShallowTypeConfigPart implements ICITypeConfigurationPart {
  
  // --- Interface methods implementation

  public final void connectionChanged(final boolean isLocal, final String remoteConnectionName, 
                                      final EValidationStatus validationStatus) {
    // Nothing to do.
  }

  public final void create(final IManagedForm managedForm, final FormToolkit toolkit, final Composite parent, 
                           final AbstractCommonSectionFormPart formPart) {
    // Nothing to do.
  }

  public final void dispose(final IMessageManager... messageManagers) {
    // Nothing to do.
  }

  public final boolean hasCompleteInfo() {
    return true;
  }

}
