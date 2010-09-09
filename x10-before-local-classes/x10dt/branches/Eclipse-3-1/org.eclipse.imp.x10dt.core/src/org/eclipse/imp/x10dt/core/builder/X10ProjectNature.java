package org.eclipse.imp.x10dt.core.builder;

import org.eclipse.core.resources.IProject;
import org.eclipse.imp.builder.ProjectNatureBase;
import org.eclipse.imp.runtime.IPluginLog;
import org.eclipse.imp.x10dt.core.X10Plugin;

import com.ibm.watson.smapifier.builder.SmapiProjectNature;

public class X10ProjectNature extends ProjectNatureBase {
    public static final String k_natureID= X10Plugin.kPluginID + ".x10nature";

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
	return X10Plugin.getInstance();
    }
}
