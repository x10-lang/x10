/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.core.builder.migration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.BackingStoreException;

import x10dt.core.X10DTCorePlugin;
import x10dt.core.utils.ProjectNameComparator;
import x10dt.core.utils.X10DTCoreConstants;

/**
 * This class implements the basic logic for migrating the meta-data of existing X10 projects
 * so that those projects continue to work as expected. Specifically, the nature and builder
 * IDs are updated, along with that of the X10 Runtime Container in the .classpath file.<br>
 * This class provides API/functionality to:
 * <ul>
 *   <li> scan the workspace for projects requiring migration
 *   <li> detect new project additions into the workspace (e.g. by checkout from a source repository)
 *        and adjust their meta-data if needed
 *   <li> update the meta-data of a given set of projects (in support of explicit user actions)
 * </ul>
 * This class makes use of ProjectMigrationDialog to ask the user to confirm the migration of
 * automatically-detected projects.
 * @author rfuhrer
 */
public class ProjectMigrationAssistant {
	/**
	 * Preference key used to store the set of projects that the user told us not to migrate
	 */
	public static final String DONT_MIGRATE_PROJECTS_PREF_KEY = "dontMigrateProjects"; //$NON-NLS-1$

	//
	// The following constants are defined here to avoid cyclic bundle dependencies between x10dt.core,
	// x10dt.ui.launch.cpp and x10dt.ui.launch.java.
	//

	/**
	 * The old value of the nature ID for the Java back-end X10 nature
	 */
	private static final String OLD_JAVA_BACK_END_NATURE_ID= "org.eclipse.imp.x10dt.ui.launch.java.x10nature"; //$NON-NLS-1$

	/**
	 * An even older value of the nature ID for the Java back-end X10 nature
	 */
	private static final String OLDER_JAVA_BACK_END_NATURE_ID = "org.eclipse.imp.x10dt.core.x10nature";

	/**
	 * The old value of the builder ID for the Java back-end X10 builder
	 */
	private static final String OLD_JAVA_BACK_END_BUILDER_ID= "org.eclipse.imp.x10dt.ui.launch.java.X10JavaBuilder"; //$NON-NLS-1$

	/**
	 * The new nature ID for the X10 Java back-end project nature
	 */
	private static final String NEW_JAVA_BACK_END_NATURE_ID= "x10dt.ui.launch.java.x10nature"; //$NON-NLS-1$

	/**
	 * The old value of the nature ID for the C++ back-end X10 nature
	 */
	private static final String OLD_CPP_BACK_END_NATURE_ID= "org.eclipse.imp.x10dt.ui.launch.cpp.x10nature"; //$NON-NLS-1$

	/**
	 * The old value of the builder ID for the C++ back-end X10 builder
	 */
	private static final String OLD_CPP_BACK_END_BUILDER_ID= "org.eclipse.imp.x10dt.ui.launch.cpp.X10CppBuilder"; //$NON-NLS-1$

	/**
	 * The new nature ID for the X10 C++ back-end project nature
	 */
	private static final String NEW_CPP_BACK_END_NATURE_ID= "x10dt.ui.launch.cpp.x10nature"; //$NON-NLS-1$

	/**
	 * The old value for the ID of the X10 runtime container (as used in a project's .classpath file) 
	 */
	private static final String OLD_CONTAINER_ID= "org.eclipse.imp.x10dt.X10_CONTAINER"; //$NON-NLS-1$

