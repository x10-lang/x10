package org.eclipse.imp.x10dt.debug.ui.presentation;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.internal.ui.model.elements.DebugElementLabelProvider;
import org.eclipse.debug.internal.ui.model.elements.ElementLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.ILabelUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.imp.x10dt.debug.model.impl.X10StackFrame;
import org.eclipse.jface.viewers.TreePath;

public class AsyncLabelProvider extends DebugElementLabelProvider implements IElementLabelProvider {

	protected String getLabel(TreePath elementPath, IPresentationContext presentationContext, String columnId) throws CoreException {
		if (elementPath.getLastSegment() instanceof X10StackFrame) {
			X10StackFrame stackFrame = (X10StackFrame) elementPath.getLastSegment();
			String name = stackFrame.getName();
			if (name.equals("runX10Task")) {
				return "async "+stackFrame.getSourceName()+ " line "+stackFrame.getLineNumber();
			}
		}
		return super.getLabel(elementPath, presentationContext, columnId);
	}

}
