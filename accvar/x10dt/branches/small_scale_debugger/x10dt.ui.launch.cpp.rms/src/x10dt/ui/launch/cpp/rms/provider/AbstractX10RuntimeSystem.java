/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.rms.provider;

import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.PTPCorePlugin;
import org.eclipse.ptp.core.attributes.AttributeManager;
import org.eclipse.ptp.core.attributes.BooleanAttribute;
import org.eclipse.ptp.core.attributes.IAttribute;
import org.eclipse.ptp.core.attributes.IllegalValueException;
import org.eclipse.ptp.core.elements.IPElement;
import org.eclipse.ptp.core.elements.IPJob;
import org.eclipse.ptp.core.elements.attributes.ElementAttributeManager;
import org.eclipse.ptp.core.elements.attributes.ElementAttributes;
import org.eclipse.ptp.core.elements.attributes.JobAttributes;
import org.eclipse.ptp.core.elements.attributes.MachineAttributes;
import org.eclipse.ptp.core.elements.attributes.NodeAttributes;
import org.eclipse.ptp.core.elements.attributes.ProcessAttributes;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteConnectionManager;
import org.eclipse.ptp.remote.core.IRemoteProcessBuilder;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.PTPRemoteCorePlugin;
import org.eclipse.ptp.remote.core.exception.RemoteConnectionException;
import org.eclipse.ptp.rm.core.MPIJobAttributes;
import org.eclipse.ptp.rm.core.utils.DebugUtil;
import org.eclipse.ptp.rtsystem.AbstractRuntimeSystem;
import org.eclipse.ptp.rtsystem.events.IRuntimeEventFactory;
import org.eclipse.ptp.rtsystem.events.IRuntimeNodeChangeEvent;
import org.eclipse.ptp.rtsystem.events.RuntimeEventFactory;
import org.eclipse.ptp.utils.core.RangeSet;

import x10dt.ui.launch.cpp.rms.Messages;
import x10dt.ui.launch.cpp.rms.RMSActivator;
import x10dt.ui.launch.cpp.rms.hostmap.HostMap;
import x10dt.ui.launch.cpp.rms.hostmap.HostMapReaderFactory;
import x10dt.ui.launch.cpp.rms.hostmap.IHostMapReader;


abstract class AbstractX10RuntimeSystem extends AbstractRuntimeSystem implements IX10RuntimeSystem {
  
  AbstractX10RuntimeSystem(final int id, final IX10RMConfiguration rmConfig) {
    this.fRMId = String.valueOf(id);
    this.fNextId = id;
    this.fRMConfig = rmConfig;
  }
  
  // --- Abstract methods definition
  
  protected abstract Job createCheckRequirementsJob(final IProgressMonitor monitor, final Collection<String> hostNames);
  
  protected abstract Job createRuntimeSystemJob(final String jobID, final String queueID, 
                                                final AttributeManager attrMgr) throws CoreException;

  // --- IControlSystem's interface methods implementation
  
  public final void submitJob(final String subId, final AttributeManager attrMgr) throws CoreException {
    attrMgr.addAttribute(JobAttributes.getSubIdAttributeDefinition().create(subId));
    
    // Create the job
    final String queueID = attrMgr.getAttribute(JobAttributes.getQueueIdAttributeDefinition()).getValue();
    final String jobID = createJob(queueID, attrMgr);
    attrMgr.addAttribute(JobAttributes.getJobIdAttributeDefinition().create(jobID));

    DebugUtil.trace(DebugUtil.JOB_TRACING, "RTS {0}: job submission #{0}, job id #{1}, queue id @{2}", //$NON-NLS-1$ 
                    this.fRMConfig.getName(), subId, jobID, queueID);
    
    final Job job = createRuntimeSystemJob(jobID, queueID, attrMgr);
    this.fJobs.put(jobID, job);
    
    job.schedule();
  }

  public final void terminateJob(final IPJob ipJob) throws CoreException {
    DebugUtil.trace(DebugUtil.JOB_TRACING, "RTS {0}: terminate job #{1}", this.fRMConfig.getName(), ipJob.getID()); //$NON-NLS-1$
    final Job job = this.fJobs.get(ipJob.getID());
    job.cancel();
  }

