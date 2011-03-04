/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.rms.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.attributes.ArrayAttribute;
import org.eclipse.ptp.core.attributes.AttributeManager;
import org.eclipse.ptp.core.attributes.EnumeratedAttribute;
import org.eclipse.ptp.core.attributes.IntegerAttribute;
import org.eclipse.ptp.core.attributes.StringAttribute;
import org.eclipse.ptp.core.elementcontrols.IPJobControl;
import org.eclipse.ptp.core.elements.IPJob;
import org.eclipse.ptp.core.elements.attributes.JobAttributes;
import org.eclipse.ptp.core.elements.attributes.ProcessAttributes;
import org.eclipse.ptp.remote.core.IRemoteProcess;
import org.eclipse.ptp.remote.core.IRemoteProcessBuilder;
import org.eclipse.ptp.rm.core.utils.DebugUtil;
import org.eclipse.ptp.utils.core.ArgumentParser;

import x10dt.ui.launch.cpp.rms.Messages;
import x10dt.ui.launch.cpp.rms.RMSActivator;

/**
 * 
 * @author egeay
 */
public abstract class AbstractX10RuntimeSystemJob extends Job {
  
  protected AbstractX10RuntimeSystemJob(final String jobId, final String queueId, final String name, 
                                        final IX10RuntimeSystem runtimeSystem, final AttributeManager attrManager) {
    super(name);
    this.fRuntimeSystem = runtimeSystem;
    this.fJobId = jobId;
    this.fQueueId = queueId;
    this.fAttrManager = attrManager;
  }
  
  // --- Abstract methods definition
  
  protected abstract void completeEnvironmentVariables(final Map<String, String> envMap);

  // --- Abstract methods implementation
  
  protected final IStatus run(final IProgressMonitor monitor) {
    changeJobState(JobAttributes.State.STARTING);
    
    if (DebugUtil.RTS_JOB_TRACING_MORE) {
      System.out.println("Launch attributes:"); //$NON-NLS-1$
      String array[] = this.fAttrManager.toStringArray();
      for (int i = 0; i < array.length; i++) {
        System.out.println(array[i]);
      }
    }
    
    DebugUtil.trace(DebugUtil.RTS_JOB_TRACING_MORE, "About to run RTS job #{0}.", this.fJobId); //$NON-NLS-1$
    final Map<String, String> environment = getEnvironmentVariables();
    final String directory = getWorkingDirectory();
    final List<String> command = getLaunchCommand();

    if (DebugUtil.RTS_JOB_TRACING) {
      System.out.println("Environment variables:"); //$NON-NLS-1$
      for (Entry<String, String> env : environment.entrySet()) {
        System.out.println(NLS.bind("  export {0}={1}", env.getKey(), env.getValue())); //$NON-NLS-1$
      }
      System.out.println(NLS.bind("Work directory: {0}", directory)); //$NON-NLS-1$
      ArgumentParser argumentParser = new ArgumentParser(command);
      System.out.println(NLS.bind("Command: {0}", argumentParser.getCommandLine(false))); //$NON-NLS-1$
    }

    final IRemoteProcessBuilder processBuilder = this.fRuntimeSystem.createProcessBuilder(command, directory);
    processBuilder.environment().putAll(environment);

    if (monitor.isCanceled()) {
      changeJobState(JobAttributes.State.COMPLETED);
      return new Status(IStatus.OK, RMSActivator.PLUGIN_ID, Messages.AXRSJ_JobCancelationMsg);
    }

    try {
      DebugUtil.trace(DebugUtil.RTS_JOB_TRACING_MORE, "RTS job #{0}: running command ''{1}''", this.fJobId, command); //$NON-NLS-1$
      this.fJobProcess = processBuilder.start();
    } catch (IOException except) {
      changeJobState(JobAttributes.State.COMPLETED, EX10JobStatus.ERROR);
      return new Status(IStatus.ERROR, RMSActivator.PLUGIN_ID, Messages.AXRSJ_ProcessStartError, except);
    }

    DebugUtil.trace(DebugUtil.RTS_JOB_TRACING_MORE, "RTS job #{0}: handle start", this.fJobId); //$NON-NLS-1$
    doExecutionStarted(monitor);

    if (monitor.isCanceled()) {
      changeJobState(JobAttributes.State.COMPLETED);
      return new Status(IStatus.OK, RMSActivator.PLUGIN_ID, Messages.AXRSJ_JobCancelationMsg);
    }

    changeJobState(JobAttributes.State.RUNNING);

    DebugUtil.trace(DebugUtil.RTS_JOB_TRACING_MORE, "RTS job #{0}: wait to finish", this.fJobId); //$NON-NLS-1$
    doWaitExecution();
    
    terminateProcesses();
    if (this.fJobProcess == null) {
      changeJobStatusMessage(Messages.AXRSJ_JobCanceled);
      changeJobState(JobAttributes.State.COMPLETED, EX10JobStatus.CANCEL);
      return new Status(IStatus.CANCEL, RMSActivator.PLUGIN_ID, Messages.AXRSJ_JobCanceled);
    }
    if (this.fJobProcess.exitValue() == 0) {
      changeJobState(JobAttributes.State.COMPLETED);
    } else {
      changeJobStatusMessage(NLS.bind(Messages.AXRSJ_JobExitValueErrorMsg, new Integer(this.fJobProcess.exitValue())));
      changeJobState(JobAttributes.State.COMPLETED, EX10JobStatus.ERROR);
    }
    
    return new Status(IStatus.OK, RMSActivator.PLUGIN_ID, NLS.bind(Messages.AXRSJ_SuccessfulRunMsg,
                                                                   new Integer(this.fJobProcess.exitValue())));
  }
  
