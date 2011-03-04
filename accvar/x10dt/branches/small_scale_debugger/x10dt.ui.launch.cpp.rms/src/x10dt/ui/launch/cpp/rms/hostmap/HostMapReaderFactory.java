/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.rms.hostmap;

/**
 * Factory methods to create implementations of {@link IHostMapReader}.
 * 
 * @author egeay
 */
public final class HostMapReaderFactory {
  
  /**
   * Creates a reader that in order will try to read an OpenMPI host file, then if it fails a MPICH-2 host file, then
   * finally if it fails will take the current host name as host for the run.
   * 
   * @return A non-null object instance.
   */
  public static IHostMapReader createAllReaders() {
    return new CompositeReader(new OpenMPIHostMapReader(), new MPICH2HostMapReader(), new CurrentHostMapReader());
  }
  
  /**
   * Creates a reader that identifies the current host name and adds it to the machine identified.
   * 
   * @return A non-null object instance.
   */
  public static IHostMapReader createCurrentHostReader() {
    return new CurrentHostMapReader();
  }
  
  /**
   * Creates a reader that tries to read an OpenMPI host file and if successful adds the list of host names to the machine
   * identified.
   * 
   * @return A non-null object instance.
   */
  public static IHostMapReader createOpenMPIReader() {
    return new OpenMPIHostMapReader();
  }
  
  /**
   * Creates a reader that tries to read a MPICH-2 host file and if successful adds the list of host names to the machine
   * identified.
   * 
   * @return A non-null object instance.
   */
  public static IHostMapReader createMPICH2Reader() {
    return new MPICH2HostMapReader();
  }
  
  // --- Private code
  
  private HostMapReaderFactory() {}

}
