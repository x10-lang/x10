/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug.pdi;

import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_ARGUMENTS;
import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_EXECUTABLE_PATH;
import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_PROJECT_NAME;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.core.model.MemoryByte;
import org.eclipse.imp.x10dt.ui.cpp.debug.DebugCore;
import org.eclipse.imp.x10dt.ui.cpp.debug.DebugMessages;
import org.eclipse.imp.x10dt.ui.cpp.debug.core.X10DebuggerTranslator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.IPTPLaunchConfigurationConstants;
import org.eclipse.ptp.core.attributes.IAttribute;
import org.eclipse.ptp.core.attributes.StringAttributeDefinition;
import org.eclipse.ptp.core.elements.IPProcess;
import org.eclipse.ptp.core.elements.attributes.ProcessAttributes;
import org.eclipse.ptp.core.elements.events.IProcessChangeEvent;
import org.eclipse.ptp.core.elements.listeners.IProcessListener;
import org.eclipse.ptp.core.util.BitList;
import org.eclipse.ptp.debug.core.PTPDebugCorePlugin;
import org.eclipse.ptp.debug.core.launch.IPLaunch;
import org.eclipse.ptp.debug.core.pdi.IPDIDebugger;
import org.eclipse.ptp.debug.core.pdi.IPDIFileLocation;
import org.eclipse.ptp.debug.core.pdi.IPDIFunctionLocation;
import org.eclipse.ptp.debug.core.pdi.IPDILineLocation;
import org.eclipse.ptp.debug.core.pdi.IPDILocation;
import org.eclipse.ptp.debug.core.pdi.IPDILocator;
import org.eclipse.ptp.debug.core.pdi.IPDISession;
import org.eclipse.ptp.debug.core.pdi.PDIException;
import org.eclipse.ptp.debug.core.pdi.model.IPDIAddressBreakpoint;
import org.eclipse.ptp.debug.core.pdi.model.IPDIBreakpoint;
import org.eclipse.ptp.debug.core.pdi.model.IPDIExceptionpoint;
import org.eclipse.ptp.debug.core.pdi.model.IPDIFunctionBreakpoint;
import org.eclipse.ptp.debug.core.pdi.model.IPDILineBreakpoint;
import org.eclipse.ptp.debug.core.pdi.model.IPDISignal;
import org.eclipse.ptp.debug.core.pdi.model.IPDIWatchpoint;
import org.eclipse.ptp.debug.core.pdi.model.aif.IAIF;
import org.eclipse.ptp.debug.sdm.core.proxy.ProxyDebugClient;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugArgsEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugDataEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugEventFactory;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugInfoThreadsEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugOKEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugPartialAIFEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugSetThreadSelectEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugStackInfoDepthEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugStackframeEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugStepEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugSuspendEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugTypeEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugVarsEvent;
import org.eclipse.ptp.proxy.debug.client.ProxyDebugAIF;
import org.eclipse.ptp.proxy.debug.client.ProxyDebugStackFrame;
import org.eclipse.ptp.proxy.debug.event.IProxyDebugEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.ibm.debug.daemon.CoreDaemon;
import com.ibm.debug.daemon.DaemonConnectionInfo;
import com.ibm.debug.daemon.DaemonSocketConnection;
import com.ibm.debug.internal.epdc.EReqExpressionSubTree;
import com.ibm.debug.internal.epdc.EStdExprNode;
import com.ibm.debug.internal.epdc.EStdTreeNode;
import com.ibm.debug.internal.epdc.EStdView;
import com.ibm.debug.internal.epdc.IEPDCConstants;
import com.ibm.debug.internal.pdt.IPICLDebugConstants;
import com.ibm.debug.internal.pdt.PDTDebugElement;
import com.ibm.debug.internal.pdt.PICLDebugPlugin;
import com.ibm.debug.internal.pdt.PICLDebugTarget;
import com.ibm.debug.internal.pdt.model.Address;
import com.ibm.debug.internal.pdt.model.ArrayExprNode;
import com.ibm.debug.internal.pdt.model.Breakpoint;
import com.ibm.debug.internal.pdt.model.ClassExprNode;
import com.ibm.debug.internal.pdt.model.DebugEngineCommandLogResponseEvent;
import com.ibm.debug.internal.pdt.model.DebugEngineTerminatedEvent;
import com.ibm.debug.internal.pdt.model.DebuggeeProcess;
import com.ibm.debug.internal.pdt.model.DebuggeeThread;
import com.ibm.debug.internal.pdt.model.EngineBusyException;
import com.ibm.debug.internal.pdt.model.EngineRequestException;
import com.ibm.debug.internal.pdt.model.ErrorOccurredEvent;
import com.ibm.debug.internal.pdt.model.ExprNode;
import com.ibm.debug.internal.pdt.model.ExprNodeBase;
import com.ibm.debug.internal.pdt.model.ExpressionBase;
import com.ibm.debug.internal.pdt.model.Function;
import com.ibm.debug.internal.pdt.model.GlobalVariable;
import com.ibm.debug.internal.pdt.model.IDebugEngineEventListener;
import com.ibm.debug.internal.pdt.model.Location;
import com.ibm.debug.internal.pdt.model.Memory;
import com.ibm.debug.internal.pdt.model.MemoryException;
import com.ibm.debug.internal.pdt.model.MessageReceivedEvent;
import com.ibm.debug.internal.pdt.model.ModelStateReadyEvent;
import com.ibm.debug.internal.pdt.model.PointerExprNode;
import com.ibm.debug.internal.pdt.model.ProcessAddedEvent;
import com.ibm.debug.internal.pdt.model.ScalarExprNode;
import com.ibm.debug.internal.pdt.model.StackFrame;
import com.ibm.debug.internal.pdt.model.StructExprNode;
import com.ibm.debug.internal.pdt.model.ViewFile;
import com.ibm.debug.pdt.launch.PICLLoadInfo;

/**
 * Implementation of {@link IPDIDebugger} for X10.
 * 
 * <p>This is the main class that handles the connection and requests with PDT debugger.
 * 
 * @author egeay
 */
@SuppressWarnings("all")
public final class X10PDIDebugger implements IPDIDebugger, IDebugEngineEventListener {
  
  public X10PDIDebugger(final X10DebuggerTranslator translator, final int port) {
    this.fPort = port;
    this.fTranslator = translator;
  }
  
  // --- IPDIDebugger's interface methods implementation
  
  public void commandRequest(final BitList tasks, final String command) throws PDIException {
    throw new IllegalStateException();
  }

  public void disconnect(final Observer observer) throws PDIException {
    stopDebugger();
  }

  public int getErrorAction(final int errorCode) {
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
    throw new IllegalStateException();
  }

  private IProject fProject;

  public void initialize(final ILaunchConfiguration configuration, final List<String> args, 
                         final IProgressMonitor monitor) throws PDIException {
    try {
      this.fServerSocket = new ServerSocket(this.fPort);
      this.fState = ESessionState.CONNECTED;
      try {
        String projName = configuration.getAttribute(IPTPLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String)null);
        assert (projName != null);
        this.fProject = PTPDebugCorePlugin.getWorkspace().getRoot().getProject(projName);
        this.fTranslator.init(fProject);
      } catch (CoreException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      new Thread(new ListenerRunnable(), "Listening thread to Remote Debugger").start(); //$NON-NLS-1$
    } catch (IOException except) {
      throw new PDIException(null, NLS.bind(DebugMessages.PDID_ServerSocketInitError, except.getMessage()));
    }
  }
  
  public boolean isConnected(final IProgressMonitor monitor) throws PDIException {
    if (hasConnected(monitor)) {
      return true;
    } else {
      disconnect(null);
      return false;
    }
  }

  public void register(final Observer observer) {
    this.fProxyNotifier.addObserver(observer);
  }

  public void startDebugger(final String app, final String path, final String dir, final String[] args) throws PDIException {
    System.out.println("Debugger started");
    this.fPDISession.setRequestTimeout(Long.MAX_VALUE/2); // no timeouts
    IPProcess[] processes = this.fLaunch.getPJob().getProcesses();
    for (IPProcess process : processes) {
      process.addElementListener(new IProcessListener(){
        public void handleEvent(IProcessChangeEvent e) {
          final StringAttributeDefinition STDOUT = ProcessAttributes.getStdoutAttributeDefinition(); // TODO: make a static field
          final StringAttributeDefinition STDERR = ProcessAttributes.getStderrAttributeDefinition(); // TODO: make a static field
          IAttribute<?, ?, ?> stdout = e.getAttributes().getAttribute(STDOUT);
          if (stdout != null && stdout.getValue() != null)
            System.out.println(stdout.getValue());
          IAttribute<?, ?, ?> stderr = e.getAttributes().getAttribute(STDERR);
          if (stderr != null && stderr.getValue() != null)
        	  System.out.println(stderr.getValue());
        }
      });
    }
    BitList tasks = this.fPDISession.getTasks();
    this.fProxyNotifier.notify(new ProxyDebugOKEvent(-1, ProxyDebugClient.encodeBitSet(tasks)));
  }

  public void stopDebugger() throws PDIException {
    this.fState = ESessionState.DISCONNECTED;
    try {
      if (this.fPDTTarget != null) {
        this.fPDTTarget.terminate();
      }
      this.fPDTTarget = null;
//      PTPDebugCorePlugin.getDebugModel().shutdownSession(this.fLaunch.getPJob());
    } catch (DebugException except) {
      throw new PDIException(null, "Terminate operation failed: " + except.getMessage());
    }
  }
  
  // --- IPDIBreakpointManagement's interface methods implementation

