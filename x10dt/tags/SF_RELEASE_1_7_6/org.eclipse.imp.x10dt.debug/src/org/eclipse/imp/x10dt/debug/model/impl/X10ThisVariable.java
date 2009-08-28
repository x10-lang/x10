package org.eclipse.imp.x10dt.debug.model.impl;

import org.eclipse.imp.x10dt.debug.Activator;
import org.eclipse.jdt.internal.debug.core.model.JDIThisVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.Field;
import com.sun.jdi.Value;

public class X10ThisVariable extends JDIThisVariable {
	
	public X10ThisVariable(JDIDebugTarget target, ObjectReference ref) {
		super(target,ref);
	}
	
	protected Value retrieveValue() {
		System.out.println("X10ThisVariable:retrievevalue");
		return super.retrieveValue();
	}
	
	public String getModelIdentifier() {
		return Activator.getUniqueIdentifier();
	}
	
}