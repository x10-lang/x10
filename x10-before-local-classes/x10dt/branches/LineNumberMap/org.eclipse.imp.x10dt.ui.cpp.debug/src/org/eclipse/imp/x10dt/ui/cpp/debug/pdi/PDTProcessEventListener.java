/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug.pdi;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.imp.x10dt.ui.cpp.debug.pdi.X10PDIDebugger.ProxyNotifier;
import org.eclipse.ptp.core.util.BitList;
import org.eclipse.ptp.debug.sdm.core.proxy.ProxyDebugClient;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugBreakpointHitEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugBreakpointSetEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugExitEvent;

import com.ibm.debug.internal.epdc.ProcessStopInfo;
import com.ibm.debug.internal.pdt.model.Breakpoint;
import com.ibm.debug.internal.pdt.model.BreakpointAddedEvent;
import com.ibm.debug.internal.pdt.model.BreakpointChangedEvent;
import com.ibm.debug.internal.pdt.model.BreakpointDeletedEvent;
import com.ibm.debug.internal.pdt.model.DebuggeeProcess;
import com.ibm.debug.internal.pdt.model.DebuggeeThread;
import com.ibm.debug.internal.pdt.model.ExpressionAddedEvent;
import com.ibm.debug.internal.pdt.model.IBreakpointEventListener;
import com.ibm.debug.internal.pdt.model.IProcessEventListener;
import com.ibm.debug.internal.pdt.model.IThreadEventListener;
import com.ibm.debug.internal.pdt.model.ModuleAddedEvent;
import com.ibm.debug.internal.pdt.model.ProcessDetachedEvent;
import com.ibm.debug.internal.pdt.model.ProcessEndedEvent;
import com.ibm.debug.internal.pdt.model.ProcessPgmError;
import com.ibm.debug.internal.pdt.model.ProcessPgmOutput;
import com.ibm.debug.internal.pdt.model.ProcessStoppedEvent;
import com.ibm.debug.internal.pdt.model.StackAddedEvent;
import com.ibm.debug.internal.pdt.model.ThreadAddedEvent;
import com.ibm.debug.internal.pdt.model.ThreadChangedEvent;
import com.ibm.debug.internal.pdt.model.ThreadEndedEvent;
import com.ibm.debug.internal.pdt.model.ThreadStoppedEvent;

@SuppressWarnings("restriction")
final class PDTProcessEventListener implements IProcessEventListener, IThreadEventListener, IBreakpointEventListener {
  
  PDTProcessEventListener(final DebuggeeProcess debuggeeProcess, final ProxyNotifier proxyNotifier) {
    this.fDebuggeeProcess = debuggeeProcess;
    this.fProxyNotifier = proxyNotifier;
  }
  
  // --- IProcessEventListener's interface methods implementation

  public void breakpointAdded(final BreakpointAddedEvent event) {
    final Integer breakPointId = (Integer) event.getBreakpoint().getRequestProperty();
    String bits = this.fBits;
    if (bits == null) bits = "0:";
	this.fProxyNotifier.notify(new ProxyDebugBreakpointSetEvent(-1 /* transID */, bits, breakPointId, 
                                                                null /* breakpoint */));
  }

  public void expressionAdded(final ExpressionAddedEvent event) {
  }

  public void moduleAdded(final ModuleAddedEvent event) {
  }

  public void processDetached(final ProcessDetachedEvent event) {
    System.out.println("Process detached");
  }

  public void processEnded(final ProcessEndedEvent event) {
    System.out.println("Process ended");
    try {
      this.fProxyNotifier.notify(new ProxyDebugExitEvent(-1 /* transId */, this.fBits, event.getProcess().getExitValue()));
    } catch (DebugException except) {
      except.printStackTrace();
    } finally {
      this.fDebuggeeProcess.removeEventListener(this);
    }
  }

  public void processStopped(final ProcessStoppedEvent event) {
    System.out.println("Process stopped");
  }

  public void programError(final ProcessPgmError event) {
  }

  public void programOutput(final ProcessPgmOutput event) {
  }

  public void threadAdded(final ThreadAddedEvent event) {
    event.getThread().addEventListener(this);
  }
  
  // --- IThreadEventListener's interface methods implementation
  
  public void expressionLocalAdded(final ExpressionAddedEvent event) {
    System.out.println("Expression added");
  }

  public void stackAdded(final StackAddedEvent event) {
    System.out.println("Stack added");
  }

  public void threadChanged(final ThreadChangedEvent event) {
    System.out.println("Thread changed");
  }

  public void threadEnded(final ThreadEndedEvent event) {
    System.out.println("Thread ended");
    event.getThread().removeEventListener(this);
  }

  public void threadStopped(final ThreadStoppedEvent event) {
    System.out.println("Thread stopped");
    try {
      final ProcessStopInfo processStopInfo = event.getStopInfo();
      if (processStopInfo.isStoppedByBreakpoint()) {
        final Breakpoint[] breakpoints = processStopInfo.getBreakpointsHit(this.fDebuggeeProcess);
        final DebuggeeThread thread = processStopInfo.getStoppingThread(this.fDebuggeeProcess);
        if (breakpoints.length == 1) {
          final Integer breakPointId = (Integer) breakpoints[0].getRequestProperty();
          String bits = this.fBits;
          if (bits == null) bits = "0:";
          this.fProxyNotifier.notify(new ProxyDebugBreakpointHitEvent(-1 /* transId */, bits, breakPointId,
                                                                      thread.getId(), 0 /* depth */,
                                                                      getVariablesAsString(thread)));
        }
      } else if (processStopInfo.isStoppedByExec()) {
        System.out.println("Stopped by exec");
      } else if (processStopInfo.isStoppedByException()) {
        System.out.println("Stopped by except");
      } else if (processStopInfo.isStoppedByFork()) {
        System.out.println("Stopped by fork");
      } else if (processStopInfo.isEnded()) {
        System.out.println("Stopped is ended");
      } else if (processStopInfo.isStillProcessing()) {
        System.out.println("Thread is still processing");
      }
    } catch (DebugException except) {
      except.printStackTrace();
    }
  }
  
  // --- IBreakpointEventListener's interface methods implementation

  public void breakpointChanged(final BreakpointChangedEvent event) {
    System.out.println("Breakpoint changed event");
  }

  public void breakpointDeleted(final BreakpointDeletedEvent event) {
    System.out.println("Breakpoint deleted event");
  }
  
  // --- Internal services
  
  void setCurTasks(final BitList curTasks) {
    this.fBits = ProxyDebugClient.encodeBitSet(curTasks);
  }
  
  // --- Private code
  
  private String[] getVariablesAsString(final DebuggeeThread thread) throws DebugException {
    final IVariable[] variables = thread.getTopStackFrame().getVariables();
    final String[] strVars = new String[variables.length];
    int i = -1;
    for (final IVariable variable : variables) {
      strVars[++i] = variable.getName();
    }
    return strVars;
  }
  
  // --- Fields

  private String fBits;
  
  private final DebuggeeProcess fDebuggeeProcess;
  
  private final ProxyNotifier fProxyNotifier;

}
