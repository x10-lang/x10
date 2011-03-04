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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ptp.core.attributes.AttributeManager;
import org.eclipse.ptp.core.elementcontrols.IPJobControl;
import org.eclipse.ptp.core.elementcontrols.IPMachineControl;
import org.eclipse.ptp.core.elementcontrols.IPNodeControl;
import org.eclipse.ptp.core.elementcontrols.IPQueueControl;
import org.eclipse.ptp.core.elementcontrols.IPUniverseControl;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.rmsystem.AbstractRuntimeResourceManager;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;
import org.eclipse.ptp.rtsystem.IRuntimeSystem;

/**
 * Defines the resource manager implementation for X10 Sockets transport.
 * 
 * @author egeay
 */
public final class SocketsResourceManager extends AbstractRuntimeResourceManager implements IResourceManager {

  /**
   * Creates the resource manager instance with the parameters provided.
   * 
   * @param id The resource manager id.
   * @param universe The universe of controls.
   * @param rmConfig The resource manager configuration.
   */
  public SocketsResourceManager(final String id, final IPUniverseControl universe, 
                                final IResourceManagerConfiguration rmConfig) {
    super(id, universe, rmConfig);
  }
  
  // --- Abstract methods implementation

  protected void doAfterCloseConnection() {
  }

  protected void doAfterOpenConnection() {
  }

  protected void doBeforeCloseConnection() {
  }

  protected void doBeforeOpenConnection() {
  }

  protected IPJobControl doCreateJob(final IPQueueControl queue, final String jobId, final AttributeManager attrs) {
    return newJob(queue, jobId, attrs);
  }

  protected IPMachineControl doCreateMachine(final String machineId, final AttributeManager attrs) {
    return newMachine(machineId, attrs);
  }

  protected IPNodeControl doCreateNode(final IPMachineControl machine, final String nodeId, final AttributeManager attrs) {
    return newNode(machine, nodeId, attrs);
  }

  protected IPQueueControl doCreateQueue(final String queueId, final AttributeManager attrs) {
    return newQueue(queueId, attrs);
  }

  protected IRuntimeSystem doCreateRuntimeSystem() throws CoreException {
    final IX10RMConfiguration rmConfig = (IX10RMConfiguration) getConfiguration();
    return new SocketsX10RuntimeSystem(Integer.parseInt(getID()), rmConfig);
  }

  protected boolean doUpdateJobs(final IPQueueControl queue, final Collection<IPJobControl> jobs, 
                                 final AttributeManager attrs) {
    return updateJobs(queue, jobs, attrs);
  }

  protected boolean doUpdateMachines(final Collection<IPMachineControl> machines, final AttributeManager attrs) {
    return updateMachines(machines, attrs);
  }

  protected boolean doUpdateNodes(final IPMachineControl machine, final Collection<IPNodeControl> nodes, 
                                  final AttributeManager attrs) {
    return updateNodes(machine, nodes, attrs);
  }

  protected boolean doUpdateProcesses(final IPJobControl job, final BitSet processJobRanks, final AttributeManager attrs) {
    return updateProcessesByJobRanks(job, processJobRanks, attrs);
  }

  protected boolean doUpdateQueues(final Collection<IPQueueControl> queues, final AttributeManager attrs) {
    return updateQueues(queues, attrs);
  }

  protected boolean doUpdateRM(final AttributeManager attrs) {
    return updateRM(attrs);
  }

}
