package org.eclipse.imp.x10dt.debug.model.impl.stub;

import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.imp.x10dt.debug.model.IX10Activity;
import org.eclipse.imp.x10dt.debug.model.IX10Clock;
import org.eclipse.imp.x10dt.debug.model.IX10Place;
import org.eclipse.imp.x10dt.debug.model.IX10StackFrame;
import org.eclipse.imp.x10dt.debug.model.impl.X10DebugTargetAlt;
import org.eclipse.imp.x10dt.debug.model.impl.X10Thread;
import org.eclipse.imp.x10dt.debug.model.impl.jdi.X10DelegatingStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;

import com.sun.jdi.ObjectCollectedException;
import com.sun.jdi.ThreadReference;

public class SampleX10ActivityAsJDIThread extends X10Thread implements IX10Activity {
	public SampleX10ActivityAsJDIThread(JDIDebugTarget target, ThreadReference thread)
			throws ObjectCollectedException {
		super(target, thread);
//		_debugTarget = target;
	}

	private IX10Activity _activity;
//	private IDebugTarget _debugTarget;

	public SampleX10ActivityAsJDIThread(IX10Activity activity, JDIDebugTarget debugTarget, ThreadReference jdiThread) {
		super(debugTarget, jdiThread);
		_activity = activity;
//		_debugTarget = debugTarget;
		((SampleX10Activity)activity).setThread(this);
	}
//
//	public String getName() throws DebugException {
//		String name = _activity.getName();
//		String sep = "";
//		IX10Clock[] clocks = _activity.getClocks();
//		IX10Place place = _activity.getPlace();
//		if (place!=null || (clocks!=null && clocks.length!=0)) {
//			name += "[";
//			if (place!=null) {
//				name += "Place: "+_activity.getPlace().getName();
//				sep = "; ";
//			}
//			String key=sep+"Clocks: ";
//			sep = "";
//			for (IX10Clock c: clocks) {
//				name+=key;
//				key = "";
//				name+=sep;
//				sep = ",";
//				name += c.getName();
//			}
//			name += "]";
//		}
//		return name;
//	}
//	public boolean hasStackFrames() throws DebugException {
//		return _activity.getStackFrames().length != 0;
//	}
//
	/**
	 * NOTE: this method returns a copy of this thread's stack frames.
	 * 
	 * @see IThread#getStackFrames()
	 */
	public synchronized IX10StackFrame[] getStackFrames() {
		if (isSuspendedQuiet()) {
			return new IX10StackFrame[0];
		}
		List list;
		try {
			list = computeStackFrames();
		} catch (DebugException e) {
			list = null;
		}
		IX10StackFrame x10StackFrames[] = new IX10StackFrame[list==null? 0 : list.size()];
		if (list!=null) {
			int i=0;
			for (Object jsfo: list) {
				x10StackFrames[i++] = new X10DelegatingStackFrame(getDebugTarget(), (JDIStackFrame)jsfo);

			}
		}
		return x10StackFrames;
	}
//
////	public IDebugTarget getDebugTarget() {
////		return _debugTarget;
////	}
////
//	public ILaunch getLaunch() {
//		return getDebugTarget().getLaunch();
//	}
//
	public String getModelIdentifier() {
		return getDebugTarget().getModelIdentifier();
	}
	public IX10Clock blockedOn() {
		// TODO Auto-generated method stub
		return null;
	}
	public IX10Clock[] getClocks() {
		return _activity.getClocks();
	}
	public IX10Activity[] getFinishChildren() {
		return _activity.getFinishChildren();
	}
	public IX10Activity getFinishParent() {
		return _activity.getFinishParent();
	}
	public IX10Place getPlace() {
		return _activity.getPlace();
	}
	public X10ActivityState getRunState() {
		return _activity.getRunState();
	}
	

//	public int getPriority() throws DebugException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public Object getAdapter(Class adapterClass) {
//		return super.getAdapter(adapterClass);
//	}
}