  // --- Overridden methods

  protected void canceling() {
    terminate();
  }
  
  // --- Code for implementers
  
  protected final AttributeManager getAttrManager() {
    return this.fAttrManager;
  }
  
  // --- Private code
  
  private void changeJobState(final JobAttributes.State newState) {
    final EnumeratedAttribute<JobAttributes.State> state = JobAttributes.getStateAttributeDefinition().create(newState);
    final AttributeManager attrManager = new AttributeManager();
    attrManager.addAttribute(state);
    this.fRuntimeSystem.changeJobAttributes(this.fJobId, attrManager);
  }
  
  private void changeJobState(final JobAttributes.State newState, final EX10JobStatus status) {
    final EnumeratedAttribute<JobAttributes.State> state = JobAttributes.getStateAttributeDefinition().create(newState);
    final StringAttribute strStatus = JobAttributes.getStatusAttributeDefinition().create(status.name());
    final AttributeManager attrManager = new AttributeManager();
    attrManager.addAttribute(state);
    attrManager.addAttribute(strStatus);
    this.fRuntimeSystem.changeJobAttributes(this.fJobId, attrManager);
  }
  
  private void changeJobStatusMessage(final String newMessage) {
    final StringAttribute message = JobAttributes.getStatusMessageAttributeDefinition().create(newMessage);
    final AttributeManager attrManager = new AttributeManager();
    attrManager.addAttribute(message);
    this.fRuntimeSystem.changeJobAttributes(this.fJobId, attrManager);
  }
  
  private void doExecutionStarted(final IProgressMonitor monitor) {
    if (! monitor.isCanceled()) {
      initializeProcesses();

      final IPJob ipJob = this.fRuntimeSystem.getPJob(this.fQueueId, this.fJobId);

      final BufferedReader outReader = new BufferedReader(new InputStreamReader(this.fJobProcess.getInputStream()));
      this.fStdOutThread = new Thread(new Runnable() {

        public void run() {
          try {
            String line;
            while ((line = outReader.readLine()) != null) {
              final StringAttribute attr = ProcessAttributes.getStdoutAttributeDefinition().create(line + '\n');
              final IPJobControl ipJobControl = (IPJobControl) ipJob;
              final BitSet procZero = new BitSet();
              procZero.set(0);
              ipJobControl.addProcessAttributes(procZero, new AttributeManager(attr));
            }
          } catch (IOException except) {
            RMSActivator.log(IStatus.ERROR, Messages.AXRSJ_OutputStreamReadingError, except);
          }
        }

      });

      final BufferedReader errReader = new BufferedReader(new InputStreamReader(this.fJobProcess.getErrorStream()));
      this.fStdErrThread = new Thread(new Runnable() {

        public void run() {
          try {
            String line;
            while ((line = errReader.readLine()) != null) {
              final StringAttribute attr = ProcessAttributes.getStderrAttributeDefinition().create(line + '\n');
              final IPJobControl ipJobControl = (IPJobControl) ipJob;
              final BitSet procZero = new BitSet();
              procZero.set(0);
              ipJobControl.addProcessAttributes(procZero, new AttributeManager(attr));
            }
          } catch (IOException except) {
            RMSActivator.log(IStatus.ERROR, Messages.AXRSJ_ErrorStreamReadingError, except);
          }
        }

      });

      this.fStdOutThread.start();
      this.fStdErrThread.start();
    }
  }
  
