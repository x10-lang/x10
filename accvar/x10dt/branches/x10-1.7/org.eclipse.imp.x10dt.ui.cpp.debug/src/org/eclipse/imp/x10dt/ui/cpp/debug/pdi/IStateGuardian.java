/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug.pdi;

/**
 * Responsible for assuring that a general expected state has been been reached.
 * 
 * @see WaitingForStateRunnable
 * 
 * @author egeay
 */
public interface IStateGuardian {
  
  /**
   * Indicates if yes or no we have reached the expected state.
   * 
   * @return True if we have reached it, false otherwise.
   */
  public boolean hasReached();

}