  // --- IMonitoringSystem's interface methods implementation
  
  public final void filterEvents(final IPElement element, final boolean filterChildren, 
                                 final AttributeManager filterAttributes) throws CoreException {
  }

  public final void startEvents() throws CoreException {
  }

  public final void stopEvents() throws CoreException {
  }

  // --- IRuntimeSystem's interface methods implementation
  
  public final void shutdown() throws CoreException {
    DebugUtil.trace(DebugUtil.RTS_TRACING, "RTS {0}: shutdown", this.fRMConfig.getName()); //$NON-NLS-1$

    for (final Job job : this.fJobs.values()) {
      job.cancel();
    }
    this.fJobs.clear();

    if (this.fConnection != null) {
      this.fConnection.close();
    }

    fireRuntimeShutdownStateEvent(this.fEventFactory.newRuntimeShutdownStateEvent());
  }

  public final void startup(final IProgressMonitor monitor) throws CoreException {
    final SubMonitor subMon = SubMonitor.convert(monitor, 100);

    subMon.subTask(Messages.AXRS_InitRMTaskName);

    DebugUtil.trace(DebugUtil.RTS_TRACING, "RTS {0}: startup", this.fRMConfig.getName()); //$NON-NLS-1$

    try {
      final PTPRemoteCorePlugin rmCorePlugin = PTPRemoteCorePlugin.getDefault();
      this.fRemoteServices = rmCorePlugin.getRemoteServices(this.fRMConfig.getRemoteServicesId());
      if (this.fRemoteServices == null) {
        throw new CoreException(new Status(IStatus.ERROR, RMSActivator.PLUGIN_ID,
                                           NLS.bind(Messages.AXRS_RemoteServNotFound, this.fRMConfig.getName())));
      }
      if (! this.fRemoteServices.isInitialized() && ! initializeRemoteServices(this.fRemoteServices, subMon.newChild(1))) {
        throw new CoreException(new Status(IStatus.ERROR, RMSActivator.PLUGIN_ID, 
                                           NLS.bind(Messages.AXRS_RemoteServInitError, this.fRemoteServices.getName())));
      }

      final IRemoteConnectionManager connectionManager = this.fRemoteServices.getConnectionManager();
      Assert.isNotNull(connectionManager);

      subMon.worked(9);
      subMon.subTask(Messages.AXRS_OpeningConnTaskName);

      this.fConnection = connectionManager.getConnection(this.fRMConfig.getConnectionName());
      if (this.fConnection == null) {
        throw new CoreException(new Status(IStatus.ERROR, RMSActivator.PLUGIN_ID,
                                           NLS.bind(Messages.AXRS_ConnNotFound, 
                                           this.fRMConfig.getConnectionName(), this.fRMConfig.getName())));
      }

      if (! this.fConnection.isOpen()) {
        try {
          this.fConnection.open(subMon.newChild(40));
        } catch (RemoteConnectionException except) {
          throw new CoreException(new Status(IStatus.ERROR, RMSActivator.PLUGIN_ID, except.getMessage()));
        }
      }

      if (subMon.isCanceled()) {
        this.fConnection.close();
        return;
      }
      
      final String machineId = createMachine(this.fConnection.getName());
      createQueue(Messages.AXRS_defaultQueueName);
      
      final IHostMapReader hostMapReader = HostMapReaderFactory.createAllReaders();
      final HostMap hostMap = hostMapReader.loadMap(this, this.fConnection, this.fRemoteServices, machineId, monitor);
      if (hostMap.getHosts().isEmpty()) {
        throw new CoreException(new Status(IStatus.ERROR, RMSActivator.PLUGIN_ID, Messages.AXRS_NoHostNameFound));
      }

      if (subMon.isCanceled()) {
        this.fConnection.close();
        return;
      }
      
      if (hostMap.isOnlyLocalHost()) {
        subMon.worked(50);
      } else {
        final Job checkRequirementsJob = createCheckRequirementsJob(subMon.newChild(50), hostMap.getHosts());
        if (checkRequirementsJob != null) {
          checkRequirementsJob.schedule();
          try {
            checkRequirementsJob.join();
          } catch (InterruptedException except) {
            // Just ignores.
          }
          if (! checkRequirementsJob.getResult().isOK()) {
            throw new CoreException(checkRequirementsJob.getResult());
          }
        }
      }
      
      fireRuntimeRunningStateEvent(this.fEventFactory.newRuntimeRunningStateEvent());
    } finally {
      if (monitor != null) {
        monitor.done();
      }
    }
  }
  
