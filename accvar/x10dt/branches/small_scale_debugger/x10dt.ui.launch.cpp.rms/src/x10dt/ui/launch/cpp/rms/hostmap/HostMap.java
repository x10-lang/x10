/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.rms.hostmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Encapsulates information about the hosts detected by {@link IHostMapReader}.
 * 
 * @author egeay
 */
public class HostMap {
  
  HostMap(final Collection<String> hosts) {
    this.fHosts = hosts;
    this.fIsOnlyLocalHost = true;
  }
  
  HostMap(final String localHostName) {
    this.fHosts = Arrays.asList(localHostName);
    this.fIsOnlyLocalHost = true;
  }
  
  HostMap() {
    this.fHosts = new ArrayList<String>();
    this.fIsOnlyLocalHost = false;
  }
  
  // --- Public services
  
  /**
   * Returns the list of hosts.
   * 
   * @return A non-null possibly empty list of host names.
   */
  public Collection<String> getHosts() {
    return this.fHosts;
  }
  
  /**
   * Indicates whether we have identified only the local host as the host name.
   * 
   * @return True if it is the only the local host, false otherwise.
   */
  public boolean isOnlyLocalHost() {
    return this.fIsOnlyLocalHost;
  }
  
  // --- Fields
  
  private final boolean fIsOnlyLocalHost;
  
  private final Collection<String> fHosts;

}
