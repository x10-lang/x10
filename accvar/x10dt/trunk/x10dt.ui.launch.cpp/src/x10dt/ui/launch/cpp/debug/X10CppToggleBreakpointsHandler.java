package x10dt.ui.launch.cpp.debug;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.imp.services.IToggleBreakpointsHandler;
import org.eclipse.ptp.core.PreferenceConstants;
import org.eclipse.ptp.core.elements.IPJob;
import org.eclipse.ptp.debug.core.PDebugModel;
import org.eclipse.ptp.debug.core.model.IPLineBreakpoint;
import org.eclipse.ptp.debug.ui.PTPDebugUIPlugin;

import x10dt.ui.launch.core.debug.X10ToggleBreakpointsHandler;

/**
 * Implements the X10 toggle breakpoint handler for C++ back-end.
 * 
 * @see X10ToggleBreakpointsHandler
 * 
 * @author egeay
 * @author swanj
 */
public class X10CppToggleBreakpointsHandler implements IToggleBreakpointsHandler {

  // --- Interface methods implementation

  public void clearLineBreakpoint(final IFile file, final int lineNumber) throws CoreException {
    for (final IPLineBreakpoint breakpoint : PDebugModel.lineBreakpointsExists(file.getProjectRelativePath().toOSString(), 
                                                                               file, lineNumber)) {
      breakpoint.delete();
    }
  }

  public void setLineBreakpoint(final IFile file, final int lineNumber) throws CoreException {
    IPJob job = null;
    try {
      job = PTPDebugUIPlugin.getUIDebugManager().getCurrentSession().getJob();
    } catch (Exception except) {
      // Simply ignores.
    }
    PDebugModel.createLineBreakpoint(file.getProjectRelativePath().toOSString(), file, lineNumber, true /* enabled */, 
                                     0 /* ignoreCount */, "" /* condition */, true /* register */, //$NON-NLS-1$ 
                                     PreferenceConstants.SET_ROOT_ID, job);
  }

  public void disableLineBreakpoint(final IFile file, final int lineNumber) throws CoreException {
    for (final IPLineBreakpoint breakpoint : PDebugModel.lineBreakpointsExists(file.getProjectRelativePath().toOSString(), 
                                                                               file, lineNumber)) {
      breakpoint.setEnabled(false);
    }
  }

  public void enableLineBreakpoint(final IFile file, final int lineNumber) throws CoreException {
    for (final IPLineBreakpoint breakpoint : PDebugModel.lineBreakpointsExists(file.getProjectRelativePath().toOSString(), 
                                                                               file, lineNumber)) {
      breakpoint.setEnabled(true);
    }
  }
  
}
