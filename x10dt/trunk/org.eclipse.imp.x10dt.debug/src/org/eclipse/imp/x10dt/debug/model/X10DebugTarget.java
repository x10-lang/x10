package org.eclipse.imp.x10dt.debug.model;

import java.util.List;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdi.hcr.ReferenceType;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaReferenceType;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIReferenceType;
import org.eclipse.jdt.internal.debug.core.model.JDIType;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.ClassObjectReference;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;


public class X10DebugTarget implements IDebugTarget{

	private IDebugTarget fJDebugTarget;
	private ClassObjectReference fX10RT;
	private ILaunch fLaunch = null;
/*
	public X10DebugTarget(ILaunch launch, VirtualMachine jvm, String name,
			boolean supportTerminate, boolean supportDisconnect,
			IProcess process, boolean resume) {
		super(launch, jvm, name, supportTerminate, supportDisconnect, process, resume);
		initialize();        
	}
*/
	public X10DebugTarget(){
	    	setJDIDebugTarget();
	}
	
	public X10DebugTarget(ILaunch launch){
    	fJDebugTarget = launch.getDebugTarget();
    }
	
	
	//******SHIVALI: I don't fully understand this.
	//******According to tutorial, DebugContext is available from debug views via DebugContextProvider.  Not available directly from here.
	//******But why do you need it?  X10DebugTarget should inherit from JDIDebugTarget (that's what you proposed, right?)
	//******So the java debug target identified by this method is just "this", isn't it?
	//******If for some reason this doesn't work, I'd say the place to do it would be in the constructor, where launch is provided as parameter
	//******Or equivalently, pass launch as param from constructor to this method via initialize.
	private void setJDIDebugTarget() {
		IAdaptable context = DebugUITools.getDebugContext();
		if  (context == null) {
			ILaunch[] launches =
				DebugPlugin.getDefault().getLaunchManager().getLaunches();
			if (launches.length > 0) {
				context = launches[launches.length - 1];
			}
		}
		ILaunch launch= (ILaunch)context;
		fJDebugTarget = (IJavaDebugTarget) launch.getDebugTarget();

	}

	private void setX10RTObject() {
		VirtualMachine vm = ((JDIDebugTarget) fJDebugTarget).getVM();
		List classes = vm.classesByName("DefaultRuntime_c");
		IJavaType[] type = new IJavaType[classes.size()];
		for (int i = 0; i < type.length; i++) {
			type[i] = JDIType.createType((JDIDebugTarget) fJDebugTarget, (Type)classes.get(i));
		}
		if (type[0] instanceof IJavaReferenceType) 
			fX10RT =((ClassType)((JDIReferenceType)type[0]).getUnderlyingType()).classObject();
	}		


	public int getNumberOfPlaces() {
		ReferenceType type = (ReferenceType) fX10RT.referenceType();
		Field place = ((ClassType)type).fieldByName("places_");
		Value v = (ObjectReference)fX10RT.getValue(place);
		if (v instanceof ArrayReference){
	          return getArrayReference(v).length();
		}
		return -1;
		
	} 
	protected ArrayReference getArrayReference(Value v) {
		if (v instanceof ArrayReference) {
			return (ArrayReference)v;
		}
		return null;
	}

	public String getName() throws DebugException {
		return "TESTING --- X10 Root Activity";
	}

	public IProcess getProcess() {
		return fJDebugTarget.getProcess();
	}

	public IThread[] getThreads() throws DebugException {
		return fJDebugTarget.getThreads();
	}

	public boolean hasThreads() throws DebugException {
		return fJDebugTarget.hasThreads();
	}

	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		// TODO Auto-generated method stub
		return false;
	}

	public IDebugTarget getDebugTarget() {
		return this;
	}

	public ILaunch getLaunch() {
		return fJDebugTarget.getLaunch();
	}

	public String getModelIdentifier() {
		return fJDebugTarget.getModelIdentifier();
	}

	public Object getAdapter(Class adapter) {
		return fJDebugTarget.getAdapter(adapter);
	}

	public boolean canTerminate() {
		return fJDebugTarget.canTerminate();
	}

	public boolean isTerminated() {
		return fJDebugTarget.isTerminated();
	}

	public void terminate() throws DebugException {
		fJDebugTarget.terminate();
	}

	public boolean canResume() {
		return fJDebugTarget.canResume();
	}

	public boolean canSuspend() {
		return fJDebugTarget.canSuspend();
	}

	public boolean isSuspended() {
		return fJDebugTarget.isSuspended();
	}

	public void resume() throws DebugException {
		fJDebugTarget.resume();
	}

	public void suspend() throws DebugException {
		fJDebugTarget.suspend();
	}

	public void breakpointAdded(IBreakpoint breakpoint) {
		fJDebugTarget.breakpointAdded(breakpoint);
	}

	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		fJDebugTarget.breakpointChanged(breakpoint, delta);
	}

	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		fJDebugTarget.breakpointRemoved(breakpoint, delta);
	}

	public boolean canDisconnect() {
		return fJDebugTarget.canDisconnect();
	}

	public void disconnect() throws DebugException {
		fJDebugTarget.disconnect();
	}

	public boolean isDisconnected() {
		return fJDebugTarget.isDisconnected();
	}

	public IMemoryBlock getMemoryBlock(long startAddress, long length)
			throws DebugException {
		return fJDebugTarget.getMemoryBlock(startAddress, length);
	}

	public boolean supportsStorageRetrieval() {
		return fJDebugTarget.supportsStorageRetrieval();
	}
}
