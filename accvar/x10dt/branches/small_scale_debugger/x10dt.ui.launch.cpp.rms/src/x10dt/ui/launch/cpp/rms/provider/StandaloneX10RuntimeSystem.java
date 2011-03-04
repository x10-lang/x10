/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.rms.provider;

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ptp.core.attributes.AttributeManager;
import org.eclipse.ptp.core.elements.attributes.JobAttributes;


final class StandaloneX10RuntimeSystem extends AbstractX10RuntimeSystem implements IX10RuntimeSystem {

  StandaloneX10RuntimeSystem(final int id, final IX10RMConfiguration rmConfig) {
    super(id, rmConfig);
  }

  // --- Abstract methods implementation
  
  protected Job createCheckRequirementsJob(final IProgressMonitor monitor, final Collection<String> hostNames) {
    // No checking to be done.
    return null;
  }
  
  protected Job createRuntimeSystemJob(final String jobID, final String queueID, 
                                       final AttributeManager attrMgr) throws CoreException {
    return new StandaloneX10RuntimeSystemJob(jobID, queueID, "Standalone Run Job", this, attrMgr); //$NON-NLS-1$
  }
  
  // --- Private classes
  
  private static final class StandaloneX10RuntimeSystemJob extends AbstractX10RuntimeSystemJob {

    protected StandaloneX10RuntimeSystemJob(final String jobId, final String queueId, final String name, 
                                            final IX10RuntimeSystem runtimeSystem, final AttributeManager attrManager) {
      super(jobId, queueId, name, runtimeSystem, attrManager);
    }
    
    // --- Abstract methods implementation 
    
    protected void completeEnvironmentVariables(final Map<String, String> envMap) {
      final Integer procs = getAttrManager().getAttribute(JobAttributes.getNumberOfProcessesAttributeDefinition()).getValue();
      envMap.put("X10RT_STANDALONE_NUMPLACES", String.valueOf(procs)); //$NON-NLS-1$
    }

  }

}
