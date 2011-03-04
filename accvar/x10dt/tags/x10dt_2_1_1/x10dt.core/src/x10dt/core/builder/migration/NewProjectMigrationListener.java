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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;

import x10dt.core.X10DTCorePlugin;

/**
 * Class that listens for resource changes to detect project creation events,
 * and, if necessary, migrates the X10 nature/builder/classpath metadata in
 * the new projects. Intended to handle the creation of projects via checkout
 * from a source repository.
 * @author rfuhrer
 */
final class NewProjectMigrationListener implements IResourceChangeListener {
	private final ProjectMigrationAssistant fMigrationAssistant;

	public NewProjectMigrationListener(ProjectMigrationAssistant pma) {
		fMigrationAssistant= pma;
	}

	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta= event.getDelta();
		final Set<IProject> brokenProjects= new HashSet<IProject>();

		try {
			delta.accept(new IResourceDeltaVisitor() {
				public boolean visit(IResourceDelta delta) throws CoreException {
					IResource rsrc = delta.getResource();

					if (rsrc instanceof IProject) {
						if (delta.getKind() == IResourceDelta.ADDED ||
							delta.getKind() == IResourceDelta.CHANGED && (delta.getFlags() & IResourceDelta.DESCRIPTION) != 0) {
							IProject proj= (IProject) rsrc;

							if (fMigrationAssistant.projectIsBroken(proj)) {
								brokenProjects.add(proj);
							}
						}
						return false; // don't bother visiting children of the project
					}
					return (rsrc instanceof IWorkspaceRoot); // only visit the project children of the ws root
				}
			});
		} catch (CoreException e) {
			X10DTCorePlugin.getInstance().logException("Exception encountered while examining resource delta for new projects", e); //$NON-NLS-1$
		}
		if (brokenProjects.size() > 0) {
			fMigrationAssistant.confirmAndMigrateProjects(brokenProjects);
		}
	}
}
