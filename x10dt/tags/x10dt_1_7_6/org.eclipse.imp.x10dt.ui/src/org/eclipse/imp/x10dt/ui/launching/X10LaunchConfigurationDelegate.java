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

package org.eclipse.imp.x10dt.ui.launching;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.imp.runtime.RuntimePlugin;
import org.eclipse.imp.x10dt.core.X10DTCorePlugin;
import org.eclipse.imp.x10dt.core.X10PreferenceConstants;
import org.eclipse.imp.x10dt.core.X10Util;
import org.eclipse.imp.x10dt.ui.X10DTUIPlugin;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * TODO need to reconcile changes made to X10LaunchConfigurationDelegate in x10dt.ui for X10 1.7
 * 
 * @author beth
 *
 */
public class X10LaunchConfigurationDelegate extends AbstractJavaLaunchConfigurationDelegate {
	/** Suffix that is appended to user class to get main x10 executable class - e.g. to make Hello$Main */
	static final String USER_MAIN_SUFFIX="$Main";

    @Override
	public IVMInstall getVMInstall(final ILaunchConfiguration configuration) throws CoreException {
		IVMInstall vm = super.getVMInstall(configuration);

		if (vm instanceof IVMInstall2) {
			IVMInstall2 vm2 = (IVMInstall2) vm;
			if (!vm2.getJavaVersion().startsWith("1.5") && !vm2.getJavaVersion().startsWith("1.6")) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						Shell shell = X10DTUIPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow().getShell();
						String projectName = "";
						try {
							projectName = configuration.getAttribute("org.eclipse.jdt.launching.PROJECT_ATTR", "");
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
	 * Returns the X10 runtime arguments specified by the given launch
	 * configuration, as a string. The returned string is empty if no program
	 * arguments are specified.
	 * 
	 * @param configuration
	 *            launch configuration
	 * @return the runtime arguments specified by the given launch
	 *         configuration, possibly an empty string
	 * @exception CoreException
	 *                if unable to retrieve the attribute
	 */
    public String getRuntimeArguments(ILaunchConfiguration configuration) throws CoreException {
		String arguments = configuration.getAttribute(X10LaunchConfigAttributes.X10RuntimeArgumentsID, ""); //$NON-NLS-1$
		IPreferenceStore prefStore = RuntimePlugin.getInstance().getPreferenceStore();
		if (prefStore.contains(X10PreferenceConstants.P_NUM_PLACES)) {
			String numPlacesArg = " -NUMBER_OF_LOCAL_PLACES=" + prefStore.getInt(X10PreferenceConstants.P_NUM_PLACES);
			arguments += numPlacesArg;
		}
		

	return VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(arguments);
    }

    public void launch(final ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		boolean debug= mode.equals(ILaunchManager.DEBUG_MODE);

		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		monitor.beginTask(MessageFormat.format("{0}...", new Object[] { configuration.getName() }), 3); //$NON-NLS-1$
		// check for cancellation
		if (monitor.isCanceled()) {
			return;
		}

		monitor.subTask("Verifying launch attributes");

		String mainTypeName = verifyMainTypeName(configuration);
		if(debug)System.out.println("mainTypeName: "+mainTypeName);
		IVMRunner runner = getVMRunner(configuration, mode);

		File workingDir = verifyWorkingDirectory(configuration);
		String workingDirName = null;
		if (workingDir != null) {
			workingDirName = workingDir.getAbsolutePath();
		}

		// Environment variables
		String[] envp = getEnvironment(configuration);

		// Program & VM args
		String pgmArgs = getProgramArguments(configuration);
		String vmArgs = getVMArguments(configuration);
		String rtArgs = getRuntimeArguments(configuration);
		X10ExecutionArguments execArgs = new X10ExecutionArguments(vmArgs, rtArgs, pgmArgs);

		// VM-specific attributes
		Map vmAttributesMap = getVMSpecificAttributesMap(configuration);

		// Classpath
		String[] classpath = getClasspath(configuration);

		// Place the user-specified X10 runtime location in front of whatever we
		// obtain from the project's classpath.
		String x10RuntimeLoc = configuration.getAttribute(X10LaunchConfigAttributes.X10RuntimeAttributeID, "");

		// BRT assure we have a runtime
		// validators for launch config page shd ensure that this field is not
		// empty. can't dismiss dialog if it's empty
		if (x10RuntimeLoc.length() == 0) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					Shell shell = X10DTUIPlugin.getInstance().getWorkbench().getActiveWorkbenchWindow().getShell();
					MessageDialog.openError(shell, "Please specify the X10 Runtime location",
							"The location of the X10 Runtime is unset in the launch configuration '" + configuration.getName() + "'.");
				}
			});
			return;
		}

		// ======== start new classpath calculation
		List<String> classpathList = new ArrayList<String>();
		for (int i = 0; i < classpath.length; i++) {
			classpathList.add(classpath[i]);
		}

