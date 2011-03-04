/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors;

/**
 * Lists the different Open MPI version commands.
 * 
 * @author egeay
 */
public enum EOpenMPIVersion {
  
  /**
   * Defines an automatic detection of Open MPI commands.
   */
  EAutoDetect,
  
  /**
   * Defines commands for Open MPI 1.2.
   */
  EVersion_1_2,
  
  /**
   * Defines commands for Open MPI 1.3.
   */
  EVersion_1_3,
  
  /**
   * Defines commands for Open MPI 1.4.
   */
  EVersion_1_4;

}
