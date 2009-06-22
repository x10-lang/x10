package org.eclipse.imp.x10dt.ui.cpp.launch.launching;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTabGroup;
import org.eclipse.ptp.launch.ui.ApplicationTab;
import org.eclipse.ptp.launch.ui.EnhancedSynchronizeTab;
import org.eclipse.ptp.launch.ui.EnvironmentTab;
import org.eclipse.ptp.launch.ui.ResourcesTab;

/**
 * Defines launch configuration for X10 programs using C++ back-end.
 * 
 * @author egeay
 */
public final class CppLaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup 
                                                  implements ILaunchConfigurationTabGroup {

  // --- Interface methods implementation
  
  public void createTabs(final ILaunchConfigurationDialog dialog, final String mode) {
    if (ILaunchManager.RUN_MODE.equals(mode)) {
      setTabs(new ILaunchConfigurationTab[] {
        new ResourcesTab(),
        new CppApplicationTab(),
        new EnhancedSynchronizeTab(),
        new EnvironmentTab(),
        new CommonTab()
      });
    } else if (ILaunchManager.DEBUG_MODE.equals(mode)) {
      setTabs(new ILaunchConfigurationTab[] {
        new ResourcesTab(),
        new ApplicationTab(),
        new EnhancedSynchronizeTab(),
        new EnvironmentTab(),
        new CommonTab()
      });
    }
  }

}
