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
import java.util.List;
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
import org.eclipse.imp.x10dt.ui.cpp.debug.Constants;
import org.eclipse.imp.x10dt.ui.cpp.debug.DebugCore;
import org.eclipse.imp.x10dt.ui.cpp.debug.DebugMessages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.util.BitList;
import org.eclipse.ptp.debug.core.launch.IPLaunch;
import org.eclipse.ptp.debug.core.pdi.IPDIDebugger;
import org.eclipse.ptp.debug.core.pdi.IPDILocation;
import org.eclipse.ptp.debug.core.pdi.PDIException;
import org.eclipse.ptp.debug.core.pdi.model.IPDIAddressBreakpoint;
import org.eclipse.ptp.debug.core.pdi.model.IPDIExceptionpoint;
import org.eclipse.ptp.debug.core.pdi.model.IPDIFunctionBreakpoint;
import org.eclipse.ptp.debug.core.pdi.model.IPDILineBreakpoint;
import org.eclipse.ptp.debug.core.pdi.model.IPDISignal;
import org.eclipse.ptp.debug.core.pdi.model.IPDIWatchpoint;
import org.eclipse.ptp.debug.core.pdi.model.aif.IAIF;

import com.ibm.debug.daemon.CoreDaemon;
import com.ibm.debug.daemon.DaemonConnectionInfo;
import com.ibm.debug.daemon.DaemonSocketConnection;
import com.ibm.debug.internal.pdt.PICLDebugTarget;
import com.ibm.debug.internal.pdt.model.BreakpointAddedEvent;
import com.ibm.debug.internal.pdt.model.DebuggeeProcess;
import com.ibm.debug.internal.pdt.model.ExpressionAddedEvent;
import com.ibm.debug.internal.pdt.model.Function;
import com.ibm.debug.internal.pdt.model.IModuleEventListener;
import com.ibm.debug.internal.pdt.model.IProcessEventListener;
import com.ibm.debug.internal.pdt.model.Module;
import com.ibm.debug.internal.pdt.model.ModuleAddedEvent;
import com.ibm.debug.internal.pdt.model.ModuleUnloadedEvent;
import com.ibm.debug.internal.pdt.model.PartAddedEvent;
import com.ibm.debug.internal.pdt.model.PartDeletedEvent;
import com.ibm.debug.internal.pdt.model.ProcessDetachedEvent;
import com.ibm.debug.internal.pdt.model.ProcessEndedEvent;
import com.ibm.debug.internal.pdt.model.ProcessPgmError;
import com.ibm.debug.internal.pdt.model.ProcessPgmOutput;
import com.ibm.debug.internal.pdt.model.ProcessStoppedEvent;
import com.ibm.debug.internal.pdt.model.ThreadAddedEvent;
import com.ibm.debug.pdt.launch.PICLLoadInfo;

/**
 * Implementation of {@link IPDIDebugger} for X10.
 * 
 * <p>This is the main class that handles the connection and requests with PDT debugger.
 * 
 * @author egeay
 */
@SuppressWarnings("restriction")
public final class X10PDIDebugger implements IPDIDebugger, IProcessEventListener, IModuleEventListener {
  
  // --- IPDIDebugger's interface methods implementation
  
  public void commandRequest(final BitList tasks, final String command) throws PDIException {
    throw new IllegalStateException();
  }

  public void disconnect(final Observer observer) throws PDIException {
    getDebuggeeProcess().removeEventListener(this);
    stopDebugger();
  }

  public int getErrorAction(final int errorCode) {
    throw new IllegalStateException();
  }

  public void initialize(final ILaunchConfiguration configuration, final List<String> args, 
                         final IProgressMonitor monitor) throws PDIException {
    try {
      this.fServerSocket = new ServerSocket(configuration.getAttribute(Constants.ATTR_PORT, Constants.DEFAULT_PORT));
      this.fState = ESessionState.CONNECTED;

      final Thread accepThread = new Thread(new ListenerRunnable());
      accepThread.setDaemon(true);
      accepThread.start();
    } catch (IOException except) {
      throw new PDIException(null, NLS.bind(DebugMessages.PDID_ServerSocketInitError, except.getMessage()));
    } catch (CoreException except) {
      throw new PDIException(null,"Unable to access default port for server socket creation: " + except.getMessage());
    }
  }
  
