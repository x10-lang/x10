/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.java.launching.rms;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ptp.services.core.IServiceProvider;
import org.eclipse.ptp.services.core.IServiceProviderWorkingCopy;
import org.eclipse.ptp.services.ui.IServiceProviderContributor;
import org.eclipse.swt.widgets.Composite;

/**
 * Shallow implementation of configuration of the Sockets service provider.
 * 
 * @author egeay
 */
public final class MultiVMProviderContributor implements IServiceProviderContributor {

  // --- Interface methods implementation
  
  public void configureServiceProvider(final IServiceProviderWorkingCopy provider, final Composite composite) {
  }

  public WizardPage[] getWizardPages(final IWizard wizard, final IServiceProvider provider) {
    return null;
  }

  public IWizard getWizard(final IServiceProvider provider, final IWizardPage page) {
    return null;
  }

}
