package org.eclipse.imp.x10dt.debug.model.impl;

import org.eclipse.imp.x10dt.debug.Activator;
import org.eclipse.jdt.internal.debug.core.model.JDILocalVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Field;
import com.sun.jdi.LocalVariable;

public class X10LocalVariable extends JDILocalVariable {
	
	public X10LocalVariable(X10StackFrame frame, LocalVariable locvar) {
		super(frame,locvar);
	}
	
	public String getModelIdentifier() {
		return Activator.getUniqueIdentifier();
	}
	
	protected LocalVariable getLocal() {
		System.out.println("X10LocalVariable getLocal()");
		return super.getLocal();
	}
	
	protected void setLocal(LocalVariable local) {
		super.setLocal(local);
	}
}