	public void installProjectMigrationHook() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(new NewProjectMigrationListener(this), IResourceChangeEvent.POST_CHANGE);
	}

	public void checkAndMigrateAllProjects() {
		IProject[] allProjects= ResourcesPlugin.getWorkspace().getRoot().getProjects();

		Set<IProject> brokenProjects= determineBrokenProjects(allProjects);
		Set<IProject> dontMigrateProjects= retrieveMigrationSuppressedProjects();

		brokenProjects.removeAll(dontMigrateProjects);
		if (brokenProjects.size() > 0) {
			confirmAndMigrateProjects(brokenProjects);
		}
	}

	private Set<IProject> retrieveMigrationSuppressedProjects() {
		Set<IProject> result= new HashSet<IProject>();
		IEclipsePreferences prefs= new InstanceScope().getNode(X10DTCorePlugin.kPluginID);
		String dontAskPref= prefs.get(ProjectMigrationAssistant.DONT_MIGRATE_PROJECTS_PREF_KEY, ""); //$NON-NLS-1$
		String[] dontAskList= dontAskPref.split(":"); //$NON-NLS-1$
		IWorkspaceRoot wsRoot= ResourcesPlugin.getWorkspace().getRoot();

		for(String dontAskProjName: dontAskList) {
			if (dontAskProjName.length() == 0) { continue; }
			IProject proj= wsRoot.getProject(dontAskProjName);
			if (proj.exists()) {
				result.add(proj);
			}
		}
		return result;
	}

	public void confirmAndMigrateProjects(final Set<IProject> brokenProjects) {
		ProjectNameComparator projComp = new ProjectNameComparator();
		final Set<IProject> migrateProjects= new TreeSet<IProject>(projComp);
		final Set<IProject> sortedBrokenProjects= new TreeSet<IProject>(projComp);

		sortedBrokenProjects.addAll(brokenProjects);

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				Shell shell= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				ProjectMigrationDialog pmd= new ProjectMigrationDialog(shell, sortedBrokenProjects, migrateProjects);

				if (pmd.open() == Window.OK) {
					migrate(migrateProjects);
					if (pmd.getDontAskAgain()) {
						// save the list of projects that the user didn't select in a preference...
						Set<IProject> dontMigrateProjects= new HashSet<IProject>();

						dontMigrateProjects.addAll(brokenProjects);
						dontMigrateProjects.removeAll(migrateProjects);

						String dontMigratePref= addProjectsToPrefValue(dontMigrateProjects);

						savePrefValue(dontMigratePref);
					}
				}
			}
			private void savePrefValue(String dontMigratePref) {
				IEclipsePreferences prefs= new InstanceScope().getNode(X10DTCorePlugin.kPluginID);
				prefs.put(ProjectMigrationAssistant.DONT_MIGRATE_PROJECTS_PREF_KEY, dontMigratePref);
				try {
					prefs.flush();
				} catch (BackingStoreException e) {
					X10DTCorePlugin.getInstance().logException("Exception encountered while saving preference", e); //$NON-NLS-1$
				}
			}
			private String addProjectsToPrefValue(Set<IProject> projects) {
				IEclipsePreferences prefs= new InstanceScope().getNode(X10DTCorePlugin.kPluginID);
				String curValue= prefs.get(ProjectMigrationAssistant.DONT_MIGRATE_PROJECTS_PREF_KEY, ""); //$NON-NLS-1$
				String[] curProjectNames= curValue.split(":"); //$NON-NLS-1$
				List<String> curProjectList= Arrays.asList(curProjectNames);
				StringBuilder sb= new StringBuilder(curValue);

				for(IProject dontMigrateProj: projects) {
					if (!curProjectList.contains(dontMigrateProj.getName())) {
						if (sb.length() > 0) { sb.append(':'); }
						sb.append(dontMigrateProj.getName());
					}
				}
				return sb.toString();
			}
		});
	}

	public Set<IProject> determineBrokenProjects(IProject[] allProjects) {
		final Set<IProject> brokenProjects= new HashSet<IProject>();

		for(IProject project: allProjects) {
			if (projectIsBroken(project)) {
				brokenProjects.add(project);
			}
		}
		return brokenProjects;
	}

	public boolean projectIsBroken(IProject project) {
		if (!project.isOpen()) {
			// If the project is closed, it's not possible to retrieve the project description, so
			// just bypass it for now, and process it if/when it gets reopened.
			return false;
		}
		try {
			IProjectDescription projDesc= project.getDescription();
			String[] natures= projDesc.getNatureIds();
			ICommand[] buildCmds= projDesc.getBuildSpec();

			if (findNature(natures, OLD_JAVA_BACK_END_NATURE_ID) >= 0 ||
				findNature(natures, OLDER_JAVA_BACK_END_NATURE_ID) >= 0 ||
				findNature(natures, OLD_CPP_BACK_END_NATURE_ID) >= 0 ||
				findBuilder(buildCmds, OLD_JAVA_BACK_END_BUILDER_ID) >= 0 ||
				findBuilder(buildCmds, OLD_CPP_BACK_END_BUILDER_ID) >= 0 ||
				findOldRuntimeContainerRef(project) >= 0) {
				return true;
			}
		} catch (CoreException e) {
			X10DTCorePlugin.getInstance().logException("Exception encountered while examining project description for X10DT migration", e); //$NON-NLS-1$
		}
		return false;
	}

	private int findOldRuntimeContainerRef(IProject project) throws JavaModelException {
		IJavaProject javaProj= JavaCore.create(project);

		if (!javaProj.exists()) { // this isn't a Java/JDT project, so there's no JDT classpath to examine
			return -1;
		}

		IClasspathEntry[] cpEntries= javaProj.getRawClasspath();

		int idx= 0;
		for(IClasspathEntry cpEntry: cpEntries) {
			if (cpEntry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
				if (cpEntry.getPath().toString().equals(OLD_CONTAINER_ID)) {
					return idx;
				}
			}
			idx++;
		}
		return -1;
	}

	private int findBuilder(ICommand[] buildCmds, String oldBuilderID) {
		int idx= 0;
		for(ICommand cmd: buildCmds) {
			if (cmd.getBuilderName().equals(oldBuilderID)) {
				return idx;
			}
			idx++;
		}
		return -1;
	}

	private int findNature(String[] natures, String natureId) {
		int idx= 0;
		for(String nature: natures) {
			if (nature.equals(natureId)) {
				return idx;
			}
			idx++;
		}
		return -1;
	}

	public void migrate(Set<IProject> migrateProjects) {
		for(final IProject project: migrateProjects) {
			IWorkspaceRunnable r= new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					fixNature(project);
					fixContainer(project);
				}
			};
			try {
				ResourcesPlugin.getWorkspace().run(r, null);
			} catch (CoreException e) {
				X10DTCorePlugin.getInstance().logException("Exception encountered while attempting to migrate X10 project " + project.getName(), e);
			}
		}
	}

	private void fixContainer(IProject project) {
		try {
			IJavaProject javaProj= JavaCore.create(project);
			IClasspathEntry[] cpEntries= javaProj.getRawClasspath();
			int idx= findOldRuntimeContainerRef(project);

			if (idx >= 0) {
				cpEntries[idx]= JavaCore.newContainerEntry(new Path(X10DTCoreConstants.X10_CONTAINER_ENTRY_ID));
			}

			javaProj.setRawClasspath(cpEntries, null);
		} catch (JavaModelException e) {
			X10DTCorePlugin.getInstance().logException("Exception encountered while updating project classpath", e); //$NON-NLS-1$
		}
	}

	private void fixNature(IProject project) {
		try {
			IProjectDescription pd= project.getDescription();
			String[] natures= pd.getNatureIds();
			int natureIdx= findNature(natures, OLD_JAVA_BACK_END_NATURE_ID);

			if (natureIdx < 0) {
				natureIdx= findNature(natures, OLD_CPP_BACK_END_NATURE_ID);
				if (natureIdx < 0) {
					natureIdx= findNature(natures, OLDER_JAVA_BACK_END_NATURE_ID);
					if (natureIdx >= 0) {
						natures[natureIdx]= NEW_JAVA_BACK_END_NATURE_ID;
					}
				} else {
					if (natureIdx >= 0) {
						natures[natureIdx]= NEW_CPP_BACK_END_NATURE_ID;
					}
				}
			} else {
				natures[natureIdx]= NEW_JAVA_BACK_END_NATURE_ID;
			}
			pd.setNatureIds(natures);

			// The configuration of the new nature will take care of adding the new builder
			// to the project configuration; all we have to do here is get rid of the old one.
			ICommand[] oldCmds= pd.getBuildSpec();
			ICommand[] newCmds= new ICommand[oldCmds.length-1];
			int builderIdx= findBuilder(oldCmds, OLD_JAVA_BACK_END_BUILDER_ID);

			if (builderIdx < 0) {
				builderIdx= findBuilder(oldCmds, OLD_CPP_BACK_END_BUILDER_ID);
			}
			if (builderIdx < 0) {
				builderIdx= findBuilder(oldCmds, OLDER_JAVA_BACK_END_NATURE_ID);
			}
			if (builderIdx >= 0) {
				System.arraycopy(oldCmds, 0, newCmds, 0, builderIdx);
				System.arraycopy(oldCmds, builderIdx+1, newCmds, builderIdx, oldCmds.length - builderIdx - 1);
				pd.setBuildSpec(newCmds);
			}

			project.setDescription(pd, null);
		} catch (CoreException e) {
			X10DTCorePlugin.getInstance().logException("Exception encountered while updating X10 project configuration", e); //$NON-NLS-1$
		}
	}
}
