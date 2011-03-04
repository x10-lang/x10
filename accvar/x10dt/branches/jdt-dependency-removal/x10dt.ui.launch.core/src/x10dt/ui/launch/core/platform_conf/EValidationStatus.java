/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.core.platform_conf;

/**
 * Represents the different validation status.
 * 
 * @author egeay
 */
public enum EValidationStatus {
  
  /**
   * Identifies that the validation step ended up with an internal error.
   */
  ERROR,
  
  /**
   * Identifies that the validation failed.
   */
  FAILURE,
  
  /**
   * Identifies that the validation status is unknown.
   */
  UNKNOWN,
  
  /**
   * Identifies that the configuration is valid.
   */
  VALID;

}
