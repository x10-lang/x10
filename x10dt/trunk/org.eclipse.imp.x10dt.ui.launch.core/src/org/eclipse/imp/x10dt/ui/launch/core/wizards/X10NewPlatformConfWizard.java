/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.wizards;

import java.util.Set;

import org.eclipse.imp.x10dt.ui.launch.core.LaunchCore;
import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.IX10PlatformConfiguration;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Responsible for creating the wizard that allows creation of a new X10 Platform Configuration.
 * 
 * @author egeay
 */
public final class X10NewPlatformConfWizard extends Wizard implements INewWizard {
  
  /**
   * Creates the wizard with two pages. One with for the name and the other for the first-time definition.
   * 
   * @param confNames The current set of platform configuration names.
   */
  public X10NewPlatformConfWizard(final Set<String> confNames) {
    setWindowTitle(Messages.XNPC_WindowTitle);
    setDialogSettings(LaunchCore.getInstance().getDialogSettings());

    addPage(new PlatformConfNameWizardPage(confNames));
  }
  
  /**
   * Creates the wizard with one page. The one for editing a current configuration.
   * 
   * @param platformConfiguration The current platform configuration to consider.
   */
  public X10NewPlatformConfWizard(final IX10PlatformConfiguration platformConfiguration) {
    setWindowTitle(Messages.XNPC_EditWindowTitle);
    setDialogSettings(LaunchCore.getInstance().getDialogSettings());
    
    final IWizardPage wizardPage = new PlatformConfDefWizardPage(platformConfiguration);
    
    final String location = (platformConfiguration.isLocal()) ? Messages.PCNWP_LocalStr : Messages.PCNWP_RemoteStr;
    final String backEnd = (platformConfiguration.isCplusPlus()) ? Messages.PCNWP_CPPBt : Messages.PCNWP_JavaBt;
    wizardPage.setTitle(NLS.bind(Messages.PCNWP_DefWizPageTitle, location, backEnd));
    wizardPage.setDescription(NLS.bind(Messages.PCNWP_DefWizPageDescr, location, backEnd));
    
    addPage(wizardPage);
  }

  // --- IWizard's interface methods implementation
  
  public boolean performFinish() {
    this.fPlatformConfiguration = new X10PlatformConfiguration();
    return ((IPlaftormConfWizardPage) getStartingPage()).performFinish(this.fPlatformConfiguration);
  }
  
  // --- IWorkbenchWizard's interface methods implementation

  public void init(final IWorkbench workbench, final IStructuredSelection selection) {
  }
  
  // --- Public services
  
  /**
   * Returns the new platform configuration after editing in the wizard page.
   * 
   * @return A non-null implementation of {@link IX10PlatformConfiguration}.
   */
  public IX10PlatformConfiguration getPlatformConfiguration() {
    return this.fPlatformConfiguration;
  }
  
  // --- Overridden methods
  
  public boolean needsPreviousAndNextButtons() {
    return true;
  }
  
  // --- Fields
  
  private X10PlatformConfiguration fPlatformConfiguration;
  
}