  public boolean isConnected(final IProgressMonitor monitor) throws PDIException {
    if (hasConnected(monitor)) {
      // Registers PDT listeners now.
      getDebuggeeProcess().addEventListener(this);
      for (final Module module : getDebuggeeProcess().getModules(true /* hasDebugInfo */)) {
        module.addEventListener(this);
      }
      return true;
    } else {
      disconnect(null);
      return false;
    }
  }

  public void register(final Observer observer) {
  }

  public void startDebugger(final String app, final String path, final String dir, final String[] args) throws PDIException {
    throw new IllegalStateException();
  }

  public void stopDebugger() throws PDIException {
    try {
      this.fState = ESessionState.SHUTDOWN;
      if (this.fServerSocket != null) {
        this.fServerSocket.close();
        this.fServerSocket = null;
      }
      
      if (this.fPDTTarget != null) {
        this.fPDTTarget.disconnect();
        this.fPDTTarget = null;
      }
    } catch (IOException except) {
      throw new PDIException(null, NLS.bind(DebugMessages.PDID_SocketClosingError, except.getMessage()));
    } catch (DebugException except) {
      throw new PDIException(null, NLS.bind(DebugMessages.PDID_PDTDisconnectError, except.getMessage()));
    }
  }
  
  // --- IPDIBreakpointManagement's interface methods implementation

  public void deleteBreakpoint(final BitList tasks, final int bpid) throws PDIException {

  }

  public void setAddressBreakpoint(final BitList tasks, final IPDIAddressBreakpoint bpt) throws PDIException {
    throw new IllegalStateException();
  }

  public void setConditionBreakpoint(final BitList tasks, final int bpid, final String condition) throws PDIException {
    throw new IllegalStateException();
  }

  public void setEnabledBreakpoint(final BitList tasks, final int bpid, final boolean enabled) throws PDIException {
    throw new IllegalStateException();
  }

  public void setExceptionpoint(final BitList tasks, final IPDIExceptionpoint breakPoint) throws PDIException {
    throw new IllegalStateException();
  }

  public void setFunctionBreakpoint(final BitList tasks, final IPDIFunctionBreakpoint breakPoint) throws PDIException {
    try {
      final Function[] fns = getDebuggeeProcess().getFunctions(breakPoint.getLocator().getFunction(), true /* caseSensitive */);
      assert fns.length == 1;
      this.fPDTTarget.createEntryBreakpoint(true, fns[0], null, null, 0, 1, 1, 1, null);
    } catch (Exception except) {
      throw new PDIException(tasks, except.getMessage());
    }
  }

  public void setLineBreakpoint(final BitList tasks, final IPDILineBreakpoint breakPoint) throws PDIException {
    throw new IllegalStateException();
  }

  public void setWatchpoint(final BitList tasks, final IPDIWatchpoint breakPoint) throws PDIException {
    throw new IllegalStateException();
  }
  
  // --- IPDIExecuteManagement's interface methods implementation

  public void restart(final BitList tasks) throws PDIException {
    throw new IllegalStateException();
  }

  public void resume(final BitList tasks, final boolean passSignal) throws PDIException {
    try {
      this.fPDTTarget.resume();
    } catch (DebugException except) {
      throw new PDIException(tasks, except.getMessage());
    }
  }

  public void resume(final BitList tasks, final IPDILocation location) throws PDIException {
    try {
      this.fPDTTarget.resume();
    } catch (DebugException except) {
      throw new PDIException(tasks, NLS.bind("Resume action error: ", except.getMessage()));
    }
  }

  public void resume(final BitList tasks, final IPDISignal signal) throws PDIException {
    System.err.println(new Exception().getStackTrace()[0]);
  }

  public void start(final BitList tasks) throws PDIException {
    System.err.println(new Exception().getStackTrace()[0]);
  }

  public void stepInto(final BitList tasks, final int count) throws PDIException {
    System.err.println(new Exception().getStackTrace()[0]);
  }

  public void stepIntoInstruction(final BitList tasks, final int count) throws PDIException {
    System.err.println(new Exception().getStackTrace()[0]);
  }

  public void stepOver(final BitList tasks, final int count) throws PDIException {
    System.err.println(new Exception().getStackTrace()[0]);
  }

