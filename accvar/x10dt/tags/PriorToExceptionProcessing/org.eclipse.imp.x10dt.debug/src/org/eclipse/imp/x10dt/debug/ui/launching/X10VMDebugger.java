package org.eclipse.imp.x10dt.debug.ui.launching;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.imp.x10dt.debug.model.X10DebugModel;
import org.eclipse.jdt.internal.launching.StandardVMDebugger;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

import com.sun.jdi.VirtualMachine;

public class X10VMDebugger extends StandardVMDebugger {

	/**
	 * Creates a new launcher
	 */
	public X10VMDebugger(IVMInstall vmInstance) {
		super(vmInstance);
	}

	/**
	 * Creates a new debug target for the given virtual machine and system process
	 * that is connected on the specified port for the given launch.
	 * 
	 * @param config run configuration used to launch the VM
	 * @param launch launch to add the target to
	 * @param port port the VM is connected to
	 * @param process associated system process
	 * @param vm JDI virtual machine
	 */
	protected IDebugTarget createDebugTarget(VMRunnerConfiguration config, ILaunch launch, int port, IProcess process, VirtualMachine vm) {
		vm.setDefaultStratum("x10");
//		return JDIDebugModel.newDebugTarget(launch, vm, renderDebugTarget(config.getClassToLaunch(), port), process, true, false, config.isResumeOnStartup());
		return X10DebugModel.newDebugTarget(launch, vm, renderDebugTarget(config.getClassToLaunch(), port), process, true, false, config.isResumeOnStartup());
	}
	

}
