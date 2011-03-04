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
import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_STOP_IN_MAIN;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
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
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.imp.x10dt.ui.cpp.debug.DebugCore;
import org.eclipse.imp.x10dt.ui.cpp.debug.DebugMessages;
import org.eclipse.imp.x10dt.ui.cpp.debug.core.IDebuggerTranslator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.util.BitList;
import org.eclipse.ptp.debug.core.launch.IPLaunch;
import org.eclipse.ptp.debug.core.pdi.IPDIDebugger;
import org.eclipse.ptp.debug.core.pdi.IPDILocation;
import org.eclipse.ptp.debug.core.pdi.IPDISession;
import org.eclipse.ptp.debug.core.pdi.PDIException;
import org.eclipse.ptp.debug.core.pdi.model.IPDIAddressBreakpoint;
import org.eclipse.ptp.debug.core.pdi.model.IPDIBreakpoint;
import org.eclipse.ptp.debug.core.pdi.model.IPDIExceptionpoint;
import org.eclipse.ptp.debug.core.pdi.model.IPDIFunctionBreakpoint;
import org.eclipse.ptp.debug.core.pdi.model.IPDILineBreakpoint;
import org.eclipse.ptp.debug.core.pdi.model.IPDISignal;
import org.eclipse.ptp.debug.core.pdi.model.IPDIWatchpoint;
import org.eclipse.ptp.debug.core.pdi.model.aif.AIFFactory;
import org.eclipse.ptp.debug.core.pdi.model.aif.IAIF;
import org.eclipse.ptp.debug.sdm.core.proxy.ProxyDebugClient;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugArgsEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugEventFactory;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugInfoThreadsEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugOKEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugPartialAIFEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugStackInfoDepthEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugStackframeEvent;
import org.eclipse.ptp.internal.proxy.debug.event.ProxyDebugVarsEvent;
import org.eclipse.ptp.proxy.debug.client.ProxyDebugAIF;
import org.eclipse.ptp.proxy.debug.client.ProxyDebugStackFrame;
import org.eclipse.ptp.proxy.debug.event.IProxyDebugEvent;
import org.eclipse.ptp.proxy.debug.event.IProxyDebugOKEvent;

