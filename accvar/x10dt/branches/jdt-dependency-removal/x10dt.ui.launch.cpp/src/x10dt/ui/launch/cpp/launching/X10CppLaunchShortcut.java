/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.launching;

import static org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_CONSOLE;
import static x10dt.ui.launch.core.utils.PTPConstants.MPICH2_SERVICE_PROVIDER_ID;
import static x10dt.ui.launch.core.utils.PTPConstants.OPEN_MPI_SERVICE_PROVIDER_ID;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.imp.model.ISourceEntity;
import org.eclipse.imp.utils.Pair;
import org.eclipse.ptp.core.elements.IPMachine;
import org.eclipse.ptp.core.elements.IPNode;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.rm.mpi.mpich2.ui.launch.MPICH2LaunchConfiguration;
import org.eclipse.ptp.rm.mpi.mpich2.ui.launch.MPICH2LaunchConfigurationDefaults;
import org.eclipse.ptp.rm.mpi.openmpi.ui.launch.OpenMPILaunchConfiguration;
import org.eclipse.ptp.rm.mpi.openmpi.ui.launch.OpenMPILaunchConfigurationDefaults;

import polyglot.types.ClassType;
import x10dt.core.X10DTCorePlugin;
import x10dt.ui.launch.core.Constants;
import x10dt.ui.launch.core.utils.PTPConstants;
import x10dt.ui.launch.cpp.CppLaunchCore;
import x10dt.ui.launch.cpp.platform_conf.ICommunicationInterfaceConf;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;
import x10dt.ui.launch.cpp.utils.PTPConfUtils;
import x10dt.ui.launching.AbstractX10LaunchShortcut;



/**
 * Implements the launch shortcut for C++ back-end.
 * 
 * @author egeay
 */
public class X10CppLaunchShortcut extends AbstractX10LaunchShortcut implements ILaunchShortcut {

	/**
	 * Launch configuration attribute key. The value is a name of
	 * a Java project associated with a Java launch configuration.
	 */
	public static final String ATTR_PROJECT_NAME = "org.eclipse.jdt.launching.PROJECT_ATTR"; //$NON-NLS-1$

	/**
	 * Launch configuration attribute key. The value is a fully qualified name
	 * of a main type to launch.
	 */
	public static final String ATTR_MAIN_TYPE_NAME = "org.eclipse.jdt.launching.MAIN_TYPE";	 //$NON-NLS-1$

	
  // --- Abstract methods implementation

  protected ILaunchConfigurationType getConfigurationType() {
    return DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(LAUNCH_CONF_TYPE);
  }
  
  protected final String getProjectNatureId() {
    return X10DTCorePlugin.X10_CPP_PRJ_NATURE_ID;
  }

  protected void setLaunchConfigurationAttributes(final ILaunchConfigurationWorkingCopy workingCopy,
                                                  final Pair<ClassType, ISourceEntity> type) {
    workingCopy.setAttribute(ATTR_PROJECT_NAME, type.second.getProject().getName());
    workingCopy.setAttribute(org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_PROJECT_NAME, 
                             type.second.getProject().getName());
    workingCopy.setAttribute(ATTR_MAIN_TYPE_NAME, type.first.fullName().toString());
    workingCopy.setAttribute(ATTR_CONSOLE, true);
    workingCopy.setAttribute(Constants.ATTR_X10_MAIN_CLASS, type.first.fullName().toString());
    
    final IProject project = type.second.getResource().getProject();
    final IX10PlatformConf platformConf = CppLaunchCore.getInstance().getPlatformConfiguration(project);
    
    final String useHostListAttrKey;
    final String hostListAttrKey;
    if (OPEN_MPI_SERVICE_PROVIDER_ID.equals(platformConf.getCommunicationInterfaceConf().getServiceTypeId())) {
      try {
        OpenMPILaunchConfigurationDefaults.loadDefaults();
        setOpenMPIDefaults(workingCopy);
        useHostListAttrKey = OpenMPILaunchConfiguration.ATTR_USEHOSTLIST;
        hostListAttrKey = OpenMPILaunchConfiguration.ATTR_HOSTLIST;
      } catch (CoreException except) {
        CppLaunchCore.log(except.getStatus());
        return;
      }
    } else if (MPICH2_SERVICE_PROVIDER_ID.equals(platformConf.getCommunicationInterfaceConf().getServiceModeId())) {
      try {
        MPICH2LaunchConfigurationDefaults.loadDefaults();
        setMPICNDefaults(workingCopy);
        useHostListAttrKey = MPICH2LaunchConfiguration.ATTR_USEHOSTLIST;
        hostListAttrKey = MPICH2LaunchConfiguration.ATTR_HOSTLIST;
      } catch (CoreException except) {
        CppLaunchCore.log(except.getStatus());
        return;
      }
    } else {
      useHostListAttrKey = null;
      hostListAttrKey = null;
    }
    
    if ((useHostListAttrKey != null) && (hostListAttrKey != null)) {
      final IResourceManager resourceManager = PTPConfUtils.findResourceManagerById(platformConf);
      if (resourceManager != null) {
        final StringBuilder hostListBuilder = new StringBuilder();
        int i = 0;
        for (final IPMachine machine : resourceManager.getMachines()) {
          for (final IPNode node : machine.getNodes()) {
            if (i == 0) {
              i = 1;
            } else {
              hostListBuilder.append(',');
            }
            hostListBuilder.append(node.getName());
          }
        }
        workingCopy.setAttribute(useHostListAttrKey, true);
        workingCopy.setAttribute(hostListAttrKey, hostListBuilder.toString());
      }
    }
  }
  
