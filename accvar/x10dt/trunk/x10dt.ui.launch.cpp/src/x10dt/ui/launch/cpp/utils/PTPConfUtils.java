/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.utils;

import static org.eclipse.ptp.rm.ibm.ll.core.IBMLLPreferenceConstants.LL_NO;
import static org.eclipse.ptp.rm.ibm.ll.core.IBMLLPreferenceConstants.LL_YES;
import static org.eclipse.ptp.rm.ibm.pe.core.PEPreferenceConstants.OPTION_NO;
import static org.eclipse.ptp.rm.ibm.pe.core.PEPreferenceConstants.OPTION_YES;
import static org.eclipse.ptp.rm.ibm.pe.core.PEPreferenceConstants.TRACE_DETAIL;
import static org.eclipse.ptp.rm.ibm.pe.core.PEPreferenceConstants.TRACE_FUNCTION;
import static org.eclipse.ptp.rm.ibm.pe.core.PEPreferenceConstants.TRACE_NOTHING;
import static x10dt.ui.launch.core.utils.PTPConstants.LOCAL_CONN_SERVICE_ID;
import static x10dt.ui.launch.core.utils.PTPConstants.REMOTE_CONN_SERVICE_ID;
import static x10dt.ui.launch.cpp.platform_conf.CLoadLevelerProxyMsgs.ARGS;
import static x10dt.ui.launch.cpp.platform_conf.CLoadLevelerProxyMsgs.ERROR;
import static x10dt.ui.launch.cpp.platform_conf.CLoadLevelerProxyMsgs.FATAL;
import static x10dt.ui.launch.cpp.platform_conf.CLoadLevelerProxyMsgs.INFO;
import static x10dt.ui.launch.cpp.platform_conf.CLoadLevelerProxyMsgs.TRACE;
import static x10dt.ui.launch.cpp.platform_conf.CLoadLevelerProxyMsgs.WARNING;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.utils.Pair;
import org.eclipse.ptp.core.PTPCorePlugin;
import org.eclipse.ptp.core.elementcontrols.IPUniverseControl;
import org.eclipse.ptp.core.elementcontrols.IResourceManagerControl;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.core.events.IChangedResourceManagerEvent;
import org.eclipse.ptp.core.events.INewResourceManagerEvent;
import org.eclipse.ptp.core.events.IRemoveResourceManagerEvent;
import org.eclipse.ptp.core.listeners.IModelManagerChildListener;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteConnectionManager;
import org.eclipse.ptp.remote.core.IRemoteProxyOptions;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.PTPRemoteCorePlugin;
import org.eclipse.ptp.remote.core.exception.RemoteConnectionException;
import org.eclipse.ptp.remote.remotetools.core.RemoteToolsServices;
import org.eclipse.ptp.remotetools.environment.EnvironmentPlugin;
import org.eclipse.ptp.remotetools.environment.core.ITargetElement;
import org.eclipse.ptp.remotetools.environment.core.TargetElement;
import org.eclipse.ptp.remotetools.environment.core.TargetEnvironmentManager;
import org.eclipse.ptp.remotetools.environment.core.TargetTypeElement;
import org.eclipse.ptp.remotetools.utils.verification.ControlAttributes;
import org.eclipse.ptp.rm.core.rmsystem.IRemoteResourceManagerConfiguration;
import org.eclipse.ptp.rm.core.rmsystem.IToolRMConfiguration;
import org.eclipse.ptp.rm.ibm.ll.core.rmsystem.IIBMLLResourceManagerConfiguration;
import org.eclipse.ptp.rm.ibm.pe.core.rmsystem.IPEResourceManagerConfiguration;
import org.eclipse.ptp.rm.mpi.openmpi.core.rmsystem.IOpenMPIResourceManagerConfiguration;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;
import org.eclipse.ptp.services.core.IService;
import org.eclipse.ptp.services.core.IServiceConfiguration;
import org.eclipse.ptp.services.core.IServiceProvider;
import org.eclipse.ptp.services.core.IServiceProviderDescriptor;
import org.eclipse.ptp.services.core.ServiceModelManager;
import org.eclipse.ptp.services.core.ServicesCorePlugin;

