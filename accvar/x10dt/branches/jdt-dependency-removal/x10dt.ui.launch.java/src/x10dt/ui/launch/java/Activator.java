package x10dt.ui.launch.java;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationListener;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.ptp.core.PTPCorePlugin;
import org.eclipse.ptp.core.elementcontrols.IPUniverseControl;
import org.eclipse.ptp.core.elementcontrols.IResourceManagerControl;
import org.eclipse.ptp.launch.PTPLaunchPlugin;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteConnectionManager;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.PTPRemoteCorePlugin;
import org.eclipse.ptp.remote.remotetools.core.RemoteToolsServices;
import org.eclipse.ptp.remotetools.environment.core.ITargetElement;
import org.eclipse.ptp.remotetools.environment.core.TargetTypeElement;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;
import org.eclipse.ptp.services.core.IService;
import org.eclipse.ptp.services.core.IServiceConfiguration;
import org.eclipse.ptp.services.core.IServiceProvider;
import org.eclipse.ptp.services.core.ServiceModelManager;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import x10dt.ui.launch.core.utils.PTPConstants;
import x10dt.ui.launch.java.launching.rms.MultiVMServiceProvider;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements ILaunchConfigurationListener {

	/**
	 * The unique plugin id for <b>x10dt.ui.launch.java</b> project.
	 */
	public static final String PLUGIN_ID = "x10dt.ui.launch.java"; //$NON-NLS-1$

	/**
	 * Unique id for the Java Builder.
	 */
	public static final String BUILDER_ID = PLUGIN_ID + ".X10JavaBuilder"; //$NON-NLS-1$
	
	// --- Public services
	
  /**
   * Returns the context associated with the bundle for <b>x10dt.ui.launch.java</b> plugin.
   * 
   * @return A non-null value if the plugin is started, otherwise <b>null</b>.
   */
  public static Activator getDefault() {
    return plugin;
  }
  
  // --- ILaunchConfigurationListener's interface methods implementation
  
  public void launchConfigurationAdded(final ILaunchConfiguration configuration) {
  }

  public void launchConfigurationChanged(final ILaunchConfiguration configuration) {
  }

  public void launchConfigurationRemoved(final ILaunchConfiguration configuration) {
    try {
      final ServiceModelManager serviceModelManager = ServiceModelManager.getInstance();
      final IService service = serviceModelManager.getService(PTPConstants.LAUNCH_SERVICE_ID);
      for (final IServiceConfiguration serviceConfiguration : serviceModelManager.getConfigurations()) {
        if (configuration.getName().equals(serviceConfiguration.getName())) {
          final IServiceProvider serviceProvider = serviceConfiguration.getServiceProvider(service);
          if (serviceProvider instanceof MultiVMServiceProvider) {
            final IPUniverseControl universe = (IPUniverseControl) PTPCorePlugin.getDefault().getUniverse();
            for (final IResourceManagerControl resourceManager : universe.getResourceManagerControls()) {
              final IResourceManagerConfiguration rmConf = resourceManager.getConfiguration();
              resourceManager.shutdown();
              
              final PTPRemoteCorePlugin rmPlugin = PTPRemoteCorePlugin.getDefault();
              final IRemoteServices rmServices = rmPlugin.getRemoteServices(rmConf.getRemoteServicesId());
              final IRemoteConnectionManager rmConnManager = rmServices.getConnectionManager();
              final IRemoteConnection rmConnection = rmConnManager.getConnection(rmConf.getConnectionName());
              rmConnManager.removeConnection(rmConnection);
              final TargetTypeElement targetTypeElement = RemoteToolsServices.getTargetTypeElement();
              for (final ITargetElement targetElement : targetTypeElement.getElements()) {
                if (targetElement.getName().equals(rmConf.getConnectionName())) {
                  targetTypeElement.removeElement(targetElement);
                  break;
                }
              }
              
              if (rmConf.getUniqueName().equals(serviceProvider.getProperties().get("uniqName"))) { //$NON-NLS-1$
                serviceModelManager.remove(serviceConfiguration);
                return;
              }
              resourceManager.getConfiguration().getConnectionName();
            }
          }
        }
      }
    } catch (CoreException except) {
      // Let's forget.
    }
  }
	
	// --- Overridden methods

	public void start(final BundleContext context) throws Exception {
		super.start(context);
		final ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		manager.addLaunchConfigurationListener(this);
		// Let's activate magically the PTP core plugin to avoid some late ClassCircularityError !?
		PTPLaunchPlugin.getDefault();
		plugin = this;
	}

	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	
	// --- Fields
	
  private static Activator plugin;
  
  private static final String LAUNCH_CONF_TYPE = "x10dt.ui.launch.java.launching.X10LaunchConfigurationType"; //$NON-NLS-1$

}