  public void stepOverInstruction(final BitList tasks, final int count) throws PDIException {
    System.err.println(new Exception().getStackTrace()[0]);
  }

  public void stepReturn(final BitList tasks, final int count) throws PDIException {
    System.err.println(new Exception().getStackTrace()[0]);
  }

  public void stepReturn(final BitList tasks, final IAIF aif) throws PDIException {
    System.err.println(new Exception().getStackTrace()[0]);
  }

  public void stepUntil(final BitList tasks, final IPDILocation location) throws PDIException {
    System.err.println(new Exception().getStackTrace()[0]);
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
    System.err.println(new Exception().getStackTrace()[0]);
  }

  public void deleteVariable(final BitList tasks, final String variable) throws PDIException {
    System.err.println(new Exception().getStackTrace()[0]);
  }

  public void evaluateExpression(final BitList tasks, final String expression) throws PDIException {
    System.err.println(new Exception().getStackTrace()[0]);
  }

  public void listArguments(final BitList tasks, final int low, final int high) throws PDIException {
    System.err.println(new Exception().getStackTrace()[0]);
  }

  public void listGlobalVariables(final BitList tasks) throws PDIException {
    System.err.println(new Exception().getStackTrace()[0]);
  }

  public void listLocalVariables(final BitList tasks) throws PDIException {
    System.err.println(new Exception().getStackTrace()[0]);
  }

  public void retrieveAIF(final BitList tasks, final String expr) throws PDIException {
    System.err.println(new Exception().getStackTrace()[0]);
  }

  public void retrievePartialAIF(final BitList tasks, final String expr, final String key, final boolean listChildren, 
                                 final boolean express) throws PDIException {
    System.err.println(new Exception().getStackTrace()[0]);
  }

  public void retrieveVariableType(final BitList tasks, final String variable) throws PDIException {
    System.err.println(new Exception().getStackTrace()[0]);
  }
  
  // --- IPDISignalManagement's interface methods implementation

  public void listSignals(final BitList tasks, final String name) throws PDIException {
    System.err.println(new Exception().getStackTrace()[0]);
  }

  public void retrieveSignalInfo(final BitList tasks, final String arg) throws PDIException {
    System.err.println(new Exception().getStackTrace()[0]);
  }
  
  // --- IPDIStackframeManagement's interface methods implementation

  public void listStackFrames(final BitList tasks, final int low, final int depth) throws PDIException {
    System.err.println(new Exception().getStackTrace()[0]);
  }

  public void setCurrentStackFrame(final BitList tasks, final int level) throws PDIException {
    System.err.println(new Exception().getStackTrace()[0]);
  }
  
  // --- IPDIThreadManagement's interface methods implementation

  public void listInfoThreads(final BitList tasks) throws PDIException {
    System.err.println(new Exception().getStackTrace()[0]);
  }

  public void retrieveStackInfoDepth(final BitList tasks) throws PDIException {
    System.err.println(new Exception().getStackTrace()[0]);
  }

  public void selectThread(final BitList tasks, final int tid) throws PDIException {
    System.err.println(new Exception().getStackTrace()[0]);
  }
  
  // --- IPDIMemoryBlockManagement's interface methods implementation

  public void createDataReadMemory(final BitList tasks, final long offset, final String address, final int wordFormat, 
                                   final int wordSize, final int rows, final int cols, 
                                   final Character asChar) throws PDIException {
    System.err.println(new Exception().getStackTrace()[0]);
  }

  public void createDataWriteMemory(final BitList tasks, final long offset, final String address, final int wordFormat, 
                                    final int wordSize, final String value) throws PDIException {
    System.err.println(new Exception().getStackTrace()[0]);
  }
  
  // --- IProcessEventListener's interface methods implementation
  
  public void breakpointAdded(final BreakpointAddedEvent event) {
    System.out.println("Breakpoint " + event.getBreakpoint().getMarker().toString() + " added");
  }

  public void expressionAdded(final ExpressionAddedEvent event) {
    System.out.println("Expression " + event.getExpression().toString() + " added");
  }

  public void moduleAdded(final ModuleAddedEvent event) {
    System.out.println("Module " + event.getModule().getQualifiedName() + " added");
  }

