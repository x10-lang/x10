/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug;

import com.ibm.debug.internal.pdt.PICLDebugTarget;
import com.ibm.debug.internal.pdt.model.DebuggeeProcess;
import com.ibm.debug.internal.pdt.model.IDebugEngineEventListener;

/**
 * Encapsulates a {@link PICLDebugTarget} instance related to a given process launch.
 * 
 * @author egeay
 */
@SuppressWarnings("restriction")
public interface IPDTTarget extends IDebugEngineEventListener {
  
  public DebuggeeProcess getProcess();
  
  public PICLDebugTarget getTarget();

}
