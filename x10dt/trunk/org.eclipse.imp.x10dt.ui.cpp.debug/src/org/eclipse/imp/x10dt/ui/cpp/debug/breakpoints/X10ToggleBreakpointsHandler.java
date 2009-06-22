package org.eclipse.imp.x10dt.ui.cpp.debug.breakpoints;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.imp.services.IToggleBreakpointsHandler;
import org.eclipse.ptp.core.elements.IPJob;
import org.eclipse.ptp.debug.core.PDebugModel;
import org.eclipse.ptp.debug.core.model.IPBreakpoint;
import org.eclipse.ptp.debug.core.model.IPLineBreakpoint;
import org.eclipse.ptp.debug.internal.ui.UIDebugManager;
import org.eclipse.ptp.debug.ui.PTPDebugUIPlugin;

/**
 * Manages breakpoint requests coming from IMP.
 * 
 * @author egeay
 */
public class X10ToggleBreakpointsHandler implements IToggleBreakpointsHandler {

  // --- Interface methods implementation
  
  public void clearLineBreakpoint(final IFile file, final int lineNumber) throws CoreException {
    final String jobId = this.fUIDebugManager.getCurrentJobId();
    final String setId = this.fUIDebugManager.getCurrentSetId();
    final IPBreakpoint[] breakpoints = PDebugModel.findPBreakpoints(jobId, setId);
    for (final IPBreakpoint breakpoint : breakpoints) {
      if (breakpoint instanceof IPLineBreakpoint) {
        final IPLineBreakpoint pLineBreakPoint = (IPLineBreakpoint) breakpoint;
        if (pLineBreakPoint.getMarker().getResource().equals(file) && pLineBreakPoint.getLineNumber() == lineNumber) {
          DebugPlugin.getDefault().getBreakpointManager().removeBreakpoint(pLineBreakPoint, true /* delete */);
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
    final IPJob job = this.fUIDebugManager.getJob();
    final String setId = this.fUIDebugManager.getCurrentSetId();
    final IPBreakpoint breakpoint = PDebugModel.createLineBreakpoint(file.getLocation().toOSString(), file, lineNumber, 
                                                                     enable, 0 /* ignoreCount */, null /* condition */, 
                                                                     false /* register */, setId /* set_id */, job /* job */);
    DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(breakpoint);
  }
  
  // --- Fields

  private final UIDebugManager fUIDebugManager = PTPDebugUIPlugin.getUIDebugManager();

}
