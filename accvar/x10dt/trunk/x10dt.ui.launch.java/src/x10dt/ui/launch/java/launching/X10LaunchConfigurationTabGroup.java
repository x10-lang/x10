/*******************************************************************************
* Copyright (c) 2008 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation

*******************************************************************************/

/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package x10dt.ui.launch.java.launching;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab;

/**
 * Defines the group of tabs for the Java back-end launch configuration.
 * 
 * @author egeay
 */
public class X10LaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup {

  public void createTabs(final ILaunchConfigurationDialog dialog, final String mode) {
    final PlacesAndHostsTab placesAndHostTab = new PlacesAndHostsTab();
    final ILaunchConfigurationTab[] tabs= new ILaunchConfigurationTab[] {
      new X10MainTab(),
      new JavaArgumentsTab(),
      new VMsLocationTab(placesAndHostTab),
      placesAndHostTab,
      new JavaJRETab(),
      new JavaClasspathTab(),
      new SourceLookupTab(),
      new EnvironmentTab(),
      new CommonTab()
    };
    setTabs(tabs);
  }

}
