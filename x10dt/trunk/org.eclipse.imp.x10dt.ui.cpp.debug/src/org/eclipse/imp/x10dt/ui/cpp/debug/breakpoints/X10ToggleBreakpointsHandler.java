package org.eclipse.imp.x10dt.ui.cpp.debug.breakpoints;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.imp.runtime.RuntimePlugin;
import org.eclipse.imp.services.IToggleBreakpointsHandler;
import org.eclipse.ptp.core.elements.IPJob;
import org.eclipse.ptp.debug.core.PDebugModel;
import org.eclipse.ptp.debug.core.model.IPBreakpoint;
import org.eclipse.ptp.debug.core.model.IPLineBreakpoint;
import org.eclipse.ptp.debug.ui.PTPDebugUIPlugin;
import org.eclipse.ptp.ui.model.IElementHandler;

/**
 * Manages breakpoint requests coming from IMP.
 * 
 * @author egeay
 */
public class X10ToggleBreakpointsHandler implements IToggleBreakpointsHandler {

  // --- Interface methods implementation
  
  public void clearLineBreakpoint(final IFile file, final int lineNumber) {
    try {
      final IBreakpoint[] breakpoints = PDebugModel.getPBreakpoints();
      for (final IBreakpoint breakpoint : breakpoints) {
        if (breakpoint instanceof IPLineBreakpoint) {
          final IPLineBreakpoint pLineBreakPoint = (IPLineBreakpoint) breakpoint;
          if (pLineBreakPoint.getMarker().getResource().equals(file) && pLineBreakPoint.getLineNumber() == lineNumber) {
            DebugPlugin.getDefault().getBreakpointManager().removeBreakpoint(pLineBreakPoint, true /* delete */);
          }
        }
      }
    } catch (CoreException e) {
    	RuntimePlugin.getInstance().logException(e.getMessage(), e);
    }
  }

  public void disableLineBreakpoint(final IFile file, final int lineNumber) {
    createLineBreakpoint(file, lineNumber, false);
  }

  public void enableLineBreakpoint(final IFile file, final int lineNumber) {
    createLineBreakpoint(file, lineNumber, true);
  }

  public void setLineBreakpoint(final IFile file, final int lineNumber) {
    createLineBreakpoint(file, lineNumber, true);
  }

  // --- Private code
  
  private void createLineBreakpoint(final IFile file, final int lineNumber, final boolean enable) {
    try {
      IPBreakpoint breakpoint = PDebugModel.createLineBreakpoint(file.getLocation().toOSString(), file, lineNumber, enable,
                                                                 0 /* ignoreCount */, null /* condition */, false /* register */,
                                                                 IElementHandler.SET_ROOT_ID /* set_id */, null /* job */);
	  DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(breakpoint);
	} catch (CoreException e) {
		RuntimePlugin.getInstance().logException(e.getMessage(), e);
	}
  }
  
  // --- Fields

//  private final UIDebugManager fUIDebugManager = PTPDebugUIPlugin.getUIDebugManager();

}
