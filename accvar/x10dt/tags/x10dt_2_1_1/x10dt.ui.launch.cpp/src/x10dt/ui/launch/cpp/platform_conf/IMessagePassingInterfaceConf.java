/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;


/**
 * Contains the common options for the message passing communication interface type.
 * 
 * @author egeay
 */
public interface IMessagePassingInterfaceConf extends ICommunicationInterfaceConf {
  
  /**
   * Returns the communication interface command that should be run in debug mode.
   * 
   * @return A possibly <b>null</b> value if we take the default command or it hasn't yet been defined.
   */
  public String getDebugCommand();
  
  /**
   * Returns the command to use in order to discover certain communication interface properties.
   * 
   * @return A possibly <b>null</b> value if we take the default command or it hasn't yet been defined.
   */
  public String getDiscoverCommand();
  
  /**
   * Returns the communication interface command that should be run in debug mode.
   * 
   * @return A possibly <b>null</b> value if we take the default location or if it hasn't yet been defined..
   */
  public String getInstallLocation();
  
  /**
   * Returns the communication interface command that should be run in launch mode.
   * 
   * @return A possibly <b>null</b> value if we take the default command or it hasn't yet been defined.
   */
  public String getLaunchCommand();
  
  /**
   * Returns the command that can be used to periodically monitor system/job status.
   * 
   * @return A possibly <b>null</b> value if we take the default command or it hasn't yet been defined.
   */
  public String getMonitorCommand();
  
  /**
   * Returns the time interval to delay between issuing periodic monitor commands.
   * 
   * @return A natural number.
   */
  public int getMonitorPeriod();
  
  /**
   * Returns if yes or no we should take the default install location for the communication interface defined by all the 
   * parameters.
   * 
   * @return True if we should take default install location, false otherwise.
   */
  public boolean shouldTakeDefaultInstallLocation();
  
  /**
   * Returns if yes or no we should take the default communication interface commands defined by PTP.
   * 
   * @return True if we should take the default PTP commands for the given communication interface type, false otherwise.
   */
  public boolean shouldTakeDefaultToolCommands();

}
