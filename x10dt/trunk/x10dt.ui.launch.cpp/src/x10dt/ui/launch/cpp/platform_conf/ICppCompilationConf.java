/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

import x10dt.ui.launch.core.platform_conf.EArchitecture;
import x10dt.ui.launch.core.platform_conf.EBitsArchitecture;
import x10dt.ui.launch.core.platform_conf.ETargetOS;

/**
 * Encapsulates the parameters for compiling and linking generated C++ files.
 * 
 * @author egeay
 */
public interface ICppCompilationConf extends IStatusConfProvider {
  
  /**
   * Returns the computer architecture of the X10 platform installation.
   * 
   * @return A non-null value.
   */
  public EArchitecture getArchitecture();
  
  /**
   * Returns the archiver to use in order to archive compiled generated files.
   * 
   * @return A null value if the linking step is not required, otherwise a non-empty string.
   */
  public String getArchiver();
  
  /**
   * Returns the archiving options to use in order to archive compiled generated files.
   * 
   * @param shouldBeInterpreted Indicates if the potential variables in the option should be interpreted or not.
   * @return A null value if the linking step is not required, otherwise a non-empty string.
   */
  public String getArchivingOpts(final boolean shouldBeInterpreted);
  
  /**
   * Returns the computer bits architecture of the X10 platform installation.
   * 
   * @return A non-null value.
   */
  public EBitsArchitecture getBitsArchitecture();
  
  /**
   * Returns the compiler to use in order to compile X10 generated code.
   * 
   * @return A non-null non-empty string.
   */
  public String getCompiler();
  
  /**
   * Returns the compiling options to use in order to compile X10 generated code.
   * 
   * @param shouldBeInterpreted Indicates if the potential variables in the option should be interpreted or not.
   * @return A non-null non-empty string.
   */
  public String getCompilingOpts(final boolean shouldBeInterpreted);
  
  /**
   * Returns the linker to use in order to create executable from X10 generated files.
   * 
   * @return A possibly null value.
   */
  public String getLinker();
  
  /**
   * Returns the linking libraries to use in order to create executable from X10 generated files.
   * 
   * @param shouldBeInterpreted Indicates if the potential variables in the option should be interpreted or not.
   * @return A possibly null value.
   */
  public String getLinkingLibs(final boolean shouldBeInterpreted);
  
  /**
   * Returns the linking options to use in order to create executable from X10 generated files.
   * 
   * @param shouldBeInterpreted Indicates if the potential variables in the option should be interpreted or not.
   * @return A possibly null value.
   */
  public String getLinkingOpts(final boolean shouldBeInterpreted);
  
  /**
   * Returns the location where PGAS is installed.
   * 
   * @return A non-null non-empty string.
   */
  public String getPGASLocation();
  
  /**
   * Returns the remote output folder location where the generated files will be transferred and compiled.
   * 
   * @return A non-null empty string if the resource manager is remote, otherwise <b>null</b>.
   */
  public String getRemoteOutputFolder();
  
  /**
   * Returns the OS where X10 distribution is installed.
   * 
   * @return A non-null value.
   */
  public ETargetOS getTargetOS();
  
  /**
   * Returns the locations where X10 distribution headers are installed.
   * 
   * @return A non-null non-empty string.
   */
  public String[] getX10HeadersLocations();
  
  /**
   * Returns the location where X10 distribution is installed.
   * 
   * @return A non-null non-empty string.
   */
  public String getX10DistribLocation();
  
  /**
   * Returns the locations where X10 distribution libraries are installed.
   * 
   * @return A non-null non-empty string.
   */
  public String[] getX10LibsLocations();

}
