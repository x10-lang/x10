package org.eclipse.imp.x10dt.ui.cpp.debug.breakpoints;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.imp.services.IToggleBreakpointsHandler;
import org.eclipse.ptp.core.PreferenceConstants;
import org.eclipse.ptp.core.elements.IPJob;
import org.eclipse.ptp.debug.core.IPSession;
import org.eclipse.ptp.debug.core.PDebugModel;
import org.eclipse.ptp.debug.core.model.IPLineBreakpoint;
import org.eclipse.ptp.debug.ui.PTPDebugUIPlugin;

/**
 * Manages breakpoint requests coming from IMP.
 * 
 * @author egeay
 */
public class X10ToggleBreakpointsHandler implements IToggleBreakpointsHandler {

  // --- Interface methods implementation

  public void clearLineBreakpoint(final IFile file, final int lineNumber) throws CoreException {
    for (final IBreakpoint breakpoint : PDebugModel.getPBreakpoints()) {
      if (breakpoint instanceof IPLineBreakpoint) {
        final IPLineBreakpoint pLineBreakPoint = (IPLineBreakpoint) breakpoint;
        if (pLineBreakPoint.getMarker().getResource().equals(file) && pLineBreakPoint.getLineNumber() == lineNumber) {
          // TODO: first build an array of breakpoints, then delete them all at once
          DebugPlugin.getDefault().getBreakpointManager().removeBreakpoints(new IBreakpoint[] { pLineBreakPoint }, 
                                                                            true /* delete */);
        }
      }
    }
  }

  public void disableLineBreakpoint(final IFile file, final int lineNumber) throws CoreException {
    createLineBreakpoint(file, lineNumber, false);
  }

  public void enableLineBreakpoint(final IFile file, final int lineNumber) throws CoreException {
    createLineBreakpoint(file, lineNumber, true);
  }

  public void setLineBreakpoint(final IFile file, final int lineNumber) throws CoreException {
    createLineBreakpoint(file, lineNumber, true);
  }

  // --- Private code

  private void createLineBreakpoint(final IFile file, final int lineNumber, final boolean enable) throws CoreException {
    final IPSession curSession = PTPDebugUIPlugin.getUIDebugManager().getCurrentSession();
    final String setId;
    final IPJob job;
    if (curSession == null) {
      setId = PreferenceConstants.SET_ROOT_ID;
      job = null;
    } else {
      setId = PreferenceConstants.SET_ROOT_ID;
      job = curSession.getJob();
    }
    PDebugModel.createLineBreakpoint(file.getLocation().toOSString(), file, lineNumber, enable, 0 /* ignoreCount */,
                                     "" /* condition */, true /* register */, setId, job); //$NON-NLS-1$
  }

}
