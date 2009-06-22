package org.eclipse.imp.x10dt.debug.model.impl;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.imp.x10dt.debug.Activator;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;
import org.eclipse.jdt.internal.debug.core.model.JDIThreadGroup;

import com.sun.jdi.ObjectCollectedException;
import com.sun.jdi.ThreadGroupReference;
import com.sun.jdi.ThreadReference;

public class X10Thread extends JDIThread implements IThread {
	/**
	 * Constructs a new thread group in the given target based on the underlying
	 * thread group reference.
	 *  
	 * @param target debug target
	 * @param group thread group reference
	 */
	/**
	 * Creates a new thread on the underlying thread reference
	 * in the given debug target.
	 * 
	 * @param target the debug target in which this thread is contained
	 * @param thread the underlying thread on the VM
	 * @exception ObjectCollectedException if the underlying thread has been
	 * garbage collected and cannot be properly initialized
	 */
	public X10Thread(JDIDebugTarget target, ThreadReference thread) throws ObjectCollectedException {
		super(target, thread);
		/**
		 * @see org.eclipse.debug.core.model.IDebugElement#getModelIdentifier()
		 */

	}
	public String getModelIdentifier() {
		return Activator.getUniqueIdentifier();
	}
	
	// Copied from superclass to get around visibility gotchas
	public void terminated() {
		super.terminated();
	}
	public void disposeStackFrames() {
		super.disposeStackFrames();
	}
	protected void removeCurrentBreakpoint(IBreakpoint bp) {
		super.removeCurrentBreakpoint(bp);
	}
	protected synchronized void suspendedByVM(){
		super.suspendedByVM();
	}
	protected synchronized void resumedByVM() throws DebugException{
		super.resumedByVM();
	}
}