  // --- Overridden methods
  
  protected void updateLaunchConfig(ILaunchConfigurationWorkingCopy config) throws CoreException {
    final String projectName = config.getAttribute(ATTR_PROJECT_NAME, ""); //$NON-NLS-1$
    final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
    final IX10PlatformConf platformConf = CppLaunchCore.getInstance().getPlatformConfiguration(project);

    updateCommunicationInterfaceAttributes(platformConf.getCommunicationInterfaceConf(), config);
  }
  
  // --- Private code
 
  private boolean isMPICH2LaunchConfig(final ILaunchConfiguration config) throws CoreException {
    return config.hasAttribute(MPICH2LaunchConfiguration.ATTR_NUMPROCS);
  }
  
  private boolean isOpenMPILaunchConfig(final ILaunchConfiguration config) throws CoreException {
    return config.hasAttribute(OpenMPILaunchConfiguration.ATTR_NUMPROCS);
  }

  private void setMPICNDefaults(final ILaunchConfigurationWorkingCopy workingCopy) throws CoreException {
    MPICH2LaunchConfigurationDefaults.loadDefaults();
    workingCopy.setAttribute(MPICH2LaunchConfiguration.ATTR_NUMPROCS, MPICH2LaunchConfigurationDefaults.ATTR_NUMPROCS);
    workingCopy.setAttribute(MPICH2LaunchConfiguration.ATTR_NOLOCAL, MPICH2LaunchConfigurationDefaults.ATTR_NOLOCAL);
    workingCopy.setAttribute(MPICH2LaunchConfiguration.ATTR_PREFIX, MPICH2LaunchConfigurationDefaults.ATTR_PREFIX);
    workingCopy.setAttribute(MPICH2LaunchConfiguration.ATTR_USEPREFIX, MPICH2LaunchConfigurationDefaults.ATTR_USEPREFIX);
    workingCopy.setAttribute(MPICH2LaunchConfiguration.ATTR_HOSTFILE, MPICH2LaunchConfigurationDefaults.ATTR_HOSTFILE);
    workingCopy.setAttribute(MPICH2LaunchConfiguration.ATTR_USEHOSTFILE, MPICH2LaunchConfigurationDefaults.ATTR_USEHOSTFILE);
    workingCopy.setAttribute(MPICH2LaunchConfiguration.ATTR_HOSTLIST, MPICH2LaunchConfigurationDefaults.ATTR_HOSTLIST);
    workingCopy.setAttribute(MPICH2LaunchConfiguration.ATTR_USEHOSTLIST, MPICH2LaunchConfigurationDefaults.ATTR_USEHOSTLIST);
    workingCopy.setAttribute(MPICH2LaunchConfiguration.ATTR_ARGUMENTS, MPICH2LaunchConfigurationDefaults.ATTR_ARGUMENTS);
    workingCopy.setAttribute(MPICH2LaunchConfiguration.ATTR_USEDEFAULTARGUMENTS, 
                             MPICH2LaunchConfigurationDefaults.ATTR_USEDEFAULTARGUMENTS);
    workingCopy.setAttribute(MPICH2LaunchConfiguration.ATTR_USEDEFAULTPARAMETERS, 
                             MPICH2LaunchConfigurationDefaults.ATTR_USEDEFAULTPARAMETERS);
  }
  