import x10dt.ui.launch.core.Constants;
import x10dt.ui.launch.core.utils.PTPConstants;
import x10dt.ui.launch.cpp.platform_conf.EClusterMode;
import x10dt.ui.launch.cpp.platform_conf.ELLTemplateOpt;
import x10dt.ui.launch.cpp.platform_conf.ICIConfOptionsVisitor;
import x10dt.ui.launch.cpp.platform_conf.ICommunicationInterfaceConf;
import x10dt.ui.launch.cpp.platform_conf.IConnectionConf;
import x10dt.ui.launch.cpp.platform_conf.ILoadLevelerConf;
import x10dt.ui.launch.cpp.platform_conf.IMPICH2InterfaceConf;
import x10dt.ui.launch.cpp.platform_conf.IMessagePassingInterfaceConf;
import x10dt.ui.launch.cpp.platform_conf.IOpenMPIInterfaceConf;
import x10dt.ui.launch.cpp.platform_conf.IParallelEnvironmentConf;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;

/**
 * Utility methods for identifying PTP constructs from equivalent X10 Platform Configuration information.
 * 
 * @author egeay
 */
public final class PTPConfUtils {
  
  /**
   * Creates a PTP target element and remote connection given some PTP connection parameters.
   * 
   * @param connectionName The connection name to create.
   * @param attributes The connection attributes as expected by PTP.
   * @return A non-null target element associated to the connection configuration transmitted.
   * @throws RemoteConnectionException Occurs if we failed in creating the remote connection.
   * @throws CoreException Occurs during configuration update of a PTP target element.
   */
  public static ITargetElement createRemoteConnection(final String connectionName, 
                                                      final ControlAttributes attributes) throws RemoteConnectionException, 
                                                                                                 CoreException {
    final PTPRemoteCorePlugin plugin = PTPRemoteCorePlugin.getDefault();
    final IRemoteConnectionManager rmConnManager = plugin.getRemoteServices(REMOTE_CONN_SERVICE_ID).getConnectionManager();
    final TargetTypeElement targetTypeElement = RemoteToolsServices.getTargetTypeElement();
    final String id = EnvironmentPlugin.getDefault().getEnvironmentUniqueID();
    ITargetElement finalTargetElement = null;
    for (final ITargetElement targetElement : targetTypeElement.getElements()) {
      if (targetElement.getName().equals(connectionName)) {
        targetElement.setAttributes(attributes);
        targetElement.getControl().updateConfiguration();
        finalTargetElement = targetElement;
        break;
      }
    }
    if (finalTargetElement == null) {
      finalTargetElement = new TargetElement(targetTypeElement, connectionName, attributes, id);
      targetTypeElement.addElement((TargetElement) finalTargetElement);
      rmConnManager.getConnections();
    }
    return finalTargetElement;
  }
  
