package org.eclipse.imp.x10dt.ui.cpp.launch.launching;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTabGroup;
import org.eclipse.imp.x10dt.ui.cpp.launch.LaunchCore;
import org.eclipse.ptp.launch.ui.EnhancedSynchronizeTab;
import org.eclipse.ptp.launch.ui.EnvironmentTab;
import org.eclipse.ptp.launch.ui.ResourcesTab;

/**
 * Defines launch configuration for X10 programs using C++ back-end.
 * 
 * @author egeay
 */
public class CppLaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup 
                                            implements ILaunchConfigurationTabGroup {

  // --- Interface methods implementation
  
  public final void createTabs(final ILaunchConfigurationDialog dialog, final String mode) {
    final List<ILaunchConfigurationTab> tabs = new ArrayList<ILaunchConfigurationTab>();
    tabs.add(new ResourcesTab());
    tabs.add(new CppApplicationTab());
    if (ILaunchManager.DEBUG_MODE.equals(mode)) {
      final ILaunchConfigurationTab debugTab = getDebugTab();
      if (debugTab != null) {
        tabs.add(debugTab);
      }
    }
    tabs.add(new EnhancedSynchronizeTab());
    tabs.add(new EnvironmentTab());
    tabs.add(new CommonTab());
    
    setTabs(tabs.toArray(new ILaunchConfigurationTab[tabs.size()]));
  }
  
  // --- Private code

  private ILaunchConfigurationTab getDebugTab() {
    final IExtensionPoint extPt = Platform.getExtensionRegistry().getExtensionPoint(LaunchCore.PLUGIN_ID, DEBUGGER_TAB_EP_ID);
    if (extPt != null) {
      final IConfigurationElement[] infos = extPt.getConfigurationElements();
      if (infos.length > 0) {
        final IConfigurationElement configElement = infos[0]; // Can only have 1 by definition of the schema
        try {
          return (ILaunchConfigurationTab) configElement.createExecutableExtension(CLASS_ATTR);
        } catch (CoreException except) {
          LaunchCore.log(except.getStatus());
        }
      }
    }
    return null;
  }

  // --- Fields
  
  private static final String DEBUGGER_TAB_EP_ID = "debuggerTab"; //$NON-NLS-1$
  
  private static final String CLASS_ATTR = "class";  //$NON-NLS-1$
  
}
