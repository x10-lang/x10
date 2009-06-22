/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.launch;


/**
 * Controls the plug-in life cycle for this project and provides logging services (see {@link AbstractUIBasePlugin}).
 * 
 * @author egeay
 */
public class LaunchCore extends AbstractUIBasePlugin {
  
  /**
   * Unique id for this plugin.
   */
  public static final String PLUGIN_ID = "org.eclipse.imp.x10dt.ui.cpp.launch"; //$NON-NLS-1$
  
  /**
   * Unique id for the C++ Builder.
   */
  public static final String BUILDER_ID = PLUGIN_ID + ".X10CppBuilder"; //$NON-NLS-1$
  
  /**
   * Id for the X10DT C++ Project Nature.
   */
  public static final String X10_CPP_PRJ_NATURE_ID = PLUGIN_ID + ".x10nature"; //$NON-NLS-1$

}
