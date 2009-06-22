package org.eclipse.imp.x10dt.debug;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementContentProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementLabelProvider;
import org.eclipse.imp.x10dt.debug.model.impl.X10DebugTargetAlt;
import org.eclipse.imp.x10dt.debug.model.impl.X10StackFrame;
import org.eclipse.imp.x10dt.debug.model.impl.X10Thread;
import org.eclipse.imp.x10dt.debug.ui.presentation.AsyncLabelProvider;
import org.eclipse.imp.x10dt.debug.ui.presentation.AsyncStackFrameFilteringContentProvider;
import org.eclipse.imp.x10dt.debug.ui.presentation.QuiescentThreadFilteringContentProvider;

public class X10AdapterFactory implements IAdapterFactory {

	private static IElementContentProvider fgTargetAdapter = new QuiescentThreadFilteringContentProvider();
	private static IElementContentProvider fgThreadAdapter = new AsyncStackFrameFilteringContentProvider();
	private static IElementLabelProvider fgStackFrameLabelAdapter = new AsyncLabelProvider();

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (IElementContentProvider.class.equals(adapterType)) {
			if (adaptableObject instanceof X10DebugTargetAlt) {
				return fgTargetAdapter;
			}
			if (adaptableObject instanceof X10Thread) {
				return fgThreadAdapter;
			}
		}
		if (IElementLabelProvider.class.equals(adapterType)) {
			if (adaptableObject instanceof X10StackFrame) {
				return fgStackFrameLabelAdapter ;
			}
		}
		return null;
	}

	public Class[] getAdapterList() {
		return new Class[]{IElementContentProvider.class};
	}

}