  public void processDetached(final ProcessDetachedEvent event) {
    System.out.println("Process " + event.getProcess().getLabel() + " detached");
  }

  public void processEnded(final ProcessEndedEvent event) {
    System.out.println("Process " + event.getProcess().getLabel() + " ended");
  }

  public void processStopped(final ProcessStoppedEvent event) {
    System.out.println("Process " + event.getProcess().getLabel() + " stopped");
  }

  public void programError(final ProcessPgmError event) {
    for (final String line : event.getLines()) {
      System.err.println(line);
    }
  }

  public void programOutput(final ProcessPgmOutput event) {
    for (final String line : event.getLines()) {
      System.out.println(line);
    }
  }

  public void threadAdded(final ThreadAddedEvent event) {
    try {
      System.out.println("Thread " + event.getThread().getName() + " added");
    } catch (DebugException except) {
      except.printStackTrace();
    }
  }
  
  // --- IModuleEventListener's interface methods implementation
  
  public void moduleUnloaded(final ModuleUnloadedEvent event) {
    System.out.println("Module " + event.getModule().getQualifiedName() + " unloaded.");
  }

  public void partAdded(final PartAddedEvent event) {
    System.out.println("Part " + event.getPart().getName() + " added");
  }

  public void partDeleted(final PartDeletedEvent event) {
    System.out.println("Part " + event.getPart().getName() + " deleted");
  }
  
  // --- Public services
  
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
  
  private DebuggeeProcess getDebuggeeProcess() {
    return (DebuggeeProcess) this.fPDTTarget.getProcess();
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
        if (this.fState != ESessionState.CONNECTED) {
          return false;
        }
      }
      return true;
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
      final int version = new DataInputStream(socket.getInputStream()).readInt();
      final String[] input = CoreDaemon.readOldStyleStrings(socket.getInputStream(), version);
      
      final PICLLoadInfo loadInfo = createLoadInfo(this.fLaunch.getLaunchConfiguration());
      final DaemonConnectionInfo connectionInfo = new DaemonConnectionInfo(input[0], input[1]);
      connectionInfo.setConnection(new DaemonSocketConnection(socket));
      this.fPDTTarget = new PICLDebugTarget(this.fLaunch, loadInfo, connectionInfo);
      this.fPDTTarget.engineIsWaiting(connectionInfo, true /* socketReuse */);
      
      this.fState = ESessionState.RUNNING;
      if (this.fWaiting) {
        this.fRunningCondition.signal();
        this.fWaiting = false;
      }
    } catch (Exception except) {
      except.printStackTrace();
    } finally {
      this.fWaitLock.unlock();
    }
  }
  
  // --- Private classes
  
  private class ListenerRunnable implements Runnable {

    // --- Interface methods implementation
    
    public void run() {
      while (X10PDIDebugger.this.fState == ESessionState.CONNECTED) {
        try {
          if (! X10PDIDebugger.this.fServerSocket.isClosed()) {
            final Socket socket = X10PDIDebugger.this.fServerSocket.accept();
            if (socket != null) {
              new Thread(new ProcessRunnable(socket)).start();
            }
          }
        } catch (IOException except) {
          DebugCore.log(IStatus.ERROR, DebugMessages.PDID_SocketListeningError, except);
        }
      }
      
    }
    
  }
  
  private class ProcessRunnable implements Runnable {
    
    ProcessRunnable(final Socket socket) {
      this.fSocket = socket;
    }
    
    // --- Interface methods implementation

    public void run() {
      process(this.fSocket);
    }
    
    // --- Fields
    
    private final Socket fSocket;
    
  }
  
  private enum ESessionState {
    CONNECTED, RUNNING, SHUTDOWN
  }
  
  // --- Fields
  
  private ESessionState fState = ESessionState.SHUTDOWN;
  
  private ServerSocket fServerSocket;
  
  private IPLaunch fLaunch;  
  
  private boolean fWaiting;
  
  private PICLDebugTarget fPDTTarget;
  
  private final ReentrantLock fWaitLock = new ReentrantLock();
  
  private final Condition fRunningCondition = this.fWaitLock.newCondition();
  
  private final Condition fLaunchCondition = this.fWaitLock.newCondition();
  
  private static final String EMPTY_STRING = ""; //$NON-NLS-1$
  
}
