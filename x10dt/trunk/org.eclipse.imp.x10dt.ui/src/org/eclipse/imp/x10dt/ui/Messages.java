/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui;

import org.eclipse.osgi.util.NLS;

/**
 * Provides access to all images of this plugin.
 * 
 * @author egeay
 */
@SuppressWarnings("all")
public class Messages extends NLS {
  
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
  
  // --- Private code
  
  private Messages() {
  }
  
  private static final String BUNDLE_NAME = "org.eclipse.imp.x10dt.ui.messages"; //$NON-NLS-1$
  
  static {
    // initialize resource bundle
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }
  
}
