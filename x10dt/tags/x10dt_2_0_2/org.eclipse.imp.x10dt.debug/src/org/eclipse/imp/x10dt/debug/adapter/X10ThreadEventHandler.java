package org.eclipse.imp.x10dt.debug.adapter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementContentProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IViewerUpdate;
import org.eclipse.debug.internal.ui.viewers.provisional.AbstractModelProxy;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.imp.x10dt.debug.model.IX10StackFrame;
import org.eclipse.imp.x10dt.debug.model.impl.X10Thread;
import org.eclipse.imp.x10dt.debug.ui.presentation.AsyncStackFrameFilteringContentProvider;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.ui.snippeteditor.ScrapbookLauncher;
import org.eclipse.jdt.internal.debug.ui.threadgroups.JavaThreadEventHandler;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;

public class X10ThreadEventHandler extends JavaThreadEventHandler {
//	private static IElementContentProvider _filteringStackFrameProvider = new AsyncStackFrameFilteringContentProvider();
	
	public X10ThreadEventHandler(AbstractModelProxy proxy) {
		super(proxy);
	}

	protected boolean handlesEvent(DebugEvent event) {
		if (super.handlesEvent(event)) {
			Object source = event.getSource();
			if (source instanceof X10Thread) {
				X10Thread thread = (X10Thread) source;
				ILaunch launch = thread.getLaunch();
				if (launch != null) {
					if (launch.getAttribute(ScrapbookLauncher.SCRAPBOOK_LAUNCH) != null) {
						if (event.getKind() == DebugEvent.SUSPEND) {
							try {
								IX10StackFrame frame = (IX10StackFrame) thread.getTopStackFrame();
								if (frame.getDeclaringTypeName().startsWith("org.eclipse.jdt.internal.debug.ui.snippeteditor.ScrapbookMain")) { //$NON-NLS-1$
									return false;
								}
							} catch (DebugException e) {
							}
						}
					}
				}
			}
		} else {
			return false;
		}
		return true;
	}

	/**
	 * Returns the number of children the given thread has in the view.
	 * 
	 * @param thread thread
	 * @return number of children
	 */
	protected int childCount(IThread thread) {
		try {
			IJavaThread jThread = (IJavaThread) thread;
			int count = getFilteredChildren(jThread.getStackFrames()).length;
			if (isDisplayMonitors()) {
				if (((IJavaDebugTarget)thread.getDebugTarget()).supportsMonitorInformation()) {
					count += jThread.getOwnedMonitors().length;
					if (jThread.getContendedMonitor() != null) {
						count++;
					}
				} else {
					// make room for the 'no monitor info' element
					count++;
				}
			}
			return count;
		} catch (DebugException e) {
		} catch (CoreException e) {
		}
		return -1;
	}
	
	protected Object[] getFilteredChildren(IStackFrame[] stackFrames) throws CoreException {
		List<IStackFrame> userFrames = new ArrayList();
		for (Object f: stackFrames) {
			String reftype=((JDIStackFrame)f).getReferenceType().getName();
			if (!(reftype.contains("x10.runtime") ||
					reftype.contains("x10.lang") ||
					reftype.contains("x10.array") ||
					reftype.contains("java.util.concurrent") ||
					reftype.contains("java.lang.Thread"))) {
				userFrames.add((IStackFrame)f);
			}
		}	
		return userFrames.toArray(new IStackFrame[0]);
	}
	private Tree fTree;
	protected void init(Viewer viewer) {
		Control control = viewer.getControl();
		if (control instanceof Tree) {
			fTree = (Tree) control;
			fTree.getDisplay().asyncExec(new Runnable() {
				public void run() {
					fTree.addTreeListener(X10ThreadEventHandler.this);
				}
			});
		}
	}
	

}
