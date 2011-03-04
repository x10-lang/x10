package x10dt.ui.launch.java.debug;

import org.eclipse.imp.java.hosted.debug.JavaBreakpointHandler;

import x10dt.ui.launch.core.debug.X10ToggleBreakpointsHandler;

/**
 * Implements the X10 toggle breakpoint handler for Java back-end.
 * 
 * @see X10ToggleBreakpointsHandler
 * 
 * @author egeay
 */
public class X10JavaToggleBreakpointHandler extends JavaBreakpointHandler {

  /**
   * Creates the X10 toggle breakpoint handler for Java back-end.
   */
  public X10JavaToggleBreakpointHandler() {
    super("x10"); //$NON-NLS-1$
  }
  
}
