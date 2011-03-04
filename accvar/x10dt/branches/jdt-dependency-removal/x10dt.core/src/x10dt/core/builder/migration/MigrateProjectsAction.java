/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/

package x10dt.core.builder.migration;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * This class provides a means for users to explicitly migrate X10 projects to the
 * new meta-data (nature, builder, classpath container IDs). Useful if the user has
 * earlier specified not to be asked about migrating a given project, and later
 * changes his mind. 
 * @author rfuhrer
 */
public class MigrateProjectsAction implements IObjectActionDelegate {
	private final Set<IProject> fProjects= new HashSet<IProject>();

	public void setActivePart(IAction action, IWorkbenchPart targetPart) { }

	public void run(IAction action) {
		ProjectMigrationAssistant pma= new ProjectMigrationAssistant();

		pma.migrate(fProjects);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		fProjects.clear();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;

			for(Object selObj: structuredSelection.toArray()) {
				if (selObj instanceof ISourceProject) {
					ISourceProject javaProject = (ISourceProject) selObj;
					fProjects.add(javaProject.getRawProject());
				}
			}
		}
	}
}
