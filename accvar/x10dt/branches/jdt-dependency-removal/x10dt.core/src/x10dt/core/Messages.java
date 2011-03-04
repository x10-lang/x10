/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.core;

import org.eclipse.osgi.util.NLS;

/**
 * Bundle class for all messages of "x10dt.ui.cpp.launch".
 * 
 * @author egeay
 */
@SuppressWarnings("all")
public class Messages extends NLS {
  
  public static String XBU_URLResolutionError;
  public static String XCCI_BundleNotFoundError;
  public static String XCCI_ClasspathResIOError;
  public static String XCCI_NoX10JARFound;
  public static String XCCI_WrongClassPathContainer;
  
  // --- Private code
  
  private Messages() {
  }
  
  private static final String BUNDLE_NAME = "x10dt.core.messages"; //$NON-NLS-1$
  
  static {
    // initialize resource bundle
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }
  
}
