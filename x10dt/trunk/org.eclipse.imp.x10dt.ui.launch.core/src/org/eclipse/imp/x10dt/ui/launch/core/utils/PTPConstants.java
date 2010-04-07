/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.utils;

/**
 * Some PTP constants useful for the X10 platform configuration.
 * 
 * @author egeay
 */
public final class PTPConstants {
  
  /**
   * Defines the local connection service id.
   */
  public static final String LOCAL_CONN_SERVICE_ID = "org.eclipse.ptp.remote.LocalServices"; //$NON-NLS-1$
  
  /**
   * Defines the remote connection service id.
   */
  public static final String REMOTE_CONN_SERVICE_ID = "org.eclipse.ptp.remote.RemoteTools"; //$NON-NLS-1$
  
  /**
   * Defines the launch service id.
   */
  public static final String LAUNCH_SERVICE_ID = "org.eclipse.ptp.core.LaunchService"; //$NON-NLS-1$

  /**
   * Defines the Open MPI service provider id.
   */
  public static final String MPICH2_SERVICE_PROVIDER_ID = "org.eclipse.ptp.rm.mpi.mpich2.MPICH2ServiceProvider"; //$NON-NLS-1$
  
  /**
   * Defines the Open MPI service provider id.
   */
  public static final String OPEN_MPI_SERVICE_PROVIDER_ID = "org.eclipse.ptp.rm.mpi.openmpi.OpenMPIServiceProvider"; //$NON-NLS-1$
  
  /**
   * Defines the Runtime service category id.
   */
  public static final String RUNTIME_SERVICE_CATEGORY_ID = "org.eclipse.ptp.core.category.Runtime"; //$NON-NLS-1$
  
}
