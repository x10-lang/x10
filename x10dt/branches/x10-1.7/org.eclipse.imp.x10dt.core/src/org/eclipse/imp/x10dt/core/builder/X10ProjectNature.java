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
package org.eclipse.imp.x10dt.core.builder;

import org.eclipse.core.resources.IProject;
import org.eclipse.imp.builder.ProjectNatureBase;
import org.eclipse.imp.runtime.IPluginLog;
import org.eclipse.imp.x10dt.core.X10DTCorePlugin;

import org.eclipse.imp.smapifier.builder.SmapiProjectNature;

public class X10ProjectNature extends ProjectNatureBase {
    public static final String k_natureID= X10DTCorePlugin.kPluginID + ".x10nature";

    public String getNatureID() {
        return k_natureID;
    }

    public String getBuilderID() {
        return X10Builder.BUILDER_ID;
    }

    public void addToProject(IProject project) {
        super.addToProject(project);
        new SmapiProjectNature("x10").addToProject(project);
    }

    protected void refreshPrefs() {}

    protected String getDownstreamBuilderID() {
        return "org.eclipse.jdt.core.javabuilder";
    }

    public IPluginLog getLog() {
        return X10DTCorePlugin.getInstance();
    }
}
