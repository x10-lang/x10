package org.eclipse.imp.x10dt.debug.ui.presentation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.internal.ui.model.elements.DebugTargetContentProvider;
import org.eclipse.debug.internal.ui.model.elements.ThreadContentProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenCountUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementContentProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IHasChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IViewerUpdate;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.imp.x10dt.debug.model.impl.X10StackFrame;
import org.eclipse.imp.x10dt.debug.model.impl.X10Thread;

public class AsyncStackFrameFilteringContentProvider 
	extends ThreadContentProvider implements IElementContentProvider {
	protected Object[] getAllChildren(Object parent, IPresentationContext context, IViewerUpdate monitor) throws CoreException {
		Object[] stackFrames = super.getChildren(parent, 0, super.getChildCount(parent, context, monitor), context, monitor);
		String id = context.getId();
		if (id.equals(IDebugUIConstants.ID_DEBUG_VIEW)) {
			List<IStackFrame> userFrames = new ArrayList();
			for (Object f: stackFrames) {
				userFrames.add((IStackFrame)f);
				if ((f instanceof X10StackFrame && ((X10StackFrame)f).getName().equals("runX10Task"))) {
					return userFrames.toArray(new IStackFrame[0]);
				}
			}	
		}
        return EMPTY;
	}
	
	protected boolean hasChildren(Object element, IPresentationContext context, IViewerUpdate monitor) throws CoreException {
		String id = context.getId();
		if (id.equals(IDebugUIConstants.ID_DEBUG_VIEW))
		{
			return getChildCount(element,  context,  monitor)!=0;
		}
		return false;
	}

	protected int getChildCount(Object element, IPresentationContext context, IViewerUpdate monitor) throws CoreException {
		String id = context.getId();
		if (id.equals(IDebugUIConstants.ID_DEBUG_VIEW))
		{
			return getAllChildren(element,  context,  monitor).length;
		}
		return 0;
	}

}
