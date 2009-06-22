package org.eclipse.imp.x10dt.ui.cpp.debug.breakpoints;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.imp.runtime.RuntimePlugin;
import org.eclipse.imp.services.IToggleBreakpointsHandler;
import org.eclipse.ptp.core.elements.IPJob;
import org.eclipse.ptp.core.util.BitList;
import org.eclipse.ptp.debug.core.IPBreakpointManager;
import org.eclipse.ptp.debug.core.IPSession;
import org.eclipse.ptp.debug.core.PDebugModel;
import org.eclipse.ptp.debug.core.PTPDebugCorePlugin;
import org.eclipse.ptp.debug.core.model.IPBreakpoint;
import org.eclipse.ptp.debug.core.model.IPLineBreakpoint;
import org.eclipse.ptp.debug.core.pdi.IPDISession;
import org.eclipse.ptp.debug.core.pdi.PDIException;
import org.eclipse.ptp.debug.core.pdi.manager.IPDIBreakpointManager;
import org.eclipse.ptp.debug.core.pdi.manager.IPDITaskManager;
import org.eclipse.ptp.debug.core.pdi.model.IPDIBreakpoint;
import org.eclipse.ptp.debug.core.pdi.model.IPDILineBreakpoint;
import org.eclipse.ptp.debug.ui.PTPDebugUIPlugin;
import org.eclipse.ptp.ui.PTPUIPlugin;
import org.eclipse.ptp.ui.managers.JobManager;
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
//      IPJob job = ((JobManager)PTPUIPlugin.getDefault().getJobManager()).getJob();
//      if (job == null) { // no current job set - grab the first one and hope for the best
//    	  IPJob[] jobs = ((JobManager)PTPUIPlugin.getDefault().getJobManager()).getJobs();
//    	  if (jobs.length > 0)
//    		  job = jobs[0];
//      }
//      IPSession session = job != null ? PTPDebugCorePlugin.getDebugModel().getSession(job) : null;
//      IPBreakpointManager bpMgr = null;
//      BitList tasks = null;
//      if (session != null) {
//    	  bpMgr = session.getBreakpointManager();
//    	  tasks = session.getTasks();
//      }
      IPJob job = PTPDebugUIPlugin.getUIDebugManager().getJob();
      if (job != null) {
        IPSession session = PTPDebugCorePlugin.getDebugModel().getSession(job);
        if (session != null) {
          BitList tasks = session.getTasks();
          IPDITaskManager tm = session.getPDISession().getTaskManager();
          tm.setSuspendTasks(true, tasks); // suspend all tasks, so the breakpoint event gets added
        }
      }
      for (final IBreakpoint breakpoint : PDebugModel.getPBreakpoints()) {
        if (breakpoint instanceof IPLineBreakpoint) {
          final IPLineBreakpoint pLineBreakPoint = (IPLineBreakpoint) breakpoint;
          if (pLineBreakPoint.getMarker().getResource().equals(file) && pLineBreakPoint.getLineNumber() == lineNumber) {
            pLineBreakPoint.delete();
//            if (bpMgr != null) {
//              IPDITaskManager tm = session.getPDISession().getTaskManager();
//              BitList saveSuspendedTasks = tm.getSuspendedTasks().copy();
//              tm.setSuspendTasks(true, tasks);
//              bpMgr.deleteSetBreakpoints(tasks, new IPBreakpoint[] { pLineBreakPoint });
//              // restore old suspended tasks
//              tm.setSuspendTasks(false, tasks);
//              tm.setSuspendTasks(true, saveSuspendedTasks);
//            }
        	System.out.println("Deleted breakpoint at "+file.getLocation()+":"+lineNumber);
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
//      IPJob job = ((JobManager)PTPUIPlugin.getDefault().getJobManager()).getJob();
//      if (job == null) { // no current job set - grab the first one and hope for the best
//    	  IPJob[] jobs = ((JobManager)PTPUIPlugin.getDefault().getJobManager()).getJobs();
//    	  if (jobs.length > 0) {
//    		  job = jobs[0];
//    	  }
//      }
//      IPSession session = job != null ? PTPDebugCorePlugin.getDebugModel().getSession(job) : null;
      IPJob job = PTPDebugUIPlugin.getUIDebugManager().getJob();
      if (job != null) {
        IPSession session = PTPDebugCorePlugin.getDebugModel().getSession(job);
        if (session != null) {
          BitList tasks = session.getTasks();
          IPDITaskManager tm = session.getPDISession().getTaskManager();
          tm.setSuspendTasks(true, tasks); // suspend all tasks, so the breakpoint event gets added
        }
      }
      String path = file.getLocation().toOSString();
      IPLineBreakpoint bp = PDebugModel.createLineBreakpoint(path, file,
                                       lineNumber, enable, 0 /* ignoreCount */, "" /* condition */,
                                       true /* register */, IElementHandler.SET_ROOT_ID /* set_id */,
                                       job /* job */);
//      if (session != null) {
//        BitList tasks = session.getTasks();
//        IPDITaskManager tm = session.getPDISession().getTaskManager();
//        tm.setSuspendTasks(true, tasks);
//        session.getPDISession().getEventRequestManager().addEventRequest(
//        		session.getPDISession().getRequestFactory().getDeleteBreakpointRequest(tasks, bp, true));
//        getSession().getEventRequestManager().addEventRequest(
//        		session.getRequestFactory().getDeleteBreakpointRequest(suspendedTasks, bp, allowUpdate));
//        IPDIBreakpointManager bpMgr = session.getPDISession().getBreakpointManager();
//        IPDILineBreakpoint pdiBreakpoint = bpMgr.setLineBreakpoint(tasks, IPDIBreakpoint.REGULAR,
//        		bpMgr.createLineLocation(path, lineNumber),
//        		null, true, bp.isEnabled());
//        BitList saveSuspendedTasks = tm.getSuspendedTasks().copy();
//        tm.setSuspendTasks(true, tasks);
//        bpMgr.addSetBreakpoint(tasks, pdiBreakpoint);
//        // restore old suspended tasks
//        tm.setSuspendTasks(false, tasks);
//        tm.setSuspendTasks(true, saveSuspendedTasks);
//      }
      System.out.println("Added breakpoint at "+file.getLocation()+":"+lineNumber);
	} catch (CoreException e) {
		RuntimePlugin.getInstance().logException(e.getMessage(), e);
//	} catch (PDIException e) {
//		RuntimePlugin.getInstance().logException(e.getMessage(), e);
	}
  }
    
}
