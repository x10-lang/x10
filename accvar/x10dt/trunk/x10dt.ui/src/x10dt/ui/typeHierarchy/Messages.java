/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.typeHierarchy;

import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Provides access to all images of this plugin.
 * 
 * @author egeay
 */
@SuppressWarnings("all")
public class Messages extends NLS {

	private static final String BUNDLE_FOR_CONSTRUCTED_KEYS= "x10dt.ui.typeHierarchy.ConstructedMessages";//$NON-NLS-1$
	private static ResourceBundle fgBundleForConstructedKeys= ResourceBundle.getBundle(BUNDLE_FOR_CONSTRUCTED_KEYS);

	/**
	 * Returns the message bundle which contains constructed keys.
	 *
	 * @since 3.1
	 * @return the message bundle
	 */
	public static ResourceBundle getBundleForConstructedKeys() {
		return fgBundleForConstructedKeys;
	}
	
	public static String AXLS_ConfCreationError;
	public static String AXLS_ConfCreationSavingErrorMsg;
	public static String AXLS_MainTypeSearchError;
	public static String AXLS_MainTypeSearchErrorMsg;
	public static String AXLS_MainTypeSearchInternalError;
	public static String AXLS_MultipleConfDialogMsg;
	public static String AXLS_MultipleConfDialogTitle;
	public static String LU_MainTypeSearchResult;
	public static String LU_NoMainTypeFound;
	public static String XTSD_SelectX10TypeDlgTitle;
	public static String XTSD_SelectX10TypeMsg;
	public static String XU_ParsingX10Files;
	public static String XU_SearchForMainTypes;
	public static String XU_X10FileReadingError;

	public static String FilteredTypesSelectionDialog_TypeFiltersPreferencesAction_label;
	public static String FilteredTypesSelectionDialog_default_package;
	public static String FilteredTypesSelectionDialog_dialogMessage;
	public static String FilteredTypesSelectionDialog_error_type_doesnot_exist;
	public static String FilteredTypesSelectionDialog_library_name_format;
	public static String FilteredTypesSelectionDialog_searchJob_taskName;
	public static String FilteredTypeSelectionDialog_showContainerForDuplicatesAction;
	public static String FilteredTypeSelectionDialog_titleFormat;

	public static String TypeSelectionDialog_errorMessage;
	public static String TypeSelectionDialog_dialogMessage;
	public static String TypeSelectionDialog_errorTitle;
	public static String TypeSelectionDialog_lowerLabel;
	public static String TypeSelectionDialog_upperLabel;
	public static String TypeSelectionDialog_notypes_title;
	public static String TypeSelectionDialog_notypes_message;
	public static String TypeSelectionDialog_error3Message;
	public static String TypeSelectionDialog_error3Title;
	public static String TypeSelectionDialog_progress_consistency;
	public static String TypeSelectionDialog_error_type_doesnot_exist;
	public static String TypeSelectionDialog2_title_format;
	
	public static String TypeSelectionDialog_keybindingWarning_title;
	public static String TypeSelectionDialog_keybindingWarning_message;

	public static String TypeSelectionComponent_label;
	public static String TypeSelectionComponent_menu;
	public static String TypeSelectionComponent_show_status_line_label;
	public static String TypeSelectionComponent_fully_qualify_duplicates_label;

	public static String TypeInfoViewer_job_label;
	public static String TypeInfoViewer_job_error;
	public static String TypeInfoViewer_job_cancel;
	public static String TypeInfoViewer_default_package;
	public static String TypeInfoViewer_progress_label;
	public static String TypeInfoViewer_searchJob_taskName;
	public static String TypeInfoViewer_syncJob_label;
	public static String TypeInfoViewer_syncJob_taskName;
	public static String TypeInfoViewer_progressJob_label;
	public static String TypeInfoViewer_remove_from_history;
	public static String TypeInfoViewer_separator_message;
	public static String TypeInfoViewer_library_name_format;
	
	public static String OpenTypeAction_createProjectFirst;
	public static String OpenTypeAction_description;
	public static String OpenTypeAction_tooltip;

	public static String OpenTypeAction_no_active_WorkbenchPage;
	public static String OpenTypeAction_multiStatusMessage;
	public static String OpenTypeAction_errorMessage;
	public static String OpenTypeAction_errorTitle;
	public static String OpenTypeAction_label;
	public static String OpenTypeAction_dialogTitle;
	public static String OpenTypeAction_dialogMessage;
	
	public static String OpenTypeHierarchyUtil_error_open_view;
	public static String OpenTypeHierarchyUtil_error_open_perspective;
	public static String OpenTypeHierarchyUtil_error_open_editor;
	public static String OpenTypeHierarchyUtil_selectionDialog_title;
	public static String OpenTypeHierarchyUtil_selectionDialog_message;

	public static String StatusBarUpdater_num_elements_selected;
	
	public static String OpenTypeInHierarchyAction_label;
	public static String OpenTypeInHierarchyAction_description;
	public static String OpenTypeInHierarchyAction_tooltip;
	public static String OpenTypeInHierarchyAction_dialogMessage;
	public static String OpenTypeInHierarchyAction_dialogTitle;
	
	public static String X10ElementLabels_comma_string;
	public static String X10ElementLabels_concat_string;
	public static String X10ElementLabels_declseparator_string;
	public static String X10ElementLabels_anonym_type;
	public static String X10ElementLabels_anonym;
	public static String X10ElementLabels_default_package;

	// --- Private code

	private Messages() {
	}

	private static final String BUNDLE_NAME = "x10dt.ui.typeHierarchy.Messages"; //$NON-NLS-1$

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

}
