/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.core;

import org.eclipse.ui.IStartup;

import x10dt.core.builder.migration.ProjectMigrationAssistant;

/**
 * This IStartup implementation migrates any workspace projects that have old X10DT
 * meta-data in them to the current format. Necessitated by the renaming of the
 * nature, builder and X10 Runtime Container IDs between X10DT 2.0.6 and 2.1.0.
 * @author rfuhrer
 */
public class CoreStartup implements IStartup {
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup() {
		ProjectMigrationAssistant pma= new ProjectMigrationAssistant();

		pma.checkAndMigrateAllProjects();

		pma.installProjectMigrationHook();
	}
}