  // --- IX10RuntimeSystem's interface methods implementation
  
  public final void changeJobAttributes(final String jobID, final AttributeManager changedAttrMgr) {
    final AttributeManager attrMgr = new AttributeManager();
    attrMgr.addAttributes(changedAttrMgr.getAttributes());
    final ElementAttributeManager elementAttrs = new ElementAttributeManager();
    elementAttrs.setAttributeManager(new RangeSet(jobID), attrMgr);
    fireRuntimeJobChangeEvent(this.fEventFactory.newRuntimeJobChangeEvent(elementAttrs));

    for (final IAttribute<?, ?, ?> attr : changedAttrMgr.getAttributes()) {
      DebugUtil.trace(DebugUtil.RTS_TRACING, "RTS {0}, job #{1}: {2}={3}", this.fRMConfig.getName(), jobID, //$NON-NLS-1$ 
                      attr.getDefinition().getId(), attr.getValueAsString());
    }
  }
  
  public final void changeNode(final String nodeID, final AttributeManager changedAttrMgr) {
    final AttributeManager attrMgr = new AttributeManager();
    attrMgr.addAttributes(changedAttrMgr.getAttributes());
    final ElementAttributeManager elementAttrs = new ElementAttributeManager();
    elementAttrs.setAttributeManager(new RangeSet(nodeID), attrMgr);
    final IRuntimeNodeChangeEvent event = this.fEventFactory.newRuntimeNodeChangeEvent(elementAttrs);
    fireRuntimeNodeChangeEvent(event);

    for (IAttribute<?, ?, ?> attr : changedAttrMgr.getAttributes()) {
      DebugUtil.trace(DebugUtil.RTS_TRACING, "RTS {0}, node #{1}: {2}={3}", this.fRMConfig.getName(), nodeID, //$NON-NLS-1$ 
                      attr.getDefinition().getId(), attr.getValueAsString());
    }
  }
  
  public final void changeProcesses(final String jobId, final BitSet processJobRanks, final AttributeManager changedAttrMgr) {
    final AttributeManager attrMgr = new AttributeManager();
    attrMgr.addAttributes(changedAttrMgr.getAttributes());
    final ElementAttributeManager elementAttrs = new ElementAttributeManager();
    elementAttrs.setAttributeManager(new RangeSet(processJobRanks), attrMgr);
    fireRuntimeProcessChangeEvent(this.fEventFactory.newRuntimeProcessChangeEvent(jobId, elementAttrs));

    for (final IAttribute<?, ?, ?> attr : changedAttrMgr.getAttributes()) {
      DebugUtil.trace(DebugUtil.RTS_TRACING, "RTS {0}, processes #{1}: {2}={3}", this.fRMConfig.getName(), processJobRanks, //$NON-NLS-1$ 
                      attr.getDefinition().getId(), attr.getValueAsString());
    }
  }
  
  public final String createNode(final String parentID, final String name, final int number) {
    ++this.fNextId;
    final ElementAttributeManager mgr = new ElementAttributeManager();
    final AttributeManager attrMgr = new AttributeManager();
    attrMgr.addAttribute(NodeAttributes.getStateAttributeDefinition().create(NodeAttributes.State.UP));
    try {
      attrMgr.addAttribute(NodeAttributes.getNumberAttributeDefinition().create(new Integer(number)));
    } catch (IllegalValueException except) {
      assert false;
    }
    attrMgr.addAttribute(ElementAttributes.getNameAttributeDefinition().create(name));
    mgr.setAttributeManager(new RangeSet(this.fNextId), attrMgr);
    fireRuntimeNewNodeEvent(this.fEventFactory.newRuntimeNewNodeEvent(parentID, mgr));

    DebugUtil.trace(DebugUtil.RTS_TRACING, "RTS {0}: new node #{1}", this.fRMConfig.getName(), this.fNextId); //$NON-NLS-1$

    return String.valueOf(this.fNextId);
  }
  
