/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

/**
 * The communication interface configuration parameters for IBM LoadLeveler.
 * 
 * @author egeay
 */
public interface ILoadLevelerConf extends IIBMCommunicationInterfaceConf {
  
  /**
   * Returns the options of logging level for proxy messages.
   * 
   * @return A combination of the flags of {@link CLoadLevelerProxyMsgs}.
   */
  public int getProxyMessageOptions();
    
  /**
   * Returns the path to the template file.
   * 
   * <p>When submitting jobs using the basic mode, parameters are substituted into a template file before the job is 
   * submitted. You can specify your own template file containing a mix of the keywords to be substituted and specific 
   * job command file parameters of your choice.
   * 
   * @return An empty string if there is none, otherwise a non-empty one.
   */
  public String getTemplateFilePath();
  
  /**
   * Returns the policy for template file writing.
   * 
   * <p>You can instruct the proxy to write the template file it is using to /tmp each time the proxy is started and the 
   * main entry point is entered, or never write the template file for those cases where you are using only advanced mode 
   * or are specifying your own template file to be used.
   * 
   * @return A non-null instance.
   */
  public ELLTemplateOpt getTemplateOption();
  
}
