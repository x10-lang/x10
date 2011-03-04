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

import java.io.File;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.imp.builder.DependencyInfo;

import polyglot.types.Type;

/**
 * A compilation unit-level dependency tracking manager that speaks in terms of Polyglot Type objects.
 * @author rfuhrer
 */
public class PolyglotDependencyInfo extends DependencyInfo {
    public PolyglotDependencyInfo(IProject project) {
        super(project);
    }

    protected String typeToPath(Type type) {
        final String filePath= type.position().file().replace(File.separatorChar, '/');

//        assert(filePath.startsWith(wsPath));
        return filePath.substring(fWorkspacePath.length());
    }

    public void addDependency(Type fromType, Type uponType) {
        String fromPath= typeToPath(fromType);
        String uponPath= typeToPath(uponType);

        addDependency(fromPath, uponPath);
    }

    public void clearDependenciesOf(Type type) {
        clearDependenciesOf(typeToPath(type));
    }

    public Set getDependentsOf(Type type) {
        return getDependentsOf(typeToPath(type));
    }
}
