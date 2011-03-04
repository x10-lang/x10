/******************************************************************************
 * Copyright (c) 2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial Implementation
 *
 *****************************************************************************/
package x10dt.ui.launch.rms.core.environment;

import org.eclipse.osgi.util.NLS;

@SuppressWarnings("all")
public final class AttributeNames extends NLS {

  public static String CONNECTION_ADDRESS;

  public static String CONNECTION_PORT;

  public static String CONNECTION_TIMEOUT;

  public static String KEY_PASSPHRASE;

  public static String KEY_PATH;

  public static String LOGIN_PASSWORD;

  public static String LOGIN_USERNAME;

  // --- Private code

  private AttributeNames() {
  }

  // --- Fields

  private static final String BUNDLE_NAME = "x10dt.ui.launch.rms.core.environment.attribute_names"; //$NON-NLS-1$

  static {
    NLS.initializeMessages(BUNDLE_NAME, AttributeNames.class);
  }

}
