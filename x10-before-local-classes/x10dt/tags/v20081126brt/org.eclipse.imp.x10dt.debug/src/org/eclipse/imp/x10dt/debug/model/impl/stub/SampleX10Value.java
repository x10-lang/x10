package org.eclipse.imp.x10dt.debug.model.impl.stub;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.imp.x10dt.debug.model.IX10StackFrame;

public class SampleX10Value implements IValue {

	private String _valueString;
	private IX10StackFrame _frame;

	public SampleX10Value(IX10StackFrame frame, String valueString) {
		_frame = frame;
		_valueString = valueString;
	}
	public String getReferenceTypeName() throws DebugException {
		return "AnX10Type";
	}

	public String getValueString() throws DebugException {
		return _valueString;
	}

	public IVariable[] getVariables() throws DebugException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasVariables() throws DebugException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAllocated() throws DebugException {
		// TODO Auto-generated method stub
		return false;
	}

	public IDebugTarget getDebugTarget() {
		return _frame.getDebugTarget();
	}

	public ILaunch getLaunch() {
		return _frame.getLaunch();
	}

	public String getModelIdentifier() {
		return _frame.getModelIdentifier();
	}

	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

}
