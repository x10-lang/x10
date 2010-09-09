/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;


/**
 * Encapsulates the communication interface configuration parameters.
 * 
 * @author egeay
 */
public interface ICommunicationInterfaceConf {

  /**
   * Returns the id that identifies the communication service mode in PTP. Right now it is Launch, Debug or Profile.
   * 
   * @return A possibly <b>null</b> if it hasn't yet been defined.
   */
  public String getServiceModeId();
  
  /**
   * Returns the id that identifies the communication interface type. For instance OpenMPI, MPICH-2, etc...
   * 
   * @return A possibly <b>null</b> if it hasn't yet been defined.
   */
  public String getServiceTypeId();
  
  /**
   * Detects if the current communication interface parameters are structurally equivalent to the one contained in the PTP
   * configuration of the resource manager transmitted.
   * 
   * @param rmConfiguration The resource manager configuration to consider.
   * @return True if it is equals, false otherwise.
   */
  public boolean hasSameCommunicationInterfaceInfo(final IResourceManagerConfiguration rmConfiguration);
  
  /**
   * Returns true if the current configuration contains all the required information to define properly the communication
   * interface.
   * 
   * @return True if it is complete, false otherwise.
   */
  public boolean isComplete();
  
  /**
   * Accepts a visitor to access the options dependent of the communication interface type.
   * 
   * @param visitor The visitor implementation to consider.
   */
  public void visitInterfaceOptions(final ICIConfOptionsVisitor visitor);

}
