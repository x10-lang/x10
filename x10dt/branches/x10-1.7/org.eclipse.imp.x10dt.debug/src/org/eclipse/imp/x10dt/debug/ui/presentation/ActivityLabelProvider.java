package org.eclipse.imp.x10dt.debug.ui.presentation;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.internal.ui.model.elements.DebugElementLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.ILabelUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.imp.x10dt.debug.model.impl.X10Activity;
import org.eclipse.imp.x10dt.debug.model.impl.X10StackFrame;
import org.eclipse.jface.viewers.TreePath;

public class ActivityLabelProvider extends DebugElementLabelProvider implements IElementLabelProvider {

	protected String getLabel(TreePath elementPath, IPresentationContext presentationContext, String columnId) throws CoreException {
		if (elementPath.getLastSegment() instanceof X10Activity) {
			X10Activity a = (X10Activity) elementPath.getLastSegment();
			String name = a.getName();
			return "PENDING ACTIVITY "+((X10Activity)elementPath.getLastSegment()).uid +": " + (name==null ? "" : name);
		}
		return elementPath.getLastSegment().hashCode() +"/"+super.getLabel(elementPath, presentationContext, columnId);
	}

}
