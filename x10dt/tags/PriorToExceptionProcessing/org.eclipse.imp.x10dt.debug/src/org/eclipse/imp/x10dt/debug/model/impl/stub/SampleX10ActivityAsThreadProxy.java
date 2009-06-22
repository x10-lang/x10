package org.eclipse.imp.x10dt.debug.model.impl.stub;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.imp.x10dt.debug.model.IX10Activity;
import org.eclipse.imp.x10dt.debug.model.IX10Clock;
import org.eclipse.imp.x10dt.debug.model.IX10Place;
import org.eclipse.imp.x10dt.debug.model.IX10StackFrame;

public class SampleX10ActivityAsThreadProxy implements IThread, IX10Activity {

	private IX10Activity _activity;
	private IDebugTarget _debugTarget;

	public SampleX10ActivityAsThreadProxy(IX10Activity activity, IDebugTarget debugTarget) {
		_activity = activity;
		_debugTarget = debugTarget;
		((SampleX10Activity)activity).setThread(this);
	}
	public IBreakpoint[] getBreakpoints() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() throws DebugException {
		String name = _activity.getName();
		String sep = "";
		IX10Clock[] clocks = _activity.getClocks();
		IX10Place place = _activity.getPlace();
		if (place!=null || (clocks!=null && clocks.length!=0)) {
			name += "[";
			if (place!=null) {
				name += "Place: "+_activity.getPlace().getName();
				sep = "; ";
			}
			String key=sep+"Clocks: ";
			sep = "";
			for (IX10Clock c: clocks) {
				name+=key;
				key = "";
				name+=sep;
				sep = ",";
				name += c.getName();
			}
			name += "]";
		}
		return name;
	}

	public int getPriority() throws DebugException {
		// TODO Auto-generated method stub
		return 0;
	}

	public IX10StackFrame[] getStackFrames() throws DebugException {
		return _activity.getStackFrames();
	}

	public IStackFrame getTopStackFrame() throws DebugException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasStackFrames() throws DebugException {
		return _activity.getStackFrames().length != 0;
	}

	public IDebugTarget getDebugTarget() {
		return _debugTarget;
	}

	public ILaunch getLaunch() {
		return _debugTarget.getLaunch();
	}

	public String getModelIdentifier() {
		return _debugTarget.getModelIdentifier();
	}

	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean canResume() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canSuspend() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSuspended() {
		// TODO Auto-generated method stub
		return false;
	}

	public void resume() throws DebugException {
		// TODO Auto-generated method stub

	}

	public void suspend() throws DebugException {
		// TODO Auto-generated method stub

	}

	public boolean canStepInto() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canStepOver() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canStepReturn() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isStepping() {
		// TODO Auto-generated method stub
		return false;
	}

	public void stepInto() throws DebugException {
		// TODO Auto-generated method stub

	}

	public void stepOver() throws DebugException {
		// TODO Auto-generated method stub

	}

	public void stepReturn() throws DebugException {
		// TODO Auto-generated method stub

	}

	public boolean canTerminate() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isTerminated() {
		// TODO Auto-generated method stub
		return false;
	}

	public void terminate() throws DebugException {
		// TODO Auto-generated method stub
	}
	public IX10Clock blockedOn() {
		// TODO Auto-generated method stub
		return null;
	}
	public IX10Clock[] getClocks() {
		// TODO Auto-generated method stub
		return null;
	}
	public IX10Activity[] getFinishChildren() {
		// TODO Auto-generated method stub
		return null;
	}
	public IX10Activity getFinishParent() {
		// TODO Auto-generated method stub
		return null;
	}
	public IX10Place getPlace() {
		// TODO Auto-generated method stub
		return null;
	}
	public X10ActivityState getRunState() {
		// TODO Auto-generated method stub
		return null;
	}

}
