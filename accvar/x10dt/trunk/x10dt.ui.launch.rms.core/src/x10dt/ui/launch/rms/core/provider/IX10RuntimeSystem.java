/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.rms.core.provider;

import java.util.BitSet;
import java.util.List;

import org.eclipse.ptp.core.attributes.AttributeManager;
import org.eclipse.ptp.core.elements.IPJob;
import org.eclipse.ptp.remote.core.IRemoteProcessBuilder;
import org.eclipse.ptp.rtsystem.IRuntimeSystem;

/**
 * Provides general services for sockets and standalone runtime systems.
 * 
 * @author egeay
 */
public interface IX10RuntimeSystem extends IRuntimeSystem {
  
  /**
   * Changes the job attributes for a particular job id.
   * 
   * @param jobID The job id for which one wants to change its attributes.
   * @param changedAttrMgr The attribute manager containing the new values.
   */
  public void changeJobAttributes(final String jobID, final AttributeManager changedAttrMgr);
  
  /**
   * Changes the node attributes for a particular node id.
   * 
   * @param nodeID The node id for which one wants to change its attributes.
   * @param changedAttrMgr The attribute manager containing the new values.
   */
  public void changeNode(final String nodeID, final AttributeManager changedAttrMgr);
  
  /**
   * Changes the process attributes for a set of processes.
   * 
   * @param jobId The job id to consider.
   * @param processJobRanks The set of process identifiers.
   * @param changedAttrMgr The attribute manager containing the new values.
   */
  public void changeProcesses(final String jobId, final BitSet processJobRanks, final AttributeManager changedAttrMgr);
  
  /**
   * Creates a new node with the parameters provided.
   * 
   * @param parentID The parent id for the node to create.
   * @param name The node name.
   * @param number The node number.
   * @return A new node id.
   */
  public String createNode(final String parentID, final String name, final int number);
  
  /**
   * Creates a process builder with the command provided. Optionally a working directory can be considered.
   * 
   * @param command The command to execute.
   * @param workingDirectory The working directory to use when running the command, or <b>null</b> if we want to consider
   * the current directory.
   * @return A non-null object instance.
   */
  public IRemoteProcessBuilder createProcessBuilder(final List<String> command, final String workingDirectory);
  
  /**
   * Creates a number of processes for a particular job id.
   * 
   * @param jobId The job id to consider.
   * @param numberOfProcesses The number of processes to create.
   */
  public void createProcesses(final String jobId, final int numberOfProcesses);
  
  /**
   * Locates (if possible) the PTP job instance for the IDs given.
   * 
   * @param queueId The queue id.
   * @param jobId The job id to locate.
   * @return The job wrapper, or <b>null</b> if we could not find it with the particular IDs given.
   */
  public IPJob getPJob(final String queueId, final String jobId);
  
  /**
   * Returns the resource manager configuration for this runtime instance.
   * 
   * @return A non-null object instance.
   */
  public IX10RMConfiguration getRMConfiguration();

}
