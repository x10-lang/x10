/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

import java.io.IOException;
import java.io.Writer;

/**
 * Defines the model for X10 Platform Configuration.
 * 
 * <p>A platform configuration is a combination of 3 different entities required for proper compilation and launching, 
 * either locally or remotely.
 * <ul>
 * <li>The "Connection" configuration is used for identifying if the operations will occur locally or remotely, and in the
 * latter case it will provide the information for accessing the remote machine. See {@link IConnectionConf}</li>
 * <li>The "Communication Interface" is responsible for defining the protocol for communication between the different 
 * processes/machines (e.g., OpenMPI). See {@link ICommunicationInterfaceConf}.</li>
 * <li>Finally, the "Compilation and Linking" configuration is useful for providing the commands to compile and link the
 * generated C++ files. See {@link ICppCompilationConf}.</li>
 * </ul>
 * 
 * <p>One can get implementation(s) of that interface via {@link X10PlatformConfFactory} factory method(s).
 * 
 * @author egeay
 */
public interface IX10PlatformConf {  
  
  /**
   * Creates a working copy of the current X10 platform configuration. Such copy is mutable while the current configuration 
   * is not.
   * 
   * @return A non-null object.
   */
  public IX10PlatformConfWorkCopy createWorkingCopy();
  
  /**
   * Returns the communication interface configuration part.
   * 
   * @return A non-null object.
   */
  public ICommunicationInterfaceConf getCommunicationInterfaceConf();
  
  /**
   * Returns the connection configuration part.
   * 
   * @return A non-null object.
   */
  public IConnectionConf getConnectionConf();
  
  /**
   * Returns the C++ compilation and linking configuration part.
   * 
   * @return A non-null object.
   */
  public ICppCompilationConf getCppCompilationConf();
  
  /**
   * Returns a description for the current X10 platform configuration. It is clearly an optional parameter.
   * 
   * @return A possibly null value.
   */
  public String getDescription();
  
  /**
   * Returns a universal unique identifier for the platform configuration.
   * 
   * @return A non-null string.
   */
  public String getId();
  
  /**
   * Returns a human-readable name for the platform configuration. It is a mandatory parameter.
   * 
   * @return A non-null value.
   */
  public String getName();
  
  /**
   * Returns if the current platform configuration contains all the information required for compilation and launching.
   * 
   * @param onlyCompilation True if one wants only to check if all information are available for local or remote compilation.
   * False if one wants also to include check for communication interface parameters.
   * @return True if it is complete, false otherwise.
   */
  public boolean isComplete(final boolean onlyCompilation);
  
  /**
   * Saves the current platform configuration using the writer provided.
   * 
   * @param writer The writer to use for persisting the current platform configuration.
   * @throws IOException Occurs if we could not save the information with the writer provided.
   */
  public void save(final Writer writer) throws IOException;

}