import com.ibm.debug.daemon.CoreDaemon;
import com.ibm.debug.daemon.DaemonConnectionInfo;
import com.ibm.debug.daemon.DaemonSocketConnection;
import com.ibm.debug.internal.pdt.PICLDebugTarget;
import com.ibm.debug.internal.pdt.model.BreakpointAddedEvent;
import com.ibm.debug.internal.pdt.model.BreakpointChangedEvent;
import com.ibm.debug.internal.pdt.model.BreakpointDeletedEvent;
import com.ibm.debug.internal.pdt.model.DebugEngineCommandLogResponseEvent;
import com.ibm.debug.internal.pdt.model.DebugEngineTerminatedEvent;
import com.ibm.debug.internal.pdt.model.DebuggeeProcess;
import com.ibm.debug.internal.pdt.model.DebuggeeThread;
import com.ibm.debug.internal.pdt.model.EngineRequestException;
import com.ibm.debug.internal.pdt.model.ErrorOccurredEvent;
import com.ibm.debug.internal.pdt.model.ExpressionBase;
import com.ibm.debug.internal.pdt.model.Function;
import com.ibm.debug.internal.pdt.model.GlobalVariable;
import com.ibm.debug.internal.pdt.model.IDebugEngineEventListener;
import com.ibm.debug.internal.pdt.model.IProcessEventListener;
import com.ibm.debug.internal.pdt.model.IThreadEventListener;
import com.ibm.debug.internal.pdt.model.Location;
import com.ibm.debug.internal.pdt.model.MessageReceivedEvent;
import com.ibm.debug.internal.pdt.model.ModelStateReadyEvent;
import com.ibm.debug.internal.pdt.model.Module;
import com.ibm.debug.internal.pdt.model.ModuleAddedEvent;
import com.ibm.debug.internal.pdt.model.Part;
import com.ibm.debug.internal.pdt.model.ProcessAddedEvent;
import com.ibm.debug.internal.pdt.model.ProcessDetachedEvent;
import com.ibm.debug.internal.pdt.model.ProcessEndedEvent;
import com.ibm.debug.internal.pdt.model.ProcessPgmError;
import com.ibm.debug.internal.pdt.model.ProcessPgmOutput;
import com.ibm.debug.internal.pdt.model.ProcessStoppedEvent;
import com.ibm.debug.internal.pdt.model.StackAddedEvent;
import com.ibm.debug.internal.pdt.model.StackFrame;
import com.ibm.debug.internal.pdt.model.ThreadAddedEvent;
import com.ibm.debug.internal.pdt.model.ThreadChangedEvent;
import com.ibm.debug.internal.pdt.model.ThreadEndedEvent;
import com.ibm.debug.internal.pdt.model.ThreadStoppedEvent;
import com.ibm.debug.internal.pdt.model.View;
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
  
  public X10PDIDebugger(final IDebuggerTranslator translator, final int port) {
    this.fPort = port;
    this.fTranslator = translator;
    translator.setDebugger(this);
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

  public void initialize(final ILaunchConfiguration configuration, final List<String> args, 
                         final IProgressMonitor monitor) throws PDIException {
    try {
      this.fServerSocket = new ServerSocket(this.fPort);
      this.fState = ESessionState.CONNECTED;

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
    System.out.println("Start debugger: "+app+" "+path+" "+dir+" "+Arrays.toString(args));
    this.fProxyNotifier.notify(new IProxyDebugOKEvent() {
		public String getBitSet() { return "1:1"; }
		public String[] getAttributes() { return null; }
		public int getEventID() { return EVENT_DBG_OK; }
		public int getTransactionID() { return -1; }
    });
  }

  public void stopDebugger() throws PDIException {
    try {
      this.fState = ESessionState.DISCONNECTED;
      
      if (this.fPDTTarget != null) {
        this.fPDTTarget.terminate();
        this.fPDTTarget = null;
      }
    } catch (DebugException except) {
      throw new PDIException(null, NLS.bind(DebugMessages.PDID_PDTDisconnectError, except.getMessage()));
    } finally {
//      DebugPlugin.getDefault().removeDebugEventListener(this);
    }
  }
  
  // --- IPDIBreakpointManagement's interface methods implementation

  public void deleteBreakpoint(final BitList tasks, final int bpid) throws PDIException {
    System.out.println("Delete breakpoint");
    this.fProcessListener.setCurTasks(tasks);
  }

  public void setAddressBreakpoint(final BitList tasks, final IPDIAddressBreakpoint breakpoint) throws PDIException {
    this.fProcessListener.setCurTasks(tasks);
    initBreakPointIdIfRequired(breakpoint);
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
  }

  public void setConditionBreakpoint(final BitList tasks, final int bpid, final String condition) throws PDIException {
    this.fProcessListener.setCurTasks(tasks);
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
  }

  public void setEnabledBreakpoint(final BitList tasks, final int bpid, final boolean enabled) throws PDIException {
    this.fProcessListener.setCurTasks(tasks);
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
  }

  public void setExceptionpoint(final BitList tasks, final IPDIExceptionpoint breakpoint) throws PDIException {
    this.fProcessListener.setCurTasks(tasks);
    initBreakPointIdIfRequired(breakpoint);
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
  }

  public void setFunctionBreakpoint(final BitList tasks, final IPDIFunctionBreakpoint breakpoint) throws PDIException {
    this.fProcessListener.setCurTasks(tasks);
    initBreakPointIdIfRequired(breakpoint);
    try {
      String name = breakpoint.getLocator().getFunction();
      final Function[] fns = getDebuggeeProcess(tasks).getFunctions(name, true /* caseSensitive */);
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
    this.fProcessListener.setCurTasks(tasks);
    initBreakPointIdIfRequired(breakpoint);
    final Location location = this.fTranslator.getCppLocation(tasks, breakpoint.getLocator().getFile(), breakpoint.getLocator().getLineNumber());
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
    this.fProcessListener.setCurTasks(tasks);
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
  }
  
  // --- IPDIExecuteManagement's interface methods implementation

  public void restart(final BitList tasks) throws PDIException {
    this.fProcessListener.setCurTasks(tasks);
    throw new IllegalStateException();
  }

  int resumeCount = 0;
  public void resume(final BitList tasks, final boolean passSignal) throws PDIException {
    this.fProcessListener.setCurTasks(tasks);
    try {
      if (resumeCount > 0)
    	  this.fPDTTarget.resume();
      resumeCount++;
    } catch (DebugException except) {
      throw new PDIException(tasks, except.getMessage());
    }
  }

  public void resume(final BitList tasks, final IPDILocation location) throws PDIException {
    this.fProcessListener.setCurTasks(tasks);
    throw new IllegalArgumentException("Resuming to location not supported");
//    try {
//      this.fPDTTarget.resume();
//    } catch (DebugException except) {
//      throw new PDIException(tasks, NLS.bind("Resume action error: ", except.getMessage()));
//    }
  }

  public void resume(final BitList tasks, final IPDISignal signal) throws PDIException {
    this.fProcessListener.setCurTasks(tasks);
	throw new IllegalArgumentException("Resuming with signal not supported");
//    try {
//      this.fPDTTarget.resume();
//    } catch (DebugException except) {
//      throw new PDIException(tasks, except.getMessage());
//    }
  }

  public void start(final BitList tasks) throws PDIException {
    this.fProcessListener.setCurTasks(tasks);
    resume(tasks, false);
  }

  public void stepInto(final BitList tasks, final int count) throws PDIException {
    this.fProcessListener.setCurTasks(tasks);
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
  }

  public void stepIntoInstruction(final BitList tasks, final int count) throws PDIException {
    this.fProcessListener.setCurTasks(tasks);
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
  }

  public void stepOver(final BitList tasks, final int count) throws PDIException {
    this.fProcessListener.setCurTasks(tasks);
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
  }

  public void stepOverInstruction(final BitList tasks, final int count) throws PDIException {
    this.fProcessListener.setCurTasks(tasks);
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
  }

  public void stepReturn(final BitList tasks, final int count) throws PDIException {
    this.fProcessListener.setCurTasks(tasks);
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
  }

  public void stepReturn(final BitList tasks, final IAIF aif) throws PDIException {
    this.fProcessListener.setCurTasks(tasks);
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
  }

  public void stepUntil(final BitList tasks, final IPDILocation location) throws PDIException {
    this.fProcessListener.setCurTasks(tasks);
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
  }

  public void suspend(final BitList tasks) throws PDIException {
    if (this.fPDTTarget != null) {
      try {
        this.fPDTTarget.suspend();
      } catch (DebugException except) {
        throw new PDIException(tasks, NLS.bind("Suspend action error: ", except.getMessage()));
      }
    }
  }

  public void terminate(final BitList tasks) throws PDIException {
    try {
      this.fPDTTarget.terminate();
    } catch (DebugException except) {
      throw new PDIException(tasks, "Terminate action error: " + except.getMessage());
    }
  }
  
  // --- IPDIVariableManagement's interface methods implementation

  public void dataEvaluateExpression(final BitList tasks, final String expression) throws PDIException {
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
  }

  public void deleteVariable(final BitList tasks, final String variable) throws PDIException {
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
  }

  public void evaluateExpression(final BitList tasks, final String expression) throws PDIException {
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
  }

  public void listArguments(final BitList tasks, final int low, final int high) throws PDIException {
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
    try {
      final DebuggeeThread thread = getDebuggeeProcess(tasks).getStoppingThread();
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
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
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
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
    try {
      final DebuggeeThread thread = getDebuggeeProcess(tasks).getStoppingThread();
      final IStackFrame stackFrame = thread.getTopStackFrame();
      this.fProxyNotifier.notify(new ProxyDebugVarsEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                         getVariablesAsArrayString(stackFrame)));
    } catch (DebugException except) {
      throw new PDIException(tasks, "Unable to access stack frames in PDT: " + except.getMessage());
    }
  }

  public void retrieveAIF(final BitList tasks, final String expr) throws PDIException {
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
  }

  public void retrievePartialAIF(final BitList tasks, final String expr, final String key, final boolean listChildren, 
                                 final boolean isExpression) throws PDIException {
    if (isExpression) {
      throw new PDIException(tasks, "Not yet implemented");
    } else {
      try {
        final DebuggeeThread thread = getDebuggeeProcess(tasks).getStoppingThread();
        final StackFrame stackFrame = (StackFrame) thread.getTopStackFrame();
        final Location location = stackFrame.getCurrentLocation(thread.getViewInformation());
        final ExpressionBase expression = thread.evaluateExpression(location, expr,  0 /* expansionLevel */,  0);
        
        final String type = getVariableType(expression.getRootNode().getReferenceTypeName());
        final ProxyDebugAIF aif = new ProxyDebugAIF(type, getValue(type, expression.getRootNode().getValueString()), expr);
        this.fProxyNotifier.notify(new ProxyDebugPartialAIFEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                                 aif, expr));
      } catch (DebugException except) {
        throw new PDIException(tasks, "Unable to access stack frames in PDT: " + except.getMessage());
      } catch (EngineRequestException except) {
        throw new PDIException(tasks, NLS.bind("Unable to evaluation ''{0}'' via PDT request: " + except.getMessage(), expr));
      }
    }
  }

  public void retrieveVariableType(final BitList tasks, final String variable) throws PDIException {
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
  }
  
  // --- IPDISignalManagement's interface methods implementation

  public void listSignals(final BitList tasks, final String name) throws PDIException {
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
  }

  public void retrieveSignalInfo(final BitList tasks, final String arg) throws PDIException {
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
  }
  
  // --- IPDIStackframeManagement's interface methods implementation

  public void listStackFrames(final BitList tasks, final int low, final int depth) throws PDIException {
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
    try {
      final DebuggeeThread thread = getDebuggeeProcess(tasks).getStoppingThread();
      final IStackFrame[] stackFrames = thread.getStackFrames();
      final ProxyDebugStackFrame[] proxyStackFrames = new ProxyDebugStackFrame[stackFrames.length];
      for (int i = low; i < depth; ++i) {
        proxyStackFrames[i] = toProxyStackFrame(thread, stackFrames[i], i);
      }
      this.fProxyNotifier.notify(new ProxyDebugStackframeEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                               proxyStackFrames));
    } catch (DebugException except) {
      throw new PDIException(tasks, "Unable to access stack frames in PDT: " + except.getMessage());
    }
  }

  public void setCurrentStackFrame(final BitList tasks, final int level) throws PDIException {
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
  }
  
  // --- IPDIThreadManagement's interface methods implementation

  public void listInfoThreads(final BitList tasks) throws PDIException {
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
    final DebuggeeProcess process = getDebuggeeProcess(tasks);
    final DebuggeeThread[] threads = process.getThreads();
    final Collection<String> threadIds = new ArrayList<String>(threads.length);
    for (final DebuggeeThread thread : threads) {
      if (thread != null) {
        threadIds.add(String.valueOf(thread.getId()));
      }
    }
    this.fProxyNotifier.notify(new ProxyDebugInfoThreadsEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks),
                                                              threadIds.toArray(new String[threadIds.size()])));
  }

  public void retrieveStackInfoDepth(final BitList tasks) throws PDIException {
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
    final DebuggeeProcess process = getDebuggeeProcess(tasks);
    try {
      final int depth = process.getStoppingThread().getStackFrames().length;
      this.fProxyNotifier.notify(new ProxyDebugStackInfoDepthEvent(-1 /* transId */, ProxyDebugClient.encodeBitSet(tasks), 
                                                                   depth));
    } catch (DebugException except) {
      throw new PDIException(tasks, "Unable to access stack frames in PDT: " + except.getMessage());
    }
  }

  public void selectThread(final BitList tasks, final int tid) throws PDIException {
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
  }
  
  // --- IPDIMemoryBlockManagement's interface methods implementation

  public void createDataReadMemory(final BitList tasks, final long offset, final String address, final int wordFormat, 
                                   final int wordSize, final int rows, final int cols, 
                                   final Character asChar) throws PDIException {
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
  }

  public void createDataWriteMemory(final BitList tasks, final long offset, final String address, final int wordFormat, 
                                    final int wordSize, final String value) throws PDIException {
    System.err.println("Passed in " + new Exception().getStackTrace()[0]);
  }
  
  // --- IDebugEngineEventListener's interface methods implementation
  
  public void commandLogResponse(final DebugEngineCommandLogResponseEvent event) {
    System.out.println("Command log response:");
    for (final String responseLine : event.getResponseLines()) {
      System.out.println(responseLine);
    }
  }

  public void debugEngineTerminated(final DebugEngineTerminatedEvent event) {
    
  }

  public void errorOccurred(final ErrorOccurredEvent event) {
  }

  public void messageReceived(final MessageReceivedEvent event) {
  }

  public void modelStateChanged(final ModelStateReadyEvent event) {
    // Do nothing.
  }

  public void processAdded(final ProcessAddedEvent event) {
    this.fProcessListener = new PDTProcessEventListener(event.getProcess(), this.fProxyNotifier);
    event.getProcess().addEventListener(this.fProcessListener);
    this.fTranslator.init(event.getProcess());
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
  
  private String toHexString(final int value, final int length) {
    final String s = Integer.toHexString(value);
    if (s.length() > length) {
      // Returns rightmost length chars 
      return s.substring(s.length() - length);
    } else if (s.length() < length) {
      // Pads on left with zeros. at most 7 will be prepended
      return "0000000".substring(0, length - s.length()) + s;
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
   
    if (configuration.getAttribute(ATTR_STOP_IN_MAIN, false)) {
      loadInfo.setStartupBehaviour(PICLLoadInfo.RUN_TO_MAIN);
    } else {
      loadInfo.setStartupBehaviour(PICLLoadInfo.DEBUG_INITIALIZATION);
    }
    return loadInfo;
  }
  
  private DebuggeeProcess getDebuggeeProcess(final BitList tasks) throws PDIException {
    final int firstBit = tasks.nextSetBit(0);
    final DebuggeeProcess process = this.fTaskToProcess[firstBit];
    if (process == null) {
      throw new PDIException(tasks, "Unable to find the process associated with this task");
    }
    if (tasks.nextSetBit(firstBit + 1) != -1) {
      throw new PDIException(tasks, "Unable to handle a request with multiple tasks at a time");
    }
    return process;
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
  
  private String getValue(final String type, final String evaluatedExpression) {
    switch (type.charAt(0)) {
      case AIFFactory.FDS_INT:
        return toHexString(Integer.parseInt(evaluatedExpression), 8);
      case AIFFactory.FDS_STRING:
        return evaluatedExpression;
      default:
        return evaluatedExpression;
    }
  }
  
  private String[] getVariablesAsArrayString(final IStackFrame stackFrame) throws DebugException {
    final IVariable[] variables = stackFrame.getVariables();
    final String[] strVars = new String[variables.length];
    int i = -1;
    for (final IVariable var : variables) {
      strVars[++i] = var.getName();
    }
    return strVars;
  }
  
  private String getVariableType(final String typeName) {
    final String type;
    if ("int".equals(typeName)) { //$NON-NLS-1$
      type = "is4"; //$NON-NLS-1$
    } else if ("float".equals(typeName)) { //$NON-NLS-1$
      type = "f4"; //$NON-NLS-1$
    } else if ("unsigned char**".equals(typeName)) {
      // This is wrong!!! It needs to be an array... But then this suite of tests is not generic enough... No good!
      type = "s0";
    } else {
      type = AIFFactory.UNKNOWNTYPE.toString();
    }
    return type;
  }
  
  private void initBreakPointIdIfRequired(final IPDIBreakpoint breakpoint) {
    if (breakpoint.getBreakpointID() == -1) {
      ++this.fBrkPointIdCounter;
      breakpoint.setBreakpointID(this.fBrkPointIdCounter);
    }
  }
  
  public ViewFile searchViewFile(final BitList tasks, final String fileName) throws PDIException {
    for (final Module module : getDebuggeeProcess(tasks).getModules(false)) {
      if (module != null) {
        final Part[] parts = module.getParts();
        if (parts == null || parts.length == 0) {
          continue;
        }
        for (final Part part : parts) {
          if (part != null) {
            final View view = part.getView(this.fPDTTarget.getDebugEngine().getSourceViewInformation());
            if (view != null) {
              for (final ViewFile vf : view.getViewFiles()) {
                if (vf != null) {
                  if (vf.getBaseFileName().equals(fileName)) {
                    return vf;
                  }
                }
              }
            }
          }
        }
      }
    }
    return null;
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

      this.fPDTTarget = new PICLDebugTarget(this.fLaunch, loadInfo, connectionInfo);
      this.fPDTTarget.fDebugEngineListener = this;
      this.fPDTTarget.engineIsWaiting(connectionInfo, true /* socketReuse */);
      this.fPDTTarget.getEngineSession().setConnectionTimeout(0);
  
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
  
  private ProxyDebugStackFrame toProxyStackFrame(final DebuggeeThread thread, final IStackFrame stackFrame, final int level) {
    final Location location = thread.getLocation(this.fPDTTarget.getDebugEngine().getSourceViewInformation());
    return ProxyDebugEventFactory.toFrame(String.valueOf(level), location.getViewFile().getBaseFileName(), 
                                          location.getViewFile().getFunctions()[0].getName(), 
                                          String.valueOf(location.getLineNumber()), (String) null /* address */);
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
  
  private final IDebuggerTranslator fTranslator;
  
  private final ReentrantLock fWaitLock = new ReentrantLock();
  
  private final Condition fRunningCondition = this.fWaitLock.newCondition();
  
  private final Condition fLaunchCondition = this.fWaitLock.newCondition();
  
  private final ProxyNotifier fProxyNotifier = new ProxyNotifier();
  
  private static final String EMPTY_STRING = ""; //$NON-NLS-1$
  
}