  /**
   * Creates a PTP resource manager from the X10 platform configuration transmitted.
   * 
   * @param platformConf The X10 platform configuration to use.
   * @return A non-null resource manager if the creation went smoothly.
   * @throws RemoteConnectionException Occurs if we could not create a new valid remote connection for the resource manager.
   * @throws CoreException Occurs if we could not create the service provider from the plugin extension mechanism, or
   * if the update of a PTP target element failed.
   */
  public static IResourceManager createResourceManager(final IX10PlatformConf platformConf) throws RemoteConnectionException,
  																																																 CoreException {
    final ICommunicationInterfaceConf commConf = platformConf.getCommunicationInterfaceConf();
    final ServiceModelManager serviceModelManager = ServiceModelManager.getInstance();
    final Pair<IService, IServiceProvider> pair = createProvider(serviceModelManager, commConf);
    if ((pair == null) || (pair.second == null)) {
      return null;
    }
    final IRemoteResourceManagerConfiguration rmConf = (IRemoteResourceManagerConfiguration) pair.second;
    
    commConf.visitInterfaceOptions(new CommunicationInterfaceInitializer(rmConf));
    
    final IConnectionConf connConf = platformConf.getConnectionConf();
    if (! connConf.isLocal() && findTargetElement(connConf.getConnectionName()) == null) {
      createRemoteConnection(connConf.getConnectionName(), connConf.getAttributes());
    }
    rmConf.setConnectionName(connConf.getConnectionName());
    rmConf.setLocalAddress(connConf.getLocalAddress());
    if (connConf.shouldUsePortForwarding()) {
      rmConf.setOptions((rmConf.getOptions() & ~IRemoteProxyOptions.STDIO) | IRemoteProxyOptions.PORT_FORWARDING);
    }
    rmConf.setName(platformConf.getName());
    rmConf.setRemoteServicesId(connConf.isLocal() ? PTPConstants.LOCAL_CONN_SERVICE_ID : PTPConstants.REMOTE_CONN_SERVICE_ID);
    pair.second.putString(PLATFORM_CONF_ID, platformConf.getId());
    
    final ResourceManagerListener listener = new ResourceManagerListener();
    PTPCorePlugin.getDefault().getModelManager().addListener(listener);
    
    final IServiceConfiguration serviceConf = serviceModelManager.newServiceConfiguration(platformConf.getName());
    serviceConf.setServiceProvider(pair.first, pair.second);
    serviceModelManager.addConfiguration(serviceConf);
    
    while (! listener.hasResourceManager()) ;
  
    PTPCorePlugin.getDefault().getModelManager().removeListener(listener);
    
    return listener.getResourceManager();
  }
  
  /**
   * Deletes the resource managers for the platform configuration given.
   * 
   * @param platformConf The platform configuration to consider.
   * @throws CoreException Occurs if we could not shutdown the existing resource manager.
   */
  public static void deleteResourceManager(final IX10PlatformConf platformConf) throws CoreException {
    final IResourceManager sameRMName = findResourceManagerByName(platformConf.getName());
    if (sameRMName != null) {
      sameRMName.shutdown();
      final IResourceManagerConfiguration rmConf = ((IResourceManagerControl) sameRMName).getConfiguration();
      final ServiceModelManager modelManager = ServiceModelManager.getInstance();
      loop:
      for (final IServiceConfiguration serviceConf : modelManager.getConfigurations()) {
        for (final IService service : serviceConf.getServices()) {
          if (PTPConstants.RUNTIME_SERVICE_CATEGORY_ID.equals(service.getCategory().getId()) &&
              service.getId().equals(platformConf.getCommunicationInterfaceConf().getServiceModeId())) {
            final IServiceProvider provider = serviceConf.getServiceProvider(service);
            if (rmConf.getUniqueName().equals(provider.getProperties().get("uniqName"))) { //$NON-NLS-1$
              modelManager.remove(serviceConf);
              break loop;
            }
          }
        }
      }
    }
  }
  
  /**
   * Returns the communication interface type name from the id encapsulated in the configuration.
   * 
   * @param commInterfaceConf The communication interface configuration to consider.
   * @return A non-null non-empty string.
   */
  public static String getCommunicationInterfaceTypeName(final ICommunicationInterfaceConf commInterfaceConf) {
    final ServiceModelManager serviceModelManager = ServiceModelManager.getInstance();
    for (final IService service : serviceModelManager.getServices()) {
      if (PTPConstants.RUNTIME_SERVICE_CATEGORY_ID.equals(service.getCategory().getId()) &&
          service.getId().equals(commInterfaceConf.getServiceModeId())) {
        for (final IServiceProviderDescriptor providerDescriptor : service.getProviders()) {
          if (providerDescriptor.getId().equals(commInterfaceConf.getServiceTypeId())) {
            return providerDescriptor.getName();
          }
        }
      }
    }
    throw new AssertionError();
  }
  
  /**
   * Finds the resource manager associated with the platform configuration name.
   * 
   * @param platformConfName The platform configuration name to consider.
   * @return A non-null resource manager if we found a match, <b>null</b> otherwise.
   */
  public static IResourceManager findResourceManagerByName(final String platformConfName) {
    final IPUniverseControl universe = (IPUniverseControl) PTPCorePlugin.getDefault().getUniverse();
    
    for (final IResourceManagerControl resourceManager : universe.getResourceManagerControls()) {
      final IResourceManagerConfiguration rmConf = resourceManager.getConfiguration();
      
      if (rmConf.getName().equals(platformConfName)) {
        return resourceManager;
      }
    }
    
    return null;
  }
  
