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

/**
 * These are keys to be used in accessing attributes of X10 launch configurations
 * (e.g., x10.runtime is NOT the bundle ID)
 *
 */
public interface X10LaunchConfigAttributes {

    public static final String X10RuntimeAttributeID= "x10.runtime"; 

    public static final String X10RuntimeArgumentsID= "x10.runtime.arguments";
}
