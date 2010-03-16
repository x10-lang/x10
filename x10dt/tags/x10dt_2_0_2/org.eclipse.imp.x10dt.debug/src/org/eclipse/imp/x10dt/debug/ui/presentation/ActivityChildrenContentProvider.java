package org.eclipse.imp.x10dt.debug.ui.presentation;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.internal.ui.model.elements.DebugTargetContentProvider;
import org.eclipse.debug.internal.ui.model.elements.ElementContentProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenCountUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementContentProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IHasChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IViewerUpdate;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.imp.x10dt.debug.model.IX10Activity;
import org.eclipse.imp.x10dt.debug.model.impl.X10DebugTargetAlt;

public class ActivityChildrenContentProvider extends ElementContentProvider implements IElementContentProvider {
	protected Object[] getAllChildren(Object parent, IPresentationContext context, IViewerUpdate monitor) throws CoreException {
		IX10Activity activity = ((IX10Activity)parent);
		String id = context.getId();
		if (id.equals(IDebugUIConstants.ID_DEBUG_VIEW))
		{
			return ((IX10Activity)parent).getStackFrames();
		}
		return new Object[0];
	}
	
	protected int getChildCount(Object element, IPresentationContext context, IViewerUpdate monitor) throws CoreException {
		String id = context.getId();
		if (id.equals(IDebugUIConstants.ID_DEBUG_VIEW))
		{
			return ((IX10Activity)element).getStackFrames().length;
		}
		else if (id.equals(IDebugUIConstants.ID_MEMORY_VIEW))
		{
			return getAllChildren(element, context, monitor).length;
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.model.provisional.elements.ElementContentProvider#supportsContextId(java.lang.String)
	 */
	protected boolean supportsContextId(String id) {
		return IDebugUIConstants.ID_DEBUG_VIEW.equals(id) || IDebugUIConstants.ID_MEMORY_VIEW.equals(id);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.model.provisional.elements.ElementContentProvider#getChildren(java.lang.Object, int, int, org.eclipse.debug.internal.ui.viewers.provisional.IPresentationContext)
	 */
	protected Object[] getChildren(Object parent, int index, int length, IPresentationContext context, IViewerUpdate monitor) throws CoreException {
		return getElements(getAllChildren(parent, context, monitor), index, length);
	}



}
