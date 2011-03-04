/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Eric Rizzo - Removed unused messages for old Collapse All action
 *******************************************************************************/
package x10dt.ui.navigator.internal;

import org.eclipse.osgi.util.NLS;

public final class PackagesMessages extends NLS {

	private static final String BUNDLE_NAME= "x10dt.ui.navigator.internal.PackagesMessages";//$NON-NLS-1$

	private PackagesMessages() {
		// Do not instantiate
	}

	
	
	public static String PackageExplorer_filters;
	public static String PackageExplorer_gotoTitle;
	public static String PackageExplorer_openPerspective;
	public static String PackageExplorer_refactoringTitle;
	public static String PackageExplorer_referencedLibs;
	public static String PackageExplorer_binaryProjects;
	public static String PackageExplorer_title;
	public static String PackageExplorer_toolTip;
	public static String PackageExplorer_toolTip2;
	public static String PackageExplorer_toolTip3;
	public static String PackageExplorer_openWith;
	
	public static String PackageExplorer_element_not_present;
	
	public static String PackageExplorer_filteredDialog_title;
	public static String PackageExplorer_notFound;
	public static String PackageExplorer_removeFilters;
	public static String PackageExplorerContentProvider_update_job_description;
	
	public static String PackageExplorerPart_workspace;
	public static String PackageExplorerPart_workingSetModel;
	public static String PackageExplorerPart_notFoundSepcific;
	public static String PackageExplorerPart_removeFiltersSpecific;
	
	
	public static String LayoutActionGroup_label;
	public static String LayoutActionGroup_flatLayoutAction_label;
	public static String LayoutActionGroup_hierarchicalLayoutAction_label;
	public static String LayoutActionGroup_show_libraries_in_group;
	
	
	public static String LibraryContainer_name;

	static {
		NLS.initializeMessages(BUNDLE_NAME, PackagesMessages.class);
	}
}
