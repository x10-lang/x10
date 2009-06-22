package com.ibm.watson.safari.x10;

import java.util.HashMap;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class X10ProjectNature implements IProjectNature {
    public static final String	k_natureID = X10Plugin.kPluginID + ".x10nature";

    private IProject fProject;

    public static void addToProject(IProject project) {
	X10Plugin.refreshPrefs();
	X10Plugin.maybeWriteInfoMsg("Attempting to add X10 nature...", k_natureID);

	try {
	    IProjectDescription	description= project.getDescription();
	    String[] natures= description.getNatureIds();
	    String[] newNatures= new String[natures.length + 1];

	    System.arraycopy(natures, 0, newNatures, 0, natures.length);
	    newNatures[natures.length]= k_natureID;

	    description.setNatureIds(newNatures);
	    project.setDescription(description, null);

	    X10Plugin.maybeWriteInfoMsg("Added X10 nature.", k_natureID);
	} catch (CoreException e) {
	    // Something went wrong
	    X10Plugin.writeErrorMsg("Failed to add X10 nature: " + e.getMessage(), k_natureID);
	}
    }

    public void configure() throws CoreException {
	IProjectDescription desc= getProject().getDescription();
	ICommand[] cmds= desc.getBuildSpec();

	// Check: is the builder already in this project?
	for(int i=0; i < cmds.length; i++) {
	    if (cmds[i].getBuilderName().equals(X10Builder.BUILDER_ID))
		return; // relevant command is already in there...
	}

	// Since this builder generates Java source, it needs to run *before*
	// the Java builder.  So, find the right spot (in front of the Java builder)
	// to put our builder.
	int where= 0;
	for(; where < cmds.length; where++) {
	    if (cmds[where].getBuilderName().equals("org.eclipse.jdt.core.javabuilder"))
		break; // relevant command is already in there...
	}

	ICommand compilerCmd= desc.newCommand();

	compilerCmd.setBuilderName(X10Builder.BUILDER_ID);
	compilerCmd.setArguments(new HashMap());

	ICommand[] newCmds= new ICommand[cmds.length+1];

	System.arraycopy(cmds, 0, newCmds, 0, where);
	newCmds[where] = compilerCmd;
	System.arraycopy(cmds, where, newCmds, where+1, cmds.length-where);
	desc.setBuildSpec(newCmds);
	getProject().setDescription(desc, null);
    }

    public void deconfigure() throws CoreException {
	IProjectDescription desc= getProject().getDescription();
	ICommand[] cmds= desc.getBuildSpec();

	for(int i=0; i < cmds.length; ++i) {
	    if (cmds[i].getBuilderName().equals(X10Builder.BUILDER_ID)) {
		ICommand[] newCmds= new ICommand[cmds.length - 1];

		System.arraycopy(cmds, 0, newCmds, 0, i);
		System.arraycopy(cmds, i + 1, newCmds, i, cmds.length - i - 1);
		desc.setBuildSpec(newCmds);
		getProject().setDescription(desc, null);
		return;
	    }
	}
    }

    public IProject getProject() {
	return fProject;
    }

    public void setProject(IProject project) {
	fProject= project;
    }
}
