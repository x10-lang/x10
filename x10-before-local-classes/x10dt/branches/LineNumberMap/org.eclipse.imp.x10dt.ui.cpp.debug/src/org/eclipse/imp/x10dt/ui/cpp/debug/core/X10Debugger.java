/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.imp.x10dt.ui.cpp.debug.Constants;
import org.eclipse.imp.x10dt.ui.cpp.debug.DebugCore;
import org.eclipse.imp.x10dt.ui.cpp.debug.DebugMessages;
import org.eclipse.imp.x10dt.ui.cpp.debug.pdi.X10PDIDebugger;
import org.eclipse.ptp.core.IPTPLaunchConfigurationConstants;
import org.eclipse.ptp.core.attributes.AttributeManager;
import org.eclipse.ptp.core.elements.IPJob;
import org.eclipse.ptp.core.elements.attributes.JobAttributes;
import org.eclipse.ptp.debug.core.IPDebugger;
import org.eclipse.ptp.debug.core.launch.IPLaunch;
import org.eclipse.ptp.debug.core.pdi.IPDISession;
import org.eclipse.ptp.debug.core.pdi.PDIException;
import org.eclipse.ptp.debug.core.pdi.Session;
import org.eclipse.ptp.debug.core.pdi.event.IPDIEventFactory;
import org.eclipse.ptp.debug.core.pdi.manager.IPDIManagerFactory;
import org.eclipse.ptp.debug.core.pdi.model.IPDIModelFactory;
import org.eclipse.ptp.debug.core.pdi.request.IPDIRequestFactory;
import org.eclipse.ptp.debug.sdm.core.SDMEventFactory;
import org.eclipse.ptp.debug.sdm.core.SDMManagerFactory;
import org.eclipse.ptp.debug.sdm.core.SDMModelFactory;
import org.eclipse.ptp.debug.sdm.core.SDMRequestFactory;

/**
 * Represents the access point from PTP side to X10 debugger implementation.
 * 
 * @author egeay
 */
public final class X10Debugger implements IPDebugger {

  // --- Interface methods implementation
  
  public IPDISession createDebugSession(final long timeout, final IPLaunch launch, final IPath corefile) throws CoreException {
    if (this.fPDIModelFactory == null) {
      this.fPDIModelFactory = new SDMModelFactory();
    }
    if (this.fPDIManagerFactory == null) {
      this.fPDIManagerFactory = new SDMManagerFactory();
    }
    if (this.fPDIEventFactory == null) {
      this.fPDIEventFactory = new SDMEventFactory();
    }
    if (this.fPDIRequestFactory == null) {
      this.fPDIRequestFactory = new SDMRequestFactory();
    }

    final IPDISession pdiSession = createSession(timeout, launch, corefile);
    this.fPDIDebugger.setPDISession(pdiSession);
    this.fPDIDebugger.setLaunch(launch);
    
    return pdiSession;
  }

  public void cleanup(final ILaunchConfiguration config, final AttributeManager attrMgr, final IPLaunch launch) {
    // Nothing to do at that point.
  }

  public void getLaunchAttributes(final ILaunchConfiguration config, final AttributeManager attrMgr) throws CoreException {
    final String remoteDebuggerPath = config.getAttribute(Constants.ATTR_REMOTE_DEBUGGER_PATH, ""); //$NON-NLS-1$
    if (remoteDebuggerPath.length() == 0) {
      throw new CoreException(new Status(IStatus.ERROR, DebugCore.PLUGIN_ID, DebugMessages.XDbg_NoRmtDbgPath));
    } else {
      initExecutableAttributes(attrMgr, remoteDebuggerPath);
    }
    final List<String> args = attrMgr.getAttribute(JobAttributes.getProgramArgumentsAttributeDefinition()).getValue();
    final String[] newArgs = new String[args.size() + 2];
    newArgs[0] = getQHostOptionValue(config);
    newArgs[1] = config.getAttribute(IPTPLaunchConfigurationConstants.ATTR_EXECUTABLE_PATH, (String) null);
    int i = 1;
    for (final String arg : args) {
      newArgs[++i] = arg;
    }
    attrMgr.addAttribute(JobAttributes.getProgramArgumentsAttributeDefinition().create(newArgs));
  }

