package org.eclipse.imp.x10dt.debug.model;

import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.imp.x10dt.debug.model.IX10Variable;
/**
 * X10DT Debugger Model Interface
 * 
 * @author mmk
 * @since 10/10/08
 */
public interface IX10StackFrame extends IStackFrame {
	IX10Variable[] getVariables();
}
