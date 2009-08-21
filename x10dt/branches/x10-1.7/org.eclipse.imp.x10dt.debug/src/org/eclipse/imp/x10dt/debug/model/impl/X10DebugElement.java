package org.eclipse.imp.x10dt.debug.model.impl;

import org.eclipse.debug.core.model.DebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.imp.x10dt.debug.Activator;

public abstract class X10DebugElement extends DebugElement {

	public X10DebugElement(IDebugTarget target) {
		super(target);
	}
	
	/**
	 * @see org.eclipse.debug.core.model.IDebugElement#getModelIdentifier()
	 */
	public String getModelIdentifier() {
		return Activator./*PLUGIN_ID+".model"*/getUniqueIdentifier();
	}

	

}
