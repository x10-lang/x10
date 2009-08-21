/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug.pdi;

import static org.eclipse.imp.x10dt.ui.cpp.debug.pdi.X10DebuggerTranslator.SAVED_THIS;
import static org.eclipse.imp.x10dt.ui.cpp.debug.pdi.X10DebuggerTranslator.VARIABLE_NOT_FOUND;
import static org.eclipse.imp.x10dt.ui.cpp.debug.pdi.X10DebuggerTranslator.NOT_IN_SCOPE;
import static org.eclipse.imp.x10dt.ui.cpp.debug.pdi.X10DebuggerTranslator.inClosure;
import static org.eclipse.imp.x10dt.ui.cpp.debug.utils.PDTUtils.findMatch;
import static org.eclipse.imp.x10dt.ui.cpp.debug.utils.X10Utils.FMGL;
import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_ARGUMENTS;
import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_EXECUTABLE_PATH;
import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_PROJECT_NAME;
import static org.eclipse.ptp.core.elements.attributes.JobAttributes.State;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.eclipse.imp.x10dt.ui.cpp.debug.IDebuggerTranslator;
import org.eclipse.imp.x10dt.ui.cpp.debug.IPDTTarget;
import org.eclipse.imp.x10dt.ui.cpp.debug.utils.PDTUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.IPTPLaunchConfigurationConstants;
import org.eclipse.ptp.core.attributes.AttributeManager;
import org.eclipse.ptp.core.attributes.EnumeratedAttribute;
import org.eclipse.ptp.core.attributes.IAttribute;
import org.eclipse.ptp.core.attributes.StringAttributeDefinition;
import org.eclipse.ptp.core.elementcontrols.IResourceManagerControl;
import org.eclipse.ptp.core.elements.IPProcess;
import org.eclipse.ptp.core.elements.attributes.ElementAttributeManager;
import org.eclipse.ptp.core.elements.attributes.JobAttributes;
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
import org.eclipse.ptp.debug.core.pdi.event.IPDIErrorInfo;
import org.eclipse.ptp.debug.core.pdi.event.IPDIEvent;
import org.eclipse.ptp.debug.core.pdi.event.IPDIResumedEvent;
import org.eclipse.ptp.debug.core.pdi.model.IPDIAddressBreakpoint;
import org.eclipse.ptp.debug.core.pdi.model.IPDIExceptionpoint;
import org.eclipse.ptp.debug.core.pdi.model.IPDIFunctionBreakpoint;
import org.eclipse.ptp.debug.core.pdi.model.IPDILineBreakpoint;
import org.eclipse.ptp.debug.core.pdi.model.IPDISignal;
import org.eclipse.ptp.debug.core.pdi.model.IPDIWatchpoint;
import org.eclipse.ptp.debug.core.pdi.model.aif.IAIF;
import org.eclipse.ptp.debug.sdm.core.proxy.ProxyDebugClient;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugArgsEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugDataEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugErrorEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugEventFactory;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugInfoThreadsEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugOKEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugPartialAIFEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugSetThreadSelectEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugStackInfoDepthEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugStackframeEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugStepEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugTypeEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugVarsEvent;
import org.eclipse.ptp.proxy.debug.client.ProxyDebugAIF;
import org.eclipse.ptp.proxy.debug.client.ProxyDebugStackFrame;
import org.eclipse.ptp.rmsystem.AbstractRuntimeResourceManager;
import org.eclipse.ptp.rtsystem.AbstractRuntimeSystem;
import org.eclipse.ptp.rtsystem.events.IRuntimeChangeEvent;
import org.eclipse.ptp.rtsystem.events.IRuntimeJobChangeEvent;
import org.eclipse.ptp.utils.core.RangeSet;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import polyglot.util.Pair;

import com.ibm.debug.daemon.CoreDaemon;
import com.ibm.debug.daemon.DaemonConnectionInfo;
import com.ibm.debug.daemon.DaemonSocketConnection;
import com.ibm.debug.internal.epdc.EStdExprNode;
import com.ibm.debug.internal.epdc.EStdTreeNode;
import com.ibm.debug.internal.epdc.IEPDCConstants;
import com.ibm.debug.internal.pdt.IPICLDebugConstants;
import com.ibm.debug.internal.pdt.PDTDebugElement;
import com.ibm.debug.internal.pdt.PICLDebugPlugin;
import com.ibm.debug.internal.pdt.PICLDebugTarget;
import com.ibm.debug.internal.pdt.model.Address;
import com.ibm.debug.internal.pdt.model.ArrayExprNode;
import com.ibm.debug.internal.pdt.model.Breakpoint;
import com.ibm.debug.internal.pdt.model.ClassExprNode;
import com.ibm.debug.internal.pdt.model.DebugEngine;
import com.ibm.debug.internal.pdt.model.DebuggeeProcess;
import com.ibm.debug.internal.pdt.model.DebuggeeThread;
import com.ibm.debug.internal.pdt.model.EngineRequestException;
import com.ibm.debug.internal.pdt.model.ExprNode;
import com.ibm.debug.internal.pdt.model.ExprNodeBase;
import com.ibm.debug.internal.pdt.model.ExpressionBase;
import com.ibm.debug.internal.pdt.model.Function;
import com.ibm.debug.internal.pdt.model.GlobalVariable;
import com.ibm.debug.internal.pdt.model.Location;
import com.ibm.debug.internal.pdt.model.Memory;
import com.ibm.debug.internal.pdt.model.MemoryException;
import com.ibm.debug.internal.pdt.model.PointerExprNode;
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
public final class X10PDIDebugger implements IPDIDebugger {
  
  public X10PDIDebugger(final int port, final int nbTasks, final IDebuggerTranslator translator, final IResourceManagerControl rmControl) {
    this.fPort = port;
    this.fNbTasks = nbTasks;
    this.fTranslator = translator;
    this.fRmControl = rmControl;
  }
  
  // --- IPDIDebugger's interface methods implementation
  
  public void commandRequest(final BitList tasks, final String command) throws PDIException {
    throw new IllegalStateException();
  }

  public void disconnect(final Observer observer) throws PDIException {
    this.fProxyNotifier.deleteObserver(observer);
  }

  public int getErrorAction(final int errorCode) {
    return errorCode;
  }

