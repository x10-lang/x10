/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug.pdi;

import com.ibm.debug.internal.pdt.model.DebugEngineCommandLogResponseEvent;
import com.ibm.debug.internal.pdt.model.DebugEngineTerminatedEvent;
import com.ibm.debug.internal.pdt.model.ErrorOccurredEvent;
import com.ibm.debug.internal.pdt.model.IDebugEngineEventListener;
import com.ibm.debug.internal.pdt.model.MessageReceivedEvent;
import com.ibm.debug.internal.pdt.model.ModelStateReadyEvent;
import com.ibm.debug.internal.pdt.model.ProcessAddedEvent;


@SuppressWarnings("restriction")
final class DebugEngineTerminatedEventListener implements IDebugEngineEventListener, IStateGuardian {

  // --- IDebugEngineEventListener's interface methods implementation
  
  public void commandLogResponse(final DebugEngineCommandLogResponseEvent event) {
  }

  public void debugEngineTerminated(final DebugEngineTerminatedEvent event) {
    this.fHasReached = true;
  }

  public void errorOccurred(final ErrorOccurredEvent event) {
  }

  public void messageReceived(final MessageReceivedEvent event) {
  }

  public void modelStateChanged(final ModelStateReadyEvent event) {
  }

  public void processAdded(final ProcessAddedEvent event) {
  }
  
  // --- IStateGuardian's interface methods implementation

  public boolean hasReached() {
    return this.fHasReached;
  }
  
  // --- Fields
  
  private boolean fHasReached;

}