  public final IRemoteProcessBuilder createProcessBuilder(final List<String> command, final String workingDirectory) {
    final IRemoteProcessBuilder processBuilder = this.fRemoteServices.getProcessBuilder(this.fConnection, command);
    if (workingDirectory == null) {
      return processBuilder;
    } else {
      final IFileStore directory = this.fRemoteServices.getFileManager(this.fConnection).getResource(workingDirectory);
      return processBuilder.directory(directory);
    }
  }
  
  public final void createProcesses(final String jobId, final int numberOfProcesses) {
    for (int i = 0; i < numberOfProcesses; ++i) {
      final ElementAttributeManager mgr = new ElementAttributeManager();
      final AttributeManager attrMgr = new AttributeManager();
      attrMgr.addAttribute(ProcessAttributes.getStateAttributeDefinition().create(ProcessAttributes.State.STARTING));
      mgr.setAttributeManager(new RangeSet(i), attrMgr);
      fireRuntimeNewProcessEvent(this.fEventFactory.newRuntimeNewProcessEvent(jobId, mgr));
    }
    DebugUtil.trace(DebugUtil.RTS_TRACING,"RTS {0}: created {1} new processes", this.fRMConfig.getName(), //$NON-NLS-1$ 
                    Integer.valueOf(numberOfProcesses));
  }
  
  public final IPJob getPJob(final String queueId, final String jobId) {
    return PTPCorePlugin.getDefault().getUniverse().getResourceManager(this.fRMId).getQueueById(queueId).getJobById(jobId);
  }
  
  public final IX10RMConfiguration getRMConfiguration() {
    return this.fRMConfig;
  }
  
  // --- Private code
  
  private String createJob(final String queueID, final AttributeManager attrMgr) {
    final ElementAttributeManager mgr = new ElementAttributeManager();
    final AttributeManager jobAttrMgr = new AttributeManager();

    // Add generated attributes.
    ++this.fNextId;
    final String jobID = String.valueOf(this.fNextId);
    jobAttrMgr.addAttribute(JobAttributes.getJobIdAttributeDefinition().create(jobID));
    jobAttrMgr.addAttribute(JobAttributes.getQueueIdAttributeDefinition().create(queueID));
    jobAttrMgr.addAttribute(JobAttributes.getStatusAttributeDefinition().create(MPIJobAttributes.Status.NORMAL.toString()));
    jobAttrMgr.addAttribute(JobAttributes.getUserIdAttributeDefinition().create(System.getenv("USER"))); //$NON-NLS-1$
    ++this.fJobNumber;
    jobAttrMgr.addAttribute(ElementAttributes.getNameAttributeDefinition().create(String.format("job%d", this.fJobNumber))); //$NON-NLS-1$

    // Get relevant attributes from launch attributes.
    final String subId = attrMgr.getAttribute(JobAttributes.getSubIdAttributeDefinition()).getValue();
    final String execName = attrMgr.getAttribute(JobAttributes.getExecutableNameAttributeDefinition()).getValue();
    final String execPath = attrMgr.getAttribute(JobAttributes.getExecutablePathAttributeDefinition()).getValue();
    final String workDir = attrMgr.getAttribute(JobAttributes.getWorkingDirectoryAttributeDefinition()).getValue();
    final Integer numProcs = attrMgr.getAttribute(JobAttributes.getNumberOfProcessesAttributeDefinition()).getValue();
    final List<String> progArgs = attrMgr.getAttribute(JobAttributes.getProgramArgumentsAttributeDefinition()).getValue();
    final BooleanAttribute debugAttr = attrMgr.getAttribute(JobAttributes.getDebugFlagAttributeDefinition());

    // Copy these relevant attributes to IPJob.
    jobAttrMgr.addAttribute(JobAttributes.getSubIdAttributeDefinition().create(subId));
    jobAttrMgr.addAttribute(JobAttributes.getExecutableNameAttributeDefinition().create(execName));
    jobAttrMgr.addAttribute(JobAttributes.getExecutablePathAttributeDefinition().create(execPath));
    jobAttrMgr.addAttribute(JobAttributes.getWorkingDirectoryAttributeDefinition().create(workDir));
    try {
      jobAttrMgr.addAttribute(JobAttributes.getNumberOfProcessesAttributeDefinition().create(numProcs));
    } catch (IllegalValueException except) {
      RMSActivator.log(IStatus.ERROR, Messages.AXRS_ProcsNumberError, except);
    }
    jobAttrMgr.addAttribute(JobAttributes.getProgramArgumentsAttributeDefinition().create(progArgs.toArray(new String[0])));
    if (debugAttr != null) {
      jobAttrMgr.addAttribute(JobAttributes.getDebugFlagAttributeDefinition().create(debugAttr.getValue()));
    }

    // Notify RM.
    mgr.setAttributeManager(new RangeSet(jobID), jobAttrMgr);
    fireRuntimeNewJobEvent(this.fEventFactory.newRuntimeNewJobEvent(queueID, mgr));

    DebugUtil.trace(DebugUtil.RTS_TRACING, "RTS {0}: new job #{1}", this.fRMConfig.getName(), jobID); //$NON-NLS-1$

    return jobID;
  }
  
