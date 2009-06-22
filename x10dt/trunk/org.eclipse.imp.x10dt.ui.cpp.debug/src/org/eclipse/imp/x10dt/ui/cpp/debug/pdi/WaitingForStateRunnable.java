/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug.pdi;


final class WaitingForStateRunnable implements Runnable {
  
  WaitingForStateRunnable(final IStateGuardian stateGuardian) {
    this.fStateGuardian = stateGuardian;
  }
  
  // --- Interface methods implementation

  public void run() {
    while (! this.fStateGuardian.hasReached())
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        break;
      }
  }
  
  // --- Fields
  
  private final IStateGuardian fStateGuardian;

}
