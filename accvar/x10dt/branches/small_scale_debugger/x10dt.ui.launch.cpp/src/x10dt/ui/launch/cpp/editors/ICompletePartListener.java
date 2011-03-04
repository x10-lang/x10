/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors;

import org.eclipse.ui.forms.IFormPart;

/**
 * Gets notified if a given form part has all its required data provided.
 * 
 * @author egeay
 */
public interface ICompletePartListener {
  
  /**
   * Notifies this listener that a new data has changed in the form part and provides the new complete flag status.
   * 
   * @param formPart The form part that is being inspected.
   * @param isComplete The completeness flag.
   */
  public void completePartChanged(final IFormPart formPart, final boolean isComplete);

}
