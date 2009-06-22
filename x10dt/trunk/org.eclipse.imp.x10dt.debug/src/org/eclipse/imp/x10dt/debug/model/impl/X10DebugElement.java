package org.eclipse.imp.x10dt.debug.model.impl;

import org.eclipse.imp.x10dt.debug.Activator;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugElement;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;

public class X10DebugElement extends JDIDebugElement {

	public X10DebugElement(JDIDebugTarget target) {
		super(target);
	}
	
	/**
	 * @see org.eclipse.debug.core.model.IDebugElement#getModelIdentifier()
	 */
	public String getModelIdentifier() {
		return Activator.PLUGIN_ID+".model";
	}

	

}
