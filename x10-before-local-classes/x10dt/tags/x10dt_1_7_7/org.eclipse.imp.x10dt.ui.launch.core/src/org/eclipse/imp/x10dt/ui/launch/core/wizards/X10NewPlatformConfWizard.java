/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.wizards;

import org.eclipse.imp.x10dt.ui.launch.core.LaunchCore;
import org.eclipse.imp.x10dt.ui.launch.core.preferences.X10PlatformConfiguration;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Responsible for creating the wizard that allows creation of a new X10 Platform Configuration.
 * 
 * @author egeay
 */
public final class X10NewPlatformConfWizard extends Wizard implements INewWizard {
  
  public X10NewPlatformConfWizard(final X10PlatformConfiguration platformConf) {
    setWindowTitle("New X10 Platform Configuration");
    setDialogSettings(LaunchCore.getInstance().getDialogSettings());

    addPage(new PlatformConfNameWizardPage(platformConf));
  }

  // --- IWizard's interface methods implementation
  
  public boolean performFinish() {
    return false;
  }
  
  // --- IWorkbenchWizard's interface methods implementation

  public void init(final IWorkbench workbench, final IStructuredSelection selection) {
  }
  
  // --- Overridden methods
  
  public boolean needsPreviousAndNextButtons() {
    return true;
  }
  
}
