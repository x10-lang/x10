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
package org.eclipse.imp.x10dt.core.preferences;


import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.PreferencesTab;
import org.eclipse.imp.preferences.TabbedPreferencesPage;
import org.eclipse.imp.x10dt.core.X10Plugin;
import org.eclipse.swt.widgets.TabFolder;

/**
 * The Safari-based tabbed preferences page for language X10.
 * 
 * Naming conventions:  This template uses the language name as a prefix
 * for naming the language plugin class and the preference-tab classes.
 * 	
 */
public class X10TabbedPreferencesPage extends TabbedPreferencesPage {
	
	public X10TabbedPreferencesPage() {
		super();
		// Get the language-specific preferences service
		prefService = X10Plugin.getPreferencesService();
	}
	
	
	protected PreferencesTab[] createTabs(
			IPreferencesService prefService, TabbedPreferencesPage page, TabFolder tabFolder) 
	{
		PreferencesTab[] tabs = new PreferencesTab[4];
		
		X10ProjectPreferencesTab projectTab = new X10ProjectPreferencesTab(prefService);
		projectTab.createProjectPreferencesTab(page, tabFolder);
		tabs[0] = projectTab;

		X10InstancePreferencesTab instanceTab = new X10InstancePreferencesTab(prefService);
		instanceTab.createInstancePreferencesTab(page, tabFolder);
		tabs[1] = instanceTab;
		
		X10ConfigurationPreferencesTab configurationTab = new X10ConfigurationPreferencesTab(prefService);
		configurationTab.createConfigurationPreferencesTab(page, tabFolder);
		tabs[2] = configurationTab;

		X10DefaultPreferencesTab defaultTab = new X10DefaultPreferencesTab(prefService);
		defaultTab.createDefaultPreferencesTab(page, tabFolder);
		tabs[3] = defaultTab;
		
		return tabs;
	}

}
