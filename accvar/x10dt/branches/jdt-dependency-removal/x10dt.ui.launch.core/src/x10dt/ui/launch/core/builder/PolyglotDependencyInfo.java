package x10dt.ui.launch.core.builder;
/*******************************************************************************
* Copyright (c) 2008,2009 IBM Corporation.
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

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.imp.builder.DependencyInfo;

import polyglot.types.ClassType;
import polyglot.types.Type;
import polyglot.types.Types;
import polyglot.util.Position;

/**
 * A compilation unit-level dependency tracking manager that speaks in terms of Polyglot Type objects.
 * @author rfuhrer
 */
public class PolyglotDependencyInfo extends DependencyInfo {
    public PolyglotDependencyInfo(IProject project) {
        super(project);
    }

    //  don't forget the case when resource is outside the WS
    protected String typeToPath(Type type) {
    	ClassType classType = null;
    	if (type.isClass()){
    		classType= (ClassType) Types.baseType(type);
    	} else {
    		return null;
    	}
    	Position pos= classType.position();
    	if (pos == null) {
    		return null;
    	}
    	return getPath(pos);  
    }
    
    protected String getPath(Position pos){
    	String filePath= pos.file().replace(File.separatorChar, '/');
    	if (filePath.contains("x10.jar"))
    		return null;
    	String result=null;
        String wsPath= fWorkspacePath;
        if(filePath.startsWith(wsPath)){
        	result = filePath.substring(fWorkspacePath.length());
        }else{
        	result=filePath;
        }
    	return result;
    }

    public void addDependency(Type fromType, Type uponType) {
        String fromPath= typeToPath(fromType);
        String uponPath= typeToPath(uponType);
        if (fromPath == null || uponPath == null) return;
        // PORT1.7  ...
        if(!(uponPath.contains(".zip") || uponPath.contains(".jar")) && !(uponPath.equals(fromPath))) {
        	addDependency(fromPath, uponPath);
        }
    }

    public void clearDependenciesOf(Type type) {
    	String path = typeToPath(type);
    	if (path == null) return;
        clearDependenciesOf(path);
    }

    public Set getDependentsOf(Type type) {
    	String path = typeToPath(type);
    	if (path == null) return new HashSet();
        return getDependentsOf(path);
    }
}

