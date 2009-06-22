package com.ibm.watson.safari.x10;

import java.util.HashMap;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.uide.core.ProjectNatureBase;
import org.eclipse.uide.runtime.IPluginLog;

public class X10ProjectNature extends ProjectNatureBase {
    public static final String k_natureID= X10Plugin.kPluginID + ".x10nature";

    public String getNatureID() {
	return k_natureID;
    }

    public String getBuilderID() {
	return X10Builder.BUILDER_ID;
    }

    protected void refreshPrefs() {}

    protected String getDownstreamBuilderID() {
	return "org.eclipse.jdt.core.javabuilder";
    }

    public IPluginLog getLog() {
	return X10Plugin.getInstance();
    }
}