  /**
   * Finds the resource manager associated with the platform configuration id.
   * 
   * @param platformConf The platform configuration to consider.
   * @return A non-null resource manager if we found a match, <b>null</b> otherwise.
   */
  public static IResourceManager findResourceManagerById(final IX10PlatformConf platformConf) {
    final IPUniverseControl universe = (IPUniverseControl) PTPCorePlugin.getDefault().getUniverse();

    for (final IResourceManagerControl resourceManager : universe.getResourceManagerControls()) {
      final IServiceProvider serviceProvider = (IServiceProvider) resourceManager.getConfiguration();
      if (platformConf.getId().equals(serviceProvider.getString(PLATFORM_CONF_ID, Constants.EMPTY_STR))) {
        return resourceManager;
      }
    }
    
    return null;
  }
  
  /**
   * Returns an existing resource manager that is equivalent to information encapsulated in the platform configuration or
   * creates a new one.
   * 
   * <p>
   * Note that in the case or creation, a previous manager that would happen to share the same name will be removed first.
   * 
   * @param platformConf The platform configuration to use.
   * @return A non-null resource manager relevant for the platform configuration provided.
   * @throws RemoteConnectionException
   * @throws CoreException Occurs if we could not create the service provider from the plugin extension mechanism.
   */
  public static IResourceManager getResourceManager(final IX10PlatformConf platformConf) throws RemoteConnectionException,  
                                                                                                CoreException {
    final IPUniverseControl universe = (IPUniverseControl) PTPCorePlugin.getDefault().getUniverse();

    for (final IResourceManagerControl resourceManager : universe.getResourceManagerControls()) {
      final IServiceProvider serviceProvider = (IServiceProvider) resourceManager.getConfiguration();
      if (platformConf.getId().equals(serviceProvider.getString(PLATFORM_CONF_ID, Constants.EMPTY_STR))) {
        final ICommunicationInterfaceConf communicationInterfaceConf = platformConf.getCommunicationInterfaceConf();
        final IResourceManagerConfiguration rmConf = (IResourceManagerConfiguration) serviceProvider;
        
        if (rmConf.getResourceManagerId().equals(communicationInterfaceConf.getServiceTypeId()) &&
            ((IServiceProvider) rmConf).getServiceId().equals(communicationInterfaceConf.getServiceModeId())) {
          if (communicationInterfaceConf.hasSameCommunicationInterfaceInfo(rmConf)) {
            if (platformConf.getConnectionConf().isLocal()) {
              if (PTPConstants.LOCAL_CONN_SERVICE_ID.equals(rmConf.getRemoteServicesId())) {
                return resourceManager;
              }
            } else if (PTPConstants.REMOTE_CONN_SERVICE_ID.equals(rmConf.getRemoteServicesId())) {
              final PTPRemoteCorePlugin plugin = PTPRemoteCorePlugin.getDefault();
              final IRemoteServices remoteServices = plugin.getRemoteServices(PTPConstants.REMOTE_CONN_SERVICE_ID);

              final IRemoteConnection rmConn = remoteServices.getConnectionManager().getConnection(rmConf.getConnectionName());
              if ((rmConn != null) && platformConf.getConnectionConf().hasSameConnectionInfo(rmConn)) {
                return resourceManager;
              }
            }
          }
        }
        
        break;
      }
    }

    deleteResourceManager(platformConf);
    return createResourceManager(platformConf);
  }
  
