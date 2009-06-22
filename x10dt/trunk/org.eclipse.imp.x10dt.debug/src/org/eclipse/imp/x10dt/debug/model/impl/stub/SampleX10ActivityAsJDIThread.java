package org.eclipse.imp.x10dt.debug.model.impl.stub;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.imp.x10dt.debug.model.IX10Activity;
import org.eclipse.imp.x10dt.debug.model.IX10Clock;
import org.eclipse.imp.x10dt.debug.model.IX10Place;
import org.eclipse.imp.x10dt.debug.model.X10DebugTargetAlt;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;

import com.sun.jdi.ObjectCollectedException;
import com.sun.jdi.ThreadReference;

public class SampleX10ActivityAsJDIThread extends JDIThread {
	public SampleX10ActivityAsJDIThread(JDIDebugTarget target, ThreadReference thread)
			throws ObjectCollectedException {
		super(target, thread);
//		_debugTarget = target;
	}
//
//	private IX10Activity _activity;
//	private IDebugTarget _debugTarget;
//
	public SampleX10ActivityAsJDIThread(IX10Activity activity, JDIDebugTarget debugTarget, ThreadReference jdiThread) {
		super(debugTarget, jdiThread);
//		_activity = activity;
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
//	public IStackFrame[] getStackFrames() throws DebugException {
//		return _activity.getStackFrames();
//	}
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

//	public int getPriority() throws DebugException {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public Object getAdapter(Class adapterClass) {
//		return super.getAdapter(adapterClass);
//	}
}
