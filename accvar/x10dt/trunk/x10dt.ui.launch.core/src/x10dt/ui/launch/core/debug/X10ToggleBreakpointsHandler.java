package x10dt.ui.launch.core.debug;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.services.IToggleBreakpointsHandler;
import org.eclipse.osgi.util.NLS;

import x10dt.core.X10DTCorePlugin;
import x10dt.ui.launch.core.LaunchCore;
import x10dt.ui.launch.core.Messages;

/**
 * Declares itself as the ToggleBreakpointsHandler for the X10 language. Depending on the project nature associated
 * with the requested file, this class delegates the actual breakpoint work to breakpoint handlers specific to an appropriate
 * Java or C++ breakpoint handler.
 * 
 * @author egeay
 * @author swanj
 */
public final class X10ToggleBreakpointsHandler implements IToggleBreakpointsHandler {

  /**
   * Creates the handler and initializes the handler delegates according to their back-end type.
   */
  public X10ToggleBreakpointsHandler() {
    final IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(HANDLER_EXTENSION_POINT_ID);
    if (extensionPoint != null) {
      for (final IConfigurationElement configElement : extensionPoint.getConfigurationElements()) {
        try {
          final String backEndType = configElement.getAttribute(BACKEND_TYPE_ATTR);
          if (CPP_BACKEND.equals(backEndType)) {
            this.fCPPNatureHandler = (IToggleBreakpointsHandler) configElement.createExecutableExtension(CLASS_ATTR);
          } else if (JAVA_BACKEND.equals(backEndType)) {
            this.fJavaNatureHandler = (IToggleBreakpointsHandler) configElement.createExecutableExtension(CLASS_ATTR);
          }
        } catch (CoreException except) {
          LaunchCore.log(except.getStatus());
        }
      }
    }
  }
  
  // --- Interface methods implementation

  public void clearLineBreakpoint(final IFile file, final int lineNumber) throws CoreException {
    getToggleBreakpointHandler(file).clearLineBreakpoint(file, lineNumber);
  }

  public void setLineBreakpoint(final IFile file, final int lineNumber) throws CoreException {
    getToggleBreakpointHandler(file).setLineBreakpoint(file, lineNumber);
  }

  public void disableLineBreakpoint(final IFile file, final int lineNumber) throws CoreException {
    getToggleBreakpointHandler(file).disableLineBreakpoint(file, lineNumber);
  }

  public void enableLineBreakpoint(final IFile file, final int lineNumber) throws CoreException {
    getToggleBreakpointHandler(file).enableLineBreakpoint(file, lineNumber);
  }
  
  // --- Private code
  
  private IToggleBreakpointsHandler getToggleBreakpointHandler(final IFile file) throws CoreException {
    if (file.getProject().hasNature(X10DTCorePlugin.X10_CPP_PRJ_NATURE_ID)) {
      if (this.fCPPNatureHandler == null) {
        throw new CoreException(new Status(IStatus.ERROR, LaunchCore.PLUGIN_ID, 
                                           NLS.bind(Messages.XTBH_NoHandlerForCppBackEnd, file.getProject().getName())));
      }
      return this.fCPPNatureHandler;
    } else if (file.getProject().hasNature(X10DTCorePlugin.X10_PRJ_JAVA_NATURE_ID)) {
      if (this.fJavaNatureHandler == null) {
        throw new CoreException(new Status(IStatus.ERROR, LaunchCore.PLUGIN_ID, 
                                           NLS.bind(Messages.XTBH_NoHandlerForJavaBackEnd, file.getProject().getName())));
      }
      return this.fJavaNatureHandler;
    } else {
      throw new CoreException(new Status(IStatus.ERROR, LaunchCore.PLUGIN_ID, 
                                         NLS.bind(Messages.XTBH_NoHandler, file.getProject().getName())));
    }
  }
  
  // --- Fields
  
  private IToggleBreakpointsHandler fJavaNatureHandler;

  private IToggleBreakpointsHandler fCPPNatureHandler;
  
  
  private static final String HANDLER_EXTENSION_POINT_ID = LaunchCore.PLUGIN_ID + ".x10ToggleBreakpointHandler"; //$NON-NLS-1$
  
  private static final String BACKEND_TYPE_ATTR = "backEndType"; //$NON-NLS-1$
  
  private static final String CLASS_ATTR = "class"; //$NON-NLS-1$
  
  private static final String CPP_BACKEND = "c++"; //$NON-NLS-1$
  
  private static final String JAVA_BACKEND = "java"; //$NON-NLS-1$
  
}
