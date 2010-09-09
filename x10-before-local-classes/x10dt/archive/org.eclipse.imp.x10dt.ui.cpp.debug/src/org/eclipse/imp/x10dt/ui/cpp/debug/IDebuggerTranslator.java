/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug;

import org.eclipse.core.resources.IProject;

import com.ibm.debug.internal.pdt.model.DebuggeeProcess;
import com.ibm.debug.internal.pdt.model.Location;
import com.ibm.debug.internal.pdt.model.StackFrame;

/**
 * Responsible for translating X10 data to their C++ counterparts according to the context, and vice-versa.
 * 
 * @author egeay
 */
@SuppressWarnings("restriction")
public interface IDebuggerTranslator {
  
  public Location getCppLocation(final DebuggeeProcess process, final String x10File, final int x10LineNumber);
  
  /**
   * Return a descriptor for a struct type.
   * TBFI = to be filled in.
   * The descriptor for a Rail is { X(type), elemType, length(TBFI) }.
   * The descriptor for a String is { X(type), content(TBFI), length(TBFI) }.
   * The descriptor for any other class is { X(type), num_interfaces, [fname, ftype]... }.
   */
  public String[] getStructDescriptor(final String type);
  
  public String getX10File(final DebuggeeProcess process, final Location cppLocation);
  
  public String getX10Function(final DebuggeeProcess process, final String cppFunction, final Location cppLocation);
  
  public int getX10Line(final DebuggeeProcess process, final Location cppLocation);
  
  public String getClosureVariableType(DebuggeeProcess process, StackFrame frame, Location location, String function, String name);
  
  public String[] getClosureVars(DebuggeeProcess process, StackFrame frame, Location location, String function);

  public void init(IProject fProject);

}
