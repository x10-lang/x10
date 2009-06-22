package org.eclipse.imp.x10dt.debug.model.impl.jdi;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.imp.x10dt.debug.model.IX10Type;
import org.eclipse.imp.x10dt.debug.model.IX10Variable;
import org.eclipse.imp.x10dt.debug.model.impl.X10DebugElement;

public class X10DelegatingVariable extends X10DebugElement implements IX10Variable {

	private IVariable _variable;

	public X10DelegatingVariable (IDebugTarget target, IVariable variable) {
		super(target);
		_variable = variable;
	}
	public String getName() throws DebugException {
		return _variable.getName();
	}

	public IValue getValue() throws DebugException {
		return _variable.getValue();
	}

	public String getReferenceTypeName() throws DebugException {
		return _variable.getReferenceTypeName();
	}

	public boolean hasValueChanged() throws DebugException {
		return _variable.hasValueChanged();
	}

	public IDebugTarget getDebugTarget() {
		return _variable.getDebugTarget();
	}

	public ILaunch getLaunch() {
		return _variable.getLaunch();
	}

	public Object getAdapter(Class adapter) {
		return _variable.getAdapter(adapter);
	}

	public void setValue(String expression) throws DebugException {
		_variable.setValue(expression);
	}

	public void setValue(IValue value) throws DebugException {
		_variable.setValue(value);
	}

	public boolean supportsValueModification() {
		return _variable.supportsValueModification();
	}

	public boolean verifyValue(String expression) throws DebugException {
		return _variable.verifyValue(expression);
	}

	public boolean verifyValue(IValue value) throws DebugException {
		return _variable.verifyValue(value);
	}

}
