/*******************************************************************************
* Copyright (c) 2008 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation

*******************************************************************************/

/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.x10dt.ui.launching;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jdt.launching.ExecutionArguments;

/**
 * Like the base Java ExecutionArguments class, but adds support for X10 runtime arguments.
 * @author rfuhrer
 */
public class X10ExecutionArguments extends ExecutionArguments {
    private final String fRuntimeArgs;

    public X10ExecutionArguments(String vmArgs, String runtimeArgs, String programArgs) {
	super(vmArgs, programArgs);
	fRuntimeArgs= runtimeArgs;
    }

    public String[] getRuntimeArgumentsArray() {
        return DebugPlugin.parseArguments(fRuntimeArgs);
    }
    public String toString() {
    	String str="vmArgs: ["+this.getVMArguments().toString()+"] rtArgs: ["+fRuntimeArgs+"] programArgs= ["+this.getProgramArguments()+"]";
    	return str;
    }
}
