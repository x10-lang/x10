/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors;

import x10dt.ui.launch.core.platform_conf.EValidationStatus;

/**
 * Listener for getting notified when end-user switches between local and remote connection.
 * 
 * @author egeay
 */
public interface IConnectionTypeListener {
  
  /**
   * Indicates that a connection type has just changed.
   * 
   * @param isLocal True if the end-user switched to a local connection, false if it was to a remote connection.
   * @param remoteConnectionName If <i>isLocal</i> is false such parameter indicates potentially (it may be <b>null</b>),
   * the remote connection name.
   * @param validationStatus If <i>isLocal</i> is false such parameter indicates the remote connection validation status.
   * @param newCurrent Flag indicating if this change is related to a new connection or not.
   */
  public void connectionChanged(final boolean isLocal, final String remoteConnectionName, 
                                final EValidationStatus validationStatus, final boolean newCurrent);

}
