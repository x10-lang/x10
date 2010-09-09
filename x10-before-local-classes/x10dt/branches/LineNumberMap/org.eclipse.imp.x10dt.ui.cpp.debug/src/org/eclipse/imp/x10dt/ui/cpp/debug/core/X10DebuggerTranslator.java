/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug.core;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.imp.runtime.RuntimePlugin;
import org.eclipse.imp.x10dt.ui.cpp.debug.pdi.X10PDIDebugger;
import org.eclipse.ptp.core.util.BitList;
import org.eclipse.ptp.debug.core.pdi.PDIException;
import org.eclipse.ptp.debug.core.pdi.model.IPDILineBreakpoint;

import polyglot.ext.x10cpp.debug.LineNumberMap;

import com.ibm.debug.internal.epdc.ERepCommandLog;
import com.ibm.debug.internal.epdc.IEPDCConstants;
import com.ibm.debug.internal.pdt.model.Address;
import com.ibm.debug.internal.pdt.model.DebuggeeProcess;
import com.ibm.debug.internal.pdt.model.DebuggeeThread;
import com.ibm.debug.internal.pdt.model.EngineRequestException;
import com.ibm.debug.internal.pdt.model.ExprNodeBase;
import com.ibm.debug.internal.pdt.model.ExpressionBase;
import com.ibm.debug.internal.pdt.model.GlobalVariable;
import com.ibm.debug.internal.pdt.model.Location;
import com.ibm.debug.internal.pdt.model.MemoryException;
import com.ibm.debug.internal.pdt.model.ViewFile;


final class X10DebuggerTranslator implements IDebuggerTranslator {

	private HashMap<String, LineNumberMap> fX10ToCppMap = LineNumberMap.initMap();
	private HashMap<String, LineNumberMap> fCppToX10Map = LineNumberMap.initMap();
	private X10PDIDebugger fPDIDebugger;

	public X10DebuggerTranslator() {
	}

	public void setDebugger(X10PDIDebugger fPDIDebugger) {
		this.fPDIDebugger = fPDIDebugger;
	}

	public void init(DebuggeeProcess p) {
		GlobalVariable[] globals = p.getDebugEngine().getGlobalVariables();
		for (GlobalVariable v : globals) {
			//System.out.println("Got var: "+v.getName()+" "+v.getExpression());
			if (v.getName().contains("LNMAP_")) {
				System.out.println("\tGOT MAP: "+v.getName()+" "+v.getExpression());
				String val = null;
				try {
					DebuggeeThread t = p.getStoppingThread();
					// The code below doesn't create the right kind of monitor.
					//ExpressionBase b = t.evaluateExpression(t.getLocation(t.getViewInformation()), v.getExpression(), 1, 1000000);
					ExpressionBase b = p.monitorExpression(t.getLocation(t.getViewInformation()).getEStdView(), t.getId(), v.getExpression(), IEPDCConstants.MonEnable, IEPDCConstants.MonTypeProgram, null, null, null, null);
					// TODO
					//Address addr = p.convertToAddress(v.getExpression(), t.getLocation(t.getViewInformation()), t);
					if (b != null) {
						ExprNodeBase n = b.getRootNode();
						if (n != null)
							val = n.getValueString();
						b.remove();
					}
				} catch (EngineRequestException e) {
					RuntimePlugin.getInstance().logException(e.getMessage(), e);
				//} catch (MemoryException e) {
				//	RuntimePlugin.getInstance().logException(e.getMessage(), e);
				}
				System.out.println("\tValue = '"+val+"'");
				if (val != null) {
					assert (val.startsWith("\"") && val.endsWith("\""));
					String f = v.getName().substring("LNMAP_".length());
					if (f.startsWith("_"))
						f = f.substring(1);
					String e = f.substring(f.lastIndexOf('_')+1);
					f = f.substring(0, f.lastIndexOf('_')).replace('_', '/') + "." + e;
					LineNumberMap c2xFileMap = LineNumberMap.importMap(f, val.substring(1, val.length()-1));
					HashMap<String, LineNumberMap> c2xMap = new HashMap<String, LineNumberMap>();
					c2xMap.put(f, c2xFileMap);
					LineNumberMap.mergeMap(fCppToX10Map, c2xMap);
					HashMap<String, LineNumberMap> x2cMap = c2xFileMap.invert();
					LineNumberMap.mergeMap(fX10ToCppMap, x2cMap);
					System.out.println("m="+fCppToX10Map);
					System.out.println("im="+fX10ToCppMap);
				}
			}
		}
	}

	public String getX10File(Location cppLocation) {
		try {
			String cppFile = cppLocation.getViewFile().getFile().getLocation().toString();
			int cppLineNumber = cppLocation.getLineNumber();
			LineNumberMap cppLineToX10LineMap = fCppToX10Map.get(cppFile);
			if (cppLineToX10LineMap == null)
				return null;
			String x10File = cppLineToX10LineMap.getSourceFile(cppLineNumber);
			return x10File;
		} catch (CoreException e) {
			return null;
		}
	}

	public int getX10Line(Location cppLocation) {
		try {
			String cppFile = cppLocation.getViewFile().getFile().getLocation().toString();
			int cppLineNumber = cppLocation.getLineNumber();
			LineNumberMap cppLineToX10LineMap = fCppToX10Map.get(cppFile);
			if (cppLineToX10LineMap == null)
				return -1;
			int x10LineNumber = cppLineToX10LineMap.getSourceLine(cppLineNumber);
			return x10LineNumber;
		} catch (CoreException e) {
			return -1;
		}
	}

	public Location getCppLocation(BitList tasks, String x10File, int x10LineNumber) {
		if (!x10File.startsWith("file:/"))
			x10File = "file:/"+x10File;
		LineNumberMap x10LineToCppLineMap = fX10ToCppMap.get(x10File);
		if (x10LineToCppLineMap == null)
			return null;
		String cppFile = x10LineToCppLineMap.getSourceFile(x10LineNumber);
		int cppLineNumber = x10LineToCppLineMap.getSourceLine(x10LineNumber);
		if (cppFile == null || cppLineNumber == -1)
			return null;
		try {
			final ViewFile viewFile = fPDIDebugger.searchViewFile(tasks, cppFile);
			return new Location(viewFile, cppLineNumber);
		} catch (PDIException e) {
			return null;
		}
	}
}
