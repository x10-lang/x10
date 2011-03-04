/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

import x10dt.ui.launch.core.platform_conf.EValidationStatus;
import x10dt.ui.launch.core.utils.CodingUtils;


class StatusConfProvider implements IStatusConfProvider {

  // --- Interface methods implementation
  
  public final String getValidationErrorMessage() {
    return this.fValidationErrorMsg;
  }

  public final EValidationStatus getValidationStatus() {
    return this.fValidationStatus;
  }
  
  // --- Overridden methods
  
  public boolean equals(final Object rhs) {
    final StatusConfProvider rhsObj = (StatusConfProvider) rhs;
    return CodingUtils.equals(this.fValidationStatus, rhsObj.fValidationStatus) && 
           CodingUtils.equals(this.fValidationErrorMsg, rhsObj.fValidationErrorMsg);
  }
  
  public int hashCode() {
    int hashCode = (this.fValidationStatus == null) ? 34234 : this.fValidationStatus.hashCode();
    hashCode += (this.fValidationErrorMsg == null) ? 234254 : this.fValidationErrorMsg.hashCode();
    return hashCode;
  }
  
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("Validation Status: ").append(this.fValidationStatus.name()); //$NON-NLS-1$
    if (this.fValidationErrorMsg != null) {
      sb.append("\nValidation Error Message: ").append(this.fValidationErrorMsg); //$NON-NLS-1$
    }
    return sb.toString();
  }
  
  // --- Fields
  
  EValidationStatus fValidationStatus;
  
  String fValidationErrorMsg;

}