  /**
   * Finds a PTP remote connection equivalent to the connection configuration transmitted.
   * 
   * @param connectionConf The connection configuration to consider.
   * @return A non-null remote connection instance if one has been found, <b>null</b> otherwise.
   */
  public static IRemoteConnection findRemoteConnection(final IConnectionConf connectionConf) {
    final IPUniverseControl universe = (IPUniverseControl) PTPCorePlugin.getDefault().getUniverse();
    final PTPRemoteCorePlugin plugin = PTPRemoteCorePlugin.getDefault();
    
    final String rmServicesId = connectionConf.isLocal() ? LOCAL_CONN_SERVICE_ID : REMOTE_CONN_SERVICE_ID;
    final IRemoteConnectionManager rmConnManager = plugin.getRemoteServices(rmServicesId).getConnectionManager();
    
    final IRemoteConnection rmConnOnName = rmConnManager.getConnection(connectionConf.getConnectionName());
    if (rmConnOnName != null) {
      if (connectionConf.hasSameConnectionInfo(rmConnOnName)) {
        return rmConnOnName;
      }
    }
      
    for (final IResourceManagerControl resourceManager : universe.getResourceManagerControls()) {
      final IResourceManagerConfiguration rmConf = resourceManager.getConfiguration();
      
      final IRemoteConnection rmConn = rmConnManager.getConnection(rmConf.getConnectionName());
      if ((rmConn != null) && connectionConf.hasSameConnectionInfo(rmConn)) {
        return rmConn;
      }
    }
    
    return null;
  }
  
  /**
   * Looks for a PTP target element matching the name provided.
   * 
   * @param targetName The name to look for.
   * @return The target element if it has been found, otherwise <b>null</b>.
   */
  public static ITargetElement findTargetElement(final String targetName) {
    for (final ITargetElement targetElement : getTargetElements()) {
      if (targetElement.getName().equals(targetName)) {
        return targetElement;
      }
    }
    return null;
  }
  
  /**
   * Returns all the PTP target elements present in memory.
   * 
   * @return A non-null but possibly empty array.
   */
  public static ITargetElement[] getTargetElements() {
    final ITargetElement[] targetElements = (fTargetEnvManager == null) ? null : fTargetEnvManager.getConfigElements();
    return (targetElements == null) ? new ITargetElement[0] : targetElements;
  }
  
  /**
   * Tries to remove the target element matching the given name provided. If there is no matching, then this method is a no-op.
   * 
   * @param targetName The name of the target element to remove.
   */
  public static void removeTargetElement(final String targetName) {
    final ITargetElement targetElement = findTargetElement(targetName);
    if (targetElement != null) {
      RemoteToolsServices.getTargetTypeElement().removeElement(targetElement);
    }
  }
  
  // --- Private code
  
  private PTPConfUtils() {}
  
  private static Pair<IService, IServiceProvider> createProvider(final ServiceModelManager serviceModelManager,
                                                                 final ICommunicationInterfaceConf conf) throws CoreException {
    final IExtensionRegistry extRegistry = Platform.getExtensionRegistry();
    
    for (final IService service : serviceModelManager.getServices()) {
      if (PTPConstants.RUNTIME_SERVICE_CATEGORY_ID.equals(service.getCategory().getId()) &&
          service.getId().equals(conf.getServiceModeId())) {
        final IServiceProviderDescriptor descriptor = service.getProviderDescriptor(conf.getServiceTypeId());
        if (descriptor != null) {
          final IExtensionPoint extensionPoint = extRegistry.getExtensionPoint(ServicesCorePlugin.PLUGIN_ID, 
                                                                               PROVIDER_EXTENSION_ID);
          if (extensionPoint != null) {
            for (IExtension extension : extensionPoint.getExtensions()) {
              for (IConfigurationElement element : extension.getConfigurationElements()) {
                if (element.getName().equals(PROVIDER_ELEMENT_NAME)) {
                  if (element.getAttribute(ATTR_ID).equals(descriptor.getId())) {
                  	final IServiceProvider provider = (IServiceProvider) element.createExecutableExtension(ATTR_CLASS);
                  	provider.setDescriptor(descriptor);
                  	return new Pair<IService, IServiceProvider>(service, provider);
                  }
                }
              }
            }
          }
        }
      }
    }
    return null;
  }
  
  // --- Private classes
  
  private static final class ResourceManagerListener implements IModelManagerChildListener {
    
