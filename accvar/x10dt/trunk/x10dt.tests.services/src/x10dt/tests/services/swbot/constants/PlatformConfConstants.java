/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.tests.services.swbot.constants;

/**
 * Contains constant strings related to the X10 Platform Configuration.
 * 
 * @author egeay
 */
@SuppressWarnings("all")
public final class PlatformConfConstants {
  
  public static final String PLATFORM_CONF_FILE = "x10_platform.conf"; //$NON-NLS-1$
  
  public static final String PLATFORM_CONF_FILE_PATH = "/{0}/x10_platform.conf"; //$NON-NLS-1$
  
  public static final String CPP_COMPILATION_LINKING_TAB = "C++ Compilation and Linking"; //$NON-NLS-1$
  
  public static final String SAVE_PLATFORM_TOOLTIP_BT = "Save Configuration"; //$NON-NLS-1$
  
  public static final String VALIDATE_PLATFORM_TOOLTIP_BT = "Validate Configuration"; //$NON-NLS-1$

  // --- Connection

  public static final String PLATFORM_CONFIGURATION_NAME = "Configuration name"; //$NON-NLS-1$

  public static final String PLATFORM_CONFIGURATION_DESCRIPTION = "Description"; //$NON-NLS-1$
  
  public static final String REMOTE_CONNECTION = "Remote connection"; //$NON-NLS-1$
  
  public static final String LOCAL_CONNECTION = "Local connection"; //$NON-NLS-1$
  
  public static final String ADD_BUTTON = "Add"; //$NON-NLS-1$
  
  public static final String VALIDATE_BUTTON = "Validate"; //$NON-NLS-1$
  
  public static final String WORKSPACE_PERSISTED_GROUP = "Workspace Persisted Data (*)"; //$NON-NLS-1$
  
  public static final String HOST_TEXT_LABEL = "Host"; //$NON-NLS-1$
  
  public static final String HOST_PORT_LABEL = "Port"; //$NON-NLS-1$

  public static final String USER_TEXT_LABEL = "User"; //$NON-NLS-1$
  
  public static final String PUBLIC_KEY_AUTH_RADIO_BUTTON = "Public key authentication"; //$NON-NLS-1$
  
  public static final String PRIVATE_KEY_FILE_LABEL = "File with private key"; //$NON-NLS-1$
  
  public static final String PASSWORD_TEXT_LABEL = "Password"; //$NON-NLS-1$
  
  public static final String PORT_FORWARDING_CHECKBOX = "Use port forwarding"; //$NON-NLS-1$

  public static final String PORT_FORWARDING_TIMEOUT = "Connection timeout"; //$NON-NLS-1$

  public static final String PORT_FORWARDING_LOCAL_ADDR = "Local address"; //$NON-NLS-1$

  // --- C++ Compilation and Linking
  
  public static final String OUTPUT_FOLDER_TEXT_LABEL = "Output folder"; //$NON-NLS-1$

  public static final String X10_DIST_TEXT_LABEL = "X10 distribution"; //$NON-NLS-1$

  public static final String CPP_OS_COMBO_BOX = "Operating System"; //$NON-NLS-1$

  public static final String CPP_ARCHITECTURE_COMBO_BOX = "Architecture"; //$NON-NLS-1$

  public static final String CPP_64_BIT_CHECKBOX = "64-bit"; //$NON-NLS-1$

  public static final String REMOTE_OUTPUT_FOLDER_TEXTBOX = "Output folder"; //$NON-NLS-1$

  public static final String X10_DISTRIBUTION_PATH_TEXTBOX = "X10 distribution"; //$NON-NLS-1$

  public static final String PGAS_DISTRIBUTION_PATH_TEXTBOX = "PGAS distribution"; //$NON-NLS-1$

  public static final String CPP_USE_X10_DIST_PGAS = "Use PGAS distribution provided with X10 distribution"; //$NON-NLS-1$

  public static final String CPP_CONNECTION_COMMUNICATION_TAB = "Connection and Communication Interface"; //$NON-NLS-1$

  public static final String CPP_INTERPROCESS_TYPE = "Type"; //$NON-NLS-1$

  public static final String CPP_INTERPROCESS_MODE = "Mode"; //$NON-NLS-1$

  public static final String CPP_VALIDATION_PROGESS_DLG = "Progress Information"; //$NON-NLS-1$

  public static final String CPP_VALIDATION_FAILURE_DLG = "Validation Failure"; //$NON-NLS-1$

  public static final String REMOTE_DEBUGGER_FOLDER_TEXTBOX = "Debugger folder"; //$NON-NLS-1$

  public static final String REMOTE_DEBUGGING_PORT_LABEL = "Debugging port"; //$NON-NLS-1$


  // --- Private code
  
  private PlatformConfConstants() {}

}
