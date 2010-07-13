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
import org.eclipse.imp.x10dt.ui.cpp.debug.IPDTTarget;
import org.eclipse.imp.x10dt.ui.cpp.debug.IProxyNotifier;
import org.eclipse.ptp.debug.core.pdi.event.IPDIErrorInfo;
import org.eclipse.ptp.debug.core.pdi.model.IPDIBreakpoint;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugBreakpointHitEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugBreakpointSetEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugErrorEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugExitEvent;

import com.ibm.debug.internal.epdc.ProcessStopInfo;
import com.ibm.debug.internal.pdt.PICLDebugTarget;
import com.ibm.debug.internal.pdt.model.Breakpoint;
import com.ibm.debug.internal.pdt.model.BreakpointAddedEvent;
import com.ibm.debug.internal.pdt.model.DebugEngineCommandLogResponseEvent;
import com.ibm.debug.internal.pdt.model.DebugEngineTerminatedEvent;
import com.ibm.debug.internal.pdt.model.DebuggeeProcess;
import com.ibm.debug.internal.pdt.model.DebuggeeThread;
import com.ibm.debug.internal.pdt.model.ErrorOccurredEvent;
import com.ibm.debug.internal.pdt.model.ExpressionAddedEvent;
import com.ibm.debug.internal.pdt.model.IProcessEventListener;
import com.ibm.debug.internal.pdt.model.IThreadEventListener;
import com.ibm.debug.internal.pdt.model.MessageReceivedEvent;
import com.ibm.debug.internal.pdt.model.ModelStateReadyEvent;
import com.ibm.debug.internal.pdt.model.ModuleAddedEvent;
import com.ibm.debug.internal.pdt.model.ProcessAddedEvent;
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
final class PDTTarget implements IPDTTarget, IProcessEventListener, IThreadEventListener {
  
  PDTTarget(final PICLDebugTarget debugTarget, final IProxyNotifier proxyNotifier, final String taskBits) {
    this.fDebugTarget = debugTarget;
    this.fProxyNotifier = proxyNotifier;
    this.fBits = taskBits;
  }

  // --- IPDITarget's interface methods implementation

  public DebuggeeProcess getProcess() {
    return this.fProcess;
  }
  
  public PICLDebugTarget getTarget() {
    return this.fDebugTarget;
  }
  
  // --- IDebugEngineEventListener's interface methods implementation

  public void commandLogResponse(final DebugEngineCommandLogResponseEvent event) {
    System.out.println("** Command Log Response **");
    for (final String line : event.getResponseLines()) {
      System.out.println(line);
    }
  }

  public void debugEngineTerminated(final DebugEngineTerminatedEvent event) {
    System.out.println("Debug Engine terminated");
    event.getDebugEngine().removeEventListener(this);
  }

  public void errorOccurred(final ErrorOccurredEvent event) {
    System.err.println("*** PDT ERROR Occured ***");
    System.err.println(event.getMessage());
    System.err.println(event.getSource());
  }

  public void messageReceived(final MessageReceivedEvent event) {
    System.out.println("** Message received **");
    System.out.println(" - Request: " + event.getRequest());
    System.out.println(" - Message: " + event.getMessage());
  }

  public void modelStateChanged(final ModelStateReadyEvent event) {
  }

  public void processAdded(final ProcessAddedEvent event) {
    this.fProcess = event.getProcess();
    this.fProcess.addEventListener(this);
  }
  
  // --- IProcessEventListener's interface methods implementation

  public void breakpointAdded(final BreakpointAddedEvent event) {
    final IPDIBreakpoint pdiBreakpoint = (IPDIBreakpoint) event.getBreakpoint().getRequestProperty();
    if (pdiBreakpoint == null) {
      this.fProxyNotifier.notify(new ProxyDebugErrorEvent(-1 /* transId */, this.fBits, IPDIErrorInfo.DBG_FATAL, 
                                                          "PDT breakpoint adding is invalid."));
    } else {
      pdiBreakpoint.setBreakpointID(event.getBreakpoint().getId());
      // The breakpoint Id is enough for PDT.
      this.fProxyNotifier.notify(new ProxyDebugBreakpointSetEvent(-1 /* transID */, this.fBits, 
                                                                  event.getBreakpoint().getId(), 
                                                                  null /* breakpoint */));
    }
  }

  public void expressionAdded(final ExpressionAddedEvent event) {
  }

