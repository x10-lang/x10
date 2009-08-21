package org.eclipse.imp.x10dt.debug.ui.presentation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IDebugElement;
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
import org.eclipse.imp.x10dt.debug.model.IX10Activity;
import org.eclipse.imp.x10dt.debug.model.impl.X10DebugTargetAlt;
import org.eclipse.imp.x10dt.debug.model.impl.X10Thread;
import org.eclipse.imp.x10dt.debug.model.IX10Activity;

import sun.rmi.runtime.GetThreadPoolAction;

public class QuiescentThreadFilteringContentProvider extends
		DebugTargetContentProvider implements IElementContentProvider {

	private static Set previouslyActiveThreads = new HashSet();
	protected Object[] getAllChildren(Object parent, IPresentationContext context, IViewerUpdate monitor) throws CoreException {
		IDebugTarget target = ((IDebugElement)parent).getDebugTarget();
		X10DebugTargetAlt x10Target = (X10DebugTargetAlt)target;
		x10Target.createActivities();
		Object[] activityArray = (Object[]) x10Target.getActivities();
		
		String id = context.getId();
		if (id.equals(IDebugUIConstants.ID_DEBUG_VIEW)) {
			IThread[] threads = (IThread[])super.getAllChildren(parent, context, monitor);
			List<IThread> activeThreads = new ArrayList();
			for (IThread t: (IThread[])threads) {
				if (t instanceof X10Thread && ((X10Thread)t).getRunState()!=IX10Activity.X10ActivityState.Idle){
					synchronized(previouslyActiveThreads) {
						previouslyActiveThreads.add(t);
					}
				}
				if (previouslyActiveThreads.contains(t)) {
					activeThreads.add(t);
				}	
			}
//			return activeThreads.toArray(new IThread[0]);
			Object[] result = new Object[activityArray.length+activeThreads.size()];
			int i=0;
			for (; i<activityArray.length; i++) result[i]=activityArray[i];
			for (Object o: activeThreads) result[i++] = o;
			return result;
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

	// hasChildren then getChildCount, then getAllChildren causes a lot of recomputation.  There's gotta be a better way...
	protected int getChildCount(Object element, IPresentationContext context, IViewerUpdate monitor) throws CoreException {
		return getAllChildren(element,context, monitor).length;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.model.provisional.elements.ElementContentProvider#supportsContextId(java.lang.String)
	 */
	protected boolean supportsContextId(String id) {
		return IDebugUIConstants.ID_DEBUG_VIEW.equals(id);
	}
	public void update(final IHasChildrenUpdate[] updates) {
		Job job = new Job("has children update") { //$NON-NLS-1$
			protected IStatus run(IProgressMonitor monitor) {
				for (int i = 0; i < updates.length; i++) {
					IHasChildrenUpdate update = updates[i];
					if (!update.isCanceled()) {
						updateHasChildren(update);
					}
					update.done();					
				}
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.setRule(getRule(updates));
		job.schedule();	
	}
	protected void updateHasChildren(IHasChildrenUpdate update) {
		super.updateHasChildren(update);
	}

}
