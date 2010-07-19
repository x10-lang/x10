package org.eclipse.imp.x10dt.ui.cpp.launch.launching;

import org.eclipse.imp.x10dt.ui.cpp.launch.LaunchMessages;
import org.eclipse.ptp.core.attributes.StringAttributeDefinition;

/**
 * X10 Debugger-specific Attributes
 */
public class X10DebugAttributes {
	private static final String HOST_ADDRESS_ATTR_ID = "debugExecName"; //$NON-NLS-1$
	private final static StringAttributeDefinition debugHostAddressAttrDef = 
		new StringAttributeDefinition(HOST_ADDRESS_ATTR_ID, "Debug UI Host Address", //$NON-NLS-1$
				LaunchMessages.DBG_Attributes_1, true, ""); //$NON-NLS-1$

	public static StringAttributeDefinition getDebuggerHostAddressAttributeDefinition() {
		return debugHostAddressAttrDef;
	}
}
