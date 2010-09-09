/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.core.utils;

/**
 * Contains some constants useful for the X10DT Core plug-in and its dependents.
 * 
 * @author egeay
 */
public final class X10DTCoreConstants {
  
  /**
   * The id (or path) identifying uniquely the X10 Runtime Container for X10 project class path definition.
   */
  public static final String X10_CONTAINER_ENTRY_ID = "x10dt.X10_CONTAINER"; //$NON-NLS-1$
  
  /**
   * Problem marker ID for X10 compiler errors/warnings/infos. Must match the ID of the marker extension defined in plugin.xml.
   */
  public static final String PROBLEMMARKER_ID= "x10dt.core.problemMarker";
  
  
  // --- Private code
  
  private X10DTCoreConstants() {}

}