  public void initialize(final ILaunchConfiguration configuration, final List<String> args,
                         final IProgressMonitor monitor) throws PDIException {
    try {
      this.fServerSocket = new ServerSocket(this.fPort);
      this.fState = ESessionState.CONNECTED;
      try {
        final String projName = configuration.getAttribute(IPTPLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String) null);
        assert projName != null;
        this.fProject = PTPDebugCorePlugin.getWorkspace().getRoot().getProject(projName);
        this.fTranslator.init(this.fProject);
      } catch (CoreException except) {
        throw new PDIException(null, "Unable to access project resource defined in debug launch configuration within workspace");
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
    final IPProcess[] processes = this.fLaunch.getPJob().getProcesses();
    for (IPProcess process : processes) {
      process.addElementListener(new IProcessListener(){
        public void handleEvent(final IProcessChangeEvent event) {
          final StringAttributeDefinition STDOUT = ProcessAttributes.getStdoutAttributeDefinition(); // TODO: make a static field
          final StringAttributeDefinition STDERR = ProcessAttributes.getStderrAttributeDefinition(); // TODO: make a static field
          IAttribute<?, ?, ?> stdout = event.getAttributes().getAttribute(STDOUT);
          if (stdout != null && stdout.getValue() != null) {
            System.out.println(stdout.getValue());
          }
          final IAttribute<?, ?, ?> stderr = event.getAttributes().getAttribute(STDERR);
          if (stderr != null && stderr.getValue() != null) {
        	  System.out.println(stderr.getValue());
          }
        }
      });
    }
    fPDISession.getTaskManager().setRegisterTasks(true, fPDISession.getTasks());
    notifyOkEvent(this.fPDISession.getTasks());
  }

  public void stopDebugger() throws PDIException {
    this.fState = ESessionState.DISCONNECTED;
    
    if (! this.fTaskToTarget.isEmpty()) {
      this.fTaskToTarget.clear();
    }  

    final AbstractRuntimeResourceManager resManager = (AbstractRuntimeResourceManager) this.fRmControl;
    final AbstractRuntimeSystem runtimeSystem = (AbstractRuntimeSystem) resManager.getRuntimeSystem();
      
    final EnumeratedAttribute<State> state = JobAttributes.getStateAttributeDefinition().create(State.TERMINATED);
    final AttributeManager attrManager = new AttributeManager();
    attrManager.addAttribute(state);
    final ElementAttributeManager attrs = new ElementAttributeManager();
    attrs.setAttributeManager(new RangeSet(this.fLaunch.getPJob().getID()), attrManager);
      
    runtimeSystem.fireRuntimeJobChangeEvent(new IRuntimeJobChangeEvent() {
      public ElementAttributeManager getElementAttributeManager() {
        return attrs;
      }
    });
  }
  
  // --- IPDIBreakpointManagement's interface methods implementation

  public void deleteBreakpoint(final BitList tasks, final int bpid) throws PDIException {
    for (final Pair<BitList, DebuggeeProcess> pair : getAllProcesses(tasks)) {
      boolean found = false;
      final Breakpoint breakpoint = pair.snd().getBreakpoint(bpid);
      if (breakpoint != null) {
        pair.snd().removeBreakpoint(breakpoint);
        found = true;
      }
      if (! found) {
        DebugCore.log(IStatus.ERROR, NLS.bind("Could not find breakpoint with id ''{0}''", bpid));
      }
    }
    notifyOkEvent(tasks);
  }

  public void setAddressBreakpoint(final BitList tasks, final IPDIAddressBreakpoint breakpoint) throws PDIException {
    raiseDialogBoxNotImplemented("Set Address Breakpoint is not an available feature with X10 debugger");
  }

  public void setConditionBreakpoint(final BitList tasks, final int bpid, final String condition) throws PDIException {
    raiseDialogBoxNotImplemented("Set Condition Breakpoint is not yet implemented");
  }

  public void setEnabledBreakpoint(final BitList tasks, final int bpid, final boolean enabled) throws PDIException {
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
  }

  public void setExceptionpoint(final BitList tasks, final IPDIExceptionpoint breakpoint) throws PDIException {
    raiseDialogBoxNotImplemented("Set Exception Breakpoint is not yet implemented");
  }

  public void setFunctionBreakpoint(final BitList tasks, final IPDIFunctionBreakpoint breakpoint) throws PDIException {
    try {
      final String functionName = breakpoint.getLocator().getFunction();
      //TODO Add translator here.
      for (final Pair<BitList, DebuggeeProcess> pair : getAllProcesses(tasks)) {
        final Function[] fns = pair.snd().getFunctions(functionName, true /* caseSensitive */);
        Function res = null;
        for (Function f : fns) {
          if (f.getName().equals(functionName)) {
            if (res != null)
              throw new PDIException(pair.fst(), "Found multiple functions matching '" + functionName + "'");
            res = f;
          }
        }
        ((PICLDebugTarget) pair.snd().getDebugTarget()).createEntryBreakpoint(true, res, null, null, 0, 1, 1, 1, breakpoint);
      }
    } catch (PDIException except) {
      notifyErrorEvent(tasks, IPDIErrorInfo.DBG_FATAL, "Error during funtion breakpoint creation: " + except.getMessage());
    } catch (EngineRequestException except) {
      notifyErrorEvent(tasks, IPDIErrorInfo.DBG_FATAL, "Error during PDT funtion breakpoint creation: " + except.getMessage());
    }
  }

  public void setLineBreakpoint(final BitList tasks, final IPDILineBreakpoint breakpoint) throws PDIException {
    try {
      for (final Pair<BitList,IPDTTarget> pair : getAllPDTTargets(tasks)) {
        final Location location = this.fTranslator.getCppLocation(pair.snd().getProcess(), breakpoint.getLocator().getFile(),
                                                                  breakpoint.getLocator().getLineNumber());
        if (location == null) {
          notifyErrorEvent(tasks, IPDIErrorInfo.DBG_WARNING,
                           NLS.bind("Could not find PDT location for breakpoint {0}", breakpoint.getLocator()));
          break;
        }
        pair.snd().getTarget().createLineBreakpoint(true /* enabled */, location, null /* conditionalExpression */,
                                                    null /* brkAction */,  0 /* threadNumber */, 1 /* everyValue */,
                                                    1 /* fromValue */, 0 /* toValue */, breakpoint,
                                                    null /* stmtNumber */, null /* engineData */);
      }
    } catch (EngineRequestException except) {
      notifyErrorEvent(tasks, IPDIErrorInfo.DBG_FATAL, "Error during PDT line breakpoint creation: " + except.getMessage());
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
    try {
      for (final Pair<BitList,IPDTTarget> pair : getAllPDTTargets(tasks)) {
        notifyOkEvent(pair.fst());
        pair.snd().getTarget().resume();
      }
    } catch (DebugException except) {
      notifyErrorEvent(tasks, IPDIErrorInfo.DBG_FATAL, "Error during PDT resume event: " + except.getMessage());
    }
  }

  public void resume(final BitList tasks, final IPDILocation location) throws PDIException {
    raiseDialogBoxNotImplemented("Resume to location not implemented");
  }

  public void resume(final BitList tasks, final IPDISignal signal) throws PDIException {
    raiseDialogBoxNotImplemented("Resume to signal not implemented");
  }

  public void start(final BitList tasks) throws PDIException {
    resume(tasks, false);
  }

  public void stepInto(final BitList tasks, final int count) throws PDIException {
    try {
      fireResumeEvent(tasks, IPDIResumedEvent.STEP_INTO);
      
      for (final Pair<BitList, DebuggeeProcess> pair : getAllProcesses(tasks)) {
        final DebuggeeThread thread = pair.snd().getStoppingThread();
        if (count <= 0) {
          throw new PDIException(pair.fst(), "Step Into with count <= 0 is not supported.");
        }
        Location start = thread.getLocation(thread.getViewInformation());
        for (int i = 0; i < count; i++) {
          if (!stepInto(thread, pair.snd(), start))
            throw new PDIException(tasks, "Step Into interrupted");
        }
        IStackFrame[] stackFrames = thread.getStackFrames();
        ProxyDebugStackFrame[] proxyStackFrames = getProxyStackFrames(stackFrames, pair.snd());
        int topVisibleFrame = findSparseIndex(proxyStackFrames, 0);
        final IStackFrame stackFrame = stackFrames[topVisibleFrame];
        // TODO: consolidate same stack frames into one event
        this.fProxyNotifier.notify(new ProxyDebugStepEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(pair.fst()),
                                                           toProxyStackFrame(pair.snd(), stackFrame, 0),
                                                           thread.getId(), getDepth(thread),
                                                           getVariablesAsStringArray(stackFrame)));
      }
    } catch (DebugException except) {
      notifyErrorEvent(tasks, IPDIErrorInfo.DBG_FATAL, "Error during Step Into operation: " + except.getMessage());
    }
  }

  private static final HashSet<String> STEP_THROUGH_FUNCTIONS = new HashSet<String>();
  static {
    STEP_THROUGH_FUNCTIONS.add("x10rt_alloc");
    STEP_THROUGH_FUNCTIONS.add("malloc");
    STEP_THROUGH_FUNCTIONS.add("pthread_rwlock_destroy");
    STEP_THROUGH_FUNCTIONS.add("operator new(unsigned long,void*)");
  }

  /**
   * Keep executing thread.stepInto() until we're in an X10 function.
 * @param start TODO
   */
  private boolean stepInto(final DebuggeeThread thread, final DebuggeeProcess process, final Location start) throws DebugException {
    String function = null;
    boolean stepOut = false;
    do {
      final ThreadStoppedEventListener stoppedEventListener = new ThreadStoppedEventListener(thread);
      thread.addEventListener(stoppedEventListener);
      final Thread runnableThread = new Thread(new WaitingForStateRunnable(stoppedEventListener));
      try {
        if (stepOut)
          thread.stepReturn();
        else
          thread.stepInto();
        runnableThread.start();
        runnableThread.join();
      } catch (InterruptedException except) {
        return false;
      } finally {
        thread.removeEventListener(stoppedEventListener);
        runnableThread.interrupt();
      }
      if (!thread.isSuspended())
        return false;
      Location location = thread.getLocation(thread.getViewInformation());
      String cppFunction = getFunction(location);
      System.out.println("Step into: stepping through "+cppFunction);
      // TODO: skip system libraries more efficiently
      stepOut = STEP_THROUGH_FUNCTIONS.contains(cppFunction);
      function = fTranslator.getX10Function(process, cppFunction, location);
    } while (function == null);
    return true;
  }

  public void stepIntoInstruction(final BitList tasks, final int count) throws PDIException {
    // TODO: use to step into a C++ statement?
    raiseDialogBoxNotImplemented("Step into instruction not implemented");
  }

  public void stepOver(final BitList tasks, final int count) throws PDIException {
    try {
      fireResumeEvent(tasks, IPDIResumedEvent.STEP_OVER);
      
      for (final Pair<BitList, DebuggeeProcess> pair : getAllProcesses(tasks)) {
        final DebuggeeThread thread = pair.snd().getStoppingThread();
        if (count <= 0) {
          throw new PDIException(pair.fst(), "Step Over with count <= 0 is not supported.");
        }
        Location start = thread.getLocation(thread.getViewInformation());
        for (int i = 0; i < count; i++) {
          if (!stepOver(thread, pair.snd(), start))
            throw new PDIException(tasks, "Step Over interrupted");
        }
        IStackFrame[] stackFrames = thread.getStackFrames();
        ProxyDebugStackFrame[] proxyStackFrames = getProxyStackFrames(stackFrames, pair.snd());
        int topVisibleFrame = findSparseIndex(proxyStackFrames, 0);
        final IStackFrame stackFrame = stackFrames[topVisibleFrame];
        this.fProxyNotifier.notify(new ProxyDebugStepEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(pair.fst()),
                                                           toProxyStackFrame(pair.snd(), stackFrame, 0),
                                                           thread.getId(), getDepth(thread),
                                                           getVariablesAsStringArray(stackFrame)));
      }
    } catch (DebugException except) {
      notifyErrorEvent(tasks, IPDIErrorInfo.DBG_FATAL, "Error during Step Over operation: " + except.getMessage());
    }
  }

