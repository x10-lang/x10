package com.ibm.watson.safari.x10.builder;

import org.eclipse.uide.core.ProjectNatureBase;
import org.eclipse.uide.runtime.IPluginLog;
import com.ibm.watson.safari.x10.X10Plugin;

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
