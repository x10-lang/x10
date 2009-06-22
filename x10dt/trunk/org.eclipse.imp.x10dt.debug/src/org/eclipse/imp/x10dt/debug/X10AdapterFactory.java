package org.eclipse.imp.x10dt.debug;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementContentProvider;
import org.eclipse.imp.x10dt.debug.model.impl.QuiescentThreadFilteringContentProvider;
import org.eclipse.imp.x10dt.debug.model.impl.X10DebugTargetAlt;

public class X10AdapterFactory implements IAdapterFactory {

	private static IElementContentProvider fgTargetAdapter = new QuiescentThreadFilteringContentProvider();

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (IElementContentProvider.class.equals(adapterType)) {
			if (adaptableObject instanceof X10DebugTargetAlt) {
				return fgTargetAdapter;
			}
		}
		return null;
	}

	public Class[] getAdapterList() {
		return new Class[]{IElementContentProvider.class};
	}

}