  /**
   * Keep executing thread.stepOver() until we're in an X10 function and on a different X10 line.
   */
  private boolean stepOver(final DebuggeeThread thread, final DebuggeeProcess process, final Location start) throws DebugException {
    String startFile = getX10File(process, start);
    int startLine = getX10Line(process, start);
    assert (startFile != null && startLine != -1);
    String function = null;
    String file = startFile;
    int line = startLine;
    do {
      final ThreadStoppedEventListener stoppedEventListener = new ThreadStoppedEventListener(thread);
      thread.addEventListener(stoppedEventListener);
      final Thread runnableThread = new Thread(new WaitingForStateRunnable(stoppedEventListener));
      try {
        thread.stepOver();
        runnableThread.start();
        runnableThread.join();
      } catch (InterruptedException except) {
        return false;
      } finally {
        thread.removeEventListener(stoppedEventListener);
        runnableThread.interrupt();
      }
      Location location = thread.getLocation(thread.getViewInformation());
      String cppFunction = getFunction(location);
      System.out.println("Step over: stepping through "+cppFunction);
      function = fTranslator.getX10Function(process, cppFunction, location);
      file = getX10File(process, location);
      line = getX10Line(process, location);
    } while (function == null || (file.equals(startFile) && line == startLine));
    return true;
  }

  public void stepOverInstruction(final BitList tasks, final int count) throws PDIException {
    // TODO: use to step over a C++ statement?
    raiseDialogBoxNotImplemented("Step over instruction not implemented");
  }

