/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug.pdi;

import java.util.List;
import java.util.Observer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.ptp.core.util.BitList;
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

/**
 * 
 * @author egeay
 */
public final class X10PDIDebugTranslator implements IPDIDebugger {

  // --- Interface methods implementation
  
  public void commandRequest(final BitList tasks, final String command) throws PDIException {
  }

  public void disconnect(final Observer observer) throws PDIException {
  }

  public int getErrorAction(final int errorCode) {
    return 0;
  }

  public void initialize(final ILaunchConfiguration configuration, final List<String> args, 
                         final IProgressMonitor monitor) throws PDIException {
  }

  public boolean isConnected(final IProgressMonitor monitor) throws PDIException {
    return false;
  }

  public void register(final Observer observer) {
  }

  public void startDebugger(final String app, final String path, final String dir, final String[] args) throws PDIException {
  }

  public void stopDebugger() throws PDIException {
  }

  public void deleteBreakpoint(final BitList tasks, final int bpid) throws PDIException {
  }

  public void setAddressBreakpoint(final BitList tasks, final IPDIAddressBreakpoint bpt) throws PDIException {
  }

  public void setConditionBreakpoint(final BitList tasks, final int bpid, final String condition) throws PDIException {
  }

  public void setEnabledBreakpoint(final BitList tasks, final int bpid, final boolean enabled) throws PDIException {
  }

  public void setExceptionpoint(final BitList tasks, final IPDIExceptionpoint breakPoint) throws PDIException {
  }

  public void setFunctionBreakpoint(final BitList tasks, final IPDIFunctionBreakpoint breakPoint) throws PDIException {
  }

  public void setLineBreakpoint(final BitList tasks, final IPDILineBreakpoint breakPoint) throws PDIException {
  }

  public void setWatchpoint(final BitList tasks, final IPDIWatchpoint breakPoint) throws PDIException {
  }

  public void restart(final BitList tasks) throws PDIException {
  }

  public void resume(final BitList tasks, final boolean passSignal) throws PDIException {
  }

  public void resume(final BitList tasks, final IPDILocation location) throws PDIException {
  }

  public void resume(final BitList tasks, final IPDISignal signal) throws PDIException {
  }

  public void start(final BitList tasks) throws PDIException {
  }

  public void stepInto(final BitList tasks, final int count) throws PDIException {
  }

  public void stepIntoInstruction(final BitList tasks, final int count) throws PDIException {
  }

  public void stepOver(final BitList tasks, final int count) throws PDIException {
  }

  public void stepOverInstruction(final BitList tasks, final int count) throws PDIException {
  }

  public void stepReturn(final BitList tasks, final int count) throws PDIException {
  }

  public void stepReturn(final BitList tasks, final IAIF aif) throws PDIException {
  }

  public void stepUntil(final BitList tasks, final IPDILocation location) throws PDIException {
  }

  public void suspend(final BitList tasks) throws PDIException {
  }

  public void terminate(final BitList tasks) throws PDIException {
  }

  public void dataEvaluateExpression(final BitList tasks, final String expression) throws PDIException {
  }

  public void deleteVariable(final BitList tasks, final String variable) throws PDIException {
  }

  public void evaluateExpression(final BitList tasks, final String expression) throws PDIException {
  }

  public void listArguments(final BitList tasks, final int low, final int high) throws PDIException {
  }

  public void listGlobalVariables(final BitList tasks) throws PDIException {
  }

  public void listLocalVariables(final BitList tasks) throws PDIException {
  }

  public void retrieveAIF(final BitList tasks, final String expr) throws PDIException {
  }

  public void retrievePartialAIF(final BitList tasks, final String expr, final String key, final boolean listChildren, 
                                 final boolean express) throws PDIException {
  }

  public void retrieveVariableType(final BitList tasks, final String variable) throws PDIException {
  }

  public void listSignals(final BitList tasks, final String name) throws PDIException {
  }

  public void retrieveSignalInfo(final BitList tasks, final String arg) throws PDIException {
  }

  public void listStackFrames(final BitList tasks, final int low, final int depth) throws PDIException {
  }

  public void setCurrentStackFrame(final BitList tasks, final int level) throws PDIException {
  }

  public void listInfoThreads(final BitList tasks) throws PDIException {
  }

  public void retrieveStackInfoDepth(final BitList tasks) throws PDIException {
  }

  public void selectThread(final BitList tasks, final int tid) throws PDIException {
  }

  public void createDataReadMemory(final BitList tasks, final long offset, final String address, final int wordFormat, 
                                   final int wordSize, final int rows, final int cols, 
                                   final Character asChar) throws PDIException {
  }

  public void createDataWriteMemory(final BitList tasks, final long offset, final String address, final int wordFormat, 
                                    final int wordSize, final String value) throws PDIException {
  }

}
