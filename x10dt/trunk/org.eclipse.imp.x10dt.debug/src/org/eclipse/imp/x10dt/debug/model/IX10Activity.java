package org.eclipse.imp.x10dt.debug.model;


import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.imp.x10dt.debug.model.IX10Activity;
import org.eclipse.imp.x10dt.debug.model.IX10Clock;

/**
 * X10DT Debugger Model Interface
 * 
 * @author mmk
 * @since 10/10/08
 */
public interface IX10Activity {
	public enum X10ActivityState {
		Running,
		Blocked, // waiting for a clock or other resource
		Suspended, // not sure about this one: suspended by debugger, e.g. breakpoint
		Idle; //No activity scheduled or activity got terminated
	}
	String getName() throws DebugException;
	IX10Activity[] getFinishChildren();
	IX10Activity getFinishParent(); // => null if this is root
	IX10Clock[] getClocks();
	X10ActivityState getRunState();
	//IX10Clock blockedOn();
	IX10Place getPlace();
	IStackFrame[] getStackFrames() throws DebugException;
}