  private void setOpenMPIDefaults(final ILaunchConfigurationWorkingCopy workingCopy) throws CoreException {
    OpenMPILaunchConfigurationDefaults.loadDefaults();
    workingCopy.setAttribute(OpenMPILaunchConfiguration.ATTR_NUMPROCS, OpenMPILaunchConfigurationDefaults.ATTR_NUMPROCS);
    workingCopy.setAttribute(OpenMPILaunchConfiguration.ATTR_USEHOSTLIST, OpenMPILaunchConfigurationDefaults.ATTR_USEHOSTLIST);
    workingCopy.setAttribute(OpenMPILaunchConfiguration.ATTR_HOSTLIST, OpenMPILaunchConfigurationDefaults.ATTR_HOSTLIST);
    workingCopy.setAttribute(OpenMPILaunchConfiguration.ATTR_BYNODE, OpenMPILaunchConfigurationDefaults.ATTR_BYNODE);
    workingCopy.setAttribute(OpenMPILaunchConfiguration.ATTR_BYSLOT, OpenMPILaunchConfigurationDefaults.ATTR_BYSLOT);
    workingCopy.setAttribute(OpenMPILaunchConfiguration.ATTR_NOOVERSUBSCRIBE, 
                             OpenMPILaunchConfigurationDefaults.ATTR_NOOVERSUBSCRIBE);
    workingCopy.setAttribute(OpenMPILaunchConfiguration.ATTR_NOLOCAL, OpenMPILaunchConfigurationDefaults.ATTR_NOLOCAL);
    workingCopy.setAttribute(OpenMPILaunchConfiguration.ATTR_PREFIX, OpenMPILaunchConfigurationDefaults.ATTR_PREFIX);
    workingCopy.setAttribute(OpenMPILaunchConfiguration.ATTR_USEPREFIX, OpenMPILaunchConfigurationDefaults.ATTR_USEPREFIX);
    workingCopy.setAttribute(OpenMPILaunchConfiguration.ATTR_HOSTFILE, OpenMPILaunchConfigurationDefaults.ATTR_HOSTFILE);
    workingCopy.setAttribute(OpenMPILaunchConfiguration.ATTR_USEHOSTFILE, OpenMPILaunchConfigurationDefaults.ATTR_USEHOSTFILE);
    workingCopy.setAttribute(OpenMPILaunchConfiguration.ATTR_ARGUMENTS, OpenMPILaunchConfigurationDefaults.ATTR_ARGUMENTS);
    workingCopy.setAttribute(OpenMPILaunchConfiguration.ATTR_USEDEFAULTARGUMENTS, 
                             OpenMPILaunchConfigurationDefaults.ATTR_USEDEFAULTARGUMENTS);
    workingCopy.setAttribute(OpenMPILaunchConfiguration.ATTR_USEDEFAULTPARAMETERS, 
                             OpenMPILaunchConfigurationDefaults.ATTR_USEDEFAULTPARAMETERS);
  }
  
  private void updateCommunicationInterfaceAttributes(final ICommunicationInterfaceConf commIntfConf,
                                                      final ILaunchConfigurationWorkingCopy config) throws CoreException {
    final String serviceTypeId = commIntfConf.getServiceTypeId();
    if (serviceTypeId.equals(PTPConstants.OPEN_MPI_SERVICE_PROVIDER_ID)) {
      if (! isOpenMPILaunchConfig(config)) {
        setOpenMPIDefaults(config);
      }
    } else if (serviceTypeId.equals(PTPConstants.MPICH2_SERVICE_PROVIDER_ID)) {
      if (! isMPICH2LaunchConfig(config)) {
        setMPICNDefaults(config);
      }
    }
  }
  
  @Override
  protected boolean launchConfigMatches(ILaunchConfiguration config,
			String typeName, String projectName) throws CoreException {
	  //return config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "").equals(typeName) && //$NON-NLS-1$
	  return config.getAttribute(org.eclipse.ptp.core.IPTPLaunchConfigurationConstants.ATTR_PROJECT_NAME, "").equals(projectName);
  }
  
  // --- Fields
  
  private static final String LAUNCH_CONF_TYPE = "x10dt.ui.cpp.launch.X10LaunchConfigurationType"; //$NON-NLS-1$

}
