package org.eclipse.imp.x10dt.debug.model.impl.stub;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.imp.x10dt.debug.model.IX10StackFrame;
import org.eclipse.imp.x10dt.debug.model.IX10Variable;

public class SampleX10StackFrame implements IX10StackFrame {

	private List<IX10Variable> _variables;
	private String _name;
	private IThread _thread;
	
	public SampleX10StackFrame (String name, IThread thread) {
		_name = name;
		_thread = thread;
		_variables = new ArrayList<IX10Variable>();
	}
	
	public void addVariable(IX10Variable variable) {
		_variables.add(variable);
	}
	
	public IX10Variable[] getVariables() {
		return _variables.toArray(new IX10Variable[_variables.size()]);
	}

	public String getName() throws DebugException {
		return _name;
	}

	public IThread getThread() {
		return _thread;
	}

	public IDebugTarget getDebugTarget() {
		return _thread.getDebugTarget();
	}

	public ILaunch getLaunch() {
		return _thread.getLaunch();
	}

	public boolean hasVariables() throws DebugException {
		return _variables.size()!=0;
	}

	public String getModelIdentifier() {
		return _thread.getModelIdentifier();
	}

	public int getCharEnd() throws DebugException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getCharStart() throws DebugException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getLineNumber() throws DebugException {
		// TODO Auto-generated method stub
		return 0;
	}

	public IRegisterGroup[] getRegisterGroups() throws DebugException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasRegisterGroups() throws DebugException {
		// TODO Auto-generated method stub
		return false;
	}

	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
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

	public void setThread(IThread thread) {
		_thread = thread;
	}

}
