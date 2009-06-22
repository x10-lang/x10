package org.eclipse.imp.x10dt.debug.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.imp.x10dt.debug.model.IX10Variable;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
/**
 * X10DT Debugger Model Interface
 * 
 * @author mmk
 * @since 10/10/08
 */
public interface IX10StackFrame extends IJavaStackFrame {
	IVariable[] getVariables() throws DebugException;
}
