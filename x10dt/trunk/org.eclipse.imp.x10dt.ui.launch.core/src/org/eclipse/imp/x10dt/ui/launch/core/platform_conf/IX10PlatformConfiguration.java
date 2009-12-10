/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.platform_conf;

/**
 * Defines the information about an X10 platform for compilation and (if needed) linking of X10 generated files.
 * 
 * @author egeay
 */
public interface IX10PlatformConfiguration {  
  
  /**
   * Defines the status of the platform configuration.
   * 
   * @param validStatus The new status.
   */
  public void defineStatus(final EValidStatus validStatus);
  
  /**
   * Defines the status of the platform configuration to Error and assigns the error message to it.
   * 
   * @param errorMessage The error message associated with the platform configuration error status.
   */
  public void defineValidationErrorStatus(final String errorMessage);
  
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
   * @return A null value if the linking step is not required, otherwise a non-empty string.
   */
  public String getArchivingOpts();
  
  /**
   * Returns the compiler to use in order to compile X10 generated code.
   * 
   * @return A non-null non-empty string.
   */
  public String getCompiler();
  
  /**
   * Returns the compiler options to use in order to compile X10 generated code.
   * 
   * @return A non-null non-empty string.
   */
  public String getCompilerOpts();
  
  /**
   * Returns the linker to use in order to create executable from X10 generated files.
   * 
   * @return A possibly null value.
   */
  public String getLinker();
  
  /**
   * Returns the linking libraries to use in order to create executable from X10 generated files.
   * 
   * @return A possibly null value.
   */
  public String getLinkingLibs();
  
  /**
   * Returns the linking options to use in order to create executable from X10 generated files.
   * 
   * @return A possibly null value.
   */
  public String getLinkingOpts();
  
  /**
   * Returns the unique platform configuration name.
   * 
   * @return A possibly null value.
   */
  public String getName();
  
  /**
   * Returns the location where PGAS is installed.
   * 
   * @return A non-null non-empty string.
   */
  public String getPGASLocation();
  
  /**
   * Returns the resource manager id that can be used to access remote location(s).
   * 
   * @return A possibly null value.
   */
  public String getResourceManagerId();
  
  /**
   * Returns the OS where X10 distribution is installed.
   * 
   * @return A non-null value.
   */
  public ETargetOS getTargetOS();
  
  /**
   * Returns the validation error message if the validation failed, i.e is in {@link EValidStatus#ERROR} or
   * {@link EValidStatus#FAILURE} states.
   * 
   * @return The validation error message if the validation failed, otherwise will return <b>null</b>.
   */
  public String getValidationErrorMessage();
  
  /**
   * Returns the validation status.
   * 
   * @return A non-null enumeration value.
   */
  public EValidStatus getValidationStatus();
  
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
  
  /**
   * Returns if yes or no the archiving step is required.
   * 
   * @return True if the archiving step is mandatory, false otherwise.
   */
  public boolean hasArchivingStep();
  
  /**
   * Returns if yes or no the linking step is required.
   * 
   * @return True if the linking step is mandatory, false otherwise.
   */
  public boolean hasLinkingStep();
  
  /**
   * Returns if platform configuration is associated with C++ or Java back-end
   * 
   * @return True if it C++ back-end, false if it is Java back-end.
   */
  public boolean isCplusPlus();
  
  /**
   * Returns if the platform configuration is either local or remote.
   * 
   * @return True if it is local, false if it is remote.
   */
  public boolean isLocal();

}
