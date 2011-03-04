/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core;

import org.eclipse.osgi.util.NLS;

/**
 * Bundle class for all messages of "x10dt.search.core".
 * 
 * @author egeay
 */
@SuppressWarnings("all")
public class Messages extends NLS {

  public static String ATM_IndexingLoadingError;

  public static String ATM_IndexingSavingError;

  public static String ATM_NoDataBaseValue;

  public static String FWV_URICreationError;

  public static String SCA_IndexerName;

  public static String SCA_ProjectNatureAccessFailed;

  public static String SCA_ProjectNonExistent;

  public static String SCA_ProjectStatusError;

  public static String TH_BuildTHJobName;

  public static String TH_BuildTHTaskName;

  public static String TH_CollectingData;

  public static String TH_SearchCanceled;

  public static String THFWV_UnknownSuperType;

  public static String XFG_ClassPathResolutionError;

  public static String XFG_IndexerCompilationLogError;

  public static String XFG_JarReadingError;

  public static String XFG_ResourceAccessError;

  public static String XS_ProjectExistenceError;
  
  // --- Private code

  private static final String BUNDLE_NAME = "x10dt.search.core.messages"; //$NON-NLS-1$
  
  static {
    // initialize resource bundle
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }
  
  private Messages() {
  }

}