    // --- Interface methods implementation

    public void handleEvent(final IChangedResourceManagerEvent event) {
    }

    public void handleEvent(final INewResourceManagerEvent event) {
      this.fResourceManager = event.getResourceManager();
    }

    public void handleEvent(final IRemoveResourceManagerEvent event) {
    }
    
    // --- Internal services
    
    IResourceManager getResourceManager() {
      return this.fResourceManager;
    }
    
    boolean hasResourceManager() {
      return this.fResourceManager != null;
    }
    
    // --- Fields
    
    private IResourceManager fResourceManager;
    
  }
  
  private static final class CommunicationInterfaceInitializer implements ICIConfOptionsVisitor {
    
    CommunicationInterfaceInitializer(final IResourceManagerConfiguration rmConf) {
      this.fRMConf = rmConf;
    }
    
    // --- Interface methods implementation

    public void visit(final IMPICH2InterfaceConf configuration) {
      initMessagePassingInterface(configuration);
    }

    public void visit(final IOpenMPIInterfaceConf configuration) {
      initMessagePassingInterface(configuration);
      
      final IOpenMPIResourceManagerConfiguration rmConf = (IOpenMPIResourceManagerConfiguration) this.fRMConf;
      switch(configuration.getOpenMPIVersion()) {
        case EAutoDetect:
          rmConf.setVersionId(IOpenMPIResourceManagerConfiguration.VERSION_AUTO);
          break;
        case EVersion_1_2:
          rmConf.setVersionId(IOpenMPIResourceManagerConfiguration.VERSION_12);
          break;
        case EVersion_1_3:
          rmConf.setVersionId(IOpenMPIResourceManagerConfiguration.VERSION_13);
          break;
        case EVersion_1_4:
          rmConf.setVersionId(IOpenMPIResourceManagerConfiguration.VERSION_14);
          break;
      }
    }
    
    public void visit(final IParallelEnvironmentConf configuration) {
      final IPEResourceManagerConfiguration peConf = (IPEResourceManagerConfiguration) this.fRMConf;
      peConf.setLibraryOverride(configuration.getAlternateLibraryPath());
      peConf.setJobPollInterval(String.valueOf(configuration.getJobPolling()));
      switch (configuration.getClusterMode()) {
        case DEFAULT:
          peConf.setLoadLevelerMode("d"); //$NON-NLS-1$
          break;
        case LOCAL:
          peConf.setLoadLevelerMode("n"); //$NON-NLS-1$
          break;
        case MULTI_CLUSTER:
          peConf.setLoadLevelerMode("y"); //$NON-NLS-1$
          break;
      }
      peConf.setNodeMinPollInterval(String.valueOf(configuration.getNodePollingMin()));
      peConf.setNodeMaxPollInterval(String.valueOf(configuration.getNodePollingMax()));
      peConf.setProxyServerPath(configuration.getProxyServerPath());
      int options = 0;
      if (configuration.shouldLaunchProxyManually()) {
        options |= IRemoteProxyOptions.MANUAL_LAUNCH;
      }
      peConf.setOptions(options);
      
      switch (configuration.getDebuggingLevel()) {
        case NONE:
          peConf.setDebugLevel(TRACE_NOTHING);
          break;
        case FUNCTION:
          peConf.setDebugLevel(TRACE_FUNCTION);
          break;
        case DETAILED:
          peConf.setDebugLevel(TRACE_DETAIL);
          break;
      }
      peConf.setRunMiniproxy(configuration.shouldRunMiniProxy() ? OPTION_YES : OPTION_NO);
      peConf.setSuspendProxy(configuration.shouldSuspendProxyAtStartup() ? OPTION_YES : OPTION_NO);
      peConf.setUseLoadLeveler(configuration.shouldUseLoadLeveler() ? OPTION_YES : OPTION_NO);
    }

