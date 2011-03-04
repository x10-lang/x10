/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package x10dt.ui.launch.cpp.wizards;

import org.eclipse.imp.ui.wizards.NewProjectWizardPageOne;

import x10dt.core.X10DTCorePlugin;
import x10dt.ui.launch.core.wizards.X10ProjectPropertiesWizardPage;

final class CppProjectPropertiesWizardPage extends
		X10ProjectPropertiesWizardPage {

	public CppProjectPropertiesWizardPage(NewProjectWizardPageOne firstPage) {
		super(firstPage);
	}

	// --- Overridden methods

	protected String[] getNatureIds() {
		return new String[] { X10DTCorePlugin.X10_CPP_PRJ_NATURE_ID };
	}
}
