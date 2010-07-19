package org.eclipse.imp.x10dt.debug.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.internal.ui.model.elements.DebugElementLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementContentProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IModelProxyFactory;
import org.eclipse.debug.internal.ui.views.launch.DebugElementAdapterFactory;
import org.eclipse.imp.x10dt.debug.model.impl.X10Activity;
import org.eclipse.imp.x10dt.debug.model.impl.X10DebugTargetAlt;
import org.eclipse.imp.x10dt.debug.model.impl.X10StackFrame;
import org.eclipse.imp.x10dt.debug.model.impl.X10Thread;
import org.eclipse.imp.x10dt.debug.ui.presentation.ActivityChildrenContentProvider;
import org.eclipse.imp.x10dt.debug.ui.presentation.ActivityChildrenContentProvider;
import org.eclipse.imp.x10dt.debug.ui.presentation.ActivityFieldFilteringContentProvider;
import org.eclipse.imp.x10dt.debug.ui.presentation.ActivityLabelProvider;
import org.eclipse.imp.x10dt.debug.ui.presentation.AsyncLabelProvider;
import org.eclipse.imp.x10dt.debug.ui.presentation.AsyncStackFrameFilteringContentProvider;
import org.eclipse.imp.x10dt.debug.ui.presentation.QuiescentThreadFilteringContentProvider;
import org.eclipse.jdt.internal.debug.core.model.JDIVariable;

public class X10AdapterFactory extends DebugElementAdapterFactory implements IAdapterFactory {

	private static IElementContentProvider fgTargetThreadProvider = new QuiescentThreadFilteringContentProvider();
	private static IElementContentProvider fgFilteringStackFrameProvider = new AsyncStackFrameFilteringContentProvider();
	private static IElementContentProvider fgVariableFieldProvider = new ActivityFieldFilteringContentProvider();
	private static IElementContentProvider fgActivityChildrenProvider = new ActivityChildrenContentProvider();
	private static IElementLabelProvider fgStackFrameLabelProvider = new AsyncLabelProvider();
	private static IElementLabelProvider fgActivityLabelProvider = new ActivityLabelProvider();
	private static IModelProxyFactory fgModelProxyFactory = new X10ModelProxyFactory();

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (IElementContentProvider.class.equals(adapterType)) {
			if (adaptableObject instanceof X10DebugTargetAlt) {
				return fgTargetThreadProvider;
			}
			if (adaptableObject instanceof X10Thread) {
				return fgFilteringStackFrameProvider;
			}
			if (adaptableObject instanceof JDIVariable) {
				return fgVariableFieldProvider;
			}
			if (adaptableObject instanceof X10Activity) {
				return fgActivityChildrenProvider ;
			}
		}
		if (IElementLabelProvider.class.equals(adapterType)) {
			if (adaptableObject instanceof X10StackFrame) {
				return fgStackFrameLabelProvider ;
			}
			if (adaptableObject instanceof X10Activity) {
				return fgActivityLabelProvider;
			}
		}
		if (IModelProxyFactory.class.equals(adapterType)) {
			if (adaptableObject instanceof X10DebugTargetAlt) {
				return fgModelProxyFactory ;
			}
		}
		return super.getAdapter(adaptableObject, adapterType);
	}

	public Class[] getAdapterList() {
		return new Class[]{IElementContentProvider.class};
	}

}
