package org.eclipse.imp.x10dt.debug.model.impl.jdi;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.imp.x10dt.debug.model.IX10StackFrame;
import org.eclipse.imp.x10dt.debug.model.IX10Variable;
import org.eclipse.imp.x10dt.debug.model.impl.X10DebugElement;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;

public class X10DelegatingStackFrame extends X10DebugElement implements IX10StackFrame {
	
	JDIStackFrame _jdiStackFrame;
	
	public X10DelegatingStackFrame (IDebugTarget target, JDIStackFrame jdiStackFrame) {
		super(target);
		_jdiStackFrame = jdiStackFrame;
	}

	public IX10Variable[] getVariables() throws DebugException {
		IVariable[] baseVariables = _jdiStackFrame.getVariables();
		IX10Variable[] x10Variables = new IX10Variable[baseVariables.length];
		int i=0;
		for (IVariable v: baseVariables) {
			x10Variables[i] = new X10DelegatingVariable(getDebugTarget(), v);
		}
		return x10Variables;
	}

	public int getCharEnd() throws DebugException {
		return _jdiStackFrame.getCharEnd();
	}

	public int getCharStart() throws DebugException {
		return _jdiStackFrame.getCharStart();
	}

	public int getLineNumber() throws DebugException {
		return _jdiStackFrame.getLineNumber();
	}

	public String getName() throws DebugException {
		return _jdiStackFrame.getName();
	}

	public IRegisterGroup[] getRegisterGroups() throws DebugException {
		// TODO Auto-generated method stub
		return null;
	}

	public IThread getThread() {
		return _jdiStackFrame.getThread();
	}

	public boolean hasRegisterGroups() throws DebugException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasVariables() throws DebugException {
		return _jdiStackFrame.hasVariables();
	}

	public ILaunch getLaunch() {
		return _jdiStackFrame.getLaunch();
	}

	public Object getAdapter(Class adapter) {
		return _jdiStackFrame.getAdapter(adapter);
	}

	public boolean canStepInto() {
		return _jdiStackFrame.canStepInto();
	}

	public boolean canStepOver() {
		return _jdiStackFrame.canStepOver();
	}

	public boolean canStepReturn() {
		return _jdiStackFrame.canStepReturn();
	}

	public boolean isStepping() {
		return _jdiStackFrame.isStepping();
	}

	public void stepInto() throws DebugException {
		_jdiStackFrame.stepInto();
	}

	public void stepOver() throws DebugException {
		_jdiStackFrame.stepOver();
	}

	public void stepReturn() throws DebugException {
		_jdiStackFrame.stepReturn();
	}

	public boolean canResume() {
		return _jdiStackFrame.canResume();
	}

	public boolean canSuspend() {
		return _jdiStackFrame.canSuspend();
	}

	public boolean isSuspended() {
		return _jdiStackFrame.isSuspended();
	}

	public void resume() throws DebugException {
		_jdiStackFrame.resume();
	}

	public void suspend() throws DebugException {
		_jdiStackFrame.suspend();
	}

	public boolean canTerminate() {
		return _jdiStackFrame.canTerminate();
	}

	public boolean isTerminated() {
		return _jdiStackFrame.isTerminated();
	}

	public void terminate() throws DebugException {
		_jdiStackFrame.terminate();
	}
	
	public JDIStackFrame getDelegationTarget() {
		return _jdiStackFrame;
	}

}
