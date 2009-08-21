package org.eclipse.imp.x10dt.debug.adapter;

import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IModelProxy;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IModelProxyFactory;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.imp.x10dt.debug.model.impl.X10DebugTargetAlt;
import org.eclipse.jdt.internal.debug.ui.threadgroups.JavaModelProxyFactory;

public class X10ModelProxyFactory implements
		IModelProxyFactory {

	public IModelProxy createModelProxy(Object element, IPresentationContext context) {
		if (IDebugUIConstants.ID_DEBUG_VIEW.equals(context.getId())) {
			if (element instanceof X10DebugTargetAlt){
				return new X10DebugTargetProxy((IDebugTarget) element);
			}
		}
		return null;
	}
}
