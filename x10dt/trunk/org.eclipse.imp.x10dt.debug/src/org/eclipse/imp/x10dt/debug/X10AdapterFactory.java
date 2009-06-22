package org.eclipse.imp.x10dt.debug;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.internal.ui.model.elements.DebugElementLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementContentProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementLabelProvider;
import org.eclipse.imp.x10dt.debug.model.impl.X10Activity;
import org.eclipse.imp.x10dt.debug.model.impl.X10DebugTargetAlt;
import org.eclipse.imp.x10dt.debug.model.impl.X10StackFrame;
import org.eclipse.imp.x10dt.debug.model.impl.X10Thread;
import org.eclipse.imp.x10dt.debug.ui.presentation.ActivityFieldFilteringContentProvider;
import org.eclipse.imp.x10dt.debug.ui.presentation.ActivityLabelProvider;
import org.eclipse.imp.x10dt.debug.ui.presentation.AsyncLabelProvider;
import org.eclipse.imp.x10dt.debug.ui.presentation.AsyncStackFrameFilteringContentProvider;
import org.eclipse.imp.x10dt.debug.ui.presentation.QuiescentThreadFilteringContentProvider;
import org.eclipse.jdt.internal.debug.core.model.JDIVariable;

public class X10AdapterFactory extends DebugElementLabelProvider implements IAdapterFactory {

	private static IElementContentProvider fgTargetAdapter = new QuiescentThreadFilteringContentProvider();
	private static IElementContentProvider fgThreadAdapter = new AsyncStackFrameFilteringContentProvider();
	private static IElementContentProvider fgObjectAdapter = new ActivityFieldFilteringContentProvider();
	private static IElementLabelProvider fgStackFrameLabelAdapter = new AsyncLabelProvider();
	private static IElementLabelProvider fgActivityLabelAdapter = new ActivityLabelProvider();

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (IElementContentProvider.class.equals(adapterType)) {
			if (adaptableObject instanceof X10DebugTargetAlt) {
				return fgTargetAdapter;
			}
			if (adaptableObject instanceof X10Thread) {
				return fgThreadAdapter;
			}
			if (adaptableObject instanceof JDIVariable) {
				return fgObjectAdapter ;
			}
		}
		if (IElementLabelProvider.class.equals(adapterType)) {
			if (adaptableObject instanceof X10StackFrame) {
				return fgStackFrameLabelAdapter ;
			}
			if (adaptableObject instanceof X10Activity) {
				return fgActivityLabelAdapter;
			}
		}
		return null;
	}

	public Class[] getAdapterList() {
		return new Class[]{IElementContentProvider.class};
	}

}
