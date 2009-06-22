package x10.uide.launching;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

public class X10LaunchConfigurationDelegate extends AbstractJavaLaunchConfigurationDelegate {
    private final static String x10RuntimeType= "x10.lang.Runtime";

    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
	// boolean debug= mode.equals(ILaunchManager.DEBUG_MODE);

	if (monitor == null) {
	    monitor= new NullProgressMonitor();
	}

	monitor.beginTask(MessageFormat.format("{0}...", new String[] { configuration.getName() }), 3); //$NON-NLS-1$
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
	ExecutionArguments execArgs= new ExecutionArguments(vmArgs, pgmArgs);

	// VM-specific attributes
	Map vmAttributesMap= getVMSpecificAttributesMap(configuration);

	// Classpath
	String[] classpath= getClasspath(configuration);

	// Create VM config
	VMRunnerConfiguration runConfig= new VMRunnerConfiguration(x10RuntimeType, classpath);
	String[] explicitArgsArray= execArgs.getProgramArgumentsArray();
	String[] realArgsArray= new String[explicitArgsArray.length + 1];

	realArgsArray[0]= mainTypeName;
	System.arraycopy(explicitArgsArray, 0, realArgsArray, 1, explicitArgsArray.length);

	String x10Loc= "e:\\rmf\\eclipse\\workspaces\\safari";
	String commonLoc= x10Loc + "\\x10.common";
	String runtimeLoc= x10Loc + "\\x10.runtime";

	String[] x10ExtraVMArgs= {
		"-Djava.library.path=" + commonLoc + "\\lib",
		"-ea",
		"-classpath=" + runtimeLoc + "\\classes"
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
