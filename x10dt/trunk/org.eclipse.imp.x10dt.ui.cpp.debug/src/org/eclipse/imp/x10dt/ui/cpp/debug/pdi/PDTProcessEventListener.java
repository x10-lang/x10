/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug.pdi;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.imp.x10dt.ui.cpp.debug.DebugCore;
import org.eclipse.imp.x10dt.ui.cpp.debug.pdi.X10PDIDebugger.ProxyNotifier;
import org.eclipse.ptp.core.util.BitList;
import org.eclipse.ptp.debug.sdm.core.proxy.ProxyDebugClient;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugBreakpointHitEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugBreakpointSetEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugErrorEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugExitEvent;

import com.ibm.debug.internal.epdc.ProcessStopInfo;
import com.ibm.debug.internal.pdt.model.Breakpoint;
import com.ibm.debug.internal.pdt.model.BreakpointAddedEvent;
import com.ibm.debug.internal.pdt.model.BreakpointChangedEvent;
import com.ibm.debug.internal.pdt.model.BreakpointDeletedEvent;
import com.ibm.debug.internal.pdt.model.DebuggeeProcess;
import com.ibm.debug.internal.pdt.model.DebuggeeThread;
import com.ibm.debug.internal.pdt.model.EngineRequestException;
import com.ibm.debug.internal.pdt.model.EventBreakpoint;
import com.ibm.debug.internal.pdt.model.ExpressionAddedEvent;
import com.ibm.debug.internal.pdt.model.IBreakpointEventListener;
import com.ibm.debug.internal.pdt.model.IProcessEventListener;
import com.ibm.debug.internal.pdt.model.IThreadEventListener;
import com.ibm.debug.internal.pdt.model.LocationBreakpoint;
import com.ibm.debug.internal.pdt.model.Module;
import com.ibm.debug.internal.pdt.model.ModuleAddedEvent;
import com.ibm.debug.internal.pdt.model.Part;
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
import com.ibm.debug.internal.pdt.model.View;
import com.ibm.debug.internal.pdt.model.ViewFile;
import com.ibm.debug.pdt.breakpoints.PICLBaseBreakpoint;

@SuppressWarnings("restriction")
final class PDTProcessEventListener implements IProcessEventListener, IThreadEventListener, IBreakpointEventListener {
  
  PDTProcessEventListener(final DebuggeeProcess debuggeeProcess, final ProxyNotifier proxyNotifier) {
    this.fDebuggeeProcess = debuggeeProcess;
    this.fProxyNotifier = proxyNotifier;
  }
  
  // --- IProcessEventListener's interface methods implementation

  public void breakpointAdded(final BreakpointAddedEvent event) {
    if (this.fBits == null) {
    	System.err.println("Ignoring invalid breakpoint");
    	return;
    }
    final Integer breakPointId = (Integer) event.getBreakpoint().getRequestProperty();
    if (breakPointId == null) {
      System.err.println("Removing invalid breakpoint");
      try {
        event.getBreakpoint().remove();
      } catch (EngineRequestException e) {
        System.err.println("Could not remove invalid breakpoint");
      }
      return;
    }
    this.fProxyNotifier.notify(new ProxyDebugBreakpointSetEvent(-1 /* transID */, this.fBits, breakPointId, 
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
      removeAllBreakpoints(event.getProcess());
      this.fProxyNotifier.notify(new ProxyDebugExitEvent(-1 /* transId */, this.fBits, event.getProcess().getExitValue()));
    } catch (DebugException except) {
      this.fProxyNotifier.notify(new ProxyDebugErrorEvent(-1 /* transId */, this.fBits, 1 /* errorCode */, 
                                                          "Could not get exit value"));
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

  private Breakpoint findFirstValidBreakpoint(final Breakpoint[] breakpoints) {
    for (int i = 0; i < breakpoints.length; i++)
      if (breakpoints[i].getRequestProperty() != null)
        return breakpoints[i];
    return null;
  }

  public void threadStopped(final ThreadStoppedEvent event) {
    System.out.println("Thread stopped");
    try {
      final ProcessStopInfo processStopInfo = event.getStopInfo();
      if (processStopInfo.isStoppedByBreakpoint()) {
        final Breakpoint[] breakpoints = processStopInfo.getBreakpointsHit(this.fDebuggeeProcess);
        final DebuggeeThread thread = processStopInfo.getStoppingThread(this.fDebuggeeProcess);
        final Breakpoint breakpoint = findFirstValidBreakpoint(breakpoints);
        if (breakpoint == null) {
          System.err.println("Removing invalid breakpoints");
          for (int i = 0; i < breakpoints.length; i++) {
            try {
              breakpoints[i].remove();
            } catch (EngineRequestException e) {
              System.err.println("Could not remove invalid breakpoint");
            }
          }
          // Hit an invalid breakpoint - have to resume
          thread.resume();
          return;
        }
        final Integer breakPointId = (Integer) breakpoint.getRequestProperty();
        this.fProxyNotifier.notify(new ProxyDebugBreakpointHitEvent(-1 /* transId */, this.fBits, breakPointId,
                                                                    thread.getId(), 0 /* depth */,
                                                                    getVariablesAsString(thread)));
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
  
  private void removeAllBreakpoints(DebuggeeProcess process) {
    Breakpoint[] breakpoints = process.getBreakpoints();
    for (int m = 0; m < breakpoints.length; m++) {
      Breakpoint breakpoint = breakpoints[m];
      try {
        breakpoint.remove();
      } catch (EngineRequestException e) {
        if (breakpoint instanceof LocationBreakpoint)
          System.err.println("Unable to remove breakpoint "+((LocationBreakpoint) breakpoint).getFunction()+" at "+((LocationBreakpoint) breakpoint).getFileName()+":"+breakpoint.getRequestProperty());
        else if (breakpoint instanceof EventBreakpoint) {
          System.err.println("Unable to remove breakpoint "+((EventBreakpoint)breakpoint).getRequestProperty());
        }
      }
    }
//    final IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
//    try {
//      for (final IBreakpoint breakpoint : breakpointManager.getBreakpoints()) {
//        if (breakpoint instanceof PICLBaseBreakpoint) {
//          breakpointManager.removeBreakpoint(breakpoint, true /* delete */);
//        }
//      }
//    } catch (CoreException except) {
//      DebugCore.log(except.getStatus());
//    }
  }
  
  // --- Fields

  private String fBits;
  
  private final DebuggeeProcess fDebuggeeProcess;
  
  private final ProxyNotifier fProxyNotifier;

}