    public void visit(final ILoadLevelerConf configuration) {
      final IIBMLLResourceManagerConfiguration llConf = (IIBMLLResourceManagerConfiguration) this.fRMConf;
      llConf.setLibraryPath(configuration.getAlternateLibraryPath());
      llConf.setDefaultMulticluster((configuration.getClusterMode() == EClusterMode.DEFAULT) ? LL_YES : LL_NO);
      llConf.setForceProxyLocal((configuration.getClusterMode() == EClusterMode.LOCAL) ? LL_YES : LL_NO);
      llConf.setForceProxyMulticluster((configuration.getClusterMode() == EClusterMode.MULTI_CLUSTER) ? LL_YES : LL_NO);
      llConf.setJobPolling(configuration.getJobPolling());
      llConf.setMinNodePolling(configuration.getNodePollingMin());
      llConf.setMaxNodePolling(configuration.getNodePollingMax());
      llConf.setProxyServerPath(configuration.getProxyServerPath());
      int options = 0;
      if (configuration.shouldLaunchProxyManually()) {
        options |= IRemoteProxyOptions.MANUAL_LAUNCH;
      }
      llConf.setOptions(options);
      
      llConf.setTraceOption(((configuration.getProxyMessageOptions() & TRACE) != 0) ? LL_YES : LL_NO);
      llConf.setInfoMessage(((configuration.getProxyMessageOptions() & INFO) != 0) ? LL_YES : LL_NO);
      llConf.setWarningMessage(((configuration.getProxyMessageOptions() & WARNING) != 0) ? LL_YES : LL_NO);
      llConf.setErrorMessage(((configuration.getProxyMessageOptions() & ERROR) != 0) ? LL_YES : LL_NO);
      llConf.setFatalMessage(((configuration.getProxyMessageOptions() & FATAL) != 0) ? LL_YES : LL_NO);
      llConf.setArgsMessage(((configuration.getProxyMessageOptions() & ARGS) != 0) ? LL_YES : LL_NO);
      llConf.setProxyServerPath(configuration.getProxyServerPath());
      llConf.setTemplateFile(configuration.getTemplateFilePath());
      llConf.setTemplateWriteAlways((configuration.getTemplateOption() == ELLTemplateOpt.EAlwaysWrite) ? LL_YES : LL_NO);
      llConf.setSuppressTemplateWrite((configuration.getTemplateOption() == ELLTemplateOpt.ENeverWrite) ? LL_YES : LL_NO);
      llConf.setDebugLoop(configuration.shouldSuspendProxyAtStartup() ? LL_YES : LL_NO);
    }
    
    // --- Private code
    
    private void initMessagePassingInterface(final IMessagePassingInterfaceConf configuration) {
      final IToolRMConfiguration rmConf = (IToolRMConfiguration) this.fRMConf;
      rmConf.setUseToolDefaults(configuration.shouldTakeDefaultToolCommands());
      if (! configuration.shouldTakeDefaultToolCommands()) {
        rmConf.setLaunchCmd(configuration.getLaunchCommand());
        rmConf.setDebugCmd(configuration.getDebugCommand());
        rmConf.setDiscoverCmd(configuration.getDiscoverCommand());
        rmConf.setPeriodicMonitorCmd(configuration.getMonitorCommand());
        rmConf.setPeriodicMonitorTime(configuration.getMonitorPeriod());
      }
      rmConf.setUseInstallDefaults(configuration.shouldTakeDefaultInstallLocation());
      if (! configuration.shouldTakeDefaultInstallLocation()) {
        rmConf.setRemoteInstallPath(configuration.getInstallLocation());
      }
    }
    
    // --- Fields
    
    private final IResourceManagerConfiguration fRMConf;
    
  }
  
  // --- Fields
  
  private static TargetEnvironmentManager fTargetEnvManager = EnvironmentPlugin.getDefault().getTargetsManager();
  
  
  private static final String PROVIDER_EXTENSION_ID = "providers"; //$NON-NLS-1$
  
  private static final String PROVIDER_ELEMENT_NAME = "provider"; //$NON-NLS-1$
  
  private final static String ATTR_ID = "id"; //$NON-NLS-1$
  
  private final static String ATTR_CLASS = "class"; //$NON-NLS-1$
  
  private final static String PLATFORM_CONF_ID = "platform-conf-id"; //$NON-NLS-1$

}