  public void moduleAdded(final ModuleAddedEvent event) {
  }

  public void processDetached(final ProcessDetachedEvent event) {
    System.out.println("Process " + this.fBits + " detached");
  }

  public void processEnded(final ProcessEndedEvent event) {
    System.out.println("Process " + this.fBits + " ended");
    try {
      this.fProxyNotifier.notify(new ProxyDebugExitEvent(-1 /* transId */, this.fBits, event.getProcess().getExitValue()));
    } catch (DebugException except) {
      this.fProxyNotifier.notify(new ProxyDebugErrorEvent(-1 /* transId */, this.fBits, IPDIErrorInfo.DBG_FATAL, 
                                                          "Error with PDT retrieving the process exit value"));
    } finally {
      getProcess().removeEventListener(this);
    }
  }

  public void processStopped(final ProcessStoppedEvent event) {
    System.out.println("Process " + this.fBits + " stopped");
  }

  public void programError(final ProcessPgmError event) {
    System.err.println("Got program stderr:");
    for (final String line : event.getLines()) {
      System.err.println("====> "+line);
    }
  }

  public void programOutput(final ProcessPgmOutput event) {
    System.out.println("Got program stdout:");
    for (final String line : event.getLines()) {
      System.out.println("====> "+line);
    }
  }

  public void threadAdded(final ThreadAddedEvent event) {
    event.getThread().addEventListener(this);
  }
  
  // --- IThreadEventListener's interface methods implementation

  public void expressionLocalAdded(final ExpressionAddedEvent event) {
  }

  public void stackAdded(final StackAddedEvent event) {
  }

  public void threadChanged(final ThreadChangedEvent event) {
  }

  public void threadEnded(final ThreadEndedEvent event) {
    event.getThread().removeEventListener(this);
  }

  public void threadStopped(final ThreadStoppedEvent event) {
    try {
      final ProcessStopInfo processStopInfo = event.getStopInfo();
      if (processStopInfo.isStoppedByBreakpoint()) {
        System.out.println("Stopped by breakpoint");
        notifyPDIBreakpoint(processStopInfo);
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
      this.fProxyNotifier.notify(new ProxyDebugErrorEvent(-1 /* transId */, this.fBits, IPDIErrorInfo.DBG_FATAL, 
                                                          "PDT Stack Frame access error: " + except.getMessage()));
    }
  }
  
  // --- Overridden methods
  
  public String toString() {
    return "PDT Target for process " + this.fBits; //$NON-NLS-1$
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
  
  private void notifyPDIBreakpoint(final ProcessStopInfo processStopInfo) throws DebugException {
    final Breakpoint[] breakpoints = processStopInfo.getBreakpointsHit(getProcess());
    Breakpoint pdtBreakpoint = null;
    for (final Breakpoint breakpoint : breakpoints) {
      if (breakpoint != null) {
        if (pdtBreakpoint != null) {
          this.fProxyNotifier.notify(new ProxyDebugErrorEvent(-1 /* transId */, this.fBits, IPDIErrorInfo.DBG_FATAL, 
                                                              "We have hit multiple breakpoints... Not handled!"));
          return;
        }
        pdtBreakpoint = breakpoint;
      }
    }
    if (pdtBreakpoint == null) {
      final DebuggeeThread thread = processStopInfo.getStoppingThread(getProcess());
      System.out.println("Breakpoint not found - resuming");
      thread.resume();
//      this.fProxyNotifier.notify(new ProxyDebugErrorEvent(-1 /* transId */, this.fBits, IPDIErrorInfo.DBG_FATAL, 
//                                                          "Stopped by breakpoints... But we don't any in PDT list!"));
    } else {
      final IPDIBreakpoint pdiBreakpoint = (IPDIBreakpoint) pdtBreakpoint.getRequestProperty();
      final DebuggeeThread thread = processStopInfo.getStoppingThread(getProcess());
      this.fProxyNotifier.notify(new ProxyDebugBreakpointHitEvent(-1 /* transId */, this.fBits, 
                                                                  pdiBreakpoint.getBreakpointID(),
                                                                  thread.getId(), thread.getStackFrames().length /* depth */,
                                                                  getVariablesAsString(thread)));
    }
  }
  
  // --- Fields
  
  private final IProxyNotifier fProxyNotifier;
  
  private final PICLDebugTarget fDebugTarget;
  
  private final String fBits;
  
  private DebuggeeProcess fProcess;

}
