package org.eclipse.imp.x10dt.debug.adapter;

import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IModelProxy;
import org.eclipse.debug.internal.ui.viewers.update.DebugEventHandler;
import org.eclipse.debug.internal.ui.viewers.update.DebugTargetEventHandler;
import org.eclipse.jdt.internal.debug.ui.threadgroups.JavaDebugTargetProxy;
import org.eclipse.jdt.internal.debug.ui.threadgroups.JavaThreadEventHandler;
import org.eclipse.jface.viewers.Viewer;

public class X10DebugTargetProxy extends JavaDebugTargetProxy implements
		IModelProxy {
	
	private X10ThreadEventHandler fThreadEventHandler;

	/**
	 * @param target
	 */
	public X10DebugTargetProxy(IDebugTarget target) {
		super(target);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.update.DebugTargetProxy#createEventHandlers()
	 */
	protected DebugEventHandler[] createEventHandlers() {
		fThreadEventHandler = new X10ThreadEventHandler(this);
		super.createEventHandlers();
		return new DebugEventHandler[] { new DebugTargetEventHandler(this), new X10ThreadEventHandler(this) };
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.update.DebugTargetProxy#installed(org.eclipse.jface.viewers.Viewer)
	 */
	public void installed(Viewer viewer) {
		super.installed(viewer);
		fThreadEventHandler.init(viewer);
	}


}
