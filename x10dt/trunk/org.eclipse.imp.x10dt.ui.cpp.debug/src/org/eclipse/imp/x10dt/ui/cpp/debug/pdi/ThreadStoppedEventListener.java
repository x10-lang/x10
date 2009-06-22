/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug.pdi;

import com.ibm.debug.internal.pdt.model.DebuggeeThread;
import com.ibm.debug.internal.pdt.model.ExpressionAddedEvent;
import com.ibm.debug.internal.pdt.model.IThreadEventListener;
import com.ibm.debug.internal.pdt.model.StackAddedEvent;
import com.ibm.debug.internal.pdt.model.ThreadChangedEvent;
import com.ibm.debug.internal.pdt.model.ThreadEndedEvent;
import com.ibm.debug.internal.pdt.model.ThreadStoppedEvent;


@SuppressWarnings("restriction")
final class ThreadStoppedEventListener implements IThreadEventListener, IStateGuardian {
  
  ThreadStoppedEventListener(final DebuggeeThread[] curThreads) {
    this.fCurThread = curThreads;
    this.fStopped = new boolean[curThreads.length];
  }
  
  // --- IThreadEventListener's interface methods implementation

  public void expressionLocalAdded(final ExpressionAddedEvent event) {
  }

  public void stackAdded(final StackAddedEvent event) {
  }

  public void threadChanged(final ThreadChangedEvent event) {
  }

  public void threadEnded(final ThreadEndedEvent event) {
    System.err.println("Watched thread terminated instead of stopping");
    this.fStopped[findThread(event.getThread())] = true;
  }

  public void threadStopped(final ThreadStoppedEvent event) {
    this.fStopped[findThread(event.getThread())] = true;
  }

  private int findThread(DebuggeeThread thread) {
    for (int i = 0; i < this.fCurThread.length; i++)
      if (thread.equals(this.fCurThread[i]))
        return i;
    assert (false) : ("Unknown thread encountered");
    return -1;
  }
  
  // --- IStateGuardian's interface methods implementation
  
  public boolean hasReached() {
    for (int i = 0; i < this.fStopped.length; i++) {
	  if (!this.fStopped[i])
	    return false;
	}
    return true;
  }
  
  // --- Fields
  
  private final DebuggeeThread[] fCurThread;
  
  private boolean[] fStopped;

}
