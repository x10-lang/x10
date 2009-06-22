package org.eclipse.imp.x10dt.debug.ui.presentation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlockRetrieval;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.internal.ui.model.elements.DebugTargetContentProvider;
import org.eclipse.debug.internal.ui.model.elements.ElementContentProvider;

import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenCountUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementContentProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IHasChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IViewerUpdate;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.imp.x10dt.debug.model.impl.X10Thread;

public class QuiescentThreadFilteringContentProvider extends
		DebugTargetContentProvider implements IElementContentProvider {

	protected Object[] getAllChildren(Object parent, IPresentationContext context, IViewerUpdate monitor) throws CoreException {
		IThread[] threads = (IThread[])super.getAllChildren(parent, context, monitor);
		String id = context.getId();
		if (id.equals(IDebugUIConstants.ID_DEBUG_VIEW)) {
			List<IThread> activeThreads = new ArrayList();
			for (IThread t: (IThread[])threads) {
				if (!(t instanceof X10Thread && ((X10Thread)t).getStackFrames().length>0)) continue;
				if (t.getName().contains("InvokeMethods")) continue; // really, getName should return simple name and should be .equals("InvokeMethods")  This will do for now
				activeThreads.add(t);
			}
			return activeThreads.toArray(new IThread[0]);
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
