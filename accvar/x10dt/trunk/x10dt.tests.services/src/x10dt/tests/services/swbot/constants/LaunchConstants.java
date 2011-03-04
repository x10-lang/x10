/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.tests.services.swbot.constants;

import java.util.regex.Pattern;

/**
 * Contains string constants of the X10DT launch configurations.
 * 
 * @author egeay
 */
@SuppressWarnings("all")
public final class LaunchConstants {
  
  public static final String RUN_MENU = "Run"; //$NON-NLS-1$

  public static final String RUN_AS_MENU = "Run As"; //$NON-NLS-1$

  public static final String RUN_HISTORY_ITEM = "Run History"; //$NON-NLS-1$

  public static final String RUN_CONFS_MENU_ITEM = "Run Configurations..."; //$NON-NLS-1$

  public static final String RUN_CONF_DIALOG_TITLE = "Run Configurations"; //$NON-NLS-1$

  public static final String NEW_CONF_CONTEXT_MENU = "New"; //$NON-NLS-1$

  public static final String RUN_BUTTON = "Run"; //$NON-NLS-1$

  public static final String CPP_LAUNCH_CONFIG_NUM_PLACES = "Number of places:"; //$NON-NLS-1$

  // --- Java back-end specific

  public static final String JAVA_BACK_END_LAUNCH_CONFIG_TYPE= "X10 Application (Java back-end)"; //$NON-NLS-1$

  public static final String JAVA_BACK_END_PROJECT_DIALOG_NAME_FIELD = "Name:"; //$NON-NLS-1$

  public static final String JAVA_BACK_END_LAUNCH_CONFIG_MAIN_TAB= "Main"; //$NON-NLS-1$

  public static final String JAVA_BACK_END_LAUNCH_CONFIG_PROJECT= "Project:"; //$NON-NLS-1$

  public static final String JAVA_BACK_END_LAUNCH_CONFIG_MAIN_CLASS= "Main class:"; //$NON-NLS-1$

  public static final String JAVA_BACK_END_LAUNCH_CONFIG_PLACES_TAB = "Places and Hosts"; //$NON-NLS-1$

  // --- C++ back-end specific

  public static final String CPP_BACK_END_PROJECT_DIALOG_NAME_FIELD = "Name:"; //$NON-NLS-1$

  public static final String NEW_CPP_LAUNCH_CONFIG = "X10 Application (C++ back-end)"; //$NON-NLS-1$

  // The following two patterns are needed b/c the "Run As" menu has different contents
  // depending on what installed plugins contribute to that menu. Also, the order, and
  // therefore the numeric shortcuts, seem unreliable.
  public static final Pattern RUN_AS_X10_APPLICATION_CPP_BACK_END_PAT= Pattern.compile("(&[1-9] &)?X10 application \\(C\\+\\+ back-end\\).*"); //$NON-NLS-1$

  public static final Pattern RUN_AS_X10_APPLICATION_JAVA_BACK_END_PAT= Pattern.compile("(&[1-9] )?X10 Application \\(Java back-end\\).*"); //$NON-NLS-1$

  public static final String CPP_LAUNCH_CONFIG_APPLICATION_TAB = "Application"; //$NON-NLS-1$

  public static final String CPP_LAUNCH_CONFIG_X10_PROJECT = "X10 Project"; //$NON-NLS-1$

  public static final String CPP_LAUNCH_CONFIG_MAIN_CLASS = "Main class"; //$NON-NLS-1$

  public static final String CPP_LAUNCH_CONFIG_COMM_INTERFACE_TAB = "Communication Interface"; //$NON-NLS-1$

  // --- Private code

  private LaunchConstants() {}

}
