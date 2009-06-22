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

/*******************************************************************************
* Copyright (c) 2008 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*    Matthew Kaplan (mmk@us.ibm.com) - specialization to show only one tab (to simplify options for novices/typical users)
*******************************************************************************/

package org.eclipse.imp.x10dt.core.preferences.specialized;

import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.PreferencesTab;
import org.eclipse.imp.preferences.TabbedPreferencesPage;
import org.eclipse.imp.x10dt.core.preferences.generated.X10Preferences;
import org.eclipse.imp.x10dt.core.preferences.generated.X10PreferencesInstanceTab;
import org.eclipse.swt.widgets.TabFolder;

public class X10PreferencesOneTab extends X10Preferences {
	
	protected PreferencesTab[] createTabs(IPreferencesService prefService,
			TabbedPreferencesPage page, TabFolder tabFolder) {
		PreferencesTab[] tabs = new PreferencesTab[1];

		X10PreferencesInstanceTab instanceTab = new X10PreferencesInstanceTabNoDetails(
				prefService);
		instanceTab.createInstancePreferencesTab(page, tabFolder);
		tabs[0] = instanceTab;

		return tabs;
	}

}
