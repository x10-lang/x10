/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug.core;


import org.eclipse.imp.x10dt.ui.cpp.debug.pdi.X10PDIDebugger;
import org.eclipse.ptp.core.util.BitList;

import com.ibm.debug.internal.pdt.model.DebuggeeProcess;
import com.ibm.debug.internal.pdt.model.Location;

/**
 * Responsible for translating X10 data to their C++ counterparts according to the context, and vice-versa.
 * 
 * @author egeay
 * @author igor
 */
public interface IDebuggerTranslator {

	void init(DebuggeeProcess p);

	Location getCppLocation(BitList tasks, String file, int lineNumber);

	int getX10Line(Location cppLocation);

	String getX10File(Location cppLocation);

	void setDebugger(X10PDIDebugger fPDIDebugger);

}