  public void initialize(final ILaunchConfiguration config, final AttributeManager attrMgr, 
                         final IProgressMonitor monitor) throws CoreException {
    this.fPort = getPort(config);
    if (this.fPDIDebugger == null) {
      this.fPDIDebugger = new X10PDIDebugger(new X10DebuggerTranslator(), this.fPort);
    }
    try {
      this.fPDIDebugger.initialize(config, new ArrayList<String>(), monitor);
    } catch (PDIException except) {
      throw new CoreException(new Status(IStatus.ERROR, DebugCore.PLUGIN_ID, DebugMessages.XDbg_DebuggerInitError, except));
    }
  }
  
  // --- Private code
  
  private Session createSession(final long timeout, final IPLaunch launch, final IPath corefile) throws CoreException {
    final IPJob job = launch.getPJob();
    final int nprocs = job.getProcesses().length;
    final int jobSize = (nprocs == 0) ? 1 : nprocs;
    try {
      return new Session(this.fPDIManagerFactory, this.fPDIRequestFactory, this.fPDIEventFactory, this.fPDIModelFactory,
                         launch.getLaunchConfiguration(), timeout, this.fPDIDebugger, job.getID(), jobSize);
    } catch (PDIException except) {
      throw new CoreException(new Status(IStatus.ERROR, DebugCore.PLUGIN_ID, DebugMessages.XDbg_SessionFailed, except));
    }
  }
  
  private int getPort(final ILaunchConfiguration config) throws CoreException {
    final String rangePort = config.getAttribute(Constants.ATTR_RANGE_PORT, (String) null);
    if (rangePort == null) {
      return Integer.parseInt(config.getAttribute(Constants.ATTR_SPECIFIC_PORT, String.valueOf(Constants.DEFAULT_PORT)));
    } else {
      return getRandomPort(rangePort);
    }
  }
  
  private String getQHostOptionValue(final ILaunchConfiguration config) throws CoreException {
    final StringBuilder sb = new StringBuilder();
    sb.append("-qhost=").append(config.getAttribute(IPTPLaunchConfigurationConstants.ATTR_DEBUGGER_HOST, (String) null)) //$NON-NLS-1$
      .append(':').append(this.fPort);
    return sb.toString();
  }
  
  private int getRandomPort(final String range) {
    final String[] split = range.split(PORT_RANGE_SEP);
    final int min;
    final int max;
    if (split.length == 2) {
      min = Integer.parseInt(split[0]);
      max = Integer.parseInt(split[1]);
    } else {
      min = Constants.DEFAULT_PORT_RANGE_MIN;
      max = Constants.DEFAULT_PORT_RANGE_MAX;
    }
    final int portRange = max - min + 1;
    final int fraction = (int) (portRange * new Random(System.currentTimeMillis()).nextDouble());
    return fraction + min;
  }
  
  private void initExecutableAttributes(final AttributeManager attrMgr, final String remoteDebuggerPath) {
    int sepIndex = remoteDebuggerPath.lastIndexOf('/');
    if (sepIndex == -1) {
      // Let's try with Windows separator.
      sepIndex = remoteDebuggerPath.lastIndexOf('\\');
    }
    if (sepIndex != -1) {
      final String executablePath = remoteDebuggerPath.substring(0, sepIndex);
      attrMgr.addAttribute(JobAttributes.getExecutablePathAttributeDefinition().create(executablePath));
    }
    final String executableName = remoteDebuggerPath.substring(sepIndex + 1);
    attrMgr.addAttribute(JobAttributes.getExecutableNameAttributeDefinition().create(executableName));
  }
  
  // --- Fields
  
  private X10PDIDebugger fPDIDebugger;
  
  private IPDIModelFactory fPDIModelFactory;
  
  private IPDIManagerFactory fPDIManagerFactory;
  
  private IPDIEventFactory fPDIEventFactory;
  
  private IPDIRequestFactory fPDIRequestFactory;
  
  private int fPort;
  
  
  private static final String PORT_RANGE_SEP = "-"; //$NON-NLS-1$

  // USE_XLC=1 XLC_EXTRA_FLAGS=-bexpfull bin/x10c++ -commandlineonly -v -report postcompile=5 -o out/FSSimpleDist -d out samples/FSSimpleDist.x10
  
  //  poe /gsa/tlbgsa/projects/d/debugger/dev/aix/latest/bin/irmtdbgc -qhost=9.2.34.130:8001 ./FSSimpleDist -m 0 -procs 2 -hostfile .hostlist.x10run.345 -msg_api lapi -labelio yes

}
