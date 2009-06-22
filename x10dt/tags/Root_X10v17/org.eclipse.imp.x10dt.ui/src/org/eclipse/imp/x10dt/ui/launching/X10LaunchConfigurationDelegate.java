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

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.imp.runtime.RuntimePlugin;
import org.eclipse.imp.x10dt.core.X10PreferenceConstants;
import org.eclipse.imp.x10dt.ui.X10UIPlugin;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class X10LaunchConfigurationDelegate extends AbstractJavaLaunchConfigurationDelegate {
    private final static String x10RuntimeType= "x10.lang.Runtime";

    @Override
    public IVMInstall getVMInstall(final ILaunchConfiguration configuration) throws CoreException {
        IVMInstall vm= super.getVMInstall(configuration);

        if (vm instanceof IVMInstall2) {
	    IVMInstall2 vm2= (IVMInstall2) vm;
            if (!vm2.getJavaVersion().startsWith("1.5") && !vm2.getJavaVersion().startsWith("1.6")) {
    	    Display.getDefault().asyncExec(new Runnable() {
		public void run() {
		    Shell shell= X10UIPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow().getShell();
		    String projectName= "";
		    try {
			projectName= configuration.getAttribute("org.eclipse.jdt.launching.PROJECT_ATTR", "");
		    } catch (CoreException e) {
		    }
		    MessageDialog.openError(shell, "Launch Error", "Project '" + projectName + "' must use a Java 5.0 runtime.");
		}
	    });
        	return null;
            }
        }
        return vm;
    }

    /**
     * Returns the X10 runtime arguments specified by the given launch configuration,
     * as a string. The returned string is empty if no program arguments are specified.
     * 
     * @param configuration launch configuration
     * @return the runtime arguments specified by the given launch
     *         configuration, possibly an empty string
     * @exception CoreException if unable to retrieve the attribute
     */
    public String getRuntimeArguments(ILaunchConfiguration configuration) throws CoreException {
	String arguments= configuration.getAttribute(X10LaunchConfigAttributes.X10RuntimeArgumentsID, ""); //$NON-NLS-1$
	IPreferenceStore prefStore = RuntimePlugin.getInstance().getPreferenceStore();
	if (prefStore.contains(X10PreferenceConstants.P_NUM_PLACES)) {
		String numPlacesArg = " -NUMBER_OF_LOCAL_PLACES="+prefStore.getInt(X10PreferenceConstants.P_NUM_PLACES);
		arguments += numPlacesArg;
	}
		

	return VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(arguments);
    }

    public void launch(final ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
	// boolean debug= mode.equals(ILaunchManager.DEBUG_MODE);

	if (monitor == null) {
	    monitor= new NullProgressMonitor();
	}

	monitor.beginTask(MessageFormat.format("{0}...", new Object[] { configuration.getName() }), 3); //$NON-NLS-1$
	// check for cancellation
	if (monitor.isCanceled()) {
	    return;
	}

	monitor.subTask("Verifying launch attributes");

	String mainTypeName= verifyMainTypeName(configuration);
	IVMRunner runner= getVMRunner(configuration, mode);

	File workingDir= verifyWorkingDirectory(configuration);
	String workingDirName= null;
	if (workingDir != null) {
	    workingDirName= workingDir.getAbsolutePath();
	}

	// Environment variables
	String[] envp= getEnvironment(configuration);

	// Program & VM args
	String pgmArgs= getProgramArguments(configuration);
	String vmArgs= getVMArguments(configuration);
	String rtArgs= getRuntimeArguments(configuration);
	X10ExecutionArguments execArgs= new X10ExecutionArguments(vmArgs, rtArgs, pgmArgs);

	// VM-specific attributes
	Map vmAttributesMap= getVMSpecificAttributesMap(configuration);

	// Classpath
	String[] classpath= getClasspath(configuration);

	// Place the user-specified X10 runtime location in front of whatever we
	// obtain from the project's classpath.
	String x10RuntimeLoc= configuration.getAttribute(X10LaunchConfigAttributes.X10RuntimeAttributeID, "");

	// TODO Allow an empty X10 runtime location field.
	// In that case, fall back to the runtime specified in the project classpath.
	if (x10RuntimeLoc.length() == 0) {
	    Display.getDefault().asyncExec(new Runnable() {
		public void run() {
		    Shell shell= X10UIPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow().getShell();
		    MessageDialog.openError(shell, "Please specify the X10 Runtime location", "The location of the X10 Runtime is unset in the launch configuration '" + configuration.getName() + "'.");
		}
	    });
	    return;
	}

	String[] classPathWithExplicitRuntime= new String[classpath.length+1];

	classPathWithExplicitRuntime[0]= x10RuntimeLoc;
	System.arraycopy(classpath, 0, classPathWithExplicitRuntime, 1, classpath.length);

	// Create VM config
	VMRunnerConfiguration runConfig= new VMRunnerConfiguration(x10RuntimeType, classPathWithExplicitRuntime);
	String[] explicitRuntimeArgsArray= execArgs.getRuntimeArgumentsArray();
	String[] explicitProgArgsArray= execArgs.getProgramArgumentsArray();
	String[] realArgsArray= new String[explicitProgArgsArray.length + explicitRuntimeArgsArray.length + 1];

	System.arraycopy(explicitRuntimeArgsArray, 0, realArgsArray, 0, explicitRuntimeArgsArray.length);
	realArgsArray[explicitRuntimeArgsArray.length]= mainTypeName;
	System.arraycopy(explicitProgArgsArray, 0, realArgsArray, explicitRuntimeArgsArray.length+1, explicitProgArgsArray.length);

	String commonLoc= x10RuntimeLoc.substring(0, x10RuntimeLoc.lastIndexOf(File.separator)) + File.separator + "x10.common";

	String[] x10ExtraVMArgs= {
		"-Djava.library.path=" + commonLoc + "\\lib",
		"-ea"
	};

	String[] explicitVMArgsArray= execArgs.getVMArgumentsArray();
	String[] realVMArgsArray= new String[explicitVMArgsArray.length + x10ExtraVMArgs.length];

	System.arraycopy(x10ExtraVMArgs, 0, realVMArgsArray, 0, x10ExtraVMArgs.length);
	System.arraycopy(explicitVMArgsArray, 0, realVMArgsArray, x10ExtraVMArgs.length, explicitVMArgsArray.length);

	runConfig.setProgramArguments(realArgsArray);
	runConfig.setEnvironment(envp);
	runConfig.setVMArguments(explicitVMArgsArray);
	runConfig.setWorkingDirectory(workingDirName);
	runConfig.setVMSpecificAttributesMap(vmAttributesMap);

	// Bootpath
	runConfig.setBootClassPath(getBootpath(configuration));

	// check for cancellation
	if (monitor.isCanceled()) {
	    return;
	}

	// stop in main
	prepareStopInMain(configuration);

	// done the verification phase
	monitor.worked(1);

	monitor.subTask("Creating source locator");
	// set the default source locator if required
	setDefaultSourceLocator(launch, configuration);
	monitor.worked(1);

	// Launch the configuration - 1 unit of work
	runner.run(runConfig, launch, monitor);

	// check for cancellation
	if (monitor.isCanceled()) {
	    return;
	}

	monitor.done();
    }
}
