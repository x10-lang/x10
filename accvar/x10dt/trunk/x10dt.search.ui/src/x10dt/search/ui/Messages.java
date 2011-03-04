/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.ui;

import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Provides access to all images of this plugin.
 * 
 * @author egeay
 */
@SuppressWarnings("all")
public class Messages extends NLS {

	public static String SearchPage_searchFor_label;
	public static String SearchPage_searchFor_type;
	public static String SearchPage_searchIn_label;
	public static String SearchPage_searchIn_XRX;
	public static String SearchPage_searchFor_method;
	public static String SearchPage_searchIn_sources;
	public static String SearchPage_searchFor_field;
	public static String SearchPage_searchFor_package;
	public static String SearchPage_searchIn_projects;
	public static String SearchPage_searchIn_libraries;
	public static String SearchPage_searchFor_constructor;

	public static String SearchPage_limitTo_label;
	public static String SearchPage_limitTo_declarations;
	public static String SearchPage_limitTo_implementors;
	public static String SearchPage_limitTo_references;
	public static String SearchPage_limitTo_allOccurrences;
	public static String SearchPage_limitTo_readReferences;
	public static String SearchPage_limitTo_writeReferences;
	public static String SearchPage_expression_label;
	public static String SearchPage_expression_caseSensitive;

	public static String X10SearchQuery_task_label;
	public static String X10SearchQuery_label;
	public static String X10SearchQuery_status_ok_message;
	
	// --- Private code

	private Messages() {
	}

	private static final String BUNDLE_NAME = "x10dt.search.ui.messages"; //$NON-NLS-1$

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

}
