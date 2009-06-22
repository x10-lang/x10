package org.eclipse.imp.x10dt.debug.model.impl.stub;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.imp.x10dt.debug.model.IX10Type;
import org.eclipse.imp.x10dt.debug.model.IX10Variable;

public class SampleX10Variable implements IX10Variable {
	
	private String _name;
	private IValue _value;

	public SampleX10Variable(String name, IValue value) {
		_name = name;
		_value = value;
	}
	public String getName() {
		return _name;
	}

	public IX10Type getType() {
		return null;
	}

	public IValue getValue() {
		return _value;
	}

	public String getReferenceTypeName() throws DebugException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasValueChanged() throws DebugException {
		// TODO Auto-generated method stub
		return false;
	}

	public IDebugTarget getDebugTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	public ILaunch getLaunch() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getModelIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setValue(String expression) throws DebugException {
		// TODO Auto-generated method stub

	}

	public void setValue(IValue value) throws DebugException {
		// TODO Auto-generated method stub

	}

	public boolean supportsValueModification() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean verifyValue(String expression) throws DebugException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean verifyValue(IValue value) throws DebugException {
		// TODO Auto-generated method stub
		return false;
	}

}
