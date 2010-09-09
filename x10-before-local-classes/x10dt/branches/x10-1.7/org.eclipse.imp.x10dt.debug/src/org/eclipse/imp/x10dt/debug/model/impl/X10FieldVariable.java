package org.eclipse.imp.x10dt.debug.model.impl;

import org.eclipse.imp.x10dt.debug.Activator;
import org.eclipse.jdt.internal.debug.core.model.JDIFieldVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Field;

public class X10FieldVariable extends JDIFieldVariable {
	
	public X10FieldVariable(JDIDebugTarget target, Field field, ReferenceType refType) {
		super(target,field,refType);
	}
	
	public String getModelIdentifier() {
		return Activator.getUniqueIdentifier();
	}
	
}