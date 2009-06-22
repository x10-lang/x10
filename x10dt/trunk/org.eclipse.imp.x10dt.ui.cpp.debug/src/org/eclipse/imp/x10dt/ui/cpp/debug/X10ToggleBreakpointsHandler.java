package org.eclipse.imp.x10dt.ui.cpp.debug;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.imp.runtime.RuntimePlugin;
import org.eclipse.imp.services.IToggleBreakpointsHandler;
import org.eclipse.ptp.debug.core.model.IPLineBreakpoint;

public class X10ToggleBreakpointsHandler implements IToggleBreakpointsHandler {

	public void clearLineBreakpoint(IFile file, int lineNumber, IMarker marker) {
		IPLineBreakpoint bkpt = findBreakpoint(file, lineNumber);
		removeBreakpoint(bkpt);
	}

	public void disableLineBreakpoint(IFile file, int lineNumber, IMarker marker) {
		// TODO Auto-generated method stub
		int a = 0;
	}

	public void enableLineBreakpoint(IFile file, int lineNumber, IMarker marker) {
		// TODO Auto-generated method stub
		int a = 0;
	}

	public void setLineBreakpoint(IFile file, int lineNumber, IMarker marker) {
		IPLineBreakpoint bkpt = new X10LineBreakpointImpl(file, lineNumber, marker);
		addBreakpoint(bkpt);
	}

	private void addBreakpoint(IPLineBreakpoint bkpt) {
		try {
			DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(bkpt);
		} catch (CoreException e) {
			RuntimePlugin.getInstance().logException(e.getMessage(), e);
		}
	}

	private void removeBreakpoint(IPLineBreakpoint bkpt) {
		try {
			DebugPlugin.getDefault().getBreakpointManager().removeBreakpoint(bkpt, false);
		} catch (CoreException e) {
			RuntimePlugin.getInstance().logException(e.getMessage(), e);
		}
	}

	private IPLineBreakpoint findBreakpoint(IFile file, int lineNumber) {
		try {
			IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints();
			for (int i = 0; i < breakpoints.length; i++)
				if (breakpoints[i] instanceof IPLineBreakpoint) {
					IPLineBreakpoint b = (IPLineBreakpoint) breakpoints[i];
					if (b.getFileName().equals(file.getFullPath().toString()) && b.getLineNumber() == lineNumber)
						return b;
				}
		} catch (CoreException e) {
			RuntimePlugin.getInstance().logException(e.getMessage(), e);
		}
		return null;
	}
}
