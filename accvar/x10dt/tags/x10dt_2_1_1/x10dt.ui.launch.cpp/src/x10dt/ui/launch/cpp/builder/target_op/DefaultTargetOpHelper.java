/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.builder.target_op;

import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteServices;

final class DefaultTargetOpHelper extends AbstractTargetOpHelper implements ITargetOpHelper {

  DefaultTargetOpHelper(final IRemoteServices remoteServices, final IRemoteConnection remoteConnection) {
    super(remoteServices, remoteConnection);
  }
  
  // --- Interface methods implementation

  public String getTargetSystemPath(final String resourcePath) {
    return resourcePath.replace('\\', '/');
  }

}