  private void doWaitExecution() {
    DebugUtil.trace(DebugUtil.RTS_JOB_TRACING_MORE, "RTS job #{0}: waiting for process to finish completely", this.fJobId); //$NON-NLS-1$
    try {
      this.fJobProcess.waitFor();
    } catch (InterruptedException except) {
      // Simply ignore
    }
    
    DebugUtil.trace(DebugUtil.RTS_JOB_TRACING_MORE, "RTS job #{0}: waiting stderr thread to finish", this.fJobId); //$NON-NLS-1$
    try {
      this.fStdErrThread.join();
    } catch (InterruptedException except) {
      // Simply ignore
    }

    DebugUtil.trace(DebugUtil.RTS_JOB_TRACING_MORE, "RTS job #{0}: waiting stdout thread to finish", this.fJobId); //$NON-NLS-1$
    try {
      this.fStdOutThread.join();
    } catch (InterruptedException except) {
      // Simply ignore
    }

    DebugUtil.trace(DebugUtil.RTS_JOB_TRACING_MORE, "RTS job #{0}: completely finished", this.fJobId); //$NON-NLS-1$
  }
  
  private Map<String, String> getEnvironmentVariables() {
    final Map<String, String> environmentMap = new HashMap<String, String>();
    
    final ArrayAttribute<String> attr = this.fAttrManager.getAttribute(JobAttributes.getEnvironmentAttributeDefinition());
    if (attr != null) {
      for (final String entry : attr.getValue()) {
        final int i = entry.indexOf('=');
        environmentMap.put(entry.substring(0, i), entry.substring(i + 1));
      }
    }
    completeEnvironmentVariables(environmentMap);
    return environmentMap;
  }
  
  private List<String> getLaunchCommand() {
    final List<String> command = new ArrayList<String>();
    final StringAttribute execName = this.fAttrManager.getAttribute(JobAttributes.getExecutableNameAttributeDefinition());
    final StringAttribute execPath = this.fAttrManager.getAttribute(JobAttributes.getExecutablePathAttributeDefinition());
    command.add(new Path(execPath.getValue()).append(execName.getValue()).toString());
    command.addAll(this.fAttrManager.getAttribute(JobAttributes.getProgramArgumentsAttributeDefinition()).getValue());
    return command;
  }
  
  private String getWorkingDirectory() {
    return this.fAttrManager.getAttribute(JobAttributes.getWorkingDirectoryAttributeDefinition()).getValue();
  }
  
  private void initializeProcesses() {
    final IPJob ipJob = this.fRuntimeSystem.getPJob(this.fQueueId, this.fJobId);
    final IntegerAttribute numProcsAttr = ipJob.getAttribute(JobAttributes.getNumberOfProcessesAttributeDefinition());
    this.fRuntimeSystem.createProcesses(this.fJobId, numProcsAttr.getValue().intValue());

    final AttributeManager attrMrg = new AttributeManager();
    attrMrg.addAttribute(ProcessAttributes.getStateAttributeDefinition().create(ProcessAttributes.State.RUNNING));
    this.fRuntimeSystem.changeProcesses(ipJob.getID(), ipJob.getProcessJobRanks(), attrMrg);
  }
  
  private void terminate() {
    synchronized (this) {
      if (this.fJobProcess != null) {
        this.fJobProcess.destroy();
        this.fJobProcess = null;
      }
    }
  }
  
  private void terminateProcesses() {
    final IPJob ipJob = this.fRuntimeSystem.getPJob(this.fQueueId, this.fJobId);
    final AttributeManager attrMrg = new AttributeManager();
    attrMrg.addAttribute(ProcessAttributes.getStateAttributeDefinition().create(ProcessAttributes.State.COMPLETED));
    this.fRuntimeSystem.changeProcesses(ipJob.getID(), ipJob.getProcessJobRanks(), attrMrg);
  }
  
  // --- Fields
  
  private final IX10RuntimeSystem fRuntimeSystem;
  
  private final String fJobId;
  
  private final String fQueueId;
  
  private final AttributeManager fAttrManager;
  
  private IRemoteProcess fJobProcess;
  
  private Thread fStdOutThread;
  
  private Thread fStdErrThread;

}