		// PORT1.7 -- these are not used but when we correctly calculate where
		// all the jars should be, we probably will
		// String locPrefix=x10RuntimeLoc.substring(0,
		// x10RuntimeLoc.lastIndexOf(File.separator)) + File.separator ;
		// String commonLoc= locPrefix+ X10Plugin.X10_COMMON_BUNDLE_ID;
		// String constraintsLoc=locPrefix +
		// X10Plugin.X10_CONSTRAINTS_BUNDLE_ID;

		String locnDir = getDir(x10RuntimeLoc);
		// PORT1.7 -- common and constraints jars also need to be added to classpath

		String commonJar=X10Util.getJarLocationForBundle(X10DTCorePlugin.X10_COMMON_BUNDLE_ID);
		String constraintsJar=X10Util.getJarLocationForBundle(X10DTCorePlugin.X10_CONSTRAINTS_BUNDLE_ID);

		classpathList.add(commonJar);
		classpathList.add(constraintsJar);
		// PORT1.7 -- we might also want a third jar, the pre-built X10 classes

		// test that each exists (be paranoid)
		for (int i = 0; i < classpathList.size(); i++) {
			String path = classpathList.get(i);
			File file = new File(path);
			if (!file.exists()) {
				X10DTCorePlugin.getInstance().writeErrorMsg("X10LaunchConfigurationDelegate, cannot find expected part of runtime path: " + path);
			}

		}
		String[] classPathExpanded = classpathList.toArray(new String[] {});
		// ======== end new classpath calculation

		// x10RuntimeType is the class name that the runtime actually launches.
		// in 1.5 this was always "x10.lang.Runtime'
		// in 1.7 this is "Hello$Main" where "Hello" is main user class
		//
		// We accept the launch config value of "Main class" with or without  trailing "$Main"
		// that is, append it only if necessary
		// We accept it with trailing "$Main" because this is what the
		// "Search..." action in launch config dialog returns. ("Hello$Main")
		final String x10RuntimeType;

		if (!mainTypeName.endsWith(USER_MAIN_SUFFIX)) {
			x10RuntimeType = mainTypeName + USER_MAIN_SUFFIX;
		} else {
			x10RuntimeType = mainTypeName;
		}
		// Create VM config
		VMRunnerConfiguration runConfig = new VMRunnerConfiguration(x10RuntimeType, classPathExpanded);
		String[] explicitRuntimeArgsArray = execArgs.getRuntimeArgumentsArray();
		String[] explicitProgArgsArray = execArgs.getProgramArgumentsArray();
		String[] realArgsArray = new String[explicitProgArgsArray.length + explicitRuntimeArgsArray.length /* PORT1.7  + 1*/];

		System.arraycopy(explicitRuntimeArgsArray, 0, realArgsArray, 0, explicitRuntimeArgsArray.length);
		//realArgsArray[explicitRuntimeArgsArray.length] = mainTypeName;// PORT1.7  1.5 required main as first arg to launch
		System.arraycopy(explicitProgArgsArray, 0, realArgsArray, explicitRuntimeArgsArray.length /* PORT1.7  + 1*/, explicitProgArgsArray.length);

		// DLLs were for testcases??? in 1.5 needed some native code. but not
		// any more.
		String[] x10ExtraVMArgs = {
		// "-Djava.library.path=" + commonLoc + "\\lib",
		"-ea" // BRT ea= enable assertions, do this only if set in prefs
		};
		// 1.7: would have to get to c++ backend code
		// eventually want some UI in launch config for this

		String[] explicitVMArgsArray = execArgs.getVMArgumentsArray();
		String[] realVMArgsArray = new String[explicitVMArgsArray.length + x10ExtraVMArgs.length];

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

		if(debug) {
			System.out.println("runConfig classToLaunch: "+runConfig.getClassToLaunch());
			System.out.println("runConfig program args: "+printArray(runConfig.getProgramArguments()));
			System.out.println("runConfig VM args: "+printArray(runConfig.getVMArguments()));
		}
		// Launch the configuration - 1 unit of work
		runner.run(runConfig, launch, monitor);

		// check for cancellation
		if (monitor.isCanceled()) {
			return;
		}

		monitor.done();
	}
    private String printArray(String[] arg) {
    	StringBuffer buf = new StringBuffer();
    	buf.append("[");
    	for (String a : arg) {
			buf.append(a);
			buf.append(", ");
		}
    	buf.append("]");
    	return buf.toString();
    }


	private String getDir(String pathLocation) {
    	String result="";
    	if(pathLocation.endsWith(File.separator)) {
    		pathLocation=pathLocation.substring(0,pathLocation.length()-1);
    	}
    	int loc=pathLocation.lastIndexOf(File.separator);
    	result=pathLocation.substring(0,loc+1);
    	
    	return result;
    }
}