  public void deleteBreakpoint(final BitList tasks, final int bpid) throws PDIException {
    final DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
    boolean found = false;
    for (int i = 0; i < processes.length; i++) {
      final Breakpoint breakpoint = processes[i].getBreakpoint(bpid);
      if (breakpoint != null) {
        processes[i].removeBreakpoint(breakpoint);
        found = true;
      }
    }
    if (!found) {
      DebugCore.log(IStatus.ERROR, NLS.bind("Could not find breakpoint with id ''{0}''", bpid));
    }
    this.fProxyNotifier.notify(new ProxyDebugOKEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks)));
  }

  public void setAddressBreakpoint(final BitList tasks, final IPDIAddressBreakpoint breakpoint) throws PDIException {
    raiseDialogBoxNotImplemented("Set Address Breakpoint is not an available feature with X10 debugger");
  }

  public void setConditionBreakpoint(final BitList tasks, final int bpid, final String condition) throws PDIException {
    if (this.fProcessListener != null)
      this.fProcessListener.setCurTasks(tasks);
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
  }

  public void setEnabledBreakpoint(final BitList tasks, final int bpid, final boolean enabled) throws PDIException {
    if (this.fProcessListener != null)
      this.fProcessListener.setCurTasks(tasks);
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
  }

  public void setExceptionpoint(final BitList tasks, final IPDIExceptionpoint breakpoint) throws PDIException {
    raiseDialogBoxNotImplemented("Set Exception Breakpoint is not yet implemented");
  }

  public void setFunctionBreakpoint(final BitList tasks, final IPDIFunctionBreakpoint breakpoint) throws PDIException {
    if (this.fProcessListener != null)
      this.fProcessListener.setCurTasks(tasks);
    initBreakPointIdIfRequired(breakpoint);
    String name = breakpoint.getLocator().getFunction();
    DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
    for (int p = 0; p < processes.length; p++) {
      try {
        //TODO Add translator here.
        final Function[] fns = processes[0].getFunctions(name, true /* caseSensitive */);
        Function res = null;
        for (Function f : fns) {
          if (f.getName().equals(name)) {
            if (res != null)
            throw new PDIException(tasks, "Found multiple functions matching '"+name+"'");
            res = f;
          }
        }
        this.fPDTTarget.createEntryBreakpoint(true, res, null, null, 0, 1, 1, 1, null);
      } catch (Exception except) {
        throw new PDIException(tasks, except.getMessage());
//      } finally {
//        this.fProxyNotifier.notify(new ProxyDebugOKEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks)));
      }
    }
  }

  public void setLineBreakpoint(final BitList tasks, final IPDILineBreakpoint breakpoint) throws PDIException {
    if (this.fProcessListener != null)
      this.fProcessListener.setCurTasks(tasks);

    initBreakPointIdIfRequired(breakpoint);
    DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
    for (int p = 0; p < processes.length; p++) {
      final Location location = this.fTranslator.getCppLocation(processes[p], tasks,
                                                                breakpoint.getLocator().getFile(),
                                                                breakpoint.getLocator().getLineNumber());
      if (location == null) {
        throw new PDIException(tasks, NLS.bind("Could not find PDT location for breakpoint {0}", breakpoint.getLocator()));
      }
      try {
        this.fPDTTarget.createLineBreakpoint(true /* enabled */, location, null /* conditionalExpression */, 
                                             null /* brkAction */,  0 /* threadNumber */, 1 /* everyValue */, 1 /* fromValue */, 
                                             0 /* toValue */, breakpoint.getBreakpointID(), null /* stmtNumber */, 
                                             null /* engineData */);
      } catch (EngineRequestException except) {
        throw new PDIException(tasks, "PDT engine exception during setLinebreakpoint: " + except.getMessage());
//      } finally {
//        this.fProxyNotifier.notify(new ProxyDebugOKEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks)));
      }
    }
  }

  public void setWatchpoint(final BitList tasks, final IPDIWatchpoint breakPoint) throws PDIException {
    raiseDialogBoxNotImplemented("Set Watchpoint is not yet implemented");
  }
  
  // --- IPDIExecuteManagement's interface methods implementation

  public void restart(final BitList tasks) throws PDIException {
    raiseDialogBoxNotImplemented("Restart not yet implemented");
  }

  public void resume(final BitList tasks, final boolean passSignal) throws PDIException {
    if (this.fProcessListener != null)
      this.fProcessListener.setCurTasks(tasks);
    this.fProxyNotifier.notify(new ProxyDebugOKEvent(-1, ProxyDebugClient.encodeBitSet(tasks)));
    try {
      // TODO
//      final DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
//      for (int p = 0; p < processes.length; p++) {
//		processes[p].run(false);
//      }
      this.fPDTTarget.resume();
    } catch (DebugException except) {
      throw new PDIException(tasks, except.getMessage());
//    } catch (EngineRequestException except) {
//      throw new PDIException(tasks, "PDT engine exception during resume: " + except.getMessage());
    }
  }

  public void resume(final BitList tasks, final IPDILocation location) throws PDIException {
    raiseDialogBoxNotImplemented("Resume to location not implemented");
  }

  public void resume(final BitList tasks, final IPDISignal signal) throws PDIException {
    raiseDialogBoxNotImplemented("Resume to signal not implemented");
  }

  public void start(final BitList tasks) throws PDIException {
    if (this.fProcessListener != null)
      this.fProcessListener.setCurTasks(tasks);
    resume(tasks, false);
  }

  public void stepInto(final BitList tasks, final int count) throws PDIException {
    try {
      final DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
      final DebuggeeThread[] threads = new DebuggeeThread[processes.length];
      for (int p = 0; p < processes.length; p++) {
        threads[p] = processes[p].getStoppingThread();
      }
      final ThreadStoppedEventListener stoppedEventListener = new ThreadStoppedEventListener(threads);
      if (count <= 0) {
        throw new PDIException(tasks, "Step Into with count <= 0 is not supported.");
      }
      for (int i = 0; i < count; i++) {
        for (int p = 0; p < threads.length; p++) {
          threads[p].addEventListener(stoppedEventListener);
          threads[p].stepInto();
        }
        final Thread runnableThread = new Thread(new WaitingForStateRunnable(stoppedEventListener));
        runnableThread.start();
        try {
          runnableThread.join();
        } catch (InterruptedException except) {
          throw new PDIException(tasks, "Step Into interrupted");
        } finally {
          for (int p = 0; p < threads.length; p++) {
            threads[p].removeEventListener(stoppedEventListener);
          }
        }
      }
      // TODO: consolidate same stack frames into one event
      for (int p = 0; p < threads.length; p++) {
        final IStackFrame stackFrame = threads[p].getTopStackFrame();
        this.fProxyNotifier.notify(new ProxyDebugStepEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                           toProxyStackFrame(processes[p], stackFrame, 0), threads[p].getId(),
                                                           getDepth(threads[p]), getVariablesAsStringArray(stackFrame)));
      }
    } catch (DebugException except) {
      throw new PDIException(tasks, "Unable to step into: " + except.getMessage());
    }
  }

  public void stepIntoInstruction(final BitList tasks, final int count) throws PDIException {
    // TODO: use to step into a C++ statement?
    raiseDialogBoxNotImplemented("Step into instruction not implemented");
  }

  public void stepOver(final BitList tasks, final int count) throws PDIException {
    try {
      final DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
      final DebuggeeThread[] threads = new DebuggeeThread[processes.length];
      for (int p = 0; p < processes.length; p++) {
        threads[p] = processes[p].getStoppingThread();
      }
      final ThreadStoppedEventListener stoppedEventListener = new ThreadStoppedEventListener(threads);
      if (count <= 0) {
        throw new PDIException(tasks, "Step Over with count <= 0 is not supported.");
      }
      for (int i = 0; i < count; i++) {
        for (int p = 0; p < threads.length; p++) {
          threads[p].addEventListener(stoppedEventListener);
          threads[p].stepOver();
        }
        final Thread runnableThread = new Thread(new WaitingForStateRunnable(stoppedEventListener));
        runnableThread.start();
        try {
          runnableThread.join();
        } catch (InterruptedException except) {
          throw new PDIException(tasks, "Step Over interrupted");
        } finally {
          for (int p = 0; p < threads.length; p++) {
            threads[p].removeEventListener(stoppedEventListener);
          }
        }        
      }
      // TODO: consolidate same stack frames into one event
      for (int p = 0; p < threads.length; p++) {
        final IStackFrame stackFrame = threads[p].getTopStackFrame();
        this.fProxyNotifier.notify(new ProxyDebugStepEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                           toProxyStackFrame(processes[p], stackFrame, 0), threads[p].getId(),
                                                           getDepth(threads[p]), getVariablesAsStringArray(stackFrame)));
      }
    } catch (DebugException except) {
      throw new PDIException(tasks, "Unable to step over: " + except.getMessage());
    }
  }

  public void stepOverInstruction(final BitList tasks, final int count) throws PDIException {
    // TODO: use to step over a C++ statement?
    raiseDialogBoxNotImplemented("Step over instruction not implemented");
  }

  public void stepReturn(final BitList tasks, final int count) throws PDIException {
    try {
      final DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
      final DebuggeeThread[] threads = new DebuggeeThread[processes.length];
      for (int p = 0; p < processes.length; p++) {
        threads[p] = processes[p].getStoppingThread();
      }
      final ThreadStoppedEventListener stoppedEventListener = new ThreadStoppedEventListener(threads);
      // By default for one step return (count == 0) !!
      for (int i = 0; i < count + 1; i++) {
        for (int p = 0; p < threads.length; p++) {
          threads[p].addEventListener(stoppedEventListener);
          threads[p].stepReturn();
        }
        final Thread runnableThread = new Thread(new WaitingForStateRunnable(stoppedEventListener));
        runnableThread.start();
        try {
          runnableThread.join();
        } catch (InterruptedException except) {
          throw new PDIException(tasks, "Step Return interrupted");
        } finally {
          for (int p = 0; p < threads.length; p++) {
            threads[p].removeEventListener(stoppedEventListener);
          }
        }
      }
      // TODO: consolidate same stack frames into one event
      for (int p = 0; p < threads.length; p++) {
        final IStackFrame stackFrame = threads[p].getTopStackFrame();
        this.fProxyNotifier.notify(new ProxyDebugStepEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                           toProxyStackFrame(processes[p], stackFrame, 0), threads[p].getId(),
                                                           getDepth(threads[p]), getVariablesAsStringArray(stackFrame)));
      }
    } catch (DebugException except) {
      throw new PDIException(tasks, "Unable to step return: " + except.getMessage());
    }
  }

  public void stepReturn(final BitList tasks, final IAIF aif) throws PDIException {
    raiseDialogBoxNotImplemented("Step Return with Return Value not yet implemented");
  }

  public void stepUntil(final BitList tasks, final IPDILocation location) throws PDIException {
    try {
      final DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
      final DebuggeeThread[] threads = new DebuggeeThread[processes.length];
      for (int p = 0; p < processes.length; p++) {
        threads[p] = processes[p].getStoppingThread();
      }
      final ThreadStoppedEventListener stoppedEventListener = new ThreadStoppedEventListener(threads);
      for (int p = 0; p < threads.length; p++) {
        threads[p].addEventListener(stoppedEventListener);
        final Location pdtLocation = getPDTLocation(processes[p], threads[p], tasks, location);
        if (pdtLocation == null) {
          // We simply forget, and log the reason.
          DebugCore.log(IStatus.ERROR, NLS.bind("Unable to find location for {0}", location));
          threads[p].removeEventListener(stoppedEventListener);
        } else {
          threads[p].runToLocation(pdtLocation);
        }
      }
      final Thread runnableThread = new Thread(new WaitingForStateRunnable(stoppedEventListener));
      runnableThread.start();
      try {
        runnableThread.join();
      } catch (InterruptedException except) {
        throw new PDIException(tasks, "Step over interrupted");
      } finally {
        for (int p = 0; p < threads.length; p++) {
          threads[p].removeEventListener(stoppedEventListener);
        }
      }
      // TODO: consolidate same stack frames into one event
      for (int p = 0; p < threads.length; p++) {
        final IStackFrame stackFrame = threads[p].getTopStackFrame();
        this.fProxyNotifier.notify(new ProxyDebugStepEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                           toProxyStackFrame(processes[p], stackFrame, 0), threads[p].getId(),
                                                           getDepth(threads[p]), getVariablesAsStringArray(stackFrame)));
      }
    } catch (DebugException except) {
      throw new PDIException(tasks, "Unable to step over or access new variables: " + except.getMessage());
    } catch (EngineRequestException except) {
      throw new PDIException(tasks, "Run to Location request refused by PDT: " + except.getMessage());
    }
  }

  public void suspend(final BitList tasks) throws PDIException {
    if (this.fPDTTarget != null) {
      try {
        final DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
        for (int p = 0; p < processes.length; p++) {
          processes[p].halt();
//          DebuggeeThread firstAvailableThread = null;
//          for (final DebuggeeThread thread : processes[p].getThreads()) {
//            if (thread != null) {
//              firstAvailableThread = thread;
//              break;
//            }
//          }
//          assert firstAvailableThread != null;
//          final IStackFrame stackFrame = firstAvailableThread.getTopStackFrame();
//          this.fProxyNotifier.notify(new ProxyDebugSuspendEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
//                                                                toProxyStackFrame(stackFrame, 0),
//                                                                firstAvailableThread.getId(), 
//                                                                firstAvailableThread.getStackFrames().length,
//                                                                getVariablesAsStringArray(stackFrame)));
        }
//      } catch (DebugException except) {
//        throw new PDIException(tasks, NLS.bind("Suspend action error: ", except.getMessage()));
      } catch (EngineRequestException except) {
        throw new PDIException(tasks, NLS.bind("Suspend request refused by PDT: ", except.getMessage()));
//      } finally {
//        // FIXME: is this the right thing to do here?
//        this.fProxyNotifier.notify(new ProxyDebugOKEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks)));
      }
    }
  }

  public void terminate(final BitList tasks) throws PDIException {
    if (! this.fPDTTarget.isTerminated()) {
      try {
//        this.fPDTTarget.terminate();
        final DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
        for (int p = 0; p < processes.length; p++) {
          processes[p].terminate();
        }
        this.fProxyNotifier.notify(new ProxyDebugOKEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks)));
      } catch (DebugException except) {
        throw new PDIException(tasks, "Terminate action error: " + except.getMessage());
      }
    }
  }
  
  // --- IPDIVariableManagement's interface methods implementation

  public void dataEvaluateExpression(final BitList tasks, final String expression) throws PDIException {
    retrieveAIF(tasks, expression);
  }

  public void deleteVariable(final BitList tasks, final String variable) throws PDIException {
    raiseDialogBoxNotImplemented("Delete Variable not yet implemented");
  }

  public void evaluateExpression(final BitList tasks, final String expression) throws PDIException {
    retrieveAIF(tasks, expression);
  }

  public void listArguments(final BitList tasks, final int low, final int high) throws PDIException {
    try {
      DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
      for (int p = 0; p < processes.length; p++) {
        final DebuggeeThread thread = processes[p].getStoppingThread();
        final IStackFrame[] stackFrames = thread.getStackFrames();
        final Collection<String> args = new ArrayList<String>();
        for (int i = low; i <= high; ++i) {
          final int nbParams = ((StackFrame) stackFrames[i]).getNumOfParms();
          final IVariable[] variables = stackFrames[i].getVariables();
          for (int j = 0; j < nbParams; ++j) {
            args.add(variables[j].getName());
          }
        }
        // TODO: consolidate the events
        this.fProxyNotifier.notify(new ProxyDebugArgsEvent(1 /* transId */, ProxyDebugClient.encodeBitSet(tasks), 
                                                           args.toArray(new String[args.size()])));
      }
    } catch (DebugException except) {
      throw new PDIException(tasks, "Unable to access stack frames in PDT: " + except.getMessage());
    }
  }

  public void listGlobalVariables(final BitList tasks) throws PDIException {
    if (this.fProcessListener != null)
      this.fProcessListener.setCurTasks(tasks);
    final GlobalVariable[] globalVars = this.fPDTTarget.getDebugEngine().getGlobalVariables();
    final String[] strGlobalVars = new String[globalVars.length];
    int i = -1;
    for (final GlobalVariable globalVariable : globalVars) {
      strGlobalVars[++i] = globalVariable.getName();
    }
    this.fProxyNotifier.notify(new ProxyDebugVarsEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks), strGlobalVars));
  }

  private StackFrame getCurrentStackFrame(final DebuggeeThread thread) throws DebugException {
    StackFrame frame = thread.getStackFrameMonitored();
    if (frame == null) {
      frame = (StackFrame) thread.getTopStackFrame();
    }
    return frame;
  }
  
  public void listLocalVariables(final BitList tasks) throws PDIException {
    try {
      DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
      for (int p = 0; p < processes.length; p++) {
        final DebuggeeThread thread = processes[p].getStoppingThread();
        final StackFrame stackFrame = getCurrentStackFrame(thread);
        this.fProxyNotifier.notify(new ProxyDebugVarsEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                           getVariablesAsStringArray(stackFrame)));
      }
    } catch (DebugException except) {
      throw new PDIException(tasks, "Unable to access stack frames in PDT: " + except.getMessage());
    }
  }

  public void retrieveAIF(final BitList tasks, final String expr) throws PDIException {
    try {
      DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
      for (int p = 0; p < processes.length; p++) {
        DebuggeeProcess process = processes[p];
        final DebuggeeThread thread = process.getStoppingThread();
        final StackFrame stackFrame = getCurrentStackFrame(thread);
        final Location location = stackFrame.getCurrentLocation(thread.getViewInformation());
        final ProxyDebugAIF aif = createDebugProxyAIF(process, thread, location, expr, false);
        // TODO: consolidate the events
        this.fProxyNotifier.notify(new ProxyDebugDataEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks), aif));
      }
    } catch (EngineRequestException except) {
      throw new PDIException(tasks, NLS.bind("Unable to evaluate ''{0}'' via PDT request: " + except.getMessage(), expr));
    } catch (MemoryException except) {
      throw new PDIException(tasks, NLS.bind("Unable to evaluate ''{0}'' via memory request: " + except.getMessage(), expr));
    } catch (DebugException except) {
      throw new PDIException(tasks, "Unable to access stack frames in PDT: " + except.getMessage());
	}
    // FIXME: send a reply event even when an exception occurs
  }

  public void retrievePartialAIF(final BitList tasks, final String expr, final String key, final boolean listChildren, 
                                 final boolean isExpression) throws PDIException
  {
    System.out.println(expr);
    try {
      DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
      for (int p = 0; p < processes.length; p++) {
        DebuggeeProcess process = processes[p];
        final DebuggeeThread thread = process.getStoppingThread();
        final StackFrame stackFrame = getCurrentStackFrame(thread);
        final Location location = stackFrame.getCurrentLocation(thread.getViewInformation());
        final ProxyDebugAIF aif = createDebugProxyAIF(process, thread, location, expr, listChildren);
        // TODO: consolidate the events
        this.fProxyNotifier.notify(new ProxyDebugPartialAIFEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks), aif, expr));
      }
    } catch (EngineRequestException except) {
      throw new PDIException(tasks, NLS.bind("Unable to evaluate ''{0}'' via PDT request: " + except.getMessage(), expr));
    } catch (MemoryException except) {
      throw new PDIException(tasks, NLS.bind("Unable to evaluate ''{0}'' via memory request: " + except.getMessage(), expr));
    } catch (DebugException except) {
      throw new PDIException(tasks, "Unable to access stack frames in PDT: " + except.getMessage());
    }
    // FIXME: send a reply event even when an exception occurs
  }

  public static class MemoryObject {
    public static final int BYTE_SIZE = 1;
    public static final int CHAR_SIZE = 2;
    public static final int SHORT_SIZE = 2;
    public static final int INT_SIZE = 4;
    public static final int LONG_SIZE = 8;
    public static final int BOOLEAN_SIZE = INT_SIZE;
    public static final int FLOAT_SIZE = INT_SIZE;
    public static final int DOUBLE_SIZE = LONG_SIZE;
    public static final int PTR_SIZE = LONG_SIZE;
    private static final int OBJECT_HEADER_SIZE = PTR_SIZE*4; // FIXME: initial size

    private Memory memory = null;
    private final DebuggeeProcess process;
    protected final DebuggeeProcess getProcess() { return process; }
    private final DebuggeeThread thread;
    protected final DebuggeeThread getThread() { return thread; }
    private final Location location;
    protected final Location getLocation() { return location; }
    private final Address address;
    public MemoryObject(DebuggeeProcess process, DebuggeeThread thread, Location location, long ptr) throws MemoryException {
    	this(process, thread, location, "0x"+toHexString(ptr, 16));
    }
    public MemoryObject(DebuggeeProcess process, DebuggeeThread thread, Location location, String expr) throws MemoryException {
    	this(process, thread, location, process.convertToAddress(expr, location, thread));
    }
    public MemoryObject(DebuggeeProcess process, DebuggeeThread thread, Location location, Address address) throws MemoryException {
    	this(process, thread, location, address, OBJECT_HEADER_SIZE);
    }
    public MemoryObject(DebuggeeProcess process, DebuggeeThread thread, Location location, long ptr, int size) throws MemoryException {
      this(process, thread, location, "0x"+toHexString(ptr, 16), size);
    }
    public MemoryObject(DebuggeeProcess process, DebuggeeThread thread, Location location, String expr, int size) throws MemoryException {
      this(process, thread, location, process.convertToAddress(expr, location, thread), size);
    }
    public MemoryObject(DebuggeeProcess process, DebuggeeThread thread, Location location, Address address, int size) throws MemoryException {
      final int vptrsz = address.getAddressSize();
      assert (vptrsz == MemoryObject.PTR_SIZE);
      memory = process.getMemory(address.getAddress(), size);
      this.process = process;
      this.thread = thread;
      this.location = location;
      this.address = address;
    }
    protected final MemoryByte[] getEnoughMemory(int total) throws MemoryException {
      if (total > memory.getNumBytes()) {
        memory = process.getMemory(address.getAddress(), total);
      }
      MemoryByte[] mbytes = memory.getMemory();
      return mbytes;
    }
    public final byte[] getBytes() throws MemoryException {
      return getBytes(0, memory.getNumBytes());
    }
    public final byte[] getBytes(int offset, int length) throws MemoryException {
      MemoryByte[] mbytes = getEnoughMemory(offset+length);
      return extractBytes(mbytes, offset, length);
    }
    public final int getInt(int offset) throws MemoryException {
      MemoryByte[] mbytes = getEnoughMemory(offset+INT_SIZE);
      return extractInt(mbytes, offset);
    }
    public final long getLong(int offset) throws MemoryException {
      MemoryByte[] mbytes = getEnoughMemory(offset+LONG_SIZE);
      return extractLong(mbytes, offset);
    }
    public final long getPointer(int offset) throws MemoryException {
      if (MemoryObject.PTR_SIZE == MemoryObject.LONG_SIZE)
        return getLong(offset);
      else
        return getInt(offset);
    }
    public static void dumpBytes(MemoryByte[] cbytes) {
	  for (int i = 0; i < cbytes.length; i++) {
        String s = X10PDIDebugger.toHexString(cbytes[i].getValue()&0xFF, 2);
        System.out.print(s+" ");
      }
	  System.out.println();
    }
    public static byte[] extractBytes(final MemoryByte[] mbytes) {
      return extractBytes(mbytes, 0, mbytes.length);
    }
    public static byte[] extractBytes(final MemoryByte[] mbytes, final int offset, final int length) {
      byte[] bytes = new byte[length];
      for (int i = 0; i < length; i++) {
        bytes[i] = mbytes[offset+i].getValue();
      }
      return bytes;
	}
    public static long extractLong(final MemoryByte[] mbytes, final int offset) { // FIXME: endianness
      return ((mbytes[offset+0].getValue()&0xFFl)<<56) | ((mbytes[offset+1].getValue()&0xFFl)<<48) |
             ((mbytes[offset+2].getValue()&0xFFl)<<40) | ((mbytes[offset+3].getValue()&0xFFl)<<32) |
             ((mbytes[offset+4].getValue()&0xFFl)<<24) | ((mbytes[offset+5].getValue()&0xFFl)<<16) |
             ((mbytes[offset+6].getValue()&0xFFl)<< 8) | ((mbytes[offset+7].getValue()&0xFFl)<< 0);
    }
    static int extractInt(final MemoryByte[] mbytes, final int offset) { // FIXME: endianness
      return ((mbytes[offset+0].getValue()&0xFF)<<24) | ((mbytes[offset+1].getValue()&0xFF)<<16) |
             ((mbytes[offset+2].getValue()&0xFF)<< 8) | ((mbytes[offset+3].getValue()&0xFF)<< 0);
    }
    public static long extractPointer(final MemoryByte[] mbytes, final int offset) {
      if (MemoryObject.PTR_SIZE == MemoryObject.LONG_SIZE)
        return X10PDIDebugger.MemoryObject.extractLong(mbytes, offset);
      else
        return X10PDIDebugger.MemoryObject.extractInt(mbytes, offset);
    }
  }

  public static class X10Object extends MemoryObject {
    public static final char INT     = 'i';
    public static final char LONG    = 'l';
    public static final char STRING  = 's';
    public static final char RAIL    = 'r';
    public static final char POINTER = 'p';
    private static final class FieldDesc {
      public FieldDesc(String name, int offset, char type) {
        this.name = name;
        this.offset = offset;
        this.type = type;
      }
      public final String name;
      public final int offset;
      public final char type;
    }
    private final ArrayList<FieldDesc> fields = new ArrayList<FieldDesc>();
    // TODO: compute the offset automatically
    protected final void addField(String name, int offset, char type) {
      fields.add(new FieldDesc(name, offset, type));
    }
    private FieldDesc findField(String name) {
      for (FieldDesc f : fields) {
		if (f.name.equals(name))
		  return f;
      }
      return null;
    }
    protected final int getFieldOffset(String name) {
      return findField(name).offset;
    }
    public final MemoryObject getObject(int offset) throws MemoryException { // TODO: create an object of the right type(!)
      long ptr = getPointer(offset);
      return new MemoryObject(getProcess(), getThread(), getLocation(), ptr);
    }
    public final X10String getString(int offset) throws MemoryException {
      long ptr = getPointer(offset);
      return new X10String(getProcess(), getThread(), getLocation(), "0x"+toHexString(ptr, 16));
    }
    public final int getIntField(String name) throws MemoryException {
      assert (findField(name).type == INT);
      return getInt(getFieldOffset(name));
    }
    public final long getLongField(String name) throws MemoryException {
      assert (findField(name).type == LONG);
      return getLong(getFieldOffset(name));
    }
    public final long getPointerField(String name) throws MemoryException {
      assert (findField(name).type == POINTER || findField(name).type == RAIL);
      return getPointer(getFieldOffset(name));
    }
    public final MemoryObject getObjectField(String name) throws MemoryException { // TODO
      return getObject(getFieldOffset(name));
    }
    public final X10String getStringField(String name) throws MemoryException { // TODO
      assert (findField(name).type == STRING);
      return getString(getFieldOffset(name));
    }
    public X10Object(DebuggeeProcess process, DebuggeeThread thread, Location location, long ptr) throws MemoryException {
      super(process, thread, location, ptr);
    }
    public X10Object(DebuggeeProcess process, DebuggeeThread thread, Location location, String expr) throws MemoryException {
      super(process, thread, location, expr);
    }
    public X10Object(DebuggeeProcess process, DebuggeeThread thread, Location location, Address address) throws MemoryException {
      super(process, thread, location, address);
    }
  }

  public static String FMGL(String name) {
    return "x10__"+name;
  }

  /**
   * An in-memory representation of the X10 String object.
   * Layout:
   * <pre>
   * ptr -> .------------.  0
   *        | vtable     |
   *        |  (8 bytes) |
   *        |------------|  8
   *        || Object    |
   *        || vtable    |
   *        || (8 bytes) |
   *        ||-----------| 16
   *        || ???       |
   *        || (8 bytes) |
   *        |------------| 24
   *        | content    |
   *        |  (8 bytes) |
   *        |------------| 32
   *        | length     |
   *        |  (8 bytes) |
   *        '------------'
   * </pre>
   */
  public static class X10String extends X10Object {
    public X10String(DebuggeeProcess process, DebuggeeThread thread, Location location, String address) throws MemoryException {
      super(process, thread, location, address);
      addField("contents", PTR_SIZE*3, 'p');
      addField("length", PTR_SIZE*4, 'l');
    }
    public final long getLength() throws MemoryException {
      return getLong(getFieldOffset("length"));
    }
    public final MemoryObject getContents() throws MemoryException {
      long ptr = getPointer(getFieldOffset("contents"));
      int length = (int) getLength();
      return new MemoryObject(getProcess(), getThread(), getLocation(), ptr, length);
    }
  }

  /**
   * An in-memory representation of some X10 Rail class.  Gets the element type as argument.
   * Subclasses must override the headerSize() method.
   * Layout:
   * <pre>
   * ptr -> .------------.  0
   *        || header    |
   *        || (N bytes) |
   *        |------------|  N
   *        | length (4) |
   *        |------------|  N+4
   *        | pad (4)    |
   *        |------------|  N+8
   *        | contents   |  :
   *        |     ...    |  '
   * </pre>
   */
  public static abstract class X10AnyRail extends X10Object {
    private int elementSize;
    public final int getElementSize() { return elementSize; }
    public X10AnyRail(DebuggeeProcess process, DebuggeeThread thread, Location location, String address, int elementSize) throws MemoryException {
      super(process, thread, location, address);
      addField(FMGL("length"), headerSize(), 'i');
      this.elementSize = elementSize;
    }
    protected abstract int headerSize();
    private int getContentOffset() {
      return getFieldOffset(FMGL("length"))+INT_SIZE+INT_SIZE;
    }
    public final int getLength() throws MemoryException {
      return getInt(getFieldOffset(FMGL("length")));
    }
    private int retrieveContents() throws MemoryException {
      int length = getLength();
      getEnoughMemory(getContentOffset()+length*elementSize);
      return length;
    }
    private void checkBounds(int index) throws MemoryException {
      int length = retrieveContents();
      assert (index >= 0 && index < length);
    }
    public final int getIntAt(int index) throws MemoryException {
      checkBounds(index);
      assert (elementSize == INT_SIZE);
      return getInt(getContentOffset()+index*elementSize);
    }
    public final long getLongAt(int index) throws MemoryException {
      checkBounds(index);
      assert (elementSize == LONG_SIZE);
      return getLong(getContentOffset()+index*elementSize);
    }
    public final long getPointerAt(int index) throws MemoryException {
      checkBounds(index);
      assert (elementSize == PTR_SIZE);
      return getPointer(getContentOffset()+index*elementSize);
    }
    public final MemoryObject getObjectAt(int index) throws MemoryException { // TODO: create an object of the right type(!)
      checkBounds(index);
      assert (elementSize == PTR_SIZE);
      return getObject(getContentOffset()+index*elementSize);
    }
    public final X10String getStringAt(int index) throws MemoryException {
      checkBounds(index);
      assert (elementSize == PTR_SIZE);
      return getString(getContentOffset()+index*elementSize);
    }
    public final byte[] getRawContents() throws MemoryException {
      return getRawContents(0, getLength());
    }
    public final byte[] getRawContents(int offset, int length) throws MemoryException {
      return getBytes(getContentOffset()+offset*elementSize, length*elementSize);
    }
  }

  /**
   * An in-memory representation of the X10 ValRail class.  Gets the element type as argument.
   * Layout:
   * <pre>
   * ptr -> .------------.  0
   *        | vtable     |
   *        |  (8 bytes) |
   *        |------------|  8
   *        || Object    |
   *        || vtable    |
   *        || (8 bytes) |
   *        ||-----------| 16
   *        ||???        |
   *        || (8 bytes) |
   *        |------------| 24
   *        || Fun_0_1   |
   *        || vtable    |
   *        || (8 bytes) |
   *        ||-----------| 32
   *        || ???       |
   *        || (8 bytes) |
   *        |------------| 40
   *        || AnyRail   |
   *        || vtable    |
   *        || (8 bytes) |
   *        ||-----------| 48
   *        || ???       |
   *        || (8 bytes) |
   *        |------------| 56   \
   *        | length (4) |       |
   *        |------------| 60    |
   *        | pad (4)    |        > AnyRail layout
   *        |------------| 64    |
   *        | contents   |  :    |
   *        |     ...    |  '   /
   * </pre>
   */
  public static class X10ValRail extends X10AnyRail {
    public X10ValRail(DebuggeeProcess process, DebuggeeThread thread, Location location, String address, int elementSize) throws MemoryException {
      super(process, thread, location, address, elementSize);
    }
    protected int headerSize() {
      return PTR_SIZE*7;
    }
  }

  /**
   * An in-memory representation of the X10 Rail class.  Gets the element type as argument.
   * Layout:
   * <pre>
   * ptr -> .------------.  0
   *        | vtable     |
   *        |  (8 bytes) |
   *        |------------|  8
   *        || Object    |
   *        || vtable    |
   *        || (8 bytes) |
   *        ||-----------| 16
   *        ||???        |
   *        || (8 bytes) |
   *        |------------| 24
   *        ||| Settable |
   *        ||| vtable   |
   *        ||| (8 bytes)|
   *        |||----------| 32
   *        ||| ???      |
   *        ||| (32 bts) |
   *        |------------| 64
   *        || AnyRail   |
   *        || vtable    |
   *        || (8 bytes) |
   *        ||-----------| 72
   *        || ???       |
   *        || (8 bytes) |
   *        |------------| 80
   *        | length (4) |
   *        |------------| 84
   *        | pad (4)    |
   *        |------------| 88
   *        | contents   |  :
   *        |     ...    |  '
   * </pre>
   */
  public static class X10Rail extends X10AnyRail {
    public X10Rail(DebuggeeProcess process, DebuggeeThread thread, Location location, String address, int elementSize) throws MemoryException {
      super(process, thread, location, address, elementSize);
    }
    protected int headerSize() {
      return PTR_SIZE*10;
    }
  }

  /**
   * Finds a matching bracket/brace/parenthesis, starting at a given position.
   * If the character at that position is an opening bracket, looks forward, otherwise looks backward.
   * @param str the string
   * @param pos starting position
   * @return the position of a matching bracket, or -1 if none (or if bracket not recognized).
   */
  private int findMatch(String str, int pos) {
    boolean forward;
    char c = str.charAt(pos);
	char orig = c;
    char match;
    switch (orig) {
    case '[': match = ']'; forward = true; break;
    case '(': match = ')'; forward = true; break;
    case '{': match = '}'; forward = true; break;
    case '<': match = '>'; forward = true; break;
    case ']': match = '['; forward = false; break;
    case ')': match = '('; forward = false; break;
    case '}': match = '{'; forward = false; break;
    case '>': match = '<'; forward = false; break;
    default: return -1;
    }
    int count = 1;
    int incr = forward ? 1 : -1;
    int limit = forward ? str.length()-1 : 0;
    for (pos += incr; pos != limit; pos += incr) {
      if (c == orig) count++;
      if (c == match) {
        count--;
        if (count == 0)
          return pos;
      }
    }
    return -1;
  }

  /**
   * Expanding a derived expression - extract base expression.
   * This could be one of the following:
   * <li> *(base+off)@num
   * <li> base[idx]
   * <li> base.fld
   * @param expr the derived expression
   * @return the base expression to evaluate
   */
  private String extractBaseExpr(String expr) {
    int atIdx = expr.lastIndexOf('@');
    int dotIdx = expr.lastIndexOf('.');
    if (expr.startsWith("*") && atIdx != -1) { // *(base+off)@num
      String inner = expr.substring(1, atIdx);
      assert (inner.startsWith("(") && inner.endsWith(")"));
      inner = inner.substring(1, inner.length()-1);
      int plusIdx = inner.lastIndexOf('+');
      if (plusIdx == -1)
        return inner;
      String base = inner.substring(0, plusIdx);
      if (base.startsWith("(") && base.endsWith(")"))
        base = base.substring(1, base.length()-1);
      return base;
    }
    if (expr.endsWith("]") && atIdx == -1) { // base[idx]
      // find matching "["
      int openIdx = findMatch(expr, expr.length()-1);
      if (openIdx == -1) // no matching bracket
        return expr;
      String base = expr.substring(0, openIdx);
      if (base.startsWith("(") && base.endsWith(")"))
          base = base.substring(1, base.length()-1);
      return base;
    }
    if (expr.lastIndexOf(".") != -1 && atIdx == -1) { // base.fld
      // TODO
    }
    return expr;
  }

  private ProxyDebugAIF createDebugProxyAIF(DebuggeeProcess process, final DebuggeeThread thread,
      final Location location, final String expr, boolean listChildren) throws EngineRequestException, DebugException, MemoryException
  {
    String base = expr;
    if (listChildren) // Expanding an existing expression
      base = extractBaseExpr(expr);
    if (base.startsWith("(this).")) // FIXME: pretty useless, because PDT won't evaluate "this->x" anyway
      base = "this->" + base.substring("(this).".length());
    ExprNodeBase rootNode = evaluateExpression(base, process, thread, location);
    final EPDTVarExprType exprType = getExprType(rootNode);
    final String type = rootNode.getReferenceTypeName();
    final String[] desc = fTranslator.getStructDescriptor(type);
    if (rootNode instanceof ExprNode) {
      if (type.startsWith("class ref<") || type.startsWith("class x10aux::ref<")) { // got a reference, unwrap
        System.out.println("Got a ref: "+type);
        PDTDebugElement[] children = rootNode.getChildren();
        assert (children.length == 2); // _val and __ref{}
        rootNode = (ExprNodeBase) children[0];
        if (rootNode instanceof ExprNode) {
          final String type2 = rootNode.getReferenceTypeName();
          assert (type2.endsWith("*"));
//          children = rootNode.getChildren();
//          assert (children.length == 1); // the referenced object
//          ExprNodeBase referencedObject = (ExprNodeBase) children[0];
        }
      }
    }
    String result = rootNode.getValueString();
    if (exprType == EPDTVarExprType.ARRAY) {
//      GlobalVariable[] globals = process.getDebugEngine().getGlobalVariables();
//      for (GlobalVariable v : globals) {
//        if (v.getName().equals("x10aux::nullString"))
//          evaluateExpression(v.getExpression().replace("\nx10aux", "\n&x10aux"), process, thread, location);
//      }
//      ExprNodeBase lengthNode = evaluateExpression(expr+"._val->x10__length", process, thread, location);
//      String lengthType = lengthNode.getReferenceTypeName();
//      assert (lengthType.equals("int"));
//      int length = Integer.parseInt(lengthNode.getValueString());
      X10AnyRail rail = null;
      if (desc[0].startsWith("x10.lang.Rail"))
        rail = new X10Rail(process, thread, location, result, getExprSize(desc[1]));
      else if (desc[0].startsWith("x10.lang.ValRail"))
        rail = new X10ValRail(process, thread, location, result, getExprSize(desc[1]));
      int length = rail.getLength();
      if (listChildren) {
        EPDTVarExprType elementType = getExprType(desc[1]);
        // This could be one of the following:
        // - *(base+off)@num
        // - base[idx]
        int offset = 0;
        int num = 1;
        int atIdx = expr.lastIndexOf('@');
        if (expr.startsWith("*") && atIdx != -1) {
          num = Integer.parseInt(expr.substring(atIdx+1));
          String inner = expr.substring(1, atIdx);
          assert (inner.startsWith("(") && inner.endsWith(")"));
          inner = inner.substring(1, inner.length()-1);
          int plusIdx = inner.lastIndexOf('+');
          offset = Integer.parseInt(inner.substring(plusIdx+1));
        } else if (expr.endsWith("]") && atIdx == -1) {
          // find matching "["
          int openIdx = findMatch(expr, expr.length()-1);
          if (openIdx != -1) {
            String idx = expr.substring(openIdx+1, expr.length()-1);
            offset = Integer.parseInt(idx);
            num = 1;
          }
        }
//        assert (offset == 0);
//        assert (num == length);
//        byte[] bytes = rail.getRawContents(offset, num);
//        // TODO: extract individual elements and concat the representations
//        result = toHexString(bytes);
        StringBuilder sb = new StringBuilder(); // TODO
        for (int i = 0; i < num; i++) {
          switch (elementType) {
          case INT:
          case FLOAT: sb.append(toHexString(rail.getIntAt(offset+i), 8)); break;
          case LONG:
          case DOUBLE: sb.append(toHexString(rail.getLongAt(offset+i), 16)); break;
          case STRING: {
                         X10String str = rail.getStringAt(offset+i);
                         int len = (int) str.getLength();
                         byte[] bytes = str.getContents().getBytes();
                         sb.append(toHexString(len, 4)+toHexString(bytes));
                         break;
                       }
          default: sb.append(toHexString(rail.getPointerAt(offset+i), 16)); break;
          }
        }
        result = sb.toString();
        desc[2] = Integer.toString(offset)+".."+Integer.toString(offset+num-1); // we want an inclusive upper bound
      } else {
        if (result.startsWith("0x"))
          result = result.substring(2);
        // Encode the pointer
        boolean isNull = result.equals(toHexString(0, 16));
        result = isNull ? "00" : "01"+result;
//        desc[2] = Integer.toString(length-1));
        desc[2] = "0.."+Integer.toString(length - 1); // we want an inclusive upper bound
      }
    }
    if (exprType == EPDTVarExprType.STRING) {
      assert (!listChildren);
//      GlobalVariable[] globals = process.getDebugEngine().getGlobalVariables();
//      for (GlobalVariable v : globals) {
//        if (v.getName().equals("x10aux::nullString"))
//          evaluateExpression(v.getExpression().replace("\nx10aux", "\n&x10aux"), process, thread, location);
//      }
      X10String string = new X10String(process, thread, location, result);
      int length = (int) string.getLength();
      MemoryObject contents = string.getContents();
      byte[] bytes = contents.getBytes();
      assert (bytes.length == length);
      String content = null;
      try {
        content = "\"" + new String(bytes, "ISO-8859-1") + "\"";
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
//      ExprNodeBase contentNode = evaluateExpression(expr+"._val->x10__content", process, thread, location);
//      String contentType = contentNode.getReferenceTypeName();
//      assert (contentType.equals("char*"));
//      desc[1] = contentNode.getValueString();
      desc[1] = getValue(EPDTVarExprType.STRING, content);
//      ExprNodeBase lengthNode = evaluateExpression(expr+"._val->x10__content_length", process, thread, location);
//      String lengthType = lengthNode.getReferenceTypeName();
//      assert (lengthType.equals("int"));
//      desc[2] = lengthNode.getValueString();
      desc[2] = Integer.toString(length);
      result = toHexString(length, 4)+toHexString(bytes);
    }
    if (exprType == EPDTVarExprType.STRUCT) {
      if (listChildren) {
        // Compute the value
        // TODO: factor out into an X10Class
        final int numInterfaces = Integer.parseInt(desc[1]);
        X10Object object = new X10Object(process, thread, location, result) {
          {
            int offset = 3*PTR_SIZE + 2*PTR_SIZE*numInterfaces;
            for (int j = 2; j < desc.length; j++) {
              String name = desc[j++];
              String type = desc[j];
              EPDTVarExprType fet = getExprType(type);
              char ft = '\0';
              switch (fet) {
              case ADDRESS:
              case STRUCT: ft = X10Object.POINTER; break;
              case ARRAY:  ft = X10Object.RAIL; break;
              case STRING: ft = X10Object.STRING; break;
              case BOOL:
              case CHAR:
              case INT:
              case FLOAT:  ft = X10Object.INT; break;
              case LONG:
              case DOUBLE: ft = X10Object.LONG; break;
              }
              addField(name, offset, ft);
              int size = getExprSize(type);
              size = size + (PTR_SIZE-size)%PTR_SIZE; // pad
              offset += size;
            }
          }
        };
        StringBuilder sb = new StringBuilder();
        for (int j = 2; j < desc.length; j++) {
          String n = desc[j++];
          String t = desc[j];
          EPDTVarExprType et = getExprType(t);
          switch (et) {
          case INT:
          case FLOAT: sb.append(toHexString(object.getIntField(n), 8)); break;
          case LONG:
          case DOUBLE: sb.append(toHexString(object.getLongField(n), 16)); break;
          case STRING: {
                         X10String str = object.getStringField(n);
                         int len = (int) str.getLength();
                         byte[] bytes = str.getContents().getBytes();
                         sb.append(toHexString(len, 4)+toHexString(bytes));
                         break;
                       }
          case ARRAY:
          case STRUCT: {
                         long val = object.getPointerField(n);
                         if (val == 0)
                           sb.append("00");
                         else
                           sb.append("01").append(toHexString(val, 16));
                         break;
                       }
          default: sb.append(toHexString(object.getPointerField(n), 16)); break;
          }
        }
        result = sb.toString();
        desc[1] = "NOPTR";
      } else {
        if (result.startsWith("0x"))
          result = result.substring(2);
        // Encode the pointer
        boolean isNull = result.equals(toHexString(0, 16));
        result = isNull ? "00" : "01"+result;
      }
    }
    final String value = getValue(exprType, result);
    final ProxyDebugAIF aif = new ProxyDebugAIF(getVariableType(exprType, desc), value, expr);
    return aif;
  }

  public static void dumpMemory(DebuggeeProcess process, long ptr, int length) {
    try {
      DebuggeeThread thread = process.getStoppingThread();
      Address contentAddress = process.convertToAddress("0x"+toHexString(ptr, 16), thread.getLocation(thread.getViewInformation()), thread);
      Memory memory = process.getMemory(contentAddress.getAddress(), length);
      MemoryByte[] cbytes = memory.getMemory();
      MemoryObject.dumpBytes(cbytes);
    } catch (MemoryException e) { }
  }

  private ExprNodeBase evaluateExpression(final String expr, DebuggeeProcess process, final DebuggeeThread thread, final Location location)
    throws EngineRequestException
  {
//    ExpressionBase expression = thread.evaluateExpression(location, expr, 0 /* expansionLevel */, 0);
    ExpressionBase expression = process.monitorExpression(location.getEStdView(), thread.getId(), expr,  IEPDCConstants.MonEnable, IEPDCConstants.MonTypeProgram, null, null, null, null);
    if (expression == null)
    	System.out.println("\tGot null from evaluating '"+expr+"'");
    ExprNodeBase nodeBase = expression.getRootNode();
	return nodeBase;
  }

  public void retrieveVariableType(final BitList tasks, final String variable) throws PDIException {
    try {
      DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
      for (int p = 0; p < processes.length; p++) {
        DebuggeeProcess process = processes[p];
        final DebuggeeThread thread = process.getStoppingThread();
        final StackFrame stackFrame = getCurrentStackFrame(thread);
        final Location location = stackFrame.getCurrentLocation(thread.getViewInformation());
        final ProxyDebugAIF aif = createDebugProxyAIF(process, thread, location, variable, false);
        // TODO: consolidate the events
        this.fProxyNotifier.notify(new ProxyDebugTypeEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks), aif.getFDS()));
      }
    } catch (EngineRequestException except) {
      throw new PDIException(tasks, NLS.bind("Unable to evaluate ''{0}'' via PDT request: " + except.getMessage(), variable));
    } catch (MemoryException except) {
      throw new PDIException(tasks, NLS.bind("Unable to evaluate ''{0}'' via memory request: " + except.getMessage(), variable));
    } catch (DebugException except) {
      throw new PDIException(tasks, "Unable to access stack frames in PDT: " + except.getMessage());
    }
    // FIXME: send a reply event even when an exception occurs
  }
  
  // --- IPDISignalManagement's interface methods implementation

  public void listSignals(final BitList tasks, final String name) throws PDIException {
    raiseDialogBoxNotImplemented("List Signals is not an available feature with X10 Debugger");
  }

  public void retrieveSignalInfo(final BitList tasks, final String arg) throws PDIException {
    raiseDialogBoxNotImplemented("Retrieve Signals info is not an available feature with X10 Debugger");
  }
  
  // --- IPDIStackframeManagement's interface methods implementation

  public void listStackFrames(final BitList tasks, final int low, final int depth) throws PDIException {
    try {
      DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
      for (int p = 0; p < processes.length; p++) {
        final DebuggeeThread thread = processes[p].getStoppingThread();
        final IStackFrame[] stackFrames = thread.getStackFrames();
        final ProxyDebugStackFrame[] proxyStackFrames = new ProxyDebugStackFrame[stackFrames.length];
        for (int i = low; i < depth; ++i) {
          proxyStackFrames[i] = toProxyStackFrame(processes[p], stackFrames[i], i);
        }
        // TODO: consolidate same stack frame sets into one event
        BitList task = getTask(processes[p]);
        this.fProxyNotifier.notify(new ProxyDebugStackframeEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(task),
                                                                 proxyStackFrames));
      }
    } catch (DebugException except) {
      throw new PDIException(tasks, "Unable to access stack frames in PDT: " + except.getMessage());
    }
    // FIXME: send a reply event even when an exception occurs
  }

  public void setCurrentStackFrame(final BitList tasks, final int level) throws PDIException {
    try {
      DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
      for (int p = 0; p < processes.length; p++) {
        final DebuggeeThread thread = processes[p].getStoppingThread();
        final IStackFrame[] stackFrames = thread.getStackFrames();
        if (level < stackFrames.length) {
          StackFrame frame = (StackFrame) stackFrames[level];
          thread.getLocals(frame);
          // TODO: consolidate the events
          this.fProxyNotifier.notify(new ProxyDebugVarsEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                             getVariablesAsStringArray(frame)));
        } else {
          DebugCore.log(IStatus.ERROR, "Stack frame level selected is out of bounds");
        }
      }
    } catch (DebugException except) {
      throw new PDIException(tasks, "Unable to access stack frames in PDT: " + except.getMessage());
    }
    // FIXME: send a reply event even when an exception occurs
  }
  
  // --- IPDIThreadManagement's interface methods implementation

  public void listInfoThreads(final BitList tasks) throws PDIException {
    final DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
    for (int p = 0; p < processes.length; p++) {
      final DebuggeeThread[] threads = processes[p].getThreads();
      final Collection<String> threadIds = new ArrayList<String>(threads.length);
      for (final DebuggeeThread thread : threads) {
        if ((thread != null) && ! thread.isTerminated()) {
          threadIds.add(String.valueOf(thread.getId()));
        }
      }
      // TODO: consolidate the events
      this.fProxyNotifier.notify(new ProxyDebugInfoThreadsEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                                threadIds.toArray(new String[threadIds.size()])));
    }
    // FIXME: send a reply event even when an exception occurs
  }

  public void retrieveStackInfoDepth(final BitList tasks) throws PDIException {
    // send one event per unique depth value...
    final DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
    for (int p = 0; p < processes.length; p++) {
      try {
        final int depth = processes[p].getStoppingThread().getStackFrames().length;
        // TODO: consolidate the events
        this.fProxyNotifier.notify(new ProxyDebugStackInfoDepthEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks), 
                                                                     depth));
      } catch (DebugException except) {
        throw new PDIException(tasks, "Unable to access stack frames in PDT: " + except.getMessage());
      }
    }
    // FIXME: send a reply event even when an exception occurs
  }

  public void selectThread(final BitList tasks, final int tid) throws PDIException {
    DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
    for (int p = 0; p < processes.length; p++) {
      final DebuggeeThread thread = processes[p].getThread(tid);
      try {
        final IStackFrame stackFrame = getCurrentStackFrame(thread);
        if (stackFrame == null) {
          this.fProxyNotifier.notify(new ProxyDebugStackframeEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                                   new ProxyDebugStackFrame[0]));
        } else {
          final ProxyDebugStackFrame proxyStackFrame = toProxyStackFrame(processes[p], stackFrame, 0);
          // TODO: consolidate the events
          this.fProxyNotifier.notify(new ProxyDebugSetThreadSelectEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                                        tid, proxyStackFrame));
        }
      } catch (DebugException except) {
        throw new PDIException(tasks, "Unable to access stack frames in PDT: " + except.getMessage());
      }
    }
    // FIXME: send a reply event even when an exception occurs
  }
  
  // --- IPDIMemoryBlockManagement's interface methods implementation

  public void createDataReadMemory(final BitList tasks, final long offset, final String address, final int wordFormat, 
                                   final int wordSize, final int rows, final int cols, 
                                   final Character asChar) throws PDIException
  {
    // TODO: report error instead
    this.fProxyNotifier.notify(new ProxyDebugOKEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks)));
  }

  public void createDataWriteMemory(final BitList tasks, final long offset, final String address, final int wordFormat, 
                                    final int wordSize, final String value) throws PDIException
  {
    // TODO: report error instead
    this.fProxyNotifier.notify(new ProxyDebugOKEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks)));
  }
  
  // --- IDebugEngineEventListener's interface methods implementation
  
  public void commandLogResponse(final DebugEngineCommandLogResponseEvent event) {
  }

  public void debugEngineTerminated(final DebugEngineTerminatedEvent event) {
    event.getDebugEngine().removeEventListener(this);
  }

  public void errorOccurred(final ErrorOccurredEvent event) {
  }

  public void messageReceived(final MessageReceivedEvent event) {
  }

  public void modelStateChanged(final ModelStateReadyEvent event) {
  }

  public void processAdded(final ProcessAddedEvent event) {
    int taskNum = this.fProcessCounter++;
    DebuggeeProcess process = event.getProcess();
    PDTProcessEventListener processListener = new PDTProcessEventListener(process, this.fProxyNotifier);
    this.fProcessListener = processListener;
    this.fTaskToProcess[taskNum] = process;
    process.addEventListener(processListener);
  }
  
  // --- Public services
  
  public void setPDISession(final IPDISession pdiSession) {
    this.fPDISession = pdiSession;
    this.fTaskToProcess = new DebuggeeProcess[pdiSession.getTotalTasks()];
  }
  
  public void setLaunch(final IPLaunch launch) {
    this.fWaitLock.lock();
    try {
      this.fLaunch = launch;
      this.fLaunchCondition.signal();
    } finally {
      this.fWaitLock.unlock();
    }
  }
  
  // --- Private code
  
  private static String toHexString(final byte[] rawBytes) {
    final byte[] hex = new byte[2 * rawBytes.length];
    int index = 0;
    for (final byte b : rawBytes) {
      int v = b & 0xFF;
      hex[index++] = HEX_CHAR_TABLE[v >>> 4];
      hex[index++] = HEX_CHAR_TABLE[v & 0xF];
    }
    return new String(hex, 0);
  }
  
  private static String toHexString(final long value, final int length) {
    final String s = Long.toHexString(value);
    if (s.length() > length) {
      // Returns rightmost length chars 
      return s.substring(s.length() - length);
    } else if (s.length() < length) {
      // Pads on left with zeros. at most 15 will be prepended
      return "000000000000000".substring(0, length - s.length()) + s;
    } else {
      return s;
    }
  }
  
  private PICLLoadInfo createLoadInfo(final ILaunchConfiguration configuration) throws CoreException {
    final PICLLoadInfo loadInfo = new PICLLoadInfo();
    loadInfo.setLaunchConfig(configuration);
    loadInfo.setProgramName(configuration.getAttribute(ATTR_EXECUTABLE_PATH, EMPTY_STRING));
    loadInfo.setProgramParms(configuration.getAttribute(ATTR_ARGUMENTS, EMPTY_STRING));
    loadInfo.setProject(getProjectResource(configuration.getAttribute(ATTR_PROJECT_NAME, EMPTY_STRING)));
   
    loadInfo.setStartupBehaviour(PICLLoadInfo.DEBUG_INITIALIZATION);
    return loadInfo;
  }
  
  private DebuggeeProcess[] getDebuggeeProcesses(final BitList tasks) throws PDIException {
    final int numTasks = tasks.cardinality();
    final DebuggeeProcess[] processes = new DebuggeeProcess[numTasks];
    final int firstBit = tasks.nextSetBit(0);
    int nextBit = firstBit;
    for (int i = 0; i < numTasks; i++) {
      final DebuggeeProcess process = this.fTaskToProcess[nextBit];
      if (process == null) {
        throw new PDIException(tasks, "Unable to find the process associated with this task");
      }
      processes[i] = process;
      nextBit = tasks.nextSetBit(nextBit + 1);
      assert (nextBit != -1 || i == numTasks-1);
    }
    return processes;
  }

  private BitList getTasks(final DebuggeeProcess[] processes) {
    BitList tasks = new BitList(this.fPDISession.getTotalTasks());
    for (int p = 0; p < processes.length; p++)
	  setTask(tasks, processes[p]);
    return tasks;
  }

  private BitList getTask(final DebuggeeProcess process) {
    return getTasks(new DebuggeeProcess[] { process });
  }

  private void setTask(BitList tasks, final DebuggeeProcess process) {
    for (int i = 0; i < this.fTaskToProcess.length; i++)
      if (process.equals(this.fTaskToProcess[i]))
		tasks.set(i, i+1);
  }
  
  private int getDepth(final DebuggeeThread thread) throws DebugException {
    return thread.getStackFrames().length;
  }

  private EPDTVarExprType getExprType(final ExprNodeBase nodeBase) {
    if ((nodeBase instanceof ScalarExprNode) || (nodeBase instanceof ExprNode)) {
      final String type = ((EStdExprNode) nodeBase.getInternalNode()).getType();
      return getExprType(type);
    } else if (nodeBase instanceof PointerExprNode) {
      final EStdTreeNode treeNode = (EStdTreeNode) nodeBase.getInternalNode();
      if (treeNode.getNumChildren() > 1) {
        throw new AssertionError("Pointer Node with multiple children?");
      } else {
        final EStdTreeNode node = treeNode.getNodes()[0];
        return EPDTVarExprType.ADDRESS;
      }
    } else if ((nodeBase instanceof StructExprNode) || (nodeBase instanceof ClassExprNode)) {
      return EPDTVarExprType.STRUCT;
    } else if (nodeBase instanceof ArrayExprNode) {
      return EPDTVarExprType.ARRAY;
    } else {
      return EPDTVarExprType.UNKNOWN;
    }
  }

  private EPDTVarExprType getExprType(String type) {
    if (type.endsWith(" "))
      type = type.trim();
    if (type.endsWith("&"))
        type = type.substring(0, type.length()-1);
	if ("char".equals(type)) {
      return EPDTVarExprType.CHAR;
    } else if ("int".equals(type)) {
      return EPDTVarExprType.INT;
    } else if ("long".equals(type)) {
      return EPDTVarExprType.LONG;
    } else if ("float".equals(type)) {
      return EPDTVarExprType.FLOAT;
    } else if ("double".equals(type)) {
      return EPDTVarExprType.DOUBLE;
    } else if ("x10_boolean".equals(type)) {
      return EPDTVarExprType.BOOL;
    } else if ("x10_byte".equals(type)) {
      return EPDTVarExprType.INT;
    } else if ("x10_short".equals(type)) {
      return EPDTVarExprType.INT;
    } else if ("x10_char".equals(type)) {
      return EPDTVarExprType.CHAR;
    } else if ("x10_int".equals(type)) {
      return EPDTVarExprType.INT;
    } else if ("x10_long".equals(type)) {
      return EPDTVarExprType.LONG;
    } else if ("x10_float".equals(type)) {
      return EPDTVarExprType.FLOAT;
    } else if ("x10_double".equals(type)) {
      return EPDTVarExprType.DOUBLE;
    } else if (type.startsWith("class ref<x10__lang__Rail<")) {
      return EPDTVarExprType.ARRAY;
    } else if (type.startsWith("x10aux__ref<x10__lang__Rail<")) {
      return EPDTVarExprType.ARRAY;
    } else if (type.startsWith("class x10aux::ref<x10::lang::Rail<")) {
      return EPDTVarExprType.ARRAY;
    } else if (type.startsWith("x10aux::ref<x10::lang::Rail<")) {
      return EPDTVarExprType.ARRAY;
    } else if (type.startsWith("class ref<x10__lang__ValRail<")) {
      return EPDTVarExprType.ARRAY;
    } else if (type.startsWith("x10aux__ref<x10__lang__ValRail<")) {
      return EPDTVarExprType.ARRAY;
    } else if (type.startsWith("class x10aux::ref<x10::lang::ValRail<")) {
      return EPDTVarExprType.ARRAY;
    } else if (type.startsWith("x10aux::ref<x10::lang::ValRail<")) {
      return EPDTVarExprType.ARRAY;
    } else if (type.equals("class ref<x10__lang__String>")) {
      return EPDTVarExprType.STRING;
    } else if (type.equals("x10aux__ref<x10__lang__String>")) {
      return EPDTVarExprType.STRING;
    } else if (type.equals("class x10aux::ref<x10::lang::String>")) {
      return EPDTVarExprType.STRING;
    } else if (type.equals("x10aux::ref<x10::lang::String>")) {
      return EPDTVarExprType.STRING;
    } else if (type.startsWith("class ref<")) {
      return EPDTVarExprType.STRUCT;
    } else if (type.startsWith("x10aux__ref<")) {
      return EPDTVarExprType.STRUCT;
    } else if (type.startsWith("class x10aux::ref<")) {
      return EPDTVarExprType.STRUCT;
    } else if (type.startsWith("x10aux::ref<")) {
      return EPDTVarExprType.STRUCT;
    } else if (type.startsWith("class ") && type.endsWith("*")) {
      return EPDTVarExprType.STRUCT;
    } else if (type.endsWith("*")) {
      return EPDTVarExprType.ADDRESS;
    } else {
      return EPDTVarExprType.UNKNOWN;
    }
  }

  private int getExprSize(String type) {
    if (type.endsWith(" "))
      type = type.trim();
    if (type.endsWith("&"))
      type = type.substring(0, type.length()-1);
  	if ("char".equals(type)) {
      return MemoryObject.BYTE_SIZE;
    } else if ("short".equals(type)) {
      return MemoryObject.SHORT_SIZE;
    } else if ("int".equals(type)) {
      return MemoryObject.INT_SIZE;
    } else if ("long".equals(type)) {
      return MemoryObject.LONG_SIZE;
    } else if ("float".equals(type)) {
      return MemoryObject.FLOAT_SIZE;
    } else if ("double".equals(type)) {
      return MemoryObject.DOUBLE_SIZE;
    } else if ("x10_boolean".equals(type)) {
      return MemoryObject.BOOLEAN_SIZE;
    } else if ("x10_byte".equals(type)) {
      return MemoryObject.BYTE_SIZE;
    } else if ("x10_short".equals(type)) {
      return MemoryObject.SHORT_SIZE;
    } else if ("x10_char".equals(type)) {
      return MemoryObject.CHAR_SIZE;
    } else if ("x10_int".equals(type)) {
      return MemoryObject.INT_SIZE;
    } else if ("x10_long".equals(type)) {
      return MemoryObject.LONG_SIZE;
    } else if ("x10_float".equals(type)) {
      return MemoryObject.FLOAT_SIZE;
    } else if ("x10_double".equals(type)) {
      return MemoryObject.DOUBLE_SIZE;
    } else if (type.endsWith("*")) {
      return MemoryObject.PTR_SIZE;
    } else if (type.startsWith("class ref<")) {
      return MemoryObject.PTR_SIZE;
    } else if (type.startsWith("x10aux__ref<")) {
      return MemoryObject.PTR_SIZE;
    } else if (type.startsWith("class x10aux::ref<")) {
      return MemoryObject.PTR_SIZE;
    } else if (type.startsWith("x10aux::ref<")) {
      return MemoryObject.PTR_SIZE;
    } else {
      return -1;
    }
  }

  private Location getPDTLocation(DebuggeeProcess process, final DebuggeeThread thread, 
                                  final BitList tasks, final IPDILocation pdiLocation) throws PDIException {
    if (pdiLocation instanceof IPDILineLocation) {
      final ViewFile curViewFile = thread.getLocation(thread.getViewInformation()).getViewFile();
      final int lineNumber = ((IPDILineLocation) pdiLocation).getLineNumber();
      return this.fTranslator.getCppLocation(process, tasks, curViewFile.getBaseFileName(), lineNumber);
    } else if (pdiLocation instanceof IPDILocator) {
      final IPDILocator locator = (IPDILocator) pdiLocation;
      return this.fTranslator.getCppLocation(process, tasks, locator.getFile(), locator.getLineNumber());
    } else if ((pdiLocation instanceof IPDIFunctionLocation) || (pdiLocation instanceof IPDIFileLocation)) {
      raiseDialogBoxNotImplemented("Get PDT Location for Function of File not implemented");
    }
    throw new PDIException(tasks, "No other cases should occur");
  }
  
  private IProject getProjectResource(final String projectName) {
    final IProject projects[] = ResourcesPlugin.getWorkspace().getRoot().getProjects();
    for (final IProject project : projects) {
      if (project.getName().equals(projectName)) {
        return project;
      }
    }
    return null;
  }
  
  private String getValue(final EPDTVarExprType type, final String evaluatedExpression) {
    switch (type) {
      case CHAR:
        return toHexString(Integer.parseInt(evaluatedExpression), 4);
      case INT:
        return toHexString(Integer.parseInt(evaluatedExpression), 8);
      case ADDRESS:
        return evaluatedExpression;
      default:
        return evaluatedExpression;
    }
  }
  
  private String[] getVariablesAsStringArray(final IStackFrame stackFrame) throws DebugException {
    final IVariable[] variables = stackFrame.getVariables();
    final String[] strVars = new String[variables.length];
    int i = -1;
    for (final IVariable var : variables) {
      strVars[++i] = var.getName();
    }
    return strVars;
  }
  
  private String getVariableType(final EPDTVarExprType type, String[] desc) {
    switch (type) {
      case VOID:
        return "v0"; //$NON-NLS-1$
      case BOOL:
        return "b"; //$NON-NLS-1$
      case CHAR: 
        return "c"; //$NON-NLS-1$
      case INT:
        return "is4"; //$NON-NLS-1$
      case LONG:
        return "is8"; //$NON-NLS-1$
      case FLOAT:
        return "f4"; //$NON-NLS-1$
      case DOUBLE:
        return "f8"; //$NON-NLS-1$
      case ADDRESS:
        return "a8"; //$NON-NLS-1$
      case STRUCT: { //"{ID|FLD1=TYPE1,FLD2=TYPE2;;;}"
        if (desc == null) // a field of a struct, or an unknown struct
          return "^a8v0";
        if (!desc[1].equals("NOPTR"))
          return "^a8v0";
        StringBuilder sb = new StringBuilder();
        sb.append("{").append(desc[0]).append("|"); //$NON-NLS-1$//$NON-NLS-2$
        for (int i = 2; i < desc.length; i++) {
          sb.append(desc[i++]).append("="); //$NON-NLS-1$
          String t = desc[i];
          sb.append(getVariableType(getExprType(t), null)).append(","); //$NON-NLS-1$
        }
        sb.append(";;;}"); //$NON-NLS-1$
        return sb.toString();
      }
      case ARRAY: {
        if (desc == null) // a field of a struct, or an unknown rail
          return "^a8v0";
        StringBuilder sb = new StringBuilder();
        sb.append("[r").append(desc[2]).append("is4]"); //$NON-NLS-1$//$NON-NLS-2$
        String t = desc[1];
        sb.append(getVariableType(getExprType(t), fTranslator.getStructDescriptor(t)));
        return sb.toString();
      }
      case STRING:
        return "s"; //$NON-NLS-1$
      default:
        return "*"; //$NON-NLS-1$ // For unknown type
    }
  }
  
  private void initBreakPointIdIfRequired(final IPDIBreakpoint breakpoint) {
    if (breakpoint.getBreakpointID() == -1) {
      ++this.fBrkPointIdCounter;
      breakpoint.setBreakpointID(this.fBrkPointIdCounter);
    }
  }
    
  private boolean hasConnected(final IProgressMonitor monitor) {
    this.fWaitLock.lock();
    try {
      if (this.fState == ESessionState.CONNECTED) {
        while (this.fState != ESessionState.RUNNING && ! monitor.isCanceled()) {
          this.fWaiting = true;
          try {
            this.fRunningCondition.await(1000, TimeUnit.MILLISECONDS);
          } catch (InterruptedException except) {
            // Expect to be interrupted if monitor is canceled
            monitor.setCanceled(true);
          }
        }
        if (monitor.isCanceled()) {
          return false;
        }
      }
      return this.fState == ESessionState.RUNNING;
    } finally {
      this.fWaitLock.unlock();
      monitor.done();
    }
  }
  
  private void process(final Socket socket) {
    this.fWaitLock.lock();
    try {
      while (this.fLaunch == null) {
        this.fLaunchCondition.await();
      }
    } catch (InterruptedException except) {
      DebugCore.log(IStatus.WARNING, "Launch Thread Condition Interrupted.");
      return;
    } finally {
      this.fWaitLock.unlock();
    }
    try {
      final int version = new DataInputStream(socket.getInputStream()).readInt();
      final String[] input = CoreDaemon.readOldStyleStrings(socket.getInputStream(), version);
      
      final PICLLoadInfo loadInfo = createLoadInfo(this.fLaunch.getLaunchConfiguration());
      final DaemonConnectionInfo connectionInfo = new DaemonConnectionInfo(input[0], input[1]);
      connectionInfo.setConnection(new DaemonSocketConnection(socket));

      System.err.println("Starting debugger");

      this.fPDTTarget = new PICLDebugTarget(this.fLaunch, loadInfo, connectionInfo);
      this.fPDTTarget.fDebugEngineListener = this;
      IPreferenceStore store = PICLDebugPlugin.getInstance().getPreferenceStore();
      store.putValue(IPICLDebugConstants.PREF_SOCKETTIMEOUT, "false");
      this.fPDTTarget.engineIsWaiting(connectionInfo, true /* socketReuse */);
//      this.fPDTTarget.getEngineSession().setConnectionTimeout(0);
      
      this.fWaitLock.lock();
      try {
        this.fState = ESessionState.RUNNING;
        if (this.fWaiting) {
          this.fRunningCondition.signal();
          this.fWaiting = false;
        }
      } finally {
        this.fWaitLock.unlock();
      }
    } catch (IOException except) {
      DebugCore.log(IStatus.ERROR, "Unable to access socket input stream", except);
    } catch (CoreException except) {
      DebugCore.log(except.getStatus());
    }
  }
  
  private void raiseDialogBoxNotImplemented(final String message) {
    final Display display = PlatformUI.getWorkbench().getDisplay();
    display.syncExec(new Runnable() {
        public void run() {
          MessageDialog.openConfirm(display.getActiveShell(), "Not Implemented Error", message);
        }
    });
  }
  
  private ProxyDebugStackFrame toProxyStackFrame(final DebuggeeProcess process, final IStackFrame stackFrame, final int level) throws PDIException {
    final StackFrame frame = (StackFrame) stackFrame;
    final Location location = frame.getLocation(frame.getViewInformation());
    if (location == null) {
      throw new PDIException(null, "Unable to access location info for stack frame");
    }
    String cppFile = location.getViewFile().getBaseFileName();
	int cppLine = location.getLineNumber();
	String cppFunction = location.getFunctionsAtThisLocation()[0].getName();
	String file = fTranslator.getX10File(process, location);
	int lineNumber = fTranslator.getX10Line(process, location);
	String function = fTranslator.getX10Function(process, cppFunction, location);
	if (file == null || lineNumber == -1 || function == null) {
		file = cppFile;
		lineNumber = cppLine;
		function = cppFunction;
	}
	return ProxyDebugEventFactory.toFrame(String.valueOf(level), file, 
                                          function, 
                                          String.valueOf(lineNumber), (String) null /* address */);
  }
  
  // --- Private classes
  
  private class ListenerRunnable implements Runnable {

    // --- Interface methods implementation
    
    public void run() {
      try {
        final Socket socket = X10PDIDebugger.this.fServerSocket.accept();
        if (socket != null) {
          process(socket);
        }
      } catch (IOException except) {
        DebugCore.log(IStatus.ERROR, DebugMessages.PDID_SocketListeningError, except);
      }
    }
    
  }
  
  final class ProxyNotifier extends Observable {

    // --- Internal services
    
    void notify(final IProxyDebugEvent event) {
      setChanged();
      notifyObservers(event);
    }
  }
  
  private enum ESessionState {
    CONNECTED, RUNNING, DISCONNECTED
  }
  
  private enum EPDTVarExprType {
    VOID,
    CHAR,
    BOOL,
    INT,
    LONG,
    FLOAT,
    DOUBLE,
    ADDRESS,
    STRING,
    STRUCT,
    ARRAY,
    UNKNOWN
  }
  
  // --- Fields
  
  private ESessionState fState = ESessionState.DISCONNECTED;
  
  private ServerSocket fServerSocket;
  
  private IPLaunch fLaunch;
  
  private boolean fWaiting;
  
  private PICLDebugTarget fPDTTarget;
  
  private IPDISession fPDISession;
  
  private DebuggeeProcess[] fTaskToProcess;
  
  private int fProcessCounter;
  
  private BitList fCurTasks;
  
  private PDTProcessEventListener fProcessListener;
  
  private int fBrkPointIdCounter;
  
  private final int fPort;
  
  private final X10DebuggerTranslator fTranslator;
  
  private final ReentrantLock fWaitLock = new ReentrantLock();
  
  private final Condition fRunningCondition = this.fWaitLock.newCondition();
  
  private final Condition fLaunchCondition = this.fWaitLock.newCondition();
  
  private final ProxyNotifier fProxyNotifier = new ProxyNotifier();
  
  private static final byte[] HEX_CHAR_TABLE = {
    (byte)'0', (byte)'1', (byte)'2', (byte)'3',
    (byte)'4', (byte)'5', (byte)'6', (byte)'7',
    (byte)'8', (byte)'9', (byte)'a', (byte)'b',
    (byte)'c', (byte)'d', (byte)'e', (byte)'f'
  };    
  
  private static final String EMPTY_STRING = ""; //$NON-NLS-1$
    
}
