/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

import x10dt.ui.launch.core.platform_conf.EValidationStatus;

/**
 * Responsible for holding a validation status as well as a potential error validation message.
 * 
 * @author egeay
 */
public interface IStatusConfProvider {
  
  /**
   * Returns the validation error message if the validation failed, i.e is in {@link EValidationStatus#ERROR} or
   * {@link EValidationStatus#FAILURE} states.
   * 
   * @return The validation error message if the validation failed, otherwise will return <b>null</b>.
   */
  public String getValidationErrorMessage();
  
  /**
   * Returns the validation status.
   * 
   * @return A non-null enumeration value.
   */
  public EValidationStatus getValidationStatus();

}
