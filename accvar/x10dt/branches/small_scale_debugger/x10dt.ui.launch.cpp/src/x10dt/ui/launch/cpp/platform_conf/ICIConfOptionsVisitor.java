/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

/**
 * Visitor to the different communication interface configuration types.
 * 
 * @author egeay
 */
public interface ICIConfOptionsVisitor {
  
  /**
   * Visits the communication interface configuration for MPICH-2.
   * 
   * @param configuration The current MPICH-2 configuration.
   */
  public void visit(final IMPICH2InterfaceConf configuration);
  
  /**
   * Visits the communication interface configuration for OpenMPI.
   * 
   * @param configuration The current OpenMPI configuration.
   */
  public void visit(final IOpenMPIInterfaceConf configuration);
  
  /**
   * Visits the communication interface configuration for IBM Parallel Environment.
   * 
   * @param configuration The current IBM PE configuration.
   */
  public void visit(final IParallelEnvironmentConf configuration);
  
  /**
   * Visits the communication interface configuration for IBM LoadLeveler.
   * 
   * @param configuration The current LoadLeveler configuration.
   */
  public void visit(final ILoadLevelerConf configuration);

}