  public void stepReturn(final BitList tasks, final int count) throws PDIException {
    try {
      fireResumeEvent(tasks, IPDIResumedEvent.STEP_RETURN);
      
      for (final Pair<BitList, DebuggeeProcess> pair : getAllProcesses(tasks)) {
        final DebuggeeThread thread = pair.snd().getStoppingThread();
        // By default for one step return (count == 0) !!
        for (int i = 0; i < count + 1; i++) {
          if (!stepReturn(thread, pair.snd()))
            throw new PDIException(tasks, "Step Return interrupted");
        }
        // TODO: consolidate same stack frames into one event
        IStackFrame[] stackFrames = thread.getStackFrames();
        ProxyDebugStackFrame[] proxyStackFrames = getProxyStackFrames(stackFrames, pair.snd());
        int topVisibleFrame = findSparseIndex(proxyStackFrames, 0);
        final IStackFrame stackFrame = stackFrames[topVisibleFrame];
        this.fProxyNotifier.notify(new ProxyDebugStepEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(pair.fst()),
                                                           toProxyStackFrame(pair.snd(), stackFrame, 0),
                                                           thread.getId(), getDepth(thread),
                                                           getVariablesAsStringArray(stackFrame)));
      }
    } catch (DebugException except) {
      notifyErrorEvent(tasks, IPDIErrorInfo.DBG_FATAL, "Error during Step Return operation: " + except.getMessage());
    }
  }

  /**
   * Keep executing thread.stepReturn() until we're in an X10 function.
   */
  private boolean stepReturn(final DebuggeeThread thread, final DebuggeeProcess process) throws DebugException {
    String function = null;
    do {
      final ThreadStoppedEventListener stoppedEventListener = new ThreadStoppedEventListener(thread);
      thread.addEventListener(stoppedEventListener);
      final Thread runnableThread = new Thread(new WaitingForStateRunnable(stoppedEventListener));
      try {
        thread.stepReturn();
        runnableThread.start();
        runnableThread.join();
      } catch (InterruptedException except) {
        return false;
      } finally {
        thread.removeEventListener(stoppedEventListener);
        runnableThread.interrupt();
      }
      Location location = thread.getLocation(thread.getViewInformation());
      String cppFunction = getFunction(location);
      System.out.println("Step return: stepping through "+cppFunction);
      function = fTranslator.getX10Function(process, cppFunction, location);
    } while (function == null);
    return true;
  }

  public void stepReturn(final BitList tasks, final IAIF aif) throws PDIException {
    raiseDialogBoxNotImplemented("Step Return with Return Value not yet implemented");
  }

  public void stepUntil(final BitList tasks, final IPDILocation location) throws PDIException {
    try {
      fireResumeEvent(tasks, IPDIResumedEvent.STEP_OVER);
      
      for (final Pair<BitList, DebuggeeProcess> pair : getAllProcesses(tasks)) {
        final DebuggeeThread thread = pair.snd().getStoppingThread();
        final ThreadStoppedEventListener stoppedEventListener = new ThreadStoppedEventListener(thread);
        thread.addEventListener(stoppedEventListener);
        final Location pdtLocation = getPDTLocation(pair.snd(), thread, pair.fst(), location);
        if (pdtLocation == null) {
          // We simply forget, and log the reason.
          DebugCore.log(IStatus.ERROR, NLS.bind("Unable to find location for {0}", location));
          thread.removeEventListener(stoppedEventListener);
        } else {
          thread.runToLocation(pdtLocation);
        }
        final Thread runnableThread = new Thread(new WaitingForStateRunnable(stoppedEventListener));
        runnableThread.start();
        try {
          runnableThread.join();
        } catch (InterruptedException except) {
          throw new PDIException(tasks, "Step over interrupted");
        } finally {
          thread.removeEventListener(stoppedEventListener);
        }
        // TODO: consolidate same stack frames into one event
        IStackFrame[] stackFrames = thread.getStackFrames();
        ProxyDebugStackFrame[] proxyStackFrames = getProxyStackFrames(stackFrames, pair.snd());
        int topVisibleFrame = findSparseIndex(proxyStackFrames, 0);
        final IStackFrame stackFrame = stackFrames[topVisibleFrame];
        this.fProxyNotifier.notify(new ProxyDebugStepEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(pair.fst()),
                                                           toProxyStackFrame(pair.snd(), stackFrame, 0),
                                                           thread.getId(), getDepth(thread),
                                                           getVariablesAsStringArray(stackFrame)));
      }
    } catch (DebugException except) {
      notifyErrorEvent(tasks, IPDIErrorInfo.DBG_FATAL, "Error during Step Until operation: " + except.getMessage());
    } catch (EngineRequestException except) {
      DebugCore.log(IStatus.ERROR, "PDT run to location failed", except);
    }
  }

  public void suspend(final BitList tasks) throws PDIException {
    if (! this.fTaskToTarget.isEmpty()) {
      try {
        for (final Pair<BitList, DebuggeeProcess> pair : getAllProcesses(tasks)) {
          pair.snd().halt();
//        DebuggeeThread firstAvailableThread = null;
//        for (final DebuggeeThread thread : pair.snd().getThreads()) {
//          if (thread != null) {
//            firstAvailableThread = thread;
//            break;
//          }
//        }
//        assert firstAvailableThread != null;
//        final IStackFrame stackFrame = firstAvailableThread.getTopStackFrame();
//        this.fProxyNotifier.notify(new ProxyDebugSuspendEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(pair.fst()),
//                                                              toProxyStackFrame(pair.fst(), pair.snd(), stackFrame, 0),
//                                                              firstAvailableThread.getId(),
//                                                              firstAvailableThread.getStackFrames().length,
//                                                              getVariablesAsStringArray(stackFrame)));
      }
//    } catch (DebugException except) {
//      notifyErrorEvent(tasks, IPDIErrorInfo.DBG_FATAL, "Error during Suspend operation: " + except.getMessage());
      } catch (EngineRequestException except) {
        notifyErrorEvent(tasks, IPDIErrorInfo.DBG_FATAL, "Halt operation refused by PDT: " + except.getMessage());
      }
    }
  }

  public void terminate(final BitList tasks) throws PDIException {
    if (! this.fTaskToTarget.isEmpty()) {
      try {
        for (final Pair<BitList, DebuggeeProcess> pair : getAllProcesses(tasks)) {
          notifyOkEvent(pair.fst());
          pair.snd().terminate();
        }
      } catch (DebugException except) {
        notifyErrorEvent(tasks, IPDIErrorInfo.DBG_FATAL, "Error during PDT terminate operation: " + except.getMessage());
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
      for (final Pair<BitList, DebuggeeProcess> pair : getAllProcesses(tasks)) {
        final DebuggeeThread thread = pair.snd().getStoppingThread();
        final IStackFrame[] stackFrames = thread.getStackFrames();
        final ProxyDebugStackFrame[] proxyStackFrames = getProxyStackFrames(stackFrames, pair.snd());
        int l = findSparseIndex(proxyStackFrames, low);
        int h = findSparseIndex(proxyStackFrames, high);
        final Collection<String> args = new ArrayList<String>();
        for (int i = l; i <= h; ++i) {
          if (proxyStackFrames[i] == null)
            continue;
          final int nbParams = ((StackFrame) stackFrames[i]).getNumOfParms();
          final IVariable[] variables = stackFrames[i].getVariables();
          for (int j = 0; j < nbParams; ++j) {
            args.add(variables[j].getName());
          }
        }
        // TODO: consolidate the events
        this.fProxyNotifier.notify(new ProxyDebugArgsEvent(1 /* transId */, ProxyDebugClient.encodeBitSet(pair.fst()),
                                                           args.toArray(new String[args.size()])));
      }
    } catch (DebugException except) {
      notifyErrorEvent(tasks, IPDIErrorInfo.DBG_FATAL, "Error during List Arguments operation: " + except.getMessage());
    }
  }

  public void listGlobalVariables(final BitList tasks) throws PDIException {
    for (final Pair<BitList,IPDTTarget> pair : getAllPDTTargets(tasks)) {
      final GlobalVariable[] globalVars = pair.snd().getTarget().getDebugEngine().getGlobalVariables();
      final String[] strGlobalVars = new String[globalVars.length];
      int i = -1;
      for (final GlobalVariable globalVariable : globalVars) {
        strGlobalVars[++i] = globalVariable.getName();
      }
      // TODO: consolidate the events
      this.fProxyNotifier.notify(new ProxyDebugVarsEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(pair.fst()),
                                                         strGlobalVars));
    }
  }
  
  public void listLocalVariables(final BitList tasks) throws PDIException {
    try {
      for (final Pair<BitList, DebuggeeProcess> pair : getAllProcesses(tasks)) {
        final DebuggeeThread thread = pair.snd().getStoppingThread();
        final StackFrame stackFrame = getCurrentStackFrame(thread);
        this.fProxyNotifier.notify(new ProxyDebugVarsEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(pair.fst()),
                                                           getVariablesAsStringArray(stackFrame)));
      }
    } catch (DebugException except) {
      notifyErrorEvent(tasks, IPDIErrorInfo.DBG_FATAL, "Error during List Local Vars operation: " + except.getMessage());
    }
  }

  public void retrieveAIF(final BitList tasks, final String expr) throws PDIException {
    try {
      for (final Pair<BitList, DebuggeeProcess> pair : getAllProcesses(tasks)) {
        final DebuggeeThread thread = pair.snd().getStoppingThread();
        final StackFrame stackFrame = getCurrentStackFrame(thread);
        final Location location = stackFrame.getCurrentLocation(thread.getViewInformation());
        final ProxyDebugAIF aif = createDebugProxyAIF(pair.snd(), thread, location, expr, false);
        // TODO: consolidate the events
        this.fProxyNotifier.notify(new ProxyDebugDataEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(pair.fst()), aif));
      }
    } catch (EngineRequestException except) {
      notifyErrorEvent(tasks, IPDIErrorInfo.DBG_FATAL, "Error during expression evaluation with PDT: " + except.getMessage());
    } catch (MemoryException except) {
      notifyErrorEvent(tasks, IPDIErrorInfo.DBG_FATAL,
                       NLS.bind("Unable to evaluate ''{0}'' via memory request: " + except.getMessage(), expr));
    } catch (DebugException except) {
      notifyErrorEvent(tasks, IPDIErrorInfo.DBG_FATAL, "Error during Value Evaluation: " + except.getMessage());
    }
  }

  public void retrievePartialAIF(final BitList tasks, final String expr, final String key, final boolean listChildren,
                                 final boolean isExpression) throws PDIException {
    try {
      for (final Pair<BitList, DebuggeeProcess> pair : getAllProcesses(tasks)) {
        final DebuggeeThread thread = pair.snd().getStoppingThread();
        final StackFrame stackFrame = getCurrentStackFrame(thread);
        final Location location = stackFrame.getCurrentLocation(thread.getViewInformation());
        final ProxyDebugAIF aif = createDebugProxyAIF(pair.snd(), thread, location, expr, listChildren);
        // TODO: consolidate the events
        this.fProxyNotifier.notify(new ProxyDebugPartialAIFEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(pair.fst()), aif, expr));
      }
    } catch (EngineRequestException except) {
      notifyErrorEvent(tasks, IPDIErrorInfo.DBG_FATAL, "Error during expression evaluation with PDT: " + except.getMessage());
    } catch (MemoryException except) {
      notifyErrorEvent(tasks, IPDIErrorInfo.DBG_FATAL,
                       NLS.bind("Unable to evaluate ''{0}'' via memory request: " + except.getMessage(), expr));
    } catch (DebugException except) {
      notifyErrorEvent(tasks, IPDIErrorInfo.DBG_FATAL, "Error during Value Evaluation: " + except.getMessage());
    }
  }

  public void retrieveVariableType(final BitList tasks, final String variable) throws PDIException {
    try {
      for (final Pair<BitList, DebuggeeProcess> pair : getAllProcesses(tasks)) {
        final DebuggeeThread thread = pair.snd().getStoppingThread();
        final StackFrame stackFrame = getCurrentStackFrame(thread);
        final Location location = stackFrame.getCurrentLocation(thread.getViewInformation());
        final ProxyDebugAIF aif = createDebugProxyAIF(pair.snd(), thread, location, variable, false);
        // TODO: consolidate the events
        this.fProxyNotifier.notify(new ProxyDebugTypeEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(pair.fst()),
                                                           aif.getFDS()));
      }
    } catch (EngineRequestException except) {
      notifyErrorEvent(tasks, IPDIErrorInfo.DBG_FATAL, "Error during Expression Evaluation with PDT: " + except.getMessage());
    } catch (MemoryException except) {
      notifyErrorEvent(tasks, IPDIErrorInfo.DBG_FATAL,
                       NLS.bind("Unable to evaluate ''{0}'' via memory request: ", except.getMessage()));
    } catch (DebugException except) {
      notifyErrorEvent(tasks, IPDIErrorInfo.DBG_FATAL, "Error during Variable Type Evaluation: " + except.getMessage());
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
      for (final Pair<BitList, DebuggeeProcess> pair : getAllProcesses(tasks)) {
        final DebuggeeThread thread = pair.snd().getStoppingThread();
        final IStackFrame[] stackFrames = thread.getStackFrames();
        final ProxyDebugStackFrame[] proxyStackFrames =
          copySparseSubarray(getProxyStackFrames(stackFrames, pair.snd()),
                             low, depth-1, new ProxyDebugStackFrame[depth-low]);
        // TODO: consolidate same stack frame sets into one event
        this.fProxyNotifier.notify(new ProxyDebugStackframeEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(pair.fst()),
                                                                 proxyStackFrames));
      }
    } catch (DebugException except) {
      notifyErrorEvent(tasks, IPDIErrorInfo.DBG_FATAL, "Error during List Stack operation: " + except.getMessage());
    }
  }

  /**
   * Finds the location of index in a sparse array.
   * @return the real position of index in the array, or -1 if not found.
   */
  private <T> int findSparseIndex(final T[] array, final int index) {
    // Iterate over the array, skipping nulls
    int lvl = 0;
    for (int i = 0; i < array.length; i++) {
      if (array[i] != null) {
        if (lvl == index)
          return i;
        lvl++;
      }
    }
    return -1; // too few elements in the array
  }

  /**
   * Computes the length of a sparse array.
   * @return the length of the sparse array.
   */
  private <T> int getSparseLength(final T[] array) {
    // Iterate over the array, skipping nulls
    int lvl = 0;
    for (int i = 0; i < array.length; i++) {
      if (array[i] != null)
        lvl++;
    }
    return lvl;
  }

  /**
   * Copies out the elements of a sparse array from low to high inclusive into a dense array dest.
   * @return the dest parameter, or null if not successful.
   */
  private <T> T[] copySparseSubarray(final T[] array, final int low, final int high, final T[] dest) {
    assert (dest.length == high-low+1);
    final int start = findSparseIndex(array, low);
    if (start == -1) // too few elements in the array
      return null;
    // Iterate over the array, skipping nulls
    int p = 0;
    for (int i = start; i < array.length; i++) {
      if (array[i] != null)
        dest[p++] = array[i];
      if (p > high)
        return dest;
    }
    return null; // not enough elements to copy
  }

  public void setCurrentStackFrame(final BitList tasks, final int level) throws PDIException {
    try {
      for (final Pair<BitList, DebuggeeProcess> pair : getAllProcesses(tasks)) {
        final DebuggeeThread thread = pair.snd().getStoppingThread();
        final IStackFrame[] stackFrames = thread.getStackFrames();
        final ProxyDebugStackFrame[] proxyStackFrames = getProxyStackFrames(stackFrames, pair.snd());
        int lvl = findSparseIndex(proxyStackFrames, level);
        if (lvl != -1) {
          StackFrame frame = (StackFrame) stackFrames[lvl];
          thread.getLocals(frame);
          // TODO: consolidate the events
          this.fProxyNotifier.notify(new ProxyDebugVarsEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(pair.fst()),
                                                             getVariablesAsStringArray(frame)));
        } else {
          DebugCore.log(IStatus.ERROR, "Stack frame level selected is out of bounds: "+level);
          notifyErrorEvent(pair.fst(), IPDIErrorInfo.DBG_WARNING, "Stack frame level selected is out of bounds: "+level);
        }
      }
    } catch (DebugException except) {
      notifyErrorEvent(tasks, IPDIErrorInfo.DBG_FATAL, "Error during Set Current Stack Frame operation: " + except.getMessage());
    }
  }
  
  // --- IPDIThreadManagement's interface methods implementation

  public void listInfoThreads(final BitList tasks) throws PDIException {
    for (final Pair<BitList, DebuggeeProcess> pair : getAllProcesses(tasks)) {
      final DebuggeeThread[] threads = pair.snd().getThreads();
      final Collection<String> threadIds = new ArrayList<String>(threads.length);
      for (final DebuggeeThread thread : threads) {
        if ((thread != null) && !thread.isTerminated()) {
          threadIds.add(String.valueOf(thread.getId()));
        }
      }
      // TODO: consolidate the events
      this.fProxyNotifier.notify(new ProxyDebugInfoThreadsEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(pair.fst()),
                                                                threadIds.toArray(new String[threadIds.size()])));
    }
  }

  public void retrieveStackInfoDepth(final BitList tasks) throws PDIException {
    // send one event per unique depth value...
    try {
      for (final Pair<BitList, DebuggeeProcess> pair : getAllProcesses(tasks)) {
        final IStackFrame[] stackFrames = pair.snd().getStoppingThread().getStackFrames();
        int depth = getSparseLength(getProxyStackFrames(stackFrames, pair.snd()));
        // TODO: consolidate the events
        this.fProxyNotifier.notify(new ProxyDebugStackInfoDepthEvent(-1 /* transId */,
                                                                     ProxyDebugClient.encodeBitSet(pair.fst()), depth));
      }
    } catch (DebugException except) {
      notifyErrorEvent(tasks, IPDIErrorInfo.DBG_FATAL, "Error during Stack Frame retrieval: " + except.getMessage());
    }
  }

  public void selectThread(final BitList tasks, final int tid) throws PDIException {
    for (final Pair<BitList, DebuggeeProcess> pair : getAllProcesses(tasks)) {
      final DebuggeeThread thread = pair.snd().getThread(tid);
      try {
        final IStackFrame stackFrame = getCurrentStackFrame(thread);
        if (stackFrame == null) {
          this.fProxyNotifier.notify(new ProxyDebugStackframeEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(pair.fst()),
                                                                   new ProxyDebugStackFrame[0]));
        } else {
          final ProxyDebugStackFrame proxyStackFrame = toProxyStackFrame(pair.snd(), stackFrame, 0);
          // TODO: consolidate the events
          this.fProxyNotifier.notify(new ProxyDebugSetThreadSelectEvent(-1 /* transId */,
                                                                        ProxyDebugClient.encodeBitSet(pair.fst()),
                                                                        tid, proxyStackFrame));
        }
      } catch (DebugException except) {
        notifyErrorEvent(tasks, IPDIErrorInfo.DBG_FATAL, "Error during Stack Frame retrieval: " + except.getMessage());
      }
    }
  }
  
  // --- IPDIMemoryBlockManagement's interface methods implementation

  public void createDataReadMemory(final BitList tasks, final long offset, final String address, final int wordFormat,
                                   final int wordSize, final int rows, final int cols,
                                   final Character asChar) throws PDIException
  {
    // TODO: report error instead
    notifyOkEvent(tasks);
  }

  public void createDataWriteMemory(final BitList tasks, final long offset, final String address, final int wordFormat,
                                    final int wordSize, final String value) throws PDIException
  {
    // TODO: report error instead
    notifyOkEvent(tasks);
  }
  
  // --- Public services
  
  public void setPDISession(final IPDISession pdiSession) {
    this.fPDISession = pdiSession;
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
  
  // --- Overridden methods
  
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("PDI Debugger on port ").append(this.fPort).append(" for launch '") //$NON-NLS-1$
      .append(this.fLaunch.getLaunchConfiguration().getName()).append("'"); //$NON-NLS-1$
    return sb.toString();
  }
  
  // --- Private code
  
  private ProxyDebugStackFrame[] getProxyStackFrames(final IStackFrame[] stackFrames,
                                                     DebuggeeProcess process) throws PDIException
  {
    final ProxyDebugStackFrame[] proxyStackFrames = new ProxyDebugStackFrame[stackFrames.length];
    int count = 0;
    for (int i = 0; i < stackFrames.length; ++i) {
      ProxyDebugStackFrame frame = toProxyStackFrame(process, stackFrames[i], count);
      if (frame == null)
        continue;
      proxyStackFrames[i] = frame;
      count++;
    }
    return proxyStackFrames;
  }
  
  private static final String REMOTE_REF_VALUE = "REMOTE REFERENCE";
  private ProxyDebugAIF createDebugProxyAIF(final DebuggeeProcess process, final DebuggeeThread thread,
                                            final Location location, final String expr, boolean listChildren)
      throws EngineRequestException, DebugException, MemoryException, PDIException
  {
    System.out.println(expr);
    String base = expr;
    if (listChildren) // Expanding an existing expression
      base = extractBaseExpr(expr);
    int lastDot = -1;
    while ((lastDot = base.lastIndexOf('.')) != -1) {
      String ptr = base.substring(0, lastDot);
      String fld = base.substring(lastDot+1);
      if (ptr.startsWith("(") && ptr.endsWith(")")) {
        ptr = ptr.substring(1, ptr.length()-1);
      }
      if (!ptr.equals("this")) {
        ptr = ptr + "#_val";
      }
      base = ptr + "->" + FMGL(fld); // FIXME: HACK!
    }
    base = base.replace('#', '.');
    if (inClosure(process, getFunction(location)) && base.startsWith("this")) {
      base = "this->"+SAVED_THIS+"._val"+base.substring("this".length());
    }
    ExprNodeBase rootNode = evaluateExpression(base, process, thread, location);
    if (rootNode == null && inClosure(process, getFunction(location))) {
      // May have been a captured variable.  Try again, this time via the closure object.
      rootNode = evaluateExpression("this->"+expr, process, thread, location);
    }
    if (rootNode == null)
      return new ProxyDebugAIF("?0?", "00", expr);
    final EPDTVarExprType exprType = getExprType(rootNode);
    final String type = rootNode.getReferenceTypeName();
    final String[] desc = this.fTranslator.getStructDescriptor(type);
    if (rootNode instanceof ExprNode) {
      if (type.startsWith("class ref<") || type.startsWith("class x10aux::ref<")) { // got a reference, unwrap
        System.out.println("Got a ref: " + type);
        PDTDebugElement[] children = rootNode.getChildren();
        assert (children.length == 2); // _val and __ref{}
        rootNode = (ExprNodeBase) children[0];
        if (rootNode instanceof ExprNode) {
          final String type2 = rootNode.getReferenceTypeName();
          assert (type2.endsWith("*"));
          // children = rootNode.getChildren();
          // assert (children.length == 1); // the referenced object
          // ExprNodeBase referencedObject = (ExprNodeBase) children[0];
        }
      }
    }
    String result = rootNode.getValueString();
    if (result.equals(VARIABLE_NOT_FOUND))
        return new ProxyDebugAIF("?0?", "00", expr);
    if (result.equals(NOT_IN_SCOPE))
    	return new ProxyDebugAIF("?0?", "00", desc == null ? getTypeString(exprType) : desc[0]);
    if (isReferenceType(exprType) && isRemoteRef(result)) {
      int len = REMOTE_REF_VALUE.length();
      byte[] bytes = REMOTE_REF_VALUE.getBytes();
      String remoteVar = PDTUtils.toHexString(len, 4) + PDTUtils.toHexString(bytes);
      return new ProxyDebugAIF("s", remoteVar, expr);
    }
    if (exprType == EPDTVarExprType.ARRAY) {
      // GlobalVariable[] globals = process.getDebugEngine().getGlobalVariables();
      // for (GlobalVariable v : globals) {
      // if (v.getName().equals("x10aux::nullString"))
      // evaluateExpression(v.getExpression().replace("\nx10aux", "\n&x10aux"), process, thread, location);
      // }
      // ExprNodeBase lengthNode = evaluateExpression(expr+"._val->x10__length", process, thread, location);
      // String lengthType = lengthNode.getReferenceTypeName();
      // assert (lengthType.equals("int"));
      // int length = Integer.parseInt(lengthNode.getValueString());
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
        int num = 0;
        int atIdx = expr.lastIndexOf('@');
        if (expr.startsWith("*") && atIdx != -1) {
          num = Integer.parseInt(expr.substring(atIdx + 1));
          String inner = expr.substring(1, atIdx);
          assert (inner.startsWith("(") && inner.endsWith(")"));
          inner = inner.substring(1, inner.length() - 1);
          int plusIdx = inner.lastIndexOf('+');
          offset = Integer.parseInt(inner.substring(plusIdx + 1));
        } else if (expr.endsWith("]") && atIdx == -1) {
          // find matching "["
          int openIdx = findMatch(expr, expr.length() - 1);
          if (openIdx != -1) {
            String idx = expr.substring(openIdx + 1, expr.length() - 1);
            offset = Integer.parseInt(idx);
            num = 1;
          }
        } else {
          num = length;
          offset = 0;
        }
        // assert (offset == 0);
        // assert (num == length);
        // byte[] bytes = rail.getRawContents(offset, num);
        // // TODO: extract individual elements and concat the representations
        // result = toHexString(bytes);
        StringBuilder dsc = new StringBuilder();
        StringBuilder sb = new StringBuilder(); // TODO
        for (int i = 0; i < num; i++) {
          switch (elementType) {
          case BOOL:
            sb.append(getValue(elementType, Boolean.toString(rail.getIntAt(offset + i) != 0)));
            break;
          case INT:
          case FLOAT:
            sb.append(getValue(elementType, Integer.toString(rail.getIntAt(offset + i))));
            break; // FIXME: byte and short
          case LONG:
          case DOUBLE:
            sb.append(getValue(elementType, Long.toString(rail.getLongAt(offset + i))));
            break;
          case STRING: {
            X10String str = rail.getStringAt(offset + i);
            int len = (int) str.getLength();
            byte[] bytes = str.getContents().getBytes();
            sb.append(PDTUtils.toHexString(len, 4) + PDTUtils.toHexString(bytes));
            break;
          }
          case ARRAY:
          case STRUCT: {
            long val = rail.getPointerAt(offset + i);
            if (val == 0)
              sb.append("00");
            else
              sb.append("01").append(PDTUtils.toHexString(val, 16));
            break;
          }
          case CHAR:
            sb.append(PDTUtils.toHexString(rail.getIntAt(offset + i) >> 16, 2));
            break;
          default:
            sb.append(PDTUtils.toHexString(rail.getPointerAt(offset + i), 16));
            break;
          }
          final String[] eDesc = this.fTranslator.getStructDescriptor(desc[1]);
          if (dsc.length() > 0) dsc.append("\0");
          dsc.append(eDesc != null ? eDesc[0] : getTypeString(elementType));
        }
        desc[0] = dsc.toString();
        if (num == 0) {
          result = "v0";
          desc[2] = null;
        } else {
          result = sb.toString();
          desc[2] = Integer.toString(offset) + ".." + Integer.toString(offset + num - 1); // we want an inclusive upper bound
        }
      } else {
        if (result.startsWith("0x"))
          result = result.substring(2);
        // Encode the pointer
        boolean isNull = result.equals(PDTUtils.toHexString(0, 16));
        result = isNull ? "00" : "01" + result;
        // desc[2] = Integer.toString(length-1));
        if (length == 0)
          desc[2] = null;
        else
          desc[2] = "0.." + Integer.toString(length - 1); // we want an inclusive upper bound
      }
    }
    if (exprType == EPDTVarExprType.STRING) {
      assert (!listChildren);
      // GlobalVariable[] globals = process.getDebugEngine().getGlobalVariables();
      // for (GlobalVariable v : globals) {
      //   if (v.getName().equals("x10aux::nullString"))
      //     evaluateExpression(v.getExpression().replace("\nx10aux", "\n&x10aux"), process, thread, location);
      // }
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
      // ExprNodeBase contentNode = evaluateExpression(expr+"._val->x10__content", process, thread, location);
      // String contentType = contentNode.getReferenceTypeName();
      // assert (contentType.equals("char*"));
      // desc[1] = contentNode.getValueString();
      desc[1] = getValue(EPDTVarExprType.STRING, content);
      // ExprNodeBase lengthNode = evaluateExpression(expr+"._val->x10__content_length", process, thread, location);
      // String lengthType = lengthNode.getReferenceTypeName();
      // assert (lengthType.equals("int"));
      // desc[2] = lengthNode.getValueString();
      desc[2] = Integer.toString(length);
      result = PDTUtils.toHexString(length, 4) + PDTUtils.toHexString(bytes);
    }
    if (exprType == EPDTVarExprType.STRUCT) {
      if (listChildren) {
        // Compute the value
        // TODO: retrieve actual type
        // TODO: factor out into an X10Class
        final int numInterfaces = Integer.parseInt(desc[1]);
        X10Object object = new X10Object(process, thread, location, result) {
          {
            int offset = 3 * PTR_SIZE + 2 * PTR_SIZE * numInterfaces;
            for (int j = 2; j < desc.length; j++) {
              String name = FMGL(desc[j++]);
              String type = desc[j];
              EPDTVarExprType fet = getExprType(type);
              char ft = '\0';
              switch (fet) {
              case ADDRESS:
              case STRUCT:
                ft = X10Object.POINTER;
                break;
              case ARRAY:
                ft = X10Object.RAIL;
                break;
              case STRING:
                ft = X10Object.STRING;
                break;
              case BOOL:
              case CHAR:
              case INT:
              case FLOAT:
                ft = X10Object.INT;
                break;
              case LONG:
              case DOUBLE:
                ft = X10Object.LONG;
                break;
              }
              addField(name, offset, ft);
              int size = getExprSize(type);
              size = size + (PTR_SIZE - size) % PTR_SIZE; // pad
              offset += size;
            }
          }
        };
        StringBuilder dsc = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        for (int j = 2; j < desc.length; j++) {
          String n = FMGL(desc[j++]);
          String t = desc[j];
          EPDTVarExprType et = getExprType(t);
          switch (et) {
          case BOOL:
            sb.append(getValue(et, Boolean.toString(object.getIntField(n) != 0)));
            break;
          case INT:
          case FLOAT:
            sb.append(getValue(et, Integer.toString(object.getIntField(n))));
            break;
          case LONG:
          case DOUBLE:
            sb.append(getValue(et, Long.toString(object.getLongField(n))));
            break;
          case STRING: {
            X10String str = object.getStringField(n);
            int len = (int) str.getLength();
            byte[] bytes = str.getContents().getBytes();
            sb.append(PDTUtils.toHexString(len, 4) + PDTUtils.toHexString(bytes));
            break;
          }
          case ARRAY:
          case STRUCT: {
            long val = object.getPointerField(n);
            if (val == 0)
              sb.append("00");
            else
              sb.append("01").append(PDTUtils.toHexString(val, 16));
            break;
          }
          case CHAR:
            sb.append(PDTUtils.toHexString(object.getIntField(n) >> 16, 2));
            break;
          default:
            sb.append(PDTUtils.toHexString(object.getPointerField(n), 16));
            break;
          }
          final String[] eDesc = this.fTranslator.getStructDescriptor(t);
          if (dsc.length() > 0) dsc.append("\0");
          dsc.append(eDesc != null ? eDesc[0] : getTypeString(et));
        }
        desc[0] = dsc.toString();
        result = sb.toString();
        desc[1] = "NOPTR";
      } else {
        if (result.startsWith("0x"))
          result = result.substring(2);
        // Encode the pointer
        boolean isNull = result.equals(PDTUtils.toHexString(0, 16));
        result = isNull ? "00" : "01" + result;
      }
    }
    final String value = getValue(exprType, result);
    final ProxyDebugAIF aif = new ProxyDebugAIF(getVariableType(exprType, desc), value, desc == null ? getTypeString(exprType) : desc[0]);
    return aif;
  }

  private boolean isRemoteRef(String result) {
    assert (result.startsWith("0x"));
    long p = Long.parseLong(result.substring(2), 16);
    return (p & 0x1L) != 0;
  }

  private boolean isReferenceType(EPDTVarExprType exprType) {
    switch (exprType) {
    case ADDRESS:
    case ARRAY:
    case STRUCT:
      return true;
    }
    return false;
  }
  
  private PICLLoadInfo createLoadInfo(final ILaunchConfiguration configuration) throws CoreException {
    final PICLLoadInfo loadInfo = new PICLLoadInfo();
    loadInfo.setLaunchConfig(configuration);
    loadInfo.setProgramName(configuration.getAttribute(ATTR_EXECUTABLE_PATH, EMPTY_STRING));
    loadInfo.setProgramParms(configuration.getAttribute(ATTR_ARGUMENTS, EMPTY_STRING));
    loadInfo.setProject(getProjectResource(configuration.getAttribute(ATTR_PROJECT_NAME, EMPTY_STRING)));
    
    loadInfo.setUseProfile(false /* useProfile */);
   
    loadInfo.setStartupBehaviour(PICLLoadInfo.DEBUG_INITIALIZATION);
    return loadInfo;
  }
  
  public static void dumpMemory(final DebuggeeProcess process, final long ptr, final int length) {
    try {
      final DebuggeeThread thread = process.getStoppingThread();
      final Address contentAddress = process.convertToAddress("0x" + PDTUtils.toHexString(ptr, 16),
                                                              thread.getLocation(thread.getViewInformation()), thread);
      final Memory memory = process.getMemory(contentAddress.getAddress(), length);
      final MemoryByte[] cbytes = memory.getMemory();
      MemoryObject.dumpBytes(cbytes);
    } catch (MemoryException e) { }
  }
  
  private ExprNodeBase evaluateExpression(final String expr, DebuggeeProcess process, final DebuggeeThread thread,
                                          final Location location) throws EngineRequestException {
    // ExpressionBase expression = thread.evaluateExpression(location, expr, 0 /* expansionLevel */, 0);
    ExpressionBase expression = process.monitorExpression(location.getEStdView(), thread.getId(), expr,
                                                          IEPDCConstants.MonEnable, IEPDCConstants.MonTypeProgram, null, null,
                                                          null, null);
    if (expression == null) {
      System.out.println("\tGot null from evaluating '"+expr+"'");
      return null;
    }
    return expression.getRootNode();
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
  private String extractBaseExpr(final String expr) {
    int atIdx = expr.lastIndexOf('@');
    int dotIdx = expr.lastIndexOf('.');
    if (expr.startsWith("*") && atIdx != -1) { // *(base+off)@num
      String inner = expr.substring(1, atIdx);
      assert (inner.startsWith("(") && inner.endsWith(")"));
      inner = inner.substring(1, inner.length() - 1);
      int plusIdx = inner.lastIndexOf('+');
      if (plusIdx == -1)
        return inner;
      String base = inner.substring(0, plusIdx);
      if (base.startsWith("(") && base.endsWith(")"))
        base = base.substring(1, base.length() - 1);
      return base;
    }
    if (expr.endsWith("]") && atIdx == -1) { // base[idx]
      // find matching "["
      int openIdx = findMatch(expr, expr.length() - 1);
      if (openIdx == -1) // no matching bracket
        return expr;
      String base = expr.substring(0, openIdx);
      if (base.startsWith("(") && base.endsWith(")"))
        base = base.substring(1, base.length() - 1);
      return base;
    }
    if (expr.lastIndexOf(".") != -1 && atIdx == -1) { // base.fld
      // TODO
    }
    return expr;
  }
  
  private void fireResumeEvent(final BitList tasks, final int type) {
    final IPDIEvent resumeEvent = this.fPDISession.getEventFactory().newResumedEvent(this.fPDISession, tasks, type);
    this.fPDISession.getEventManager().fireEvent(resumeEvent);
  }
  
  private Iterable<Pair<BitList,DebuggeeProcess>> getAllProcesses(final BitList tasks) throws PDIException {
    final Collection<Pair<BitList,DebuggeeProcess>> allProcesses = new ArrayList<Pair<BitList,DebuggeeProcess>>();
    for (final int bit : tasks.toArray()) {
      final BitList task = new BitList(this.fPDISession.getTotalTasks());
      task.set(bit);
      final IPDTTarget target = this.fTaskToTarget.get(task);
      if (target == null) {
        throw new PDIException(task, "Unable to find the process associated with this task");
      }
      allProcesses.add(new Pair(task, (DebuggeeProcess) target.getProcess()));
    }
    return allProcesses;
  }
  
  private Iterable<Pair<BitList,IPDTTarget>> getAllPDTTargets(final BitList tasks) throws PDIException {
    final Collection<Pair<BitList,IPDTTarget>> allTargets = new ArrayList<Pair<BitList,IPDTTarget>>();
    for (final int bit : tasks.toArray()) {
      final BitList task = new BitList(this.fPDISession.getTotalTasks());
      task.set(bit);
      final IPDTTarget target = this.fTaskToTarget.get(task);
      if (target == null) {
        throw new PDIException(task, "Unable to find the process associated with this task");
      }
      allTargets.add(new Pair(task, target));
    }
    return allTargets;
  }
  
  private StackFrame getCurrentStackFrame(final DebuggeeThread thread) throws DebugException {
    final StackFrame frame = thread.getStackFrameMonitored();
    if (frame == null) {
      return (StackFrame) thread.getTopStackFrame();
      // This is ok, because we will always have a valid stop location if unselected
    } else {
      return frame;
    }
  }
  
  private int getDepth(final DebuggeeThread thread) throws DebugException {
    return thread.getStackFrames().length;
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
    } else if (type.startsWith("class ref<") && type.contains("__closure__")) {
    	return EPDTVarExprType.CLOSURE;
    } else if (type.startsWith("x10aux__ref<") && type.contains("__closure__")) {
    	return EPDTVarExprType.CLOSURE;
    } else if (type.startsWith("class x10aux::ref<") && type.contains("__closure__")) {
    	return EPDTVarExprType.CLOSURE;
    } else if (type.startsWith("x10aux::ref<") && type.contains("__closure__")) {
    	return EPDTVarExprType.CLOSURE;
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
  
  private Location getPDTLocation(final DebuggeeProcess process, final DebuggeeThread thread, final BitList tasks,
                                  final IPDILocation pdiLocation) throws PDIException {
    if (pdiLocation instanceof IPDILineLocation) {
      final ViewFile curViewFile = thread.getLocation(thread.getViewInformation()).getViewFile();
      final int lineNumber = ((IPDILineLocation) pdiLocation).getLineNumber();
      return this.fTranslator.getCppLocation(process, curViewFile.getBaseFileName(), lineNumber);
    } else if (pdiLocation instanceof IPDILocator) {
      final IPDILocator locator = (IPDILocator) pdiLocation;
      return this.fTranslator.getCppLocation(process, locator.getFile(), locator.getLineNumber());
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

  private String getTypeString(EPDTVarExprType type) {
    switch (type) {
    case BOOL: return "x10.lang.Boolean";
    case DOUBLE: return "x10.lang.Double";
    case FLOAT: return "x10.lang.Float";
    case INT: return "x10.lang.Int";
    case LONG: return "x10.lang.Long";
    case CHAR: return "x10.lang.Char";
    case STRING: return "x10.lang.String";
    default: return type.toString();
    }
  }
  
  private String getValue(final EPDTVarExprType type, final String evaluatedExpression) {
    switch (type) {
      case CHAR: // FIXME
        return PDTUtils.toHexString(Integer.parseInt(evaluatedExpression), 4);
      case INT:
      case FLOAT:
        return PDTUtils.toHexString(Integer.parseInt(evaluatedExpression), 8);
      case LONG:
      case DOUBLE:
        return PDTUtils.toHexString(Long.parseLong(evaluatedExpression), 16);
      case ADDRESS:
        return evaluatedExpression;
      case BOOL:
        return Boolean.parseBoolean(evaluatedExpression) ? "01" : "00";
      default:
        return evaluatedExpression;
    }
  }
  
  private String[] getVariablesAsStringArray(final IStackFrame stackFrame) throws DebugException, PDIException {
    final StackFrame frame = (StackFrame) stackFrame;
    final Location location = frame.getLocation(frame.getViewInformation());
    if (location == null) {
      throw new PDIException(null, "Unable to access location info for stack frame");
    }
    final String function = getFunction(location);
    DebuggeeProcess process = (DebuggeeProcess) frame.getDebugTarget().getProcess();
    final IVariable[] variables = stackFrame.getVariables();
    final ArrayList<String> strVars = new ArrayList<String>();
    for (final IVariable var : variables) {
      if (var.getName().equals("no local variables are available for the selected stackframe"))
        continue;
      if (var.getName().equals("this") && inClosure(process, function)) {
        final String[] vars = this.fTranslator.getClosureVars(process, frame, location, function);
        // FIXME: filter out the duplicates if any vars in the frame shadow the captured variables
        for (int i = 0; i < vars.length; i++) {
		  strVars.add(vars[i]);
		}
      } else {
        strVars.add(var.getName());
      }
    }
    return strVars.toArray(new String[strVars.size()]);
  }
  
  private String getVariableType(final EPDTVarExprType type, String[] desc) throws PDIException {
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
        // desc is null, or contains [ typestr, flag, ( name, type )* ]
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
      case ARRAY: { //"[rLOW..HIGHis4]TYPE"
        // desc is null or contains [ typestr, elemtype, "LOW..HIGH" ] 
        if (desc == null) // a field of a struct, or an unknown rail
          return "^a8v0";
        if (desc[2] == null) // 0-length rail
          return "{EMPTY_RAIL|;;;}";
        StringBuilder sb = new StringBuilder();
        sb.append("[r").append(desc[2]).append("is4]"); //$NON-NLS-1$//$NON-NLS-2$
        String t = desc[1];
        sb.append(getVariableType(getExprType(t), this.fTranslator.getStructDescriptor(t)));
        return sb.toString();
      }
      case STRING:
        return "s"; //$NON-NLS-1$
      case CLOSURE: //"{captured variables|VAR1=TYPE1,VAR2=TYPE2;;;}"
      default:
        return "*"; //$NON-NLS-1$ // For unknown type
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
  
  private void notifyErrorEvent(final BitList tasks, final int errorCode, final String message) {
    this.fProxyNotifier.notify(new ProxyDebugErrorEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks), errorCode,
                                                        message));
  }
  
  private void notifyOkEvent(final BitList tasks) {
    this.fProxyNotifier.notify(new ProxyDebugOKEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks)));
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

      final PICLDebugTarget target = new PICLDebugTarget(this.fLaunch, loadInfo, connectionInfo);

      final int[] tasksArray = this.fPDISession.getTasks().toArray();
      final BitList taskBits = new BitList(this.fPDISession.getTotalTasks());
      taskBits.set(tasksArray[this.fProcessCounter]);
 
      ++this.fProcessCounter;

      final IPDTTarget pdtTarget = new PDTTarget(target, this.fProxyNotifier, ProxyDebugClient.encodeBitSet(taskBits));
      this.fTaskToTarget.put(taskBits, pdtTarget);
      
      target.setShouldCreatePICLBreakpoints(false /* shouldCreatePICLBreakpoints */);
      final DebugEngine debugEngine = target.createDebugEngine(connectionInfo, true /* socketReuse */);
      debugEngine.addEventListener(pdtTarget);
      
      final IPreferenceStore store = PICLDebugPlugin.getInstance().getPreferenceStore();
      store.putValue(IPICLDebugConstants.PREF_SOCKETTIMEOUT, "false");
      target.engineIsWaiting(connectionInfo, true /* socketReuse */);
      
      if (this.fProcessCounter == this.fNbTasks) {
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
  
  private ProxyDebugStackFrame toProxyStackFrame(final DebuggeeProcess process, final IStackFrame stackFrame,
                                                 final int level) throws PDIException {
    final StackFrame frame = (StackFrame) stackFrame;
    final Location location = frame.getLocation(frame.getViewInformation());
    if (location == null) {
      throw new PDIException(null, "Unable to access location info for stack frame");
    }
    String cppFile = location.getViewFile().getBaseFileName();
    int cppLine = location.getLineNumber();
    String cppFunction = getFunction(location);
    boolean isLib = (cppFile.startsWith("lib") && cppFile.endsWith("-.text-1"));
    String file = isLib ? null : getX10File(process, location);
    int lineNumber = isLib ? -1 : getX10Line(process, location);
    String function = this.fTranslator.getX10Function(process, cppFunction, location);
    if (file == null && lineNumber == -1 && function == null)
        return null;
    if (file == null || lineNumber == -1 || function == null) {
      file = cppFile;
      lineNumber = cppLine;
      function = cppFunction;
    }
    return ProxyDebugEventFactory.toFrame(String.valueOf(level), file, function, String.valueOf(lineNumber),
                                          (String) null /* address */);
  }

  /**
   * Walk up to the preceding line within the same function that has the mapping.
   * @return the file, or null if no mapping within the function
   */
  private String getX10File(final DebuggeeProcess process, Location location) {
    String x10File = this.fTranslator.getX10File(process, location);
    if (x10File != null)
      return x10File;
    int count = 0;
    String function = getFunction(location);
    if (function == null)
    	return x10File;
    while (x10File == null && count++ < MAX_LINE_LOOKBEHIND && location.getLineNumber() > 0) {
      location = new Location(location.getViewFile(), location.getLineNumber()-1);
      String newFunction = getFunction(location);
      if (newFunction != function)
    	  break;
      x10File = this.fTranslator.getX10File(process, location);
    }
	return x10File;
  }

  /**
   * Walk up to the preceding line within the same function that has the mapping.
   * @return the file, or null if no mapping within the function
   */
  private int getX10Line(final DebuggeeProcess process, Location location) {
    int x10Line = this.fTranslator.getX10Line(process, location);
    if (x10Line != -1)
      return x10Line;
    int count = 0;
    String function = getFunction(location);
    if (function == null)
      return x10Line;
    while (x10Line == -1 && count++ < MAX_LINE_LOOKBEHIND && location.getLineNumber() > 0) {
      location = new Location(location.getViewFile(), location.getLineNumber()-1);
      String newFunction = getFunction(location);
      if (newFunction != function)
    	  break;
      x10Line = this.fTranslator.getX10Line(process, location);
    }
	return x10Line;
  }

  private String getFunction(final Location location) {
    Function[] functions = location.getFunctionsAtThisLocation();
    String cppFunction = functions.length == 0 ? "UNKNOWN" : functions[0].getName();
    return cppFunction;
  }
  
  // --- Private classes
  
  private class ListenerRunnable implements Runnable {

    // --- Interface methods implementation
    
    public void run() {
      while (this.fProcCounter < X10PDIDebugger.this.fNbTasks) {
        try {
          final Socket socket = X10PDIDebugger.this.fServerSocket.accept();
          if (socket != null) {
            ++this.fProcCounter;
            process(socket);
          }
        } catch (IOException except) {
          DebugCore.log(IStatus.ERROR, DebugMessages.PDID_SocketListeningError, except);
        }
      }
    }
    
    // --- Fields
    
    private int fProcCounter;
    
  }
  
  private enum ESessionState {
    CONNECTED, RUNNING, DISCONNECTED
  }
  
  private enum EPDTVarExprType { // TODO: add BYTE and SHORT
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
    CLOSURE,
    UNKNOWN
  }
  
  // --- Fields
  
  private ESessionState fState = ESessionState.DISCONNECTED;
  
  private ServerSocket fServerSocket;
  
  private IPLaunch fLaunch;
  
  private boolean fWaiting;
  
  private IPDISession fPDISession;
  
  private int fProcessCounter;
  
  private BitList fCurTasks;
  
  private IProject fProject;
  
  private final int fPort;
  
  private final int fNbTasks;
  
  private final IResourceManagerControl fRmControl;
  
  private final Map<BitList, IPDTTarget> fTaskToTarget = new HashMap<BitList, IPDTTarget>();
  
  private final IDebuggerTranslator fTranslator;
  
  private final ReentrantLock fWaitLock = new ReentrantLock();
  
  private final Condition fRunningCondition = this.fWaitLock.newCondition();
  
  private final Condition fLaunchCondition = this.fWaitLock.newCondition();
  
  private final ProxyNotifier fProxyNotifier = new ProxyNotifier();
  
  private static final int INT_SIZE = 4;
  
  private static final int LONG_SIZE = 8;
  
  private static final int PTR_SIZE = LONG_SIZE;
  
  private static final String EMPTY_STRING = ""; //$NON-NLS-1$
  
  private static final int MAX_LINE_LOOKBEHIND = 10;
    
}
