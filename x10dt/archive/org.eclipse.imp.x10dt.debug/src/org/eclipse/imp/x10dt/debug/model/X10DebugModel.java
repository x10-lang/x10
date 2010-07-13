package org.eclipse.imp.x10dt.debug.model;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.imp.x10dt.debug.Activator;
import org.eclipse.imp.x10dt.debug.model.impl.X10DebugTargetAlt;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.internal.debug.core.JDIDebugPlugin;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;

import com.sun.jdi.VirtualMachine;
public class X10DebugModel {
//	public static X10DebugTarget fX10DebugTarget; 

	
	/**
	 * Returns the identifier for the JDI debug model plug-in
	 *
	 * @return plug-in identifier
	 */
	public static String getPluginIdentifier() {
		return Activator.getUniqueIdentifier();
	}
	
	public static IDebugTarget newDebugTarget(final ILaunch launch, final VirtualMachine vm, final String name, final IProcess process, final boolean allowTerminate, final boolean allowDisconnect, final boolean resume) {
		final IDebugTarget[] target = new IJavaDebugTarget[1];
		IWorkspaceRunnable r = new IWorkspaceRunnable() {
			public void run(IProgressMonitor m) {
				target[0]= new X10DebugTargetAlt(launch, vm, name, allowTerminate, allowDisconnect, process, resume);
//				target[0]= new JDIDebugTarget(launch, vm, name, allowTerminate, allowDisconnect, process, resume);
			}
		};
		try {
			ResourcesPlugin.getWorkspace().run(r, null, 0, null);
		} catch (CoreException e) {
			JDIDebugPlugin.log(e);
		}
		return target[0];
	}
	
	
//	public static X10DebugTarget newX10DebugTarget(){
//        X10DebugModel.fX10DebugTarget =  new X10DebugTarget();
//        return X10DebugModel.fX10DebugTarget;
//    }
//    public static X10DebugTarget newX10DebugTarget(ILaunch launch){
//        X10DebugModel.fX10DebugTarget =  new X10DebugTarget(launch);
//        return X10DebugModel.fX10DebugTarget;
//    }
//    
//    public static X10DebugTarget getX10DebugTarget(){
//    	return X10DebugModel.fX10DebugTarget;
//    }
    
}