  private String createMachine(final String name) {
    ++this.fNextId;
    ElementAttributeManager mgr = new ElementAttributeManager();
    AttributeManager attrMgr = new AttributeManager();
    attrMgr.addAttribute(MachineAttributes.getStateAttributeDefinition().create(MachineAttributes.State.UP));
    attrMgr.addAttribute(ElementAttributes.getNameAttributeDefinition().create(name));
    mgr.setAttributeManager(new RangeSet(this.fNextId), attrMgr);
    fireRuntimeNewMachineEvent(this.fEventFactory.newRuntimeNewMachineEvent(this.fRMId, mgr));

    DebugUtil.trace(DebugUtil.RTS_TRACING, "RTS {0}: new machine #{1}", this.fRMConfig.getName(), this.fNextId); //$NON-NLS-1$

    return String.valueOf(this.fNextId);
  }
  
  private String createQueue(final String name) {
    ++this.fNextId;
    final ElementAttributeManager mgr = new ElementAttributeManager();
    final AttributeManager attrMgr = new AttributeManager();
    attrMgr.addAttribute(ElementAttributes.getNameAttributeDefinition().create(name));
    mgr.setAttributeManager(new RangeSet(this.fNextId), attrMgr);
    fireRuntimeNewQueueEvent(this.fEventFactory.newRuntimeNewQueueEvent(this.fRMId, mgr));

    DebugUtil.trace(DebugUtil.RTS_TRACING, "RTS {0}: new queue #{1}", this.fRMConfig.getName(), this.fNextId); //$NON-NLS-1$

    return String.valueOf(this.fNextId);
  }
  
  private synchronized boolean initializeRemoteServices(final IRemoteServices services, final IProgressMonitor monitor) {
    SubMonitor progress = SubMonitor.convert(monitor, NLS.bind(Messages.AXRS_InitRemoteServTaskName, services.getName()), 10);
    try {
      while (! services.isInitialized() && ! progress.isCanceled()) {
        progress.setWorkRemaining(9);
        try {
          wait(100);
        } catch (InterruptedException except) {
          // Ignore
        }
        services.initialize();
        progress.worked(1);
      }
      if (progress.isCanceled()) {
        return false;
      }
    } finally {
      if (monitor != null) {
        monitor.done();
      }
    }
    return true;
  }
  
  // --- Fields
  
  private final String fRMId;
  
  private final IX10RMConfiguration fRMConfig;
  
  private final IRuntimeEventFactory fEventFactory = new RuntimeEventFactory();
  
  private final Map<String, Job> fJobs = Collections.synchronizedMap(new HashMap<String, Job>());
  
  private IRemoteConnection fConnection;
  
  private IRemoteServices fRemoteServices;
  
  private int fNextId;
  
  private int fJobNumber;

}
