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
import org.eclipse.ptp.core.elements.IPProcess;
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
          IAttribute<?, ?, ?> stdout = e.getAttributes().getAttribute("stdout");
          if (stdout != null && stdout.getValue() != null)
            System.out.println(stdout.getValue());
          IAttribute<?, ?, ?> stderr = e.getAttributes().getAttribute("stderr");
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
    try {
      String name = breakpoint.getLocator().getFunction();
      //TODO Add translator here.
      DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
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
    }
  }

  public void setLineBreakpoint(final BitList tasks, final IPDILineBreakpoint breakpoint) throws PDIException {
    if (this.fProcessListener != null)
      this.fProcessListener.setCurTasks(tasks);

    initBreakPointIdIfRequired(breakpoint);
    final Location location = this.fTranslator.getCppLocation(tasks, breakpoint.getLocator().getFile(),
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
    try {
      this.fPDTTarget.resume();
    } catch (DebugException except) {
      throw new PDIException(tasks, except.getMessage());
    } finally {
      this.fProxyNotifier.notify(new ProxyDebugOKEvent(-1, ProxyDebugClient.encodeBitSet(tasks)));
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
      final DebuggeeThread thread = processes[0].getStoppingThread();
      final ThreadStoppedEventListener stoppedEventListener = new ThreadStoppedEventListener(thread);
      if (count <= 0) {
        throw new PDIException(tasks, "Step Into with count <= 0 is not supported.");
      }
      for (int i = 0; i < count; i++) {
        thread.addEventListener(stoppedEventListener);
        thread.stepInto();
        final Thread runnableThread = new Thread(new WaitingForStateRunnable(stoppedEventListener));
        runnableThread.start();
        try {
          runnableThread.join();
        } catch (InterruptedException except) {
          throw new PDIException(tasks, "Step Into interrupted");
        } finally {
          thread.removeEventListener(stoppedEventListener);
        }
      }
      final IStackFrame stackFrame = thread.getTopStackFrame();
      
      this.fProxyNotifier.notify(new ProxyDebugStepEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                         toProxyStackFrame(stackFrame, 0), thread.getId(), getDepth(thread), 
                                                         getVariablesAsStringArray(stackFrame)));
    } catch (DebugException except) {
      throw new PDIException(tasks, "Unable to step into: " + except.getMessage());
    }
  }

  public void stepIntoInstruction(final BitList tasks, final int count) throws PDIException {
    stepInto(tasks, count);
  }

  public void stepOver(final BitList tasks, final int count) throws PDIException {
    try {
      final DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
      final DebuggeeThread thread = processes[0].getStoppingThread();
      final ThreadStoppedEventListener stoppedEventListener = new ThreadStoppedEventListener(thread);
      if (count <= 0) {
        throw new PDIException(tasks, "Step Over with count <= 0 is not supported.");
      }
      for (int i = 0; i < count; i++) {
        thread.addEventListener(stoppedEventListener);
        thread.stepOver();
        final Thread runnableThread = new Thread(new WaitingForStateRunnable(stoppedEventListener));
        runnableThread.start();
        try {
          runnableThread.join();
        } catch (InterruptedException except) {
          throw new PDIException(tasks, "Step Over interrupted");
        } finally {
          thread.removeEventListener(stoppedEventListener);
        }        
      }
      final IStackFrame stackFrame = thread.getTopStackFrame();
      
      this.fProxyNotifier.notify(new ProxyDebugStepEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                         toProxyStackFrame(stackFrame, 0), thread.getId(), getDepth(thread), 
                                                         getVariablesAsStringArray(stackFrame)));
    } catch (DebugException except) {
      throw new PDIException(tasks, "Unable to step over: " + except.getMessage());
    }
  }

  public void stepOverInstruction(final BitList tasks, final int count) throws PDIException {
    stepOver(tasks, count);
  }

  public void stepReturn(final BitList tasks, final int count) throws PDIException {
    try {
      final DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
      final DebuggeeThread thread = processes[0].getStoppingThread();
      final ThreadStoppedEventListener stoppedEventListener = new ThreadStoppedEventListener(thread);
      // By default for one step return (count == 0) !!
      for (int i = 0; i < count + 1; i++) {
        thread.addEventListener(stoppedEventListener);
        thread.stepReturn();
        final Thread runnableThread = new Thread(new WaitingForStateRunnable(stoppedEventListener));
        runnableThread.start();
        try {
          runnableThread.join();
        } catch (InterruptedException except) {
          throw new PDIException(tasks, "Step Return interrupted");
        } finally {
          thread.removeEventListener(stoppedEventListener);
        }
      }
      final IStackFrame stackFrame = thread.getTopStackFrame();
      
      this.fProxyNotifier.notify(new ProxyDebugStepEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                         toProxyStackFrame(stackFrame, 0), thread.getId(), getDepth(thread), 
                                                         getVariablesAsStringArray(stackFrame)));
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
      final DebuggeeThread thread = processes[0].getStoppingThread();
      final ThreadStoppedEventListener stoppedEventListener = new ThreadStoppedEventListener(thread);
      thread.addEventListener(stoppedEventListener);
      final Location pdtLocation = getPDTLocation(thread, tasks, location);
      if (pdtLocation == null) {
        // We simply forget, and log the reason.
        DebugCore.log(IStatus.ERROR, NLS.bind("Unable to find location for {0}", location));
      } else {
        thread.runToLocation(pdtLocation);
        final Thread runnableThread = new Thread(new WaitingForStateRunnable(stoppedEventListener));
        runnableThread.start();
        try {
          runnableThread.join();
        } catch (InterruptedException except) {
          throw new PDIException(tasks, "Step over interrupted");
        } finally {
          thread.removeEventListener(stoppedEventListener);
        }
        final IStackFrame stackFrame = thread.getTopStackFrame();
      
        this.fProxyNotifier.notify(new ProxyDebugStepEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                           toProxyStackFrame(stackFrame, 0), thread.getId(), getDepth(thread), 
                                                           getVariablesAsStringArray(stackFrame)));
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
        processes[0].halt();

        DebuggeeThread firstAvailableThread = null;
        for (final DebuggeeThread thread : processes[0].getThreads()) {
          if (thread != null) {
            firstAvailableThread = thread;
            break;
          }
        }
        assert firstAvailableThread != null;
        final IStackFrame stackFrame = firstAvailableThread.getTopStackFrame();
        this.fProxyNotifier.notify(new ProxyDebugSuspendEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                              toProxyStackFrame(stackFrame, 0),
                                                              firstAvailableThread.getId(), 
                                                              firstAvailableThread.getStackFrames().length,
                                                              getVariablesAsStringArray(stackFrame)));
      } catch (DebugException except) {
        throw new PDIException(tasks, NLS.bind("Suspend action error: ", except.getMessage()));
      } catch (EngineRequestException except) {
        throw new PDIException(tasks, NLS.bind("Suspend request refused by PDT: ", except.getMessage()));
      }
    }
  }

  public void terminate(final BitList tasks) throws PDIException {
    if (! this.fPDTTarget.isTerminated()) {
      try {
        this.fPDTTarget.terminate();
        
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
      final DebuggeeThread thread = processes[0].getStoppingThread();
      final IStackFrame[] stackFrames = thread.getStackFrames();
      final Collection<String> args = new ArrayList<String>();
      for (int i = low; i <= high; ++i) {
        final int nbParams = ((StackFrame) stackFrames[i]).getNumOfParms();
        final IVariable[] variables = stackFrames[i].getVariables();
        for (int j = 0; j < nbParams; ++j) {
          args.add(variables[j].getName());
        }
      }
      this.fProxyNotifier.notify(new ProxyDebugArgsEvent(1 /* transId */, ProxyDebugClient.encodeBitSet(tasks), 
                                                         args.toArray(new String[args.size()])));
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

  public void listLocalVariables(final BitList tasks) throws PDIException {
    try {
      DebuggeeProcess[] process = getDebuggeeProcesses(tasks);
      final DebuggeeThread thread = process[0].getStoppingThread();
      final IStackFrame stackFrame = thread.getTopStackFrame();
      this.fProxyNotifier.notify(new ProxyDebugVarsEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                         getVariablesAsStringArray(stackFrame)));
    } catch (DebugException except) {
      throw new PDIException(tasks, "Unable to access stack frames in PDT: " + except.getMessage());
    }
  }

  public void retrieveAIF(final BitList tasks, final String expr) throws PDIException {
    try {
      DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
      DebuggeeProcess process = processes[0];
      final DebuggeeThread thread = process.getStoppingThread();
      final StackFrame stackFrame = (StackFrame) thread.getTopStackFrame();
      final Location location = stackFrame.getCurrentLocation(thread.getViewInformation());
      final ProxyDebugAIF aif = createDebugProxyAIF(process, thread, location, expr);
      this.fProxyNotifier.notify(new ProxyDebugDataEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks), aif));
    } catch (EngineRequestException except) {
      throw new PDIException(tasks, NLS.bind("Unable to evaluate ''{0}'' via PDT request: " + except.getMessage(), expr));
    } catch (DebugException except) {
      throw new PDIException(tasks, "Unable to access stack frames in PDT: " + except.getMessage());
    }
  }

  public void retrievePartialAIF(final BitList tasks, final String expr, final String key, final boolean listChildren, 
                                 final boolean isExpression) throws PDIException
  {
    try {
      DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
      DebuggeeProcess process = processes[0];
      final DebuggeeThread thread = process.getStoppingThread();
      final StackFrame stackFrame = (StackFrame) thread.getTopStackFrame();
      final Location location = stackFrame.getCurrentLocation(thread.getViewInformation());
      if (listChildren) { // Expanding an existing expression
        System.out.println(expr);
        raiseDialogBoxNotImplemented("Retrieve Partial AIF with request to list children not yet implemented");
        final ProxyDebugAIF aif = new ProxyDebugAIF("?0?", "", expr);
        this.fProxyNotifier.notify(new ProxyDebugPartialAIFEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                                 aif, expr));
      } else {
        final ProxyDebugAIF aif = createDebugProxyAIF(process, thread, location, expr);
        this.fProxyNotifier.notify(new ProxyDebugPartialAIFEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                                 aif, expr));
      }
    } catch (EngineRequestException except) {
        throw new PDIException(tasks, NLS.bind("Unable to evaluate ''{0}'' via PDT request: " + except.getMessage(), expr));
      } catch (DebugException except) {
        throw new PDIException(tasks, "Unable to access stack frames in PDT: " + except.getMessage());
      }
  }

  private ProxyDebugAIF createDebugProxyAIF(DebuggeeProcess process, final DebuggeeThread thread,
      final Location location, final String expr) throws EngineRequestException, DebugException
  {
    ExprNodeBase rootNode = evaluateExpression(expr, process, thread, location);
    final String type = rootNode.getReferenceTypeName();
    final String[] desc = fTranslator.getStructDescriptor(type);
    final EPDTVarExprType exprType = getExprType(rootNode);
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
      int length = 0;
      try {
        Address railAddress = process.convertToAddress(result, location, thread);
        final int vptrsz = railAddress.getAddressSize();
        assert (vptrsz == PTR_SIZE);
        // Rail layout:
        // ptr -> .------------.  0
        //        | vtable     |
        //        |  (8 bytes) |
        //        |------------|  8
        //        || Object    |
        //        || vtable    |
        //        || (8 bytes) |
        //        ||-----------| 16
        //        ||???        |
        //        || (8 bytes) |
        //        |------------| 24
        //        || Ref       |
        //        || vtable    |
        //        || (8 bytes) |
        //        ||-----------| 32
        //        || ???       |
        //        || (8 bytes) |
        //        |------------| 40
        //        || Settable  |
        //        || vtable    |
        //        || (8 bytes) |
        //        ||-----------| 48
        //        || ???       |
        //        || (8 bytes) |
        //        |------------| 56
        //        || ???       |
        //        || vtable    |
        //        || (8 bytes) |
        //        ||-----------| 64
        //        || ???       |
        //        || (8 bytes) |
        //        |------------| 72
        //        || ???       |
        //        || vtable    |
        //        || (8 bytes) |
        //        ||-----------| 80
        //        || ???       |
        //        || (8 bytes) |
        //        |------------| 88
        //        | length     |
        //        |  (8 bytes) |
        //        |------------| 96
        //        | contents   |  :
        //        |     ...    |  '
		Memory memory = process.getMemory(railAddress.getAddress(), PTR_SIZE*12);
		MemoryByte[] mbytes = memory.getMemory();
		length = extractInt(mbytes, PTR_SIZE*11);
      } catch (MemoryException e) {
        e.printStackTrace();
      }
//      ExprNodeBase lengthNode = evaluateExpression(expr+"._val->x10__length", process, thread, location);
//      String lengthType = lengthNode.getReferenceTypeName();
//      assert (lengthType.equals("int"));
//      desc[2] = Integer.toString(Integer.parseInt(lengthNode.getValueString())-1));
      desc[2] = Integer.toString(length - 1); // we want an inclusive upper bound
    }
    if (exprType == EPDTVarExprType.STRING) {
//      GlobalVariable[] globals = process.getDebugEngine().getGlobalVariables();
//      for (GlobalVariable v : globals) {
//        if (v.getName().equals("x10aux::nullString"))
//          evaluateExpression(v.getExpression().replace("\nx10aux", "\n&x10aux"), process, thread, location);
//      }
      int length = 0;
      byte[] bytes = null;
      String content = null;
      try {
        Address stringAddress = process.convertToAddress(result, location, thread);
        final int vptrsz = stringAddress.getAddressSize();
        assert (vptrsz == PTR_SIZE);
        // String layout:
        // ptr -> .------------.  0
        //        | vtable     |
        //        |  (8 bytes) |
        //        |------------|  8
        //        || Object    |
        //        || vtable    |
        //        || (8 bytes) |
        //        ||-----------| 16
        //        || ???       |
        //        || (8 bytes) |
        //        |------------| 24
        //        | content    |
        //        |  (8 bytes) |
        //        |------------| 32
        //        | length     |
        //        |  (8 bytes) |
        //        '------------'
		Memory memory = process.getMemory(stringAddress.getAddress(), PTR_SIZE*5);
		MemoryByte[] mbytes = memory.getMemory();
        long ptr = extractPointer(mbytes, PTR_SIZE*3);
        length = (int) extractLong(mbytes, PTR_SIZE*4);
        Address contentAddress = process.convertToAddress("0x"+toHexString(ptr, 16), location, thread);
		memory = process.getMemory(contentAddress.getAddress(), length);
        MemoryByte[] cbytes = memory.getMemory();
        bytes = extractBytes(cbytes);
        content = "\"" + new String(bytes, "ISO-8859-1") + "\"";
      } catch (UnsupportedEncodingException e) {
    	  e.printStackTrace();
      } catch (MemoryException e) {
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
      dumpBytes(cbytes);
    } catch (MemoryException e) { }
  }

  private static void dumpBytes(MemoryByte[] cbytes) {
	for (int i = 0; i < cbytes.length; i++) {
      String s = toHexString(cbytes[i].getValue()&0xFF, 2);
      System.out.print(s+" ");
    }
    System.out.println();
  }

  public static final int INT_SIZE = 4;
  public static final int LONG_SIZE = 8;
  public static final int PTR_SIZE = LONG_SIZE;
  
  private byte[] extractBytes(MemoryByte[] mbytes) {
	  byte[] bytes = new byte[mbytes.length];
	  for (int i = 0; i < mbytes.length; i++) {
		  bytes[i] = mbytes[i].getValue();
	  }
	  return bytes;
  }

  private int extractInt(final MemoryByte[] mbytes, final int offset) {
    return ((mbytes[offset+0].getValue()&0xFF)<<24) | ((mbytes[offset+1].getValue()&0xFF)<<16) |
           ((mbytes[offset+2].getValue()&0xFF)<< 8) | ((mbytes[offset+3].getValue()&0xFF)<< 0);
  }

  private long extractLong(final MemoryByte[] mbytes, final int offset) {
    return ((mbytes[offset+0].getValue()&0xFFl)<<56) | ((mbytes[offset+1].getValue()&0xFFl)<<48) |
           ((mbytes[offset+2].getValue()&0xFFl)<<40) | ((mbytes[offset+3].getValue()&0xFFl)<<32) |
           ((mbytes[offset+4].getValue()&0xFFl)<<24) | ((mbytes[offset+5].getValue()&0xFFl)<<16) |
           ((mbytes[offset+6].getValue()&0xFFl)<< 8) | ((mbytes[offset+7].getValue()&0xFFl)<< 0);
  }
  
  private long extractPointer(final MemoryByte[] mbytes, final int offset) {
    if (PTR_SIZE == LONG_SIZE)
      return extractLong(mbytes, offset);
    else
      return extractInt(mbytes, offset);
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
      DebuggeeProcess process = processes[0];
      final DebuggeeThread thread = process.getStoppingThread();
      final StackFrame stackFrame = (StackFrame) thread.getTopStackFrame();
      final Location location = stackFrame.getCurrentLocation(thread.getViewInformation());
      final ProxyDebugAIF aif = createDebugProxyAIF(process, thread, location, variable);
      this.fProxyNotifier.notify(new ProxyDebugTypeEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                         aif.getFDS()));
    } catch (EngineRequestException except) {
      throw new PDIException(tasks, NLS.bind("Unable to evaluate ''{0}'' via PDT request: " + except.getMessage(), variable));
    } catch (DebugException except) {
      throw new PDIException(tasks, "Unable to access stack frames in PDT: " + except.getMessage());
    }
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
      final DebuggeeThread thread = processes[0].getStoppingThread();
      final IStackFrame[] stackFrames = thread.getStackFrames();
      final ProxyDebugStackFrame[] proxyStackFrames = new ProxyDebugStackFrame[stackFrames.length];
      for (int i = low; i < depth; ++i) {
        proxyStackFrames[i] = toProxyStackFrame(stackFrames[i], i);
      }
      this.fProxyNotifier.notify(new ProxyDebugStackframeEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                               proxyStackFrames));
    } catch (DebugException except) {
      throw new PDIException(tasks, "Unable to access stack frames in PDT: " + except.getMessage());
    }
  }

  public void setCurrentStackFrame(final BitList tasks, final int level) throws PDIException {
    try {
      DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
      final DebuggeeThread thread = processes[0].getStoppingThread();
      final IStackFrame[] stackFrames = thread.getStackFrames();
      if (level < stackFrames.length) {
        this.fProxyNotifier.notify(new ProxyDebugVarsEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                           getVariablesAsStringArray(stackFrames[level])));
      } else {
        DebugCore.log(IStatus.ERROR, "Stack frame level selected is out of bounds");
      }
    } catch (DebugException except) {
      throw new PDIException(tasks, "Unable to access stack frames in PDT: " + except.getMessage());
    }
  }
  
  // --- IPDIThreadManagement's interface methods implementation

  public void listInfoThreads(final BitList tasks) throws PDIException {
    final DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
    final DebuggeeThread[] threads = processes[0].getThreads();
    final Collection<String> threadIds = new ArrayList<String>(threads.length);
    for (final DebuggeeThread thread : threads) {
      if ((thread != null) && ! thread.isTerminated()) {
        threadIds.add(String.valueOf(thread.getId()));
      }
    }
    this.fProxyNotifier.notify(new ProxyDebugInfoThreadsEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                              threadIds.toArray(new String[threadIds.size()])));
  }

  public void retrieveStackInfoDepth(final BitList tasks) throws PDIException {
    final DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
    try {
      final int depth = processes[0].getStoppingThread().getStackFrames().length;
      this.fProxyNotifier.notify(new ProxyDebugStackInfoDepthEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks), 
                                                                   depth));
    } catch (DebugException except) {
      throw new PDIException(tasks, "Unable to access stack frames in PDT: " + except.getMessage());
    }
  }

  public void selectThread(final BitList tasks, final int tid) throws PDIException {
    DebuggeeProcess[] processes = getDebuggeeProcesses(tasks);
    final DebuggeeThread thread = processes[0].getThread(tid);
    try {
      final IStackFrame stackFrame = thread.getTopStackFrame();
      if (stackFrame == null) {
        this.fProxyNotifier.notify(new ProxyDebugStackframeEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                                 new ProxyDebugStackFrame[0]));
      } else {
        final ProxyDebugStackFrame proxyStackFrame = toProxyStackFrame(stackFrame, 0);
        
        this.fProxyNotifier.notify(new ProxyDebugSetThreadSelectEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                                      tid, proxyStackFrame));
      }
    } catch (DebugException except) {
      throw new PDIException(tasks, "Unable to access stack frames in PDT: " + except.getMessage());
    }
  }
  
  // --- IPDIMemoryBlockManagement's interface methods implementation

  public void createDataReadMemory(final BitList tasks, final long offset, final String address, final int wordFormat, 
                                   final int wordSize, final int rows, final int cols, 
                                   final Character asChar) throws PDIException {
    this.fProxyNotifier.notify(new ProxyDebugOKEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks)));
  }

  public void createDataWriteMemory(final BitList tasks, final long offset, final String address, final int wordFormat, 
                                    final int wordSize, final String value) throws PDIException {
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
    this.fProcessListener = new PDTProcessEventListener(event.getProcess(), this.fProxyNotifier);
    event.getProcess().addEventListener(this.fProcessListener);
    this.fTranslator.init(event.getProcess(), fProject);
    this.fTaskToProcess[this.fProcessCounter++] = event.getProcess();
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
    } else if (type.endsWith("*")) {
      return EPDTVarExprType.ADDRESS;
    } else if (type.startsWith("class ref<x10__lang__Rail<")) {
      return EPDTVarExprType.ARRAY;
    } else if (type.startsWith("x10aux__ref<x10__lang__Rail<")) {
      return EPDTVarExprType.ARRAY;
    } else if (type.startsWith("class x10aux::ref<x10::lang::Rail<")) {
      return EPDTVarExprType.ARRAY;
    } else if (type.startsWith("class ref<x10__lang__ValRail<")) {
      return EPDTVarExprType.ARRAY;
    } else if (type.startsWith("x10aux__ref<x10__lang__ValRail<")) {
      return EPDTVarExprType.ARRAY;
    } else if (type.startsWith("class x10aux::ref<x10::lang::ValRail<")) {
      return EPDTVarExprType.ARRAY;
    } else if (type.equals("class ref<x10__lang__String>")) {
      return EPDTVarExprType.STRING;
    } else if (type.equals("x10aux__ref<x10__lang__String>")) {
      return EPDTVarExprType.STRING;
    } else if (type.equals("class x10aux::ref<x10::lang::String>")) {
      return EPDTVarExprType.STRING;
    } else if (type.startsWith("class ref<")) {
      return EPDTVarExprType.STRUCT;
    } else if (type.startsWith("x10aux__ref<")) {
      return EPDTVarExprType.STRUCT;
    } else {
      return EPDTVarExprType.UNKNOWN;
    }
  }
  private Location getPDTLocation(final DebuggeeThread thread, final BitList tasks, 
                                  final IPDILocation pdiLocation) throws PDIException {
    if (pdiLocation instanceof IPDILineLocation) {
      final ViewFile curViewFile = thread.getLocation(thread.getViewInformation()).getViewFile();
      final int lineNumber = ((IPDILineLocation) pdiLocation).getLineNumber();
      return this.fTranslator.getCppLocation(tasks, curViewFile.getBaseFileName(), lineNumber);
    } else if (pdiLocation instanceof IPDILocator) {
      final IPDILocator locator = (IPDILocator) pdiLocation;
      return this.fTranslator.getCppLocation(tasks, locator.getFile(), locator.getLineNumber());
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
        StringBuilder sb = new StringBuilder();
        sb.append("{").append(desc[0]).append("|"); //$NON-NLS-1$//$NON-NLS-2$
        for (int i = 2; i < desc.length; i++) {
          sb.append(desc[i++]).append("="); //$NON-NLS-1$
          String t = desc[i];
          sb.append(getVariableType(getExprType(t), fTranslator.getStructDescriptor(t))).append(","); //$NON-NLS-1$
        }
        sb.append(";;;}"); //$NON-NLS-1$
        return sb.toString();
      }
      case ARRAY: {
        StringBuilder sb = new StringBuilder();
        sb.append("[r0..").append(desc[2]).append("i4]"); //$NON-NLS-1$//$NON-NLS-2$
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
      
      this.fTranslator.setPDTTarget(this.fPDTTarget);

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
  
  private ProxyDebugStackFrame toProxyStackFrame(final IStackFrame stackFrame, final int level) throws PDIException {
    final StackFrame frame = (StackFrame) stackFrame;
    final Location location = frame.getLocation(frame.getViewInformation());
    if (location == null) {
      throw new PDIException(null, "Unable to access location info for stack frame");
    }
    String cppFile = location.getViewFile().getBaseFileName();
	int cppLine = location.getLineNumber();
	String file = fTranslator.getX10File(location);
	String function = location.getViewFile().getFunctions()[0].getName();
	int lineNumber = fTranslator.getX10Line(location);
	if (file == null || lineNumber == -1) {
		file = cppFile;
		lineNumber = cppLine;
